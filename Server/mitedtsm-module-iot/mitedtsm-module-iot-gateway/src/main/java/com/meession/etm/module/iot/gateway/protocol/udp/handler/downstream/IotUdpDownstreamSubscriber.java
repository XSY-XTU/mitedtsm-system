package com.meession.etm.module.iot.gateway.protocol.udp.handler.downstream;

import com.meession.etm.module.iot.core.messagebus.core.IotMessageBus;
import com.meession.etm.module.iot.core.mq.message.IotDeviceMessage;
import com.meession.etm.module.iot.gateway.protocol.IotProtocol;
import com.meession.etm.module.iot.gateway.protocol.AbstractIotProtocolDownstreamSubscriber;
import lombok.extern.slf4j.Slf4j;

/**
 * IoT 网关 UDP 下游订阅者：接收下行给设备的消息
 *
 * @author 密讯
 */
@Slf4j
public class IotUdpDownstreamSubscriber extends AbstractIotProtocolDownstreamSubscriber {

    private final IotUdpDownstreamHandler downstreamHandler;

    public IotUdpDownstreamSubscriber(IotProtocol protocol,
                                      IotUdpDownstreamHandler downstreamHandler,
                                      IotMessageBus messageBus) {
        super(protocol, messageBus);
        this.downstreamHandler = downstreamHandler;
    }

    @Override
    protected void handleMessage(IotDeviceMessage message) {
        downstreamHandler.handle(message);
    }

}
