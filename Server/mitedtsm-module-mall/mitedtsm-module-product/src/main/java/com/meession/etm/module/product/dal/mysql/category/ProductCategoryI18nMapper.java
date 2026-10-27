package com.meession.etm.module.product.dal.mysql.category;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.module.product.dal.dataobject.category.ProductCategoryI18nDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品分类国际化 Mapper
 *
 * @author system
 */
@Mapper
public interface ProductCategoryI18nMapper extends BaseMapperX<ProductCategoryI18nDO> {

    /**
     * 根据分类ID和语言查询国际化记录
     *
     * @param categoryId 分类ID
     * @param language   语言代码
     * @return 国际化记录
     */
    default ProductCategoryI18nDO selectByCategoryIdAndLanguage(Long categoryId, String language) {
        return selectOne(ProductCategoryI18nDO::getCategoryId, categoryId,
                ProductCategoryI18nDO::getLanguage, language);
    }

    /**
     * 根据语言查询所有国际化记录
     *
     * @param language 语言代码
     * @return 国际化记录列表
     */
    default List<ProductCategoryI18nDO> selectListByLanguage(String language) {
        return selectList(ProductCategoryI18nDO::getLanguage, language);
    }

    /**
     * 根据分类ID列表和语言查询国际化记录
     *
     * @param categoryIds 分类ID列表
     * @param language    语言代码
     * @return 国际化记录列表
     */
    default List<ProductCategoryI18nDO> selectListByCategoryIdsAndLanguage(List<Long> categoryIds, String language) {
        return selectList(new LambdaQueryWrapper<ProductCategoryI18nDO>()
                .in(ProductCategoryI18nDO::getCategoryId, categoryIds)
                .eq(ProductCategoryI18nDO::getLanguage, language));
    }

}
