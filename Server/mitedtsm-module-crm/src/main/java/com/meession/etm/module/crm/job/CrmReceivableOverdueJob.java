package com.meession.etm.module.crm.job;

import com.meession.etm.module.crm.service.receivable.CrmReceivablePlanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 回款逾期标记定时任务
 * <p>
 * 每天凌晨 2 点执行，扫描所有超过计划回款日期且尚未回款的回款计划，标记为逾期状态
 *
 * @author 密讯
 */
@Slf4j
@Component
public class CrmReceivableOverdueJob {

    /**
     * 分布式锁 Key，防止多节点重复执行
     */
    private static final String LOCK_KEY = "crm:receivable:overdue-mark:lock";

    @Resource
    private CrmReceivablePlanService receivablePlanService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 每天凌晨 2 点执行逾期标记
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void markOverduePlans() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.tryLock()) {
            try {
                int count = receivablePlanService.markOverduePlans();
                log.info("[markOverduePlans][标记逾期回款计划完成，数量：{}]", count);
            } catch (Exception e) {
                log.error("[markOverduePlans][标记逾期回款计划失败]", e);
            } finally {
                lock.unlock();
            }
        }
    }

}
