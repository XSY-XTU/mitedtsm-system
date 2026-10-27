package com.meession.etm.module.infra.controller.admin.file;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.infra.controller.admin.file.vo.largefile.*;
import com.meession.etm.module.infra.service.file.LargeFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

/**
 * 大文件上传 Controller
 *
 * @author iFlow
 */
@Tag(name = "管理后台 - 大文件上传")
@RestController
@RequestMapping("/infra/large-file")
@Validated
@Slf4j
public class LargeFileController {

    @Resource
    private LargeFileService largeFileService;

    @PostMapping("/check")
    @Operation(summary = "检查文件上传状态", description = "用于断点续传，检查文件是否已有上传记录")
    public CommonResult<LargeFileCheckRespVO> checkFile(@Valid @RequestBody LargeFileCheckReqVO reqVO) {
        return success(largeFileService.checkFile(reqVO));
    }

    @PostMapping("/chunk")
    @Operation(summary = "上传文件分片")
    public CommonResult<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileId") String fileId,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("totalChunks") Integer totalChunks) {
        LargeFileChunkUploadReqVO reqVO = new LargeFileChunkUploadReqVO();
        reqVO.setFileId(fileId);
        reqVO.setChunkIndex(chunkIndex);
        reqVO.setTotalChunks(totalChunks);
        largeFileService.uploadChunk(reqVO, file);
        return success("分片上传成功");
    }

    @PostMapping("/merge")
    @Operation(summary = "合并文件分片")
    public CommonResult<String> mergeChunks(@Valid @RequestBody LargeFileMergeReqVO reqVO) {
        return success(largeFileService.mergeChunks(reqVO));
    }

    @GetMapping("/task/page")
    @Operation(summary = "获取上传任务分页")
    @PreAuthorize("@ss.hasPermission('infra:large-file:query')")
    public CommonResult<PageResult<LargeFileRespVO>> getTaskPage(@Valid LargeFilePageReqVO reqVO) {
        return success(largeFileService.getTaskPage(reqVO));
    }

    @GetMapping("/task/get")
    @Operation(summary = "获取上传任务详情")
    @Parameter(name = "id", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:large-file:query')")
    public CommonResult<LargeFileRespVO> getTask(@RequestParam("id") Long id) {
        return success(largeFileService.getTask(id));
    }

    @GetMapping("/task/get-by-fileId")
    @Operation(summary = "根据fileId获取上传任务详情")
    @Parameter(name = "fileId", description = "文件唯一标识", required = true)
    public CommonResult<LargeFileRespVO> getTaskByFileId(@RequestParam("fileId") String fileId) {
        return success(largeFileService.getTaskByFileId(fileId));
    }

    @DeleteMapping("/task/delete")
    @Operation(summary = "删除上传任务")
    @Parameter(name = "id", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:large-file:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        largeFileService.deleteTask(id);
        return success(true);
    }

    @PostMapping("/task/reset")
    @Operation(summary = "重置失败任务状态", description = "用于重试失败的上传任务")
    @Parameter(name = "id", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:large-file:update')")
    public CommonResult<Boolean> resetTask(@RequestParam("id") Long id) {
        largeFileService.resetTask(id);
        return success(true);
    }

    @PostMapping("/task/interrupt")
    @Operation(summary = "中断上传任务", description = "用户取消上传时调用")
    @Parameter(name = "fileId", description = "文件唯一标识", required = true)
    public CommonResult<Boolean> interruptTask(@RequestParam("fileId") String fileId) {
        largeFileService.interruptTask(fileId);
        return success(true);
    }

    @GetMapping("/task/chunks")
    @Operation(summary = "获取任务的已上传分片列表")
    @Parameter(name = "fileId", description = "文件唯一标识", required = true)
    public CommonResult<java.util.List<Integer>> getUploadedChunks(@RequestParam("fileId") String fileId) {
        return success(largeFileService.getUploadedChunks(fileId));
    }

    @GetMapping("/task/download")
    @Operation(summary = "下载已完成的文件")
    @Parameter(name = "id", description = "任务编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:large-file:query')")
    public void downloadFile(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
        largeFileService.downloadFile(id, response);
    }

}
