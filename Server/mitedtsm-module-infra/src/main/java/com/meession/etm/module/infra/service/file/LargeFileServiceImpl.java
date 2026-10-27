package com.meession.etm.module.infra.service.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.infra.controller.admin.file.vo.largefile.*;
import com.meession.etm.module.infra.dal.dataobject.file.LargeFileDO;
import com.meession.etm.module.infra.dal.mysql.file.LargeFileMapper;
import com.meession.etm.module.infra.framework.file.core.utils.FileTypeUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 大文件上传 Service 实现类
 * 
 * 设计理念（参照 LargeFileHandler）：
 * - 分片是临时数据，通过文件系统管理，不需要 chunks 表
 * - 分片存储路径：chunks/{fileId}/{chunkIndex}.chunk
 * - 最终文件存储路径：uploads/{yyyyMMdd}/{filename}
 * - 获取已上传分片：扫描文件系统目录
 * - 合并后删除分片目录
 * - 仅限本地上传，绑定用户
 *
 * @author iFlow
 */
@Slf4j
@Service
public class LargeFileServiceImpl implements LargeFileService {

    /**
     * 分片临时存储路径
     */
    @Value("${mitedtsm.file.chunk-path:./uploads/chunks}")
    private String chunkPath;

    /**
     * 最终文件存储路径
     */
    @Value("${mitedtsm.file.upload-path:./uploads}")
    private String uploadPath;

    @Resource
    private LargeFileMapper largeFileMapper;

    /**
     * 初始化存储目录
     */
    @PostConstruct
    public void initDirectories() {
        try {
            Path chunkDir = Paths.get(chunkPath);
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(chunkDir)) {
                Files.createDirectories(chunkDir);
                log.info("[initDirectories] 创建分片存储目录: {}", chunkPath);
            }
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("[initDirectories] 创建文件存储目录: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("[initDirectories] 创建存储目录失败", e);
        }
    }

    @Override
    public LargeFileCheckRespVO checkFile(LargeFileCheckReqVO reqVO) {
        // 1. 获取当前用户ID
        Long userId = getLoginUserId();
        
        // 2. 文件名安全校验
        validateFilename(reqVO.getFilename());

        // 3. 查找是否有相同文件名、大小和用户的未完成任务（断点续传）
        LargeFileDO existingTask = findExistingTask(reqVO.getFilename(), reqVO.getFileSize(), userId);

        String fileId;
        List<Integer> uploadedChunks;

        if (existingTask != null) {
            // 3.1 找到已有任务，使用该任务的ID（断点续传）
            fileId = existingTask.getFileId();
            // 通过扫描文件系统获取已上传分片
            uploadedChunks = getUploadedChunksFromFileSystem(fileId);
            // 重置任务状态
            if (Arrays.asList(
                    LargeFileDO.STATUS_UPLOADING_FAILED,
                    LargeFileDO.STATUS_MERGING_FAILED,
                    LargeFileDO.STATUS_INTERRUPTED
            ).contains(existingTask.getStatus())) {
                resetTaskStatus(existingTask.getId());
            }
            log.info("[checkFile] 断点续传: fileId={}, filename={}, userId={}, 已上传分片数={}",
                    fileId, reqVO.getFilename(), userId, uploadedChunks.size());
        } else {
            // 3.2 没有已有任务，创建新的上传任务
            fileId = IdUtil.fastSimpleUUID();
            uploadedChunks = new ArrayList<>();

            // 创建上传任务（绑定用户）
            LargeFileDO task = LargeFileDO.builder()
                    .fileId(fileId)
                    .filename(reqVO.getFilename())
                    .fileSize(reqVO.getFileSize())
                    .chunkSize(reqVO.getChunkSize())
                    .totalChunks(reqVO.getTotalChunks())
                    .uploadedChunks(0)
                    .mergedChunks(0)
                    .status(LargeFileDO.STATUS_UPLOADING)
                    .contentType(reqVO.getContentType())
                    .userId(userId)
                    .build();
            largeFileMapper.insert(task);
            log.info("[checkFile] 创建新任务: fileId={}, filename={}, userId={}", fileId, reqVO.getFilename(), userId);
        }

        // 4. 返回结果
        LargeFileCheckRespVO respVO = new LargeFileCheckRespVO();
        respVO.setFileId(fileId);
        respVO.setUploadedChunks(uploadedChunks);
        respVO.setNeedUpload(uploadedChunks.size() < reqVO.getTotalChunks());
        respVO.setMessage(uploadedChunks.isEmpty() ? "需要上传" : "断点续传");
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadChunk(LargeFileChunkUploadReqVO reqVO, MultipartFile file) {
        // 1. 获取任务
        LargeFileDO task = largeFileMapper.selectByFileId(reqVO.getFileId());
        if (task == null) {
            log.warn("[uploadChunk] 上传任务不存在: fileId={}，可能是用户已取消", reqVO.getFileId());
            return;
        }
        
        // 2. 验证任务归属（只能操作自己的任务）
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            log.warn("[uploadChunk] 无权操作此任务: fileId={}, taskUserId={}, currentUserId={}", 
                    reqVO.getFileId(), task.getUserId(), userId);
            return;
        }
        
        // 3. 检查任务状态
        if (LargeFileDO.STATUS_INTERRUPTED.equals(task.getStatus())) {
            log.warn("[uploadChunk] 任务已中断: fileId={}", reqVO.getFileId());
            return;
        }

        try {
            // 4. 保存分片文件到文件系统
            saveChunkFile(reqVO.getFileId(), reqVO.getChunkIndex(), file);

            // 5. 更新任务进度（原子递增）
            largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                    .setSql("uploaded_chunks = uploaded_chunks + 1")
                    .eq(LargeFileDO::getId, task.getId()));

            log.info("[uploadChunk] 分片上传成功: fileId={}, chunkIndex={}/{}",
                    reqVO.getFileId(), reqVO.getChunkIndex(), reqVO.getTotalChunks() - 1);

        } catch (Exception e) {
            log.error("[uploadChunk] 分片上传失败: fileId={}, chunkIndex={}", reqVO.getFileId(), reqVO.getChunkIndex(), e);
            markUploadFailed(task.getId(), "分片上传失败: " + e.getMessage());
            throw new RuntimeException("分片上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String mergeChunks(LargeFileMergeReqVO reqVO) {
        // 1. 获取任务
        LargeFileDO task = largeFileMapper.selectByFileId(reqVO.getFileId());
        if (task == null) {
            log.warn("[mergeChunks] 上传任务不存在: fileId={}", reqVO.getFileId());
            return null;
        }
        
        // 2. 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            log.warn("[mergeChunks] 无权操作此任务: fileId={}, taskUserId={}, currentUserId={}", 
                    reqVO.getFileId(), task.getUserId(), userId);
            return null;
        }
        
        // 3. 检查任务状态
        if (LargeFileDO.STATUS_INTERRUPTED.equals(task.getStatus())) {
            log.warn("[mergeChunks] 任务已中断: fileId={}", reqVO.getFileId());
            return null;
        }

        // 4. 检查分片是否完整
        List<Integer> uploadedChunks = getUploadedChunksFromFileSystem(reqVO.getFileId());
        if (uploadedChunks.size() != task.getTotalChunks()) {
            throw new RuntimeException("分片不完整，无法合并。已上传: " + uploadedChunks.size() + ", 需要: " + task.getTotalChunks());
        }

        // 5. 标记任务为合并中
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_MERGING)
                .set(LargeFileDO::getMergedChunks, 0)
                .set(LargeFileDO::getErrorMessage, null)
                .eq(LargeFileDO::getId, task.getId()));

        // 6. 异步执行合并
        final Long taskId = task.getId();
        final String fileId = task.getFileId();
        final int totalChunks = task.getTotalChunks();
        final String filename = reqVO.getFilename();
        final String directory = reqVO.getDirectory();
        final String contentType = StrUtil.isNotEmpty(task.getContentType())
                ? task.getContentType()
                : FileTypeUtils.getMineType(filename);

        CompletableFuture.runAsync(() -> {
            try {
                doMergeChunks(taskId, fileId, filename, directory, contentType, totalChunks);
            } catch (Exception e) {
                log.error("[mergeChunks-async] 合并文件失败: fileId={}", fileId, e);
                markMergeFailed(taskId, "合并文件失败: " + e.getMessage());
            }
        });

        return null;
    }

    /**
     * 执行合并分片的实际逻辑（本地文件系统）
     * 严格参照 LargeFileHandler 实现
     */
    private void doMergeChunks(Long taskId, String fileId, String filename, String directory, 
                               String contentType, int totalChunks) throws Exception {
        // 1. 生成目标文件路径
        String targetPath = generateTargetPath(filename, directory);
        Path targetFile = Paths.get(uploadPath, targetPath).toAbsolutePath();

        // 2. 确保目标目录存在
        Path parentDir = targetFile.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 3. 获取分片目录
        Path chunkDir = Paths.get(chunkPath, fileId).toAbsolutePath();
        if (!Files.exists(chunkDir)) {
            throw new IOException("分片目录不存在: " + chunkDir);
        }

        // 4. 检查分片完整性
        long chunkCount;
        try (Stream<Path> stream = Files.list(chunkDir)) {
            chunkCount = stream.filter(Files::isRegularFile).count();
        }
        if (chunkCount < totalChunks) {
            throw new IOException("分片不完整，缺少 " + (totalChunks - chunkCount) + " 个分片");
        }

        // 5. 流式合并分片到目标文件
        try (
                OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetFile))
        ) {
            for (int i = 0; i < totalChunks; i++) {
                // 检查任务状态，如果已中断则停止合并
                LargeFileDO task = largeFileMapper.selectById(taskId);
                if (task != null && LargeFileDO.STATUS_INTERRUPTED.equals(task.getStatus())) {
                    log.warn("[doMergeChunks] 任务已中断，停止合并: fileId={}", fileId);
                    // 删除已合并的临时文件
                    Files.deleteIfExists(targetFile);
                    return;
                }

                Path chunkFile = chunkDir.resolve(i + ".chunk");
                if (!Files.exists(chunkFile)) {
                    throw new IOException("分片文件不存在: " + chunkFile);
                }
                
                // 流式复制分片到目标文件（每个分片只读取一次）
                Files.copy(chunkFile, out);

                // 更新合并进度
                final int mergedCount = i + 1;
                largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                        .set(LargeFileDO::getMergedChunks, mergedCount)
                        .eq(LargeFileDO::getId, taskId));
            }
        }

        // 6. 获取文件大小
        long fileSize = Files.size(targetFile);

        // 7. 更新任务状态为完成
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_COMPLETED)
                .set(LargeFileDO::getPath, targetPath)
                .set(LargeFileDO::getErrorMessage, null)
                .eq(LargeFileDO::getId, taskId));

        // 8. 删除分片目录
        deleteChunkDirectory(fileId);

        log.info("[doMergeChunks] 文件合并完成: fileId={}, filename={}, size={}, path={}",
                fileId, filename, fileSize, targetPath);
    }

    @Override
    public PageResult<LargeFileRespVO> getTaskPage(LargeFilePageReqVO reqVO) {
        Long userId = getLoginUserId();
        // 添加 userId 过滤条件，只查看当前用户的任务
        PageResult<LargeFileDO> pageResult = largeFileMapper.selectPage(reqVO, userId);
        List<LargeFileRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public LargeFileRespVO getTask(Long id) {
        LargeFileDO task = largeFileMapper.selectById(id);
        if (task == null) {
            return null;
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            return null;
        }
        return convertToRespVO(task);
    }

    @Override
    public LargeFileRespVO getTaskByFileId(String fileId) {
        LargeFileDO task = largeFileMapper.selectByFileId(fileId);
        if (task == null) {
            return null;
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            return null;
        }
        return convertToRespVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        LargeFileDO task = largeFileMapper.selectById(id);
        if (task == null) {
            return;
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            log.warn("[deleteTask] 无权删除此任务: id={}, taskUserId={}, currentUserId={}", 
                    id, task.getUserId(), userId);
            return;
        }

        // 删除数据库记录
        largeFileMapper.deleteById(id);

        // 删除分片目录
        deleteChunkDirectory(task.getFileId());

        log.info("[deleteTask] 删除上传任务: id={}, filename={}", id, task.getFilename());
    }

    @Override
    public void resetTask(Long id) {
        LargeFileDO task = largeFileMapper.selectById(id);
        if (task == null) {
            return;
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            log.warn("[resetTask] 无权重置此任务: id={}, taskUserId={}, currentUserId={}", 
                    id, task.getUserId(), userId);
            return;
        }
        
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_UPLOADING)
                .set(LargeFileDO::getErrorMessage, null)
                .eq(LargeFileDO::getId, id));
        log.info("[resetTask] 重置任务状态: id={}", id);
    }

    @Override
    public void interruptTask(String fileId) {
        LargeFileDO task = largeFileMapper.selectByFileId(fileId);
        if (task == null) {
            log.warn("[interruptTask] 任务不存在: fileId={}", fileId);
            return;
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            log.warn("[interruptTask] 无权中断此任务: fileId={}, taskUserId={}, currentUserId={}", 
                    fileId, task.getUserId(), userId);
            return;
        }
        
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_INTERRUPTED)
                .eq(LargeFileDO::getId, task.getId()));
        log.info("[interruptTask] 任务已中断: fileId={}", fileId);
    }

    @Override
    public Integer cleanupExpiredTasks(Integer retainDays) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(retainDays);

        // 查询过期任务
        List<LargeFileDO> expiredTasks = largeFileMapper.selectList(
                new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<LargeFileDO>()
                        .lt(LargeFileDO::getCreateTime, expireTime)
        );

        // 删除过期任务和分片文件
        for (LargeFileDO task : expiredTasks) {
            deleteChunkDirectory(task.getFileId());
        }

        // 批量删除任务记录
        if (!expiredTasks.isEmpty()) {
            largeFileMapper.deleteBatchIds(expiredTasks.stream()
                    .map(LargeFileDO::getId)
                    .collect(Collectors.toList()));
        }

        log.info("[cleanupExpiredTasks] 清理过期任务完成: count={}, expireTime={}", expiredTasks.size(), expireTime);
        return expiredTasks.size();
    }

    @Override
    public List<Integer> getUploadedChunks(String fileId) {
        return getUploadedChunksFromFileSystem(fileId);
    }

    // ========== 核心方法：文件系统管理分片 ==========

    /**
     * 通过扫描文件系统获取已上传分片列表
     */
    private List<Integer> getUploadedChunksFromFileSystem(String fileId) {
        Path chunkDir = Paths.get(chunkPath, fileId);
        if (!Files.exists(chunkDir)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(chunkDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".chunk"))
                    .map(name -> name.replace(".chunk", ""))
                    .map(Integer::parseInt)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("[getUploadedChunksFromFileSystem] 扫描分片目录失败: fileId={}", fileId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 保存分片文件到文件系统
     */
    private void saveChunkFile(String fileId, Integer chunkIndex, MultipartFile file) throws IOException {
        Path chunkDir = Paths.get(chunkPath, fileId);
        if (!Files.exists(chunkDir)) {
            Files.createDirectories(chunkDir);
        }

        String filename = chunkIndex + ".chunk";
        Path chunkFilePath = chunkDir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, chunkFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.debug("[saveChunkFile] 保存分片: {}", chunkFilePath);
    }

    /**
     * 删除分片目录
     */
    private void deleteChunkDirectory(String fileId) {
        try {
            Path chunkDir = Paths.get(chunkPath, fileId);
            if (Files.exists(chunkDir)) {
                try (Stream<Path> stream = Files.walk(chunkDir)) {
                    stream.sorted((a, b) -> -a.compareTo(b))
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    log.warn("[deleteChunkDirectory] 删除文件失败: {}", path);
                                }
                            });
                }
            }
            log.info("[deleteChunkDirectory] 删除分片目录完成: fileId={}", fileId);
        } catch (IOException e) {
            log.warn("[deleteChunkDirectory] 删除分片目录失败: fileId={}", fileId, e);
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 查找已有的上传任务（同用户、同文件名、同大小）
     */
    private LargeFileDO findExistingTask(String filename, Long fileSize, Long userId) {
        return largeFileMapper.selectOne(
                new com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX<LargeFileDO>()
                        .eq(LargeFileDO::getFilename, filename)
                        .eq(LargeFileDO::getFileSize, fileSize)
                        .eq(LargeFileDO::getUserId, userId)
                        .in(LargeFileDO::getStatus, Arrays.asList(
                                LargeFileDO.STATUS_UPLOADING,
                                LargeFileDO.STATUS_UPLOADING_FAILED,
                                LargeFileDO.STATUS_MERGING,
                                LargeFileDO.STATUS_MERGING_FAILED,
                                LargeFileDO.STATUS_INTERRUPTED
                        ))
                        .orderByDesc(LargeFileDO::getCreateTime)
                        .last("LIMIT 1")
        );
    }

    /**
     * 重置任务状态（内部方法）
     */
    private void resetTaskStatus(Long taskId) {
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_UPLOADING)
                .set(LargeFileDO::getErrorMessage, null)
                .eq(LargeFileDO::getId, taskId));
    }

    /**
     * 标记上传失败
     */
    private void markUploadFailed(Long taskId, String errorMessage) {
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_UPLOADING_FAILED)
                .set(LargeFileDO::getErrorMessage, errorMessage)
                .eq(LargeFileDO::getId, taskId));
    }

    /**
     * 标记合并失败
     */
    private void markMergeFailed(Long taskId, String errorMessage) {
        largeFileMapper.update(null, new LambdaUpdateWrapper<LargeFileDO>()
                .set(LargeFileDO::getStatus, LargeFileDO.STATUS_MERGING_FAILED)
                .set(LargeFileDO::getErrorMessage, errorMessage)
                .eq(LargeFileDO::getId, taskId));
    }

    /**
     * 生成目标文件路径
     * 格式：{directory/}yyyyMMdd/timestamp_filename.ext
     */
    private String generateTargetPath(String filename, String directory) {
        // 1. 生成日期前缀
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 2. 生成时间戳后缀
        String suffix = String.valueOf(System.currentTimeMillis());
        String ext = FileUtil.extName(filename);
        String mainName = FileUtil.mainName(filename);
        String newName;
        if (StrUtil.isNotEmpty(ext)) {
            newName = mainName + "_" + suffix + "." + ext;
        } else {
            newName = filename + "_" + suffix;
        }
        
        // 3. 拼接路径
        String path = datePrefix + "/" + newName;
        if (StrUtil.isNotEmpty(directory)) {
            path = directory + "/" + path;
        }
        return path;
    }

    /**
     * 文件名安全校验
     */
    private void validateFilename(String filename) {
        if (StrUtil.contains(filename, "..") ||
                StrUtil.startWithAny(filename, "/", "\\")) {
            throw new IllegalArgumentException("文件名不合法");
        }
    }

    /**
     * 转换为响应 VO
     */
    private LargeFileRespVO convertToRespVO(LargeFileDO task) {
        LargeFileRespVO vo = BeanUtils.toBean(task, LargeFileRespVO.class);
        // 计算上传进度
        if (task.getTotalChunks() != null && task.getTotalChunks() > 0) {
            vo.setProgress(task.getUploadedChunks() * 100.0 / task.getTotalChunks());
        }
        // 状态名称
        vo.setStatusName(getStatusName(task.getStatus()));
        return vo;
    }

    @Override
    public void downloadFile(Long id, HttpServletResponse response) throws Exception {
        LargeFileDO task = largeFileMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        // 验证任务归属
        Long userId = getLoginUserId();
        if (!userId.equals(task.getUserId())) {
            throw new IllegalArgumentException("无权下载此文件");
        }
        // 验证任务状态
        if (!LargeFileDO.STATUS_COMPLETED.equals(task.getStatus())) {
            throw new IllegalArgumentException("文件尚未上传完成");
        }
        if (StrUtil.isBlank(task.getPath())) {
            throw new IllegalArgumentException("文件路径为空");
        }

        File file = Paths.get(uploadPath, task.getPath()).toAbsolutePath().toFile();
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在");
        }

        // 设置响应头
        String encodedFilename = URLEncoder.encode(task.getFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
        response.setContentLengthLong(file.length());

        // 流式输出文件
        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(String status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case LargeFileDO.STATUS_UPLOADING:
                return "上传中";
            case LargeFileDO.STATUS_UPLOADING_FAILED:
                return "上传失败";
            case LargeFileDO.STATUS_MERGING:
                return "合并中";
            case LargeFileDO.STATUS_MERGING_FAILED:
                return "合并失败";
            case LargeFileDO.STATUS_COMPLETED:
                return "已完成";
            case LargeFileDO.STATUS_INTERRUPTED:
                return "已中断";
            default:
                return "未知";
        }
    }

}
