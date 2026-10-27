package com.meession.etm.module.promotion.service.diy;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.module.promotion.dal.dataobject.diy.PromotionDiyMenuI18nDO;
import com.meession.etm.module.promotion.dal.mysql.diy.PromotionDiyMenuI18nMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 装修菜单国际化 Service 实现类
 *
 * @author mitedtsm
 */
@Service
public class PromotionDiyMenuI18nServiceImpl implements PromotionDiyMenuI18nService {

    @Resource
    private PromotionDiyMenuI18nMapper promotionDiyMenuI18nMapper;

    @Override
    public PromotionDiyMenuI18nDO getByMenuKeyAndLanguage(String menuKey, String language) {
        return promotionDiyMenuI18nMapper.selectByMenuKeyAndLanguage(menuKey, language);
    }

    @Override
    public List<PromotionDiyMenuI18nDO> getListByLanguage(String language) {
        return promotionDiyMenuI18nMapper.selectListByLanguage(language);
    }

    @Override
    public List<PromotionDiyMenuI18nDO> getListByLanguageAndPosition(String language, String position) {
        return promotionDiyMenuI18nMapper.selectListByLanguageAndPosition(language, position);
    }

    @Override
    public Map<String, String> getTranslationMap(String language) {
        List<PromotionDiyMenuI18nDO> list = getListByLanguage(language);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream()
                .collect(Collectors.toMap(
                        PromotionDiyMenuI18nDO::getMenuKey,
                        PromotionDiyMenuI18nDO::getName,
                        (existing, replacement) -> existing // 如果有重复的key，保留第一个
                ));
    }

    @Override
    public Map<String, String> getTranslationMap(String language, String position) {
        List<PromotionDiyMenuI18nDO> list = getListByLanguageAndPosition(language, position);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream()
                .collect(Collectors.toMap(
                        PromotionDiyMenuI18nDO::getMenuKey,
                        PromotionDiyMenuI18nDO::getName,
                        (existing, replacement) -> existing // 如果有重复的key，保留第一个
                ));
    }

}
