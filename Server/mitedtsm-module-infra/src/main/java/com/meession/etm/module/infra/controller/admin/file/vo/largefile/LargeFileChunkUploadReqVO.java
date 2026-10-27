package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 大文件分片上传 Request VO
 */
@Schema(description = "管理后台 - 大文件分片上传 Request VO")
@Data
public class LargeFileChunkUploadReqVO {

    @Schema(description = "文件唯一标识(UUID)", requiredMode = Schema.RequiredMode.REQUIRED, example = "a1b2c3d4e5f6")
    @NotNull(message = "文件ID不能为空")
    private String fileId;

    @Schema(description = "分片索引(从0开始)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "分片索引不能为空")
    private Integer chunkIndex;

    @Schema(description = "总分片数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

}
