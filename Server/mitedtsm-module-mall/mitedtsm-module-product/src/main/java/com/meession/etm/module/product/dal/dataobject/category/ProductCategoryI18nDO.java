package com.meession.etm.module.product.dal.dataobject.category;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.meession.etm.framework.mybatis.core.dataobject.BaseDO;
import com.meession.etm.framework.tenant.core.aop.TenantIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类国际化 DO
 *
 * @author system
 */
@TenantIgnore // 忽略多租户，分类国际化数据是全局共享的
@TableName("product_category_i18n")
@KeySequence("product_category_i18n_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCategoryI18nDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 语言代码 (zh-CN, en 等)
     */
    private String language;

    /**
     * 分类名称翻译
     */
    private String name;

}
