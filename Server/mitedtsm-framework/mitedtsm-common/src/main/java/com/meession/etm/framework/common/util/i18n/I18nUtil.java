package com.meession.etm.framework.common.util.i18n;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 国际化工具类
 * 
 * 提供获取国际化消息的静态方法
 *
 * @author mitedtsm
 */
public class I18nUtil {

    private static MessageSource messageSource;

    /**
     * 获取 MessageSource
     */
    private static MessageSource getMessageSource() {
        if (messageSource == null) {
            messageSource = SpringUtil.getBean(MessageSource.class);
        }
        return messageSource;
    }

    /**
     * 获取当前语言环境
     */
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * 设置当前语言环境
     */
    public static void setLocale(Locale locale) {
        LocaleContextHolder.setLocale(locale);
    }

    /**
     * 根据消息 key 获取国际化消息
     *
     * @param code 消息 key
     * @return 国际化消息
     */
    public static String getMessage(String code) {
        return getMessage(code, null, code);
    }

    /**
     * 根据消息 key 获取国际化消息
     *
     * @param code 消息 key
     * @param defaultMessage 默认消息
     * @return 国际化消息
     */
    public static String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    /**
     * 根据消息 key 获取国际化消息
     *
     * @param code 消息 key
     * @param args 参数
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args) {
        return getMessage(code, args, code);
    }

    /**
     * 根据消息 key 获取国际化消息
     *
     * @param code 消息 key
     * @param args 参数
     * @param defaultMessage 默认消息
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        try {
            return getMessageSource().getMessage(code, args, defaultMessage, getLocale());
        } catch (Exception e) {
            return defaultMessage;
        }
    }

    /**
     * 根据消息 key 获取国际化消息（指定语言）
     *
     * @param code 消息 key
     * @param locale 语言环境
     * @return 国际化消息
     */
    public static String getMessage(String code, Locale locale) {
        return getMessage(code, null, code, locale);
    }

    /**
     * 根据消息 key 获取国际化消息（指定语言）
     *
     * @param code 消息 key
     * @param args 参数
     * @param defaultMessage 默认消息
     * @param locale 语言环境
     * @return 国际化消息
     */
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        try {
            return getMessageSource().getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            return defaultMessage;
        }
    }

    /**
     * 根据错误码获取国际化错误消息
     *
     * @param errorCode 错误码
     * @return 国际化错误消息
     */
    public static String getErrorMessage(Integer errorCode) {
        return getMessage("error." + errorCode, "错误码: " + errorCode);
    }

    /**
     * 根据错误码获取国际化错误消息
     *
     * @param errorCode 错误码
     * @param args 参数
     * @return 国际化错误消息
     */
    public static String getErrorMessage(Integer errorCode, Object[] args) {
        return getMessage("error." + errorCode, args, "错误码: " + errorCode);
    }

}
