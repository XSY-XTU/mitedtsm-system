package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 大文件上传检查 Response VO
 */
@Schema(description = "管理后台 - 大文件上传检查 Response VO")
@Data
public class LargeFileCheckRespVO {

    @Schema(description = "文件唯一标识(UUID)", example = "a1b2c3d4e5f6")
    private String fileId;

    @Schema(description = "已上传的分片索引列表", example = "[0, 1, 2]")
    private List<Integer> uploadedChunks;

    @Schema(description = "是否需要上传", example = "true")
    private Boolean needUpload;

    @Schema(description = "提示信息", example = "需要上传")
    private String message;

}
