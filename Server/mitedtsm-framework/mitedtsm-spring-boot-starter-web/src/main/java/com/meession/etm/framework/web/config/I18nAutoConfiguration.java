package com.meession.etm.framework.web.config;

import com.meession.etm.framework.common.util.i18n.I18nLocaleResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.nio.charset.StandardCharsets;

/**
 * 国际化自动配置类
 *
 * @author mitedtsm
 */
@AutoConfiguration
public class I18nAutoConfiguration {

    /**
     * 配置 MessageSource
     * 从 classpath:i18n/messages 读取国际化资源文件
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 设置国际化资源文件路径（不带语言后缀和扩展名）
        messageSource.setBasenames("i18n/messages");
        // 设置默认编码
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置默认语言
        messageSource.setDefaultLocale(java.util.Locale.SIMPLIFIED_CHINESE);
        // 找不到 key 时使用 key 本身作为默认值
        messageSource.setUseCodeAsDefaultMessage(true);
        // 缓存时间（秒），-1 表示永久缓存，开发环境可设置为 0
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    /**
     * 配置 LocaleResolver
     * 用于解析请求中的语言信息
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        return new I18nLocaleResolver();
    }

}
