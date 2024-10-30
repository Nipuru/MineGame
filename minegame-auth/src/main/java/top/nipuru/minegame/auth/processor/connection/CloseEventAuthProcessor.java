package top.nipuru.minegame.auth.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventType;
import org.springframework.stereotype.Component;

@Component
public class CloseEventAuthProcessor implements ConnectionEventProcessor {

    @Override
    public ConnectionEventType type() {
        return ConnectionEventType.CLOSE;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {

    }
}
