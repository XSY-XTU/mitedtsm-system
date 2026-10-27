package com.meession.etm.module.crm.controller.admin.receivable.vo.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 回款计划汇总 Response VO
 *
 * @author 密讯
 */
@Schema(description = "管理后台 - 回款计划汇总 Response VO")
@Data
public class CrmReceivablePlanSummaryRespVO {

    @Schema(description = "总计划数", example = "20")
    private long totalCount;

    @Schema(description = "总计划金额", example = "500000.00")
    private BigDecimal totalPrice;

    @Schema(description = "已完成数", example = "8")
    private long receivedCount;

    @Schema(description = "已完成金额", example = "200000.00")
    private BigDecimal receivedPrice;

    @Schema(description = "完成率（百分比）", example = "40.0")
    private double completionRate;

}
