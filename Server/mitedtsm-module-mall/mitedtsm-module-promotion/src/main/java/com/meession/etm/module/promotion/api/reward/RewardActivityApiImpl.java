package com.meession.etm.module.promotion.api.reward;

import com.meession.etm.module.promotion.api.reward.dto.RewardActivityMatchRespDTO;
import com.meession.etm.module.promotion.service.reward.RewardActivityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * 满减送活动 API 实现类
 *
 * @author 密讯
 */
@Service
@Validated
public class RewardActivityApiImpl implements RewardActivityApi {

    @Resource
    private RewardActivityService rewardActivityService;

    @Override
    public List<RewardActivityMatchRespDTO> getMatchRewardActivityListBySpuIds(Collection<Long> spuIds) {
        return rewardActivityService.getMatchRewardActivityListBySpuIds(spuIds);
    }

}
