package com.meession.etm.module.infra.framework.web.config;

import com.meession.etm.framework.swagger.config.MitedtsmSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * infra 模块的 web 组件的 Configuration
 *
 * @author 密讯
 */
@Configuration(proxyBeanMethods = false)
public class InfraWebConfiguration {

    /**
     * infra 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi infraGroupedOpenApi() {
        return MitedtsmSwaggerAutoConfiguration.buildGroupedOpenApi("infra");
    }

}
