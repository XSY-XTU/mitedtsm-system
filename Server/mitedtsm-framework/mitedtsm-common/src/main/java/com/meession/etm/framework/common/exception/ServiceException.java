package com.meession.etm.framework.common.exception;

import com.meession.etm.framework.common.exception.enums.ServiceErrorCodeRange;
import com.meession.etm.framework.common.util.i18n.I18nUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends RuntimeException {

    /**
     * 业务错误码
     *
     * @see ServiceErrorCodeRange
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public ServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 获取国际化错误消息
     * 优先从 i18n 资源文件获取，找不到则使用默认消息
     *
     * @return 国际化错误消息
     */
    public String getI18nMessage() {
        String i18nMsg = I18nUtil.getMessage("error." + code, null, null);
        // 如果找到了 i18n 消息则返回，否则返回默认消息
        return i18nMsg != null ? i18nMsg : message;
    }

}
