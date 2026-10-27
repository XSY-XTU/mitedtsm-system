package com.meession.etm.module.iot.core.topic.topo;

import com.meession.etm.module.iot.core.enums.IotDeviceMessageMethodEnum;
import com.meession.etm.module.iot.core.topic.IotDeviceIdentity;
import lombok.Data;

import java.util.List;

/**
 * IoT 设备拓扑关系获取 Response DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#TOPO_GET} 响应
 *
 * @author 密讯
 * @see <a href="https://help.aliyun.com/zh/marketplace/obtain-topological-relationship">阿里云 - 获取拓扑关系</a>
 */
@Data
public class IotDeviceTopoGetRespDTO {

    /**
     * 子设备列表
     */
    private List<IotDeviceIdentity> subDevices;

}
