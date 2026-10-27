package com.meession.etm.framework.websocket.core.sender.local;

import com.meession.etm.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import com.meession.etm.framework.websocket.core.sender.WebSocketMessageSender;
import com.meession.etm.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 *
 * 注意：仅仅适合单机场景！！！
 *
 * @author 密讯
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
