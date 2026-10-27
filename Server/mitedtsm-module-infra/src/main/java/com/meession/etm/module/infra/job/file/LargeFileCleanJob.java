package com.meession.etm.module.infra.job.file;

import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import com.meession.etm.module.infra.service.file.LargeFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 清理过期的大文件上传任务 Job
 *
 * @author iFlow
 */
@Component
@Slf4j
public class LargeFileCleanJob implements JobHandler {

    @Resource
    private LargeFileService largeFileService;

    /**
     * 默认清理超过（7）天的上传任务
     */
    private static final Integer DEFAULT_RETAIN_DAY = 7;

    @Override
    @TenantIgnore
    public String execute(String param) {
        // 解析参数，默认保留7天
        Integer retainDays = DEFAULT_RETAIN_DAY;
        if (param != null && !param.isEmpty()) {
            try {
                retainDays = Integer.parseInt(param);
            } catch (NumberFormatException e) {
                log.warn("[execute] 参数解析失败，使用默认值: {}", DEFAULT_RETAIN_DAY);
            }
        }

        Integer count = largeFileService.cleanupExpiredTasks(retainDays);
        log.info("[execute][定时执行清理过期大文件上传任务数量 ({}) 个]", count);
        return String.format("定时执行清理过期大文件上传任务数量 %s 个", count);
    }

}
