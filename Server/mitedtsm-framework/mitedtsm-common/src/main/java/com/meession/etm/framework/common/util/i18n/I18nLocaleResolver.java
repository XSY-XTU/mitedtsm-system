package com.meession.etm.framework.common.util.i18n;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * 国际化语言解析器
 * 
 * 支持多种方式获取语言：
 * 1. 请求参数 lang
 * 2. 请求头 Accept-Language
 * 3. 默认语言（简体中文）
 *
 * @author mitedtsm
 */
public class I18nLocaleResolver implements LocaleResolver {

    /**
     * 语言参数名
     */
    private static final String LANG_PARAM = "lang";

    /**
     * 语言请求头
     */
    private static final String LANG_HEADER = "Accept-Language";

    /**
     * 默认语言
     */
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    @Override
    @NonNull
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        // 1. 优先从请求参数获取
        String langParam = request.getParameter(LANG_PARAM);
        if (StrUtil.isNotBlank(langParam)) {
            return parseLocale(langParam);
        }

        // 2. 从请求头获取
        String headerLang = request.getHeader(LANG_HEADER);
        if (StrUtil.isNotBlank(headerLang)) {
            return parseLocale(headerLang);
        }

        // 3. 返回默认语言
        return DEFAULT_LOCALE;
    }

    @Override
    public void setLocale(@NonNull HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            LocaleContextHolder.setLocale(locale);
        } else {
            LocaleContextHolder.setLocale(DEFAULT_LOCALE);
        }
    }

    /**
     * 解析语言字符串
     *
     * @param lang 语言字符串，如 "zh-CN", "en", "zh_CN"
     * @return Locale 对象
     */
    private Locale parseLocale(String lang) {
        if (StrUtil.isBlank(lang)) {
            return DEFAULT_LOCALE;
        }

        // 处理常见的语言格式
        lang = lang.replace("-", "_");
        String[] parts = lang.split("_");
        
        if (parts.length == 1) {
            // 只有语言代码，如 "en", "zh"
            String language = parts[0].toLowerCase();
            return switch (language) {
                case "en" -> Locale.ENGLISH;
                case "zh" -> Locale.SIMPLIFIED_CHINESE;
                case "ja" -> Locale.JAPANESE;
                case "ko" -> Locale.KOREAN;
                default -> new Locale(language);
            };
        } else if (parts.length >= 2) {
            // 有语言和国家代码，如 "zh_CN", "en_US"
            String language = parts[0].toLowerCase();
            String country = parts[1].toUpperCase();
            return new Locale(language, country);
        }

        return DEFAULT_LOCALE;
    }

}
