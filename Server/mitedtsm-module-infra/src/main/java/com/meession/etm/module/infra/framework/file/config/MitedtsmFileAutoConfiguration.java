package com.meession.etm.module.infra.framework.file.config;

import com.meession.etm.module.infra.framework.file.core.client.FileClientFactory;
import com.meession.etm.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author 密讯
 */
@Configuration(proxyBeanMethods = false)
public class MitedtsmFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
