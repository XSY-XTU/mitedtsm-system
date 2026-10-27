package com.meession.etm.module.iot.core.topic.topo;

import com.meession.etm.module.iot.core.biz.dto.IotDeviceAuthReqDTO;
import com.meession.etm.module.iot.core.enums.IotDeviceMessageMethodEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * IoT 设备拓扑添加 Request DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#TOPO_ADD} 消息的 params 参数
 *
 * @author 密讯
 * @see <a href="http://help.aliyun.com/zh/marketplace/add-topological-relationship">阿里云 - 添加拓扑关系</a>
 */
@Data
public class IotDeviceTopoAddReqDTO {

    /**
     * 子设备认证信息列表
     * <p>
     * 复用 {@link IotDeviceAuthReqDTO}，包含 clientId、username、password
     */
    @NotEmpty(message = "子设备认证信息列表不能为空")
    private List<IotDeviceAuthReqDTO> subDevices;

}
