package com.meession.etm.framework.common.exception;

import com.meession.etm.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.meession.etm.framework.common.exception.enums.ServiceErrorCodeRange;
import com.meession.etm.framework.common.util.i18n.I18nUtil;
import lombok.Data;

/**
 * 错误码对象
 *
 * 全局错误码，占用 [0, 999], 参见 {@link GlobalErrorCodeConstants}
 * 业务异常错误码，占用 [1 000 000 000, +∞)，参见 {@link ServiceErrorCodeRange}
 *
 * 支持国际化：通过 code 自动查找对应的 i18n 消息
 * 消息 key 格式：error.{code}，如 error.400、error.401
 * 消息定义位置：resources/i18n/messages_*.properties
 */
@Data
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示（默认消息，当 i18n 找不到对应 key 时使用）
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    /**
     * 获取国际化错误消息
     * 优先从 i18n 资源文件获取，找不到则使用默认消息
     *
     * @return 国际化错误消息
     */
    public String getI18nMessage() {
        return getI18nMessage(null);
    }

    /**
     * 获取国际化错误消息（带参数）
     *
     * @param args 消息参数
     * @return 国际化错误消息
     */
    public String getI18nMessage(Object[] args) {
        String i18nMsg = I18nUtil.getMessage("error." + code, args, null);
        // 如果找到了 i18n 消息则返回，否则返回默认消息
        return i18nMsg != null ? i18nMsg : msg;
    }

}
