package com.meession.etm.module.promotion.dal.dataobject.diy;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 装修菜单国际化 DO
 *
 * 用于存储装修页面中菜单项的国际化翻译
 * 使用 url 路径作为 menu_key 进行匹配
 *
 * @author mitedtsm
 */
@TableName("promotion_diy_menu_i18n")
@KeySequence("promotion_diy_menu_i18n_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDiyMenuI18nDO extends BaseDO {

    /**
     * 主键编号
     */
    @TableId
    private Long id;

    /**
     * 菜单标识（url路径）
     * 
     * 例如：/pages/activity/groupon/list
     */
    private String menuKey;

    /**
     * 语言代码
     * 
     * 例如：zh-CN, en, ja
     */
    private String language;

    /**
     * 菜单位置
     * 
     * 用于区分同一URL在不同位置的不同翻译
     * - home: 首页菜单
     * - user: 我的页面菜单
     * - tabbar: 底部导航栏
     * - float: 浮动按钮
     */
    private String position;

    /**
     * 菜单名称翻译
     */
    private String name;

}
