package top.nipuru.minegame.server;


import com.alipay.remoting.exception.RemotingException;
import top.nipuru.minegame.common.message.AuthServerRequest;
import top.nipuru.minegame.common.message.DatabaseServerRequest;
import top.nipuru.minegame.common.message.SharedServerRequest;
import top.nipuru.minegame.common.message.ResponseMessage;
import net.afyer.afybroker.client.Broker;
import top.nipuru.minegame.common.processor.RequestDispatcher;

import static top.nipuru.minegame.common.message.RequestMessage.createRequest;

/**
 * 对消息进行一层封装 使得消息只在 brokerClient 完成序列化和反序列化
 * 一些逻辑简单的 转发类的消息可以考虑使用此方法
 * {@link  RequestDispatcher}
 */
public final class Router {

    public static <T> T sharedRequest(Object request) throws RemotingException, InterruptedException {
        SharedServerRequest requestMessage = new SharedServerRequest(createRequest(request));
        ResponseMessage responseMessage = Broker.invokeSync(requestMessage);
        return responseMessage.getData();
    }

    public static void sharedNotify(Object request) throws RemotingException, InterruptedException {
        SharedServerRequest requestMessage = new SharedServerRequest(createRequest(request));
        Broker.oneway(requestMessage);
    }

    public static <T> T authRequest(Object request) throws RemotingException, InterruptedException {
        AuthServerRequest requestMessage = new AuthServerRequest(createRequest(request));
        ResponseMessage responseMessage = Broker.invokeSync(requestMessage);
        return responseMessage.getData();
    }

    public static void authNotify(Object request) throws RemotingException, InterruptedException {
        AuthServerRequest requestMessage = new AuthServerRequest(createRequest(request));
        Broker.oneway(requestMessage);
    }

    public static <T> T databaseRequest(int dbId, Object request) throws RemotingException, InterruptedException {
        DatabaseServerRequest requestMessage = new DatabaseServerRequest(dbId, createRequest(request));
        ResponseMessage responseMessage = Broker.invokeSync(requestMessage);
        return responseMessage.getData();
    }

    public static void databaseNotify(int dbId, Object request) throws RemotingException, InterruptedException {
        DatabaseServerRequest requestMessage = new DatabaseServerRequest(dbId, createRequest(request));
        Broker.oneway(requestMessage);
    }
}
