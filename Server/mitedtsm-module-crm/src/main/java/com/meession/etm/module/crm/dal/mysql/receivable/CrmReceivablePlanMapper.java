package com.meession.etm.module.crm.dal.mysql.receivable;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.module.crm.controller.admin.receivable.vo.plan.CrmReceivablePlanPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.common.CrmSceneTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 回款计划 Mapper
 *
 * @author 密讯
 */
@Mapper
public interface CrmReceivablePlanMapper extends BaseMapperX<CrmReceivablePlanDO> {

    default CrmReceivablePlanDO selectMaxPeriodByContractId(Long contractId) {
        return selectOne(new MPJLambdaWrapperX<CrmReceivablePlanDO>()
                .eq(CrmReceivablePlanDO::getContractId, contractId)
                .orderByDesc(CrmReceivablePlanDO::getPeriod)
                .last("LIMIT 1"));
    }

    default PageResult<CrmReceivablePlanDO> selectPageByCustomerId(CrmReceivablePlanPageReqVO reqVO) {
        MPJLambdaWrapperX<CrmReceivablePlanDO> query = new MPJLambdaWrapperX<>();
        if (Objects.nonNull(reqVO.getContractNo())) { // 根据合同编号检索
            query.innerJoin(CrmContractDO.class, on -> on.like(CrmContractDO::getNo, reqVO.getContractNo())
                    .eq(CrmContractDO::getId, CrmReceivablePlanDO::getContractId));
        }
        query.eq(CrmReceivablePlanDO::getCustomerId, reqVO.getCustomerId()) // 必须传递
                .eqIfPresent(CrmReceivablePlanDO::getContractId, reqVO.getContractId())
                .orderByDesc(CrmReceivablePlanDO::getPeriod);
        return selectJoinPage(reqVO, CrmReceivablePlanDO.class, query);
    }

    default PageResult<CrmReceivablePlanDO> selectPage(CrmReceivablePlanPageReqVO pageReqVO, Long userId) {
        MPJLambdaWrapperX<CrmReceivablePlanDO> query = new MPJLambdaWrapperX<>();
        // 拼接数据权限的查询条件
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_RECEIVABLE_PLAN.getType(),
                CrmReceivablePlanDO::getId, userId, pageReqVO.getSceneType());
        // 拼接自身的查询条件
        query.selectAll(CrmReceivablePlanDO.class)
                .eqIfPresent(CrmReceivablePlanDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmReceivablePlanDO::getContractId, pageReqVO.getContractId())
                .orderByDesc(CrmReceivablePlanDO::getPeriod);
        if (Objects.nonNull(pageReqVO.getContractNo())) { // 根据合同编号检索
            query.innerJoin(CrmContractDO.class, on -> on.like(CrmContractDO::getNo, pageReqVO.getContractNo())
                    .eq(CrmContractDO::getId, CrmReceivablePlanDO::getContractId));
        }

        // Backlog: 回款提醒类型
        LocalDateTime beginOfToday = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        if (CrmReceivablePlanPageReqVO.REMIND_TYPE_NEEDED.equals(pageReqVO.getRemindType())) { // 待回款
            // 查询条件：未回款 + 提醒时间 <= 当前时间（反过来即当前时间 >= 提醒时间，已经到达提醒的时间点）
            query.isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                    .le(CrmReceivablePlanDO::getRemindTime, beginOfToday); // 今天开始提醒
        } else if (CrmReceivablePlanPageReqVO.REMIND_TYPE_EXPIRED.equals(pageReqVO.getRemindType())) { // 已逾期
            // 查询条件：未回款 + 回款时间 < 当前时间（反过来即当前时间 > 回款时间，已经过了回款时间点）
            query.isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                    .lt(CrmReceivablePlanDO::getReturnTime, beginOfToday); // 已逾期
        } else if (CrmReceivablePlanPageReqVO.REMIND_TYPE_RECEIVED.equals(pageReqVO.getRemindType())) { // 已回款
            query.isNotNull(CrmReceivablePlanDO::getReceivableId);
        }
        return selectJoinPage(pageReqVO, CrmReceivablePlanDO.class, query);
    }

    default Long selectReceivablePlanCountByRemind(Long userId) {
        MPJLambdaWrapperX<CrmReceivablePlanDO> query = new MPJLambdaWrapperX<>();
        // 我负责的 + 非公海
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_RECEIVABLE_PLAN.getType(),
                CrmReceivablePlanDO::getId, userId, CrmSceneTypeEnum.OWNER.getType());
        // 未回款 + 已逾期 + 今天开始提醒
        LocalDateTime beginOfToday = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        query.isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                .lt(CrmReceivablePlanDO::getReturnTime, beginOfToday) // 已逾期
                .lt(CrmReceivablePlanDO::getRemindTime, beginOfToday); // 今天开始提醒
        return selectCount(query);
    }

    /**
     * 查询逾期的回款计划列表（未回款且超过计划回款日期）
     *
     * @return 逾期回款计划列表
     */
    default List<CrmReceivablePlanDO> selectOverduePlans() {
        LocalDateTime now = LocalDateTime.now();
        return selectList(new MPJLambdaWrapperX<CrmReceivablePlanDO>()
                .isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                .lt(CrmReceivablePlanDO::getReturnTime, now)); // 已超过计划回款日期
    }

    /**
     * 统计逾期回款计划的数量和总金额
     *
     * @return 逾期统计 [count, totalPrice]
     */
    default Map<String, Object> selectOverdueSummary() {
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> result = selectMaps(new MPJLambdaWrapperX<CrmReceivablePlanDO>()
                .select(CrmReceivablePlanDO::getId, CrmReceivablePlanDO::getPrice)
                .isNull(CrmReceivablePlanDO::getReceivableId) // 未回款
                .lt(CrmReceivablePlanDO::getReturnTime, now)); // 已逾期
        return MapUtil.<String, Object>builder()
                .put("count", result.size())
                .put("totalPrice", result.stream()
                        .map(m -> (BigDecimal) m.getOrDefault("price", BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    /**
     * 统计回款计划汇总信息
     *
     * @return 汇总统计 [totalCount, totalPrice, receivedCount, receivedPrice]
     */
    default Map<String, Object> selectSummary() {
        List<CrmReceivablePlanDO> allPlans = selectList();
        long totalCount = allPlans.size();
        BigDecimal totalPrice = allPlans.stream()
                .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long receivedCount = allPlans.stream()
                .filter(p -> p.getReceivableId() != null)
                .count();
        BigDecimal receivedPrice = allPlans.stream()
                .filter(p -> p.getReceivableId() != null && p.getPrice() != null)
                .map(CrmReceivablePlanDO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return MapUtil.<String, Object>builder()
                .put("totalCount", totalCount)
                .put("totalPrice", totalPrice)
                .put("receivedCount", receivedCount)
                .put("receivedPrice", receivedPrice)
                .build();
    }

}
