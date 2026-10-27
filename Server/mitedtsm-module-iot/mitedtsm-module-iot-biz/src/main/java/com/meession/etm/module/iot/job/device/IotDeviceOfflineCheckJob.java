package com.meession.etm.module.iot.job.device;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.util.json.JsonUtils;
import com.meession.etm.framework.quartz.core.handler.JobHandler;
import com.meession.etm.framework.tenant.core.job.TenantJob;
import com.meession.etm.module.iot.core.enums.device.IotDeviceStateEnum;
import com.meession.etm.module.iot.core.mq.message.IotDeviceMessage;
import com.meession.etm.module.iot.dal.dataobject.device.IotDeviceDO;
import com.meession.etm.module.iot.framework.iot.config.MitedtsmIotProperties;
import com.meession.etm.module.iot.service.device.IotDeviceService;
import com.meession.etm.module.iot.service.device.message.IotDeviceMessageService;
import com.meession.etm.module.iot.service.device.property.IotDevicePropertyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * IoT 设备离线检查 Job
 *
 * 检测逻辑：设备最后一条 {@link IotDeviceMessage} 消息超过一定时间，则认为设备离线
 *
 * @see <a href="https://help.aliyun.com/zh/iot/support/faq-about-device-status#98f7056b2957y">阿里云 IoT —— 设备离线分析</a>
 * @author 密讯
 */
@Component
public class IotDeviceOfflineCheckJob implements JobHandler {

    @Resource
    private MitedtsmIotProperties iotProperties;

    @Resource
    private IotDeviceService deviceService;
    @Resource
    private IotDevicePropertyService devicePropertyService;
    @Resource
    private IotDeviceMessageService deviceMessageService;

    @Override
    @TenantJob
    public String execute(String param) {
        // 1.1 获得在线设备列表
        List<IotDeviceDO> devices = deviceService.getDeviceListByState(IotDeviceStateEnum.ONLINE.getState());
        if (CollUtil.isEmpty(devices)) {
            return JsonUtils.toJsonString(Collections.emptyList());
        }
        // 1.2 获取超时的设备集合
        Set<Long> timeoutDeviceIds = devicePropertyService.getDeviceIdListByReportTime(getTimeoutTime());

        // 2. 下线设备
        List<String[]> offlineDevices = CollUtil.newArrayList();
        for (IotDeviceDO device : devices) {
            if (!timeoutDeviceIds.contains(device.getId())) {
                continue;
            }
            offlineDevices.add(new String[]{device.getProductKey(), device.getDeviceName()});
            // 为什么不直接更新状态呢？因为通过 IotDeviceMessage 可以经过一系列的处理，例如说记录日志等等
            deviceMessageService.sendDeviceMessage(IotDeviceMessage.buildStateOffline().setDeviceId(device.getId()));
        }
        return JsonUtils.toJsonString(offlineDevices);
    }

    private LocalDateTime getTimeoutTime() {
        return LocalDateTime.now().minus(Duration.ofNanos(
                (long) (iotProperties.getKeepAliveTime().toNanos() * iotProperties.getKeepAliveFactor())));
    }

}
