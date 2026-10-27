package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 大文件上传任务 Response VO
 */
@Schema(description = "管理后台 - 大文件上传任务 Response VO")
@Data
public class LargeFileRespVO {

    @Schema(description = "任务编号", example = "1")
    private Long id;

    @Schema(description = "文件唯一标识(UUID)", example = "a1b2c3d4e5f6")
    private String fileId;

    @Schema(description = "文件名", example = "example.zip")
    private String filename;

    @Schema(description = "文件大小(字节)", example = "104857600")
    private Long fileSize;

    @Schema(description = "分片大小(字节)", example = "5242880")
    private Integer chunkSize;

    @Schema(description = "总分片数", example = "20")
    private Integer totalChunks;

    @Schema(description = "已上传分片数", example = "10")
    private Integer uploadedChunks;

    @Schema(description = "已合并分片数", example = "5")
    private Integer mergedChunks;

    @Schema(description = "上传进度(百分比)", example = "50.0")
    private Double progress;

    @Schema(description = "状态", example = "uploading")
    private String status;

    @Schema(description = "状态名称", example = "上传中")
    private String statusName;

    @Schema(description = "文件配置编号", example = "1")
    private Long configId;

    @Schema(description = "文件存储路径", example = "20260321/example.zip")
    private String path;

    @Schema(description = "MIME类型", example = "application/zip")
    private String contentType;

    @Schema(description = "错误信息", example = "上传失败")
    private String errorMessage;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
