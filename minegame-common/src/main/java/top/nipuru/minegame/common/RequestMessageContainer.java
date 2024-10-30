package top.nipuru.minegame.common;

import top.nipuru.minegame.common.message.RequestMessage;

/**
 * 包含了一个请求消息用于转发
 *
 * @author Nipuru
 * @since 2024/10/24 13:08
 */
public interface RequestMessageContainer {
    RequestMessage getRequestMessage();
}
