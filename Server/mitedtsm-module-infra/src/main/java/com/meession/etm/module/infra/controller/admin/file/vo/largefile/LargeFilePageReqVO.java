package com.meession.etm.module.infra.controller.admin.file.vo.largefile;

import com.meession.etm.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 大文件上传任务分页 Request VO
 */
@Schema(description = "管理后台 - 大文件上传任务分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LargeFilePageReqVO extends PageParam {

    @Schema(description = "文件名，模糊匹配", example = "example")
    private String filename;

    @Schema(description = "状态(uploading/uploading_failed/merging/merging_failed/completed/interrupted)", example = "uploading")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}