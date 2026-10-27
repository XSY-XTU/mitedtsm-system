package com.meession.etm.module.pay.api.transfer;

import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.pay.framework.pay.core.client.impl.weixin.WxPayClientConfig;
import com.meession.etm.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import com.meession.etm.module.pay.api.transfer.dto.PayTransferCreateRespDTO;
import com.meession.etm.module.pay.api.transfer.dto.PayTransferRespDTO;
import com.meession.etm.module.pay.dal.dataobject.channel.PayChannelDO;
import com.meession.etm.module.pay.dal.dataobject.transfer.PayTransferDO;
import com.meession.etm.module.pay.service.channel.PayChannelService;
import com.meession.etm.module.pay.service.transfer.PayTransferService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 转账单 API 实现类
 *
 * @author jason
 */
@Service
@Validated
public class PayTransferApiImpl implements PayTransferApi {

    @Resource
    private PayTransferService payTransferService;
    @Resource
    private PayChannelService payChannelService;

    @Override
    public PayTransferCreateRespDTO createTransfer(PayTransferCreateReqDTO reqDTO) {
        return payTransferService.createTransfer(reqDTO);
    }

    @Override
    public PayTransferRespDTO getTransfer(Long id) {
        PayTransferDO transfer = payTransferService.getTransfer(id);
        if (transfer == null) {
            return null;
        }
        PayChannelDO channel = payChannelService.getChannel(transfer.getChannelId());
        String mchId = null;
        if (channel != null && channel.getConfig() instanceof WxPayClientConfig) {
            mchId = ((WxPayClientConfig) channel.getConfig()).getMchId();
        }
        return BeanUtils.toBean(transfer, PayTransferRespDTO.class).setChannelMchId(mchId);
    }

}
