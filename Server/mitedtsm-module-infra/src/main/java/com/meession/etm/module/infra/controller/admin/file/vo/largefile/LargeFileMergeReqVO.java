package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 大文件分片合并 Request VO
 */
@Schema(description = "管理后台 - 大文件分片合并 Request VO")
@Data
public class LargeFileMergeReqVO {

    @Schema(description = "文件唯一标识(UUID)", requiredMode = Schema.RequiredMode.REQUIRED, example = "a1b2c3d4e5f6")
    @NotBlank(message = "文件ID不能为空")
    private String fileId;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "example.zip")
    @NotBlank(message = "文件名不能为空")
    private String filename;

    @Schema(description = "总分片数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

    @Schema(description = "上传目录", example = "documents")
    private String directory;

}
