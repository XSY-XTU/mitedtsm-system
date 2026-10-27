package com.meession.etm.module.iot.gateway.protocol.http.handler.upstream;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.module.iot.core.biz.IotDeviceCommonApi;
import com.meession.etm.module.iot.core.biz.dto.IotSubDeviceRegisterFullReqDTO;
import com.meession.etm.module.iot.core.topic.auth.IotSubDeviceRegisterReqDTO;
import com.meession.etm.module.iot.core.topic.auth.IotSubDeviceRegisterRespDTO;
import io.vertx.ext.web.RoutingContext;
import lombok.Data;

import java.util.List;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

/**
 * IoT 网关 HTTP 协议的【子设备动态注册】处理器
 * <p>
 * 用于子设备的动态注册，需要网关认证
 *
 * @author 密讯
 * @see <a href="https://help.aliyun.com/zh/iot/user-guide/register-devices">阿里云 - 动态注册子设备</a>
 */
public class IotHttpRegisterSubHandler extends IotHttpAbstractHandler {

    /**
     * 路径：/auth/register/sub-device/:productKey/:deviceName
     * <p>
     * productKey 和 deviceName 是网关设备的标识
     */
    public static final String PATH = "/auth/register/sub-device/:productKey/:deviceName";

    private final IotDeviceCommonApi deviceApi;

    public IotHttpRegisterSubHandler() {
        this.deviceApi = SpringUtil.getBean(IotDeviceCommonApi.class);
    }

    @Override
    public CommonResult<Object> handle0(RoutingContext context) {
        // 1.1 解析通用参数
        String productKey = context.pathParam("productKey");
        String deviceName = context.pathParam("deviceName");
        // 1.2 解析子设备列表
        SubDeviceRegisterRequest request = deserializeRequest(context, SubDeviceRegisterRequest.class);
        Assert.notNull(request, "请求参数不能为空");
        Assert.notEmpty(request.getParams(), "params 不能为空");

        // 2. 调用子设备动态注册
        IotSubDeviceRegisterFullReqDTO reqDTO = new IotSubDeviceRegisterFullReqDTO()
                .setGatewayProductKey(productKey)
                .setGatewayDeviceName(deviceName)
                .setSubDevices(request.getParams());
        CommonResult<List<IotSubDeviceRegisterRespDTO>> result = deviceApi.registerSubDevices(reqDTO);
        result.checkError();

        // 3. 返回结果
        return success(result.getData());
    }

    @Data
    public static class SubDeviceRegisterRequest {

        private List<IotSubDeviceRegisterReqDTO> params;

    }

}
