package com.meession.etm.module.product.service.category;

import com.meession.etm.module.product.dal.dataobject.category.ProductCategoryI18nDO;
import com.meession.etm.module.product.dal.mysql.category.ProductCategoryI18nMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类国际化 Service 实现类
 *
 * @author system
 */
@Service
@Validated
public class ProductCategoryI18nServiceImpl implements ProductCategoryI18nService {

    @Resource
    private ProductCategoryI18nMapper categoryI18nMapper;

    @Override
    public String getNameByCategoryIdAndLanguage(Long categoryId, String language) {
        if (categoryId == null || language == null) {
            return null;
        }
        ProductCategoryI18nDO i18n = categoryI18nMapper.selectByCategoryIdAndLanguage(categoryId, language);
        return i18n != null ? i18n.getName() : null;
    }

    @Override
    public Map<Long, String> getCategoryNameMap(String language) {
        if (language == null) {
            return Collections.emptyMap();
        }
        List<ProductCategoryI18nDO> list = categoryI18nMapper.selectListByLanguage(language);
        return list.stream()
                .collect(Collectors.toMap(
                        ProductCategoryI18nDO::getCategoryId,
                        ProductCategoryI18nDO::getName,
                        (v1, v2) -> v2 // 如果有重复，取后者
                ));
    }

    @Override
    public Map<Long, String> getCategoryNameMap(List<Long> categoryIds, String language) {
        if (categoryIds == null || categoryIds.isEmpty() || language == null) {
            return Collections.emptyMap();
        }
        List<ProductCategoryI18nDO> list = categoryI18nMapper.selectListByCategoryIdsAndLanguage(categoryIds, language);
        return list.stream()
                .collect(Collectors.toMap(
                        ProductCategoryI18nDO::getCategoryId,
                        ProductCategoryI18nDO::getName,
                        (v1, v2) -> v2
                ));
    }

    @Override
    public void saveOrUpdate(Long categoryId, String language, String name) {
        if (categoryId == null || language == null || name == null) {
            return;
        }
        ProductCategoryI18nDO existing = categoryI18nMapper.selectByCategoryIdAndLanguage(categoryId, language);
        if (existing != null) {
            // 更新
            ProductCategoryI18nDO update = new ProductCategoryI18nDO();
            update.setId(existing.getId());
            update.setName(name);
            categoryI18nMapper.updateById(update);
        } else {
            // 新增
            ProductCategoryI18nDO insert = new ProductCategoryI18nDO();
            insert.setCategoryId(categoryId);
            insert.setLanguage(language);
            insert.setName(name);
            categoryI18nMapper.insert(insert);
        }
    }

    @Override
    public void saveOrUpdateBatch(List<ProductCategoryI18nDO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (ProductCategoryI18nDO item : list) {
            saveOrUpdate(item.getCategoryId(), item.getLanguage(), item.getName());
        }
    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        // 删除该分类的所有国际化记录
        categoryI18nMapper.delete(ProductCategoryI18nDO::getCategoryId, categoryId);
    }

}
