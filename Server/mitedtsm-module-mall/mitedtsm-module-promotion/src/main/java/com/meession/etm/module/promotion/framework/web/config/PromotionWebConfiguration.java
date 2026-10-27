package com.meession.etm.module.promotion.framework.web.config;

import com.meession.etm.framework.swagger.config.MitedtsmSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * promotion 模块的 web 组件的 Configuration
 *
 * @author 密讯
 */
@Configuration(proxyBeanMethods = false)
public class PromotionWebConfiguration {

    /**
     * promotion 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi promotionGroupedOpenApi() {
        return MitedtsmSwaggerAutoConfiguration.buildGroupedOpenApi("promotion");
    }

}
