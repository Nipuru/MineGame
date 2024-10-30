package top.nipuru.minegame.database.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;

public class CloseEventDBProcessor implements ConnectionEventProcessor {
    @Override
    public void onEvent(String remoteAddress, Connection connection) {

    }
}
