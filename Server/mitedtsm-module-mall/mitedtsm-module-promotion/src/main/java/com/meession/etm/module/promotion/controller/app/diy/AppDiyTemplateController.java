package com.meession.etm.module.promotion.controller.app.diy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.util.i18n.I18nUtil;
import com.meession.etm.module.promotion.controller.app.diy.vo.AppDiyTemplatePropertyRespVO;
import com.meession.etm.module.promotion.convert.diy.DiyTemplateConvert;
import com.meession.etm.module.promotion.dal.dataobject.diy.DiyPageDO;
import com.meession.etm.module.promotion.dal.dataobject.diy.DiyTemplateDO;
import com.meession.etm.module.promotion.enums.diy.DiyPageEnum;
import com.meession.etm.module.promotion.service.diy.DiyPageService;
import com.meession.etm.module.promotion.service.diy.DiyTemplateService;
import com.meession.etm.module.promotion.service.diy.PromotionDiyMenuI18nService;
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

import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.findFirst;

@Tag(name = "用户 APP - 装修模板")
@RestController
@RequestMapping("/promotion/diy-template")
@Validated
public class AppDiyTemplateController {

    @Resource
    private DiyTemplateService diyTemplateService;
    @Resource
    private DiyPageService diyPageService;
    @Resource
    private PromotionDiyMenuI18nService promotionDiyMenuI18nService;

    // TODO @疯狂：要不要把 used 和 get 接口合并哈；不传递 id，直接拿默认；
    @GetMapping("/used")
    @Operation(summary = "使用中的装修模板")
    @PermitAll
    public CommonResult<AppDiyTemplatePropertyRespVO> getUsedDiyTemplate() {
        DiyTemplateDO diyTemplate = diyTemplateService.getUsedDiyTemplate();
        return success(buildVo(diyTemplate));
    }

    @GetMapping("/get")
    @Operation(summary = "获得装修模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PermitAll
    public CommonResult<AppDiyTemplatePropertyRespVO> getDiyTemplate(@RequestParam("id") Long id) {
        DiyTemplateDO diyTemplate = diyTemplateService.getDiyTemplate(id);
        return success(buildVo(diyTemplate));
    }

    private AppDiyTemplatePropertyRespVO buildVo(DiyTemplateDO diyTemplate) {
        if (diyTemplate == null) {
            return null;
        }
        // 查询模板下的页面
        List<DiyPageDO> pages = diyPageService.getDiyPageByTemplateId(diyTemplate.getId());
        String home = findFirst(pages, page -> DiyPageEnum.INDEX.getName().equals(page.getName()), DiyPageDO::getProperty);
        String user = findFirst(pages, page -> DiyPageEnum.MY.getName().equals(page.getName()), DiyPageDO::getProperty);
        
        // 国际化替换 - 按位置分别获取翻译映射
        String language = I18nUtil.getLocale().toLanguageTag(); // 例如: zh-CN, en
        Map<String, String> homeTranslationMap = promotionDiyMenuI18nService.getTranslationMap(language, "home");
        Map<String, String> userTranslationMap = promotionDiyMenuI18nService.getTranslationMap(language, "user");
        Map<String, String> tabbarTranslationMap = promotionDiyMenuI18nService.getTranslationMap(language, "tabbar");
        Map<String, String> floatTranslationMap = promotionDiyMenuI18nService.getTranslationMap(language, "float");
        // 按钮文本翻译映射
        Map<String, String> buttonTranslationMap = promotionDiyMenuI18nService.getTranslationMap(language, "button");
        
        // 翻译首页和我的页面
        home = translateProperty(home, homeTranslationMap, floatTranslationMap, buttonTranslationMap);
        user = translateProperty(user, userTranslationMap, floatTranslationMap, buttonTranslationMap);
        
        // 翻译 TabBar (在 template.property 中)
        String property = diyTemplate.getProperty();
        if (StrUtil.isNotBlank(property)) {
            property = translateTabbarProperty(property, tabbarTranslationMap);
        }
        
        // 拼接返回
        AppDiyTemplatePropertyRespVO vo = DiyTemplateConvert.INSTANCE.convertPropertyVo2(diyTemplate, home, user);
        vo.setProperty(property);
        return vo;
    }

    /**
     * 翻译 property JSON 中的菜单标题
     *
     * @param propertyJson 原始 JSON 字符串
     * @param pageTranslationMap 页面组件翻译映射 (menuKey -> name)
     * @param floatTranslationMap 浮动按钮翻译映射 (menuKey -> name)
     * @param buttonTranslationMap 按钮文本翻译映射 (btnText -> translatedText)
     * @return 翻译后的 JSON 字符串
     */
    private String translateProperty(String propertyJson, Map<String, String> pageTranslationMap, 
            Map<String, String> floatTranslationMap, Map<String, String> buttonTranslationMap) {
        if (StrUtil.isBlank(propertyJson) || (CollUtil.isEmpty(pageTranslationMap) 
                && CollUtil.isEmpty(floatTranslationMap) && CollUtil.isEmpty(buttonTranslationMap))) {
            return propertyJson;
        }
        
        try {
            JSONObject root = JSONUtil.parseObj(propertyJson);
            JSONArray components = root.getJSONArray("components");
            if (components == null || components.isEmpty()) {
                return propertyJson;
            }
            
            // 遍历所有组件
            for (int i = 0; i < components.size(); i++) {
                JSONObject component = components.getJSONObject(i);
                translateComponent(component, pageTranslationMap, floatTranslationMap, buttonTranslationMap);
            }
            
            return root.toString();
        } catch (Exception e) {
            // 解析失败，返回原始 JSON
            return propertyJson;
        }
    }

    /**
     * 翻译组件中的菜单标题
     */
    private void translateComponent(JSONObject component, Map<String, String> pageTranslationMap, 
            Map<String, String> floatTranslationMap, Map<String, String> buttonTranslationMap) {
        if (component == null) {
            return;
        }
        
        String componentType = component.getStr("id");
        JSONObject property = component.getJSONObject("property");
        if (property == null) {
            return;
        }
        
        // 处理 MenuGrid 组件 - 使用页面翻译映射
        if ("MenuGrid".equals(componentType)) {
            translateMenuGrid(property, pageTranslationMap);
        }
        
        // 处理 MenuList 组件 - 使用页面翻译映射
        if ("MenuList".equals(componentType)) {
            translateMenuList(property, pageTranslationMap);
        }
        
        // 处理浮动按钮组件 - 使用浮动按钮翻译映射
        if ("FloatTabBar".equals(componentType)) {
            translateFloatTabBar(property, floatTranslationMap);
        }
        
        // 处理商品卡片类组件的按钮文本翻译
        if ("GoodsCard".equals(componentType) 
                || "SeckillCard".equals(componentType) || "SeckillBlock".equals(componentType)
                || "GrouponCard".equals(componentType) || "GrouponBlock".equals(componentType)
                || "PointCard".equals(componentType) || "PointBlock".equals(componentType)
                || "PromotionSeckill".equals(componentType) || "PromotionGroupon".equals(componentType)
                || "PromotionPoint".equals(componentType)) {
            translateGoodsCardButton(property, buttonTranslationMap);
        }
    }
    
    /**
     * 翻译商品卡片类组件的按钮文本
     */
    private void translateGoodsCardButton(JSONObject property, Map<String, String> buttonTranslationMap) {
        if (CollUtil.isEmpty(buttonTranslationMap)) {
            return;
        }
        
        JSONObject btnBuy = property.getJSONObject("btnBuy");
        if (btnBuy == null) {
            return;
        }
        
        String btnText = btnBuy.getStr("text");
        if (StrUtil.isNotBlank(btnText)) {
            // 使用按钮文本作为 key 查找翻译
            String translatedText = buttonTranslationMap.get(btnText);
            if (StrUtil.isNotBlank(translatedText)) {
                btnBuy.set("text", translatedText);
            }
        }
    }

    /**
     * 翻译 MenuGrid 组件
     */
    private void translateMenuGrid(JSONObject property, Map<String, String> translationMap) {
        JSONArray list = property.getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String url = item.getStr("url");
            if (StrUtil.isNotBlank(url)) {
                String translatedName = translationMap.get(url);
                if (StrUtil.isNotBlank(translatedName)) {
                    item.set("title", translatedName);
                }
            }
        }
    }

    /**
     * 翻译 MenuList 组件
     */
    private void translateMenuList(JSONObject property, Map<String, String> translationMap) {
        JSONArray list = property.getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String url = item.getStr("url");
            if (StrUtil.isNotBlank(url)) {
                String translatedName = translationMap.get(url);
                if (StrUtil.isNotBlank(translatedName)) {
                    item.set("title", translatedName);
                }
            }
        }
    }

    /**
     * 翻译 TabBar 组件
     */
    private void translateTabBar(JSONObject property, Map<String, String> translationMap) {
        JSONArray list = property.getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String url = item.getStr("url");
            if (StrUtil.isNotBlank(url)) {
                String translatedName = translationMap.get(url);
                if (StrUtil.isNotBlank(translatedName)) {
                    item.set("text", translatedName);
                }
            }
        }
    }

    /**
     * 翻译浮动按钮组件
     */
    private void translateFloatTabBar(JSONObject property, Map<String, String> translationMap) {
        JSONArray list = property.getJSONArray("list");
        if (list == null || list.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            String url = item.getStr("url");
            if (StrUtil.isNotBlank(url)) {
                String translatedName = translationMap.get(url);
                if (StrUtil.isNotBlank(translatedName)) {
                    item.set("text", translatedName);
                }
            }
        }
    }

    /**
     * 翻译 TabBar 属性（template.property 中的 basic.tabbar）
     * 
     * @param propertyJson 原始 JSON 字符串
     * @param translationMap 翻译映射 (menuKey -> name)
     * @return 翻译后的 JSON 字符串
     */
    private String translateTabbarProperty(String propertyJson, Map<String, String> translationMap) {
        if (StrUtil.isBlank(propertyJson) || CollUtil.isEmpty(translationMap)) {
            return propertyJson;
        }
        
        try {
            JSONObject root = JSONUtil.parseObj(propertyJson);
            JSONObject basic = root.getJSONObject("basic");
            if (basic == null) {
                return propertyJson;
            }
            
            JSONArray tabbar = basic.getJSONArray("tabbar");
            if (tabbar == null || tabbar.isEmpty()) {
                return propertyJson;
            }
            
            for (int i = 0; i < tabbar.size(); i++) {
                JSONObject item = tabbar.getJSONObject(i);
                String url = item.getStr("url");
                if (StrUtil.isNotBlank(url)) {
                    String translatedName = translationMap.get(url);
                    if (StrUtil.isNotBlank(translatedName)) {
                        item.set("text", translatedName);
                    }
                }
            }
            
            return root.toString();
        } catch (Exception e) {
            // 解析失败，返回原始 JSON
            return propertyJson;
        }
    }

}