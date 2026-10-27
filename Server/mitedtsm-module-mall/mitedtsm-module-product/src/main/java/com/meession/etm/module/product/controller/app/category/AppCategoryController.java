package com.meession.etm.module.product.controller.app.category;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.util.i18n.I18nUtil;
import com.meession.etm.module.product.controller.app.category.vo.AppCategoryRespVO;
import com.meession.etm.module.product.dal.dataobject.category.ProductCategoryDO;
import com.meession.etm.module.product.service.category.ProductCategoryI18nService;
import com.meession.etm.module.product.service.category.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 商品分类")
@RestController
@RequestMapping("/product/category")
@Validated
public class AppCategoryController {

    @Resource
    private ProductCategoryService categoryService;

    @Resource
    private ProductCategoryI18nService categoryI18nService;

    @GetMapping("/list")
    @Operation(summary = "获得商品分类列表")
    @PermitAll
    public CommonResult<List<AppCategoryRespVO>> getProductCategoryList() {
        List<ProductCategoryDO> list = categoryService.getEnableCategoryList();
        list.sort(Comparator.comparing(ProductCategoryDO::getSort));
        return success(convertToVOWithI18n(list));
    }

    @GetMapping("/list-by-ids")
    @Operation(summary = "获得商品分类列表，指定编号")
    @Parameter(name = "ids", description = "商品分类编号数组", required = true)
    @PermitAll
    public CommonResult<List<AppCategoryRespVO>> getProductCategoryList(@RequestParam("ids") List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return success(Collections.emptyList());
        }
        List<ProductCategoryDO> list = categoryService.getEnableCategoryList(ids);
        list.sort(Comparator.comparing(ProductCategoryDO::getSort));
        return success(convertToVOWithI18n(list));
    }

    /**
     * 将分类列表转换为 VO 并应用国际化
     *
     * @param categories 分类列表
     * @return 带国际化的 VO 列表
     */
    private List<AppCategoryRespVO> convertToVOWithI18n(List<ProductCategoryDO> categories) {
        if (CollUtil.isEmpty(categories)) {
            return Collections.emptyList();
        }

        // 获取当前语言
        Locale locale = I18nUtil.getLocale();
        String language = locale.toString();

        // 获取所有分类ID
        List<Long> categoryIds = categories.stream()
                .map(ProductCategoryDO::getId)
                .collect(Collectors.toList());

        // 获取国际化映射
        Map<Long, String> i18nMap = categoryI18nService.getCategoryNameMap(categoryIds, language);

        // 转换为 VO 并应用国际化名称
        return categories.stream().map(category -> {
            AppCategoryRespVO vo = new AppCategoryRespVO();
            vo.setId(category.getId());
            vo.setParentId(category.getParentId());
            vo.setPicUrl(category.getPicUrl());
            // 优先使用国际化名称，如果没有则使用原始名称
            String i18nName = i18nMap.get(category.getId());
            vo.setName(i18nName != null ? i18nName : category.getName());
            return vo;
        }).collect(Collectors.toList());
    }

}
