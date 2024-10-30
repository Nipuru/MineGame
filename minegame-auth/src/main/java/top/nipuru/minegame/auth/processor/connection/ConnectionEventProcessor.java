package top.nipuru.minegame.auth.processor.connection;

import com.alipay.remoting.ConnectionEventType;

public interface ConnectionEventProcessor extends com.alipay.remoting.ConnectionEventProcessor {
    ConnectionEventType type();
}
