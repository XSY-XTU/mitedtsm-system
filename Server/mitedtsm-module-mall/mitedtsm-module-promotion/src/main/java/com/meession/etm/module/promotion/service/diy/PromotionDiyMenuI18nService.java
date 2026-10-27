package com.meession.etm.module.promotion.service.diy;

import com.meession.etm.module.promotion.dal.dataobject.diy.PromotionDiyMenuI18nDO;

import java.util.List;
import java.util.Map;

/**
 * 装修菜单国际化 Service 接口
 *
 * @author mitedtsm
 */
public interface PromotionDiyMenuI18nService {

    /**
     * 根据菜单标识和语言获取翻译
     *
     * @param menuKey 菜单标识（url路径）
     * @param language 语言代码
     * @return 国际化记录，不存在返回 null
     */
    PromotionDiyMenuI18nDO getByMenuKeyAndLanguage(String menuKey, String language);

    /**
     * 根据语言获取所有菜单翻译
     *
     * @param language 语言代码
     * @return 该语言的所有菜单翻译列表
     */
    List<PromotionDiyMenuI18nDO> getListByLanguage(String language);

    /**
     * 根据语言和位置获取菜单翻译
     *
     * @param language 语言代码
     * @param position 菜单位置 (home, user, tabbar, float)
     * @return 该语言和位置的所有菜单翻译列表
     */
    List<PromotionDiyMenuI18nDO> getListByLanguageAndPosition(String language, String position);

    /**
     * 根据语言获取菜单翻译映射
     * 
     * 返回 Map<menuKey, name> 格式，方便快速查找
     *
     * @param language 语言代码
     * @return 菜单翻译映射
     */
    Map<String, String> getTranslationMap(String language);

    /**
     * 根据语言和位置获取菜单翻译映射
     * 
     * 返回 Map<menuKey, name> 格式，方便快速查找
     *
     * @param language 语言代码
     * @param position 菜单位置 (home, user, tabbar, float)
     * @return 菜单翻译映射
     */
    Map<String, String> getTranslationMap(String language, String position);

}
