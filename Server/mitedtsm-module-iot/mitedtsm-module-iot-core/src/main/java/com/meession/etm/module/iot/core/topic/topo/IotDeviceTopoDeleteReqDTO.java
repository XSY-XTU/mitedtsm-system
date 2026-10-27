package com.meession.etm.module.iot.core.topic.topo;

import com.meession.etm.module.iot.core.enums.IotDeviceMessageMethodEnum;
import com.meession.etm.module.iot.core.topic.IotDeviceIdentity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * IoT 设备拓扑删除 Request DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#TOPO_DELETE} 消息的 params 参数
 *
 * @author 密讯
 * @see <a href="https://help.aliyun.com/zh/marketplace/delete-a-topological-relationship">阿里云 - 删除拓扑关系</a>
 */
@Data
public class IotDeviceTopoDeleteReqDTO {

    /**
     * 子设备标识列表
     */
    @Valid
    @NotEmpty(message = "子设备标识列表不能为空")
    private List<IotDeviceIdentity> subDevices;

}
