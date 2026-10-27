package com.meession.etm.module.promotion.dal.mysql.diy;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.promotion.dal.dataobject.diy.PromotionDiyMenuI18nDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 装修菜单国际化 Mapper
 *
 * @author mitedtsm
 */
@InterceptorIgnore(tenantLine = "true") // 菜单国际化是全局共享的，不需要租户隔离
@Mapper
public interface PromotionDiyMenuI18nMapper extends BaseMapperX<PromotionDiyMenuI18nDO> {

    /**
     * 根据菜单标识和语言查询
     *
     * @param menuKey 菜单标识（url路径）
     * @param language 语言代码
     * @return 国际化记录
     */
    default PromotionDiyMenuI18nDO selectByMenuKeyAndLanguage(String menuKey, String language) {
        return selectOne(new LambdaQueryWrapperX<PromotionDiyMenuI18nDO>()
                .eq(PromotionDiyMenuI18nDO::getMenuKey, menuKey)
                .eq(PromotionDiyMenuI18nDO::getLanguage, language));
    }

    /**
     * 根据语言查询所有菜单翻译
     *
     * @param language 语言代码
     * @return 该语言的所有菜单翻译列表
     */
    default List<PromotionDiyMenuI18nDO> selectListByLanguage(String language) {
        return selectList(new LambdaQueryWrapperX<PromotionDiyMenuI18nDO>()
                .eq(PromotionDiyMenuI18nDO::getLanguage, language));
    }

    /**
     * 根据语言和位置查询菜单翻译
     *
     * @param language 语言代码
     * @param position 菜单位置 (home, user, tabbar, float)
     * @return 该语言和位置的所有菜单翻译列表
     */
    default List<PromotionDiyMenuI18nDO> selectListByLanguageAndPosition(String language, String position) {
        return selectList(new LambdaQueryWrapperX<PromotionDiyMenuI18nDO>()
                .eq(PromotionDiyMenuI18nDO::getLanguage, language)
                .eq(PromotionDiyMenuI18nDO::getPosition, position));
    }

}
