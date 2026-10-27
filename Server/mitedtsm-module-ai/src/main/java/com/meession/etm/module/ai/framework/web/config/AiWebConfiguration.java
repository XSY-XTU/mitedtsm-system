package com.meession.etm.module.ai.framework.web.config;

import com.meession.etm.framework.swagger.config.MitedtsmSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ai 模块的 web 组件的 Configuration
 *
 * @author 密讯
 */
@Configuration(proxyBeanMethods = false)
public class AiWebConfiguration {

    /**
     * ai 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi aiGroupedOpenApi() {
        return MitedtsmSwaggerAutoConfiguration.buildGroupedOpenApi("ai");
    }

}
