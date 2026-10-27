package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 大文件上传检查 Request VO
 */
@Schema(description = "管理后台 - 大文件上传检查 Request VO")
@Data
public class LargeFileCheckReqVO {

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "example.zip")
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(description = "文件大小(字节)", requiredMode = Schema.RequiredMode.REQUIRED, example = "104857600")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @Schema(description = "分片大小(字节)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5242880")
    @NotNull(message = "分片大小不能为空")
    private Integer chunkSize;

    @Schema(description = "总分片数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

    @Schema(description = "MIME类型", example = "application/zip")
    private String contentType;

}
