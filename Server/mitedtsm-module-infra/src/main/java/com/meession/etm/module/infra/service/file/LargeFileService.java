package com.meession.etm.module.infra.service.file;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.infra.controller.admin.file.vo.largefile.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 大文件上传 Service 接口
 *
 * @author iFlow
 */
public interface LargeFileService {

    /**
     * 检查文件上传状态（支持断点续传）
     *
     * @param reqVO 检查请求
     * @return 检查结果
     */
    LargeFileCheckRespVO checkFile(LargeFileCheckReqVO reqVO);

    /**
     * 上传文件分片
     *
     * @param reqVO 分片请求
     * @param file 分片文件
     */
    void uploadChunk(LargeFileChunkUploadReqVO reqVO, MultipartFile file);

    /**
     * 合并文件分片
     *
     * @param reqVO 合并请求
     * @return 文件URL
     */
    String mergeChunks(LargeFileMergeReqVO reqVO);

    /**
     * 获取上传任务分页（只查看当前用户的任务）
     *
     * @param reqVO 分页请求
     * @return 分页结果
     */
    PageResult<LargeFileRespVO> getTaskPage(LargeFilePageReqVO reqVO);

    /**
     * 获取上传任务详情
     *
     * @param id 任务编号
     * @return 任务详情
     */
    LargeFileRespVO getTask(Long id);

    /**
     * 获取上传任务详情（根据fileId）
     *
     * @param fileId 文件唯一标识
     * @return 任务详情
     */
    LargeFileRespVO getTaskByFileId(String fileId);

    /**
     * 删除上传任务
     *
     * @param id 任务编号
     */
    void deleteTask(Long id);

    /**
     * 重置失败任务状态（用于重试）
     *
     * @param id 任务编号
     */
    void resetTask(Long id);

    /**
     * 中断上传任务
     *
     * @param fileId 文件唯一标识
     */
    void interruptTask(String fileId);

    /**
     * 清理过期的上传任务
     *
     * @param retainDays 保留天数
     * @return 清理数量
     */
    Integer cleanupExpiredTasks(Integer retainDays);

    /**
     * 获取任务的已上传分片列表
     *
     * @param fileId 文件唯一标识
     * @return 分片索引列表
     */
    List<Integer> getUploadedChunks(String fileId);

    /**
     * 下载已完成的文件
     *
     * @param id 任务编号
     * @param response HTTP响应
     */
    void downloadFile(Long id, HttpServletResponse response) throws Exception;

}
