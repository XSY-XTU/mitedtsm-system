package com.meession.etm.module.product.service.category;

import com.meession.etm.module.product.dal.dataobject.category.ProductCategoryI18nDO;

import java.util.List;
import java.util.Map;

/**
 * 商品分类国际化 Service 接口
 *
 * @author system
 */
public interface ProductCategoryI18nService {

    /**
     * 根据分类ID和语言获取国际化名称
     *
     * @param categoryId 分类ID
     * @param language   语言代码
     * @return 国际化名称，如果没有则返回 null
     */
    String getNameByCategoryIdAndLanguage(Long categoryId, String language);

    /**
     * 根据语言获取所有分类的国际化映射
     *
     * @param language 语言代码
     * @return 分类ID -> 国际化名称 的映射
     */
    Map<Long, String> getCategoryNameMap(String language);

    /**
     * 根据分类ID列表和语言获取国际化映射
     *
     * @param categoryIds 分类ID列表
     * @param language    语言代码
     * @return 分类ID -> 国际化名称 的映射
     */
    Map<Long, String> getCategoryNameMap(List<Long> categoryIds, String language);

    /**
     * 创建或更新分类国际化记录
     *
     * @param categoryId 分类ID
     * @param language   语言代码
     * @param name       国际化名称
     */
    void saveOrUpdate(Long categoryId, String language, String name);

    /**
     * 批量创建或更新分类国际化记录
     *
     * @param list 国际化记录列表
     */
    void saveOrUpdateBatch(List<ProductCategoryI18nDO> list);

    /**
     * 根据分类ID删除国际化记录
     *
     * @param categoryId 分类ID
     */
    void deleteByCategoryId(Long categoryId);

}
