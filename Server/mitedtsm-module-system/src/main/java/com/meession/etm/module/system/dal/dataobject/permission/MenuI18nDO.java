package com.meession.etm.module.system.dal.dataobject.permission;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单国际化 DO
 *
 * @author system
 */
@TenantIgnore // 忽略多租户，菜单国际化数据是全局共享的
@TableName("system_menu_i18n")
@KeySequence("system_menu_i18n_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuI18nDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 语言代码 (zh-CN, en 等)
     */
    private String language;

    /**
     * 菜单名称
     */
    private String name;

}
