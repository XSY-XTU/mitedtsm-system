package com.meession.etm.module.crm.controller.admin.receivable.vo.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 回款逾期汇总 Response VO
 *
 * @author 密讯
 */
@Schema(description = "管理后台 - 回款逾期汇总 Response VO")
@Data
public class CrmReceivablePlanOverdueSummaryRespVO {

    @Schema(description = "逾期计划数量", example = "5")
    private long overdueCount;

    @Schema(description = "逾期总金额", example = "150000.00")
    private BigDecimal overdueTotalPrice;

    @Schema(description = "待回款总数", example = "12")
    private long pendingCount;

    @Schema(description = "待回款总金额", example = "350000.00")
    private BigDecimal pendingTotalPrice;

}
