package com.meession.etm.module.iot.core.topic.topo;

import com.meession.etm.module.iot.core.enums.IotDeviceMessageMethodEnum;
import lombok.Data;

/**
 * IoT 设备拓扑关系获取 Request DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#TOPO_GET} 请求的 params 参数（目前为空，预留扩展）
 *
 * @author 密讯
 * @see <a href="https://help.aliyun.com/zh/marketplace/obtain-topological-relationship">阿里云 - 获取拓扑关系</a>
 */
@Data
public class IotDeviceTopoGetReqDTO {

}
