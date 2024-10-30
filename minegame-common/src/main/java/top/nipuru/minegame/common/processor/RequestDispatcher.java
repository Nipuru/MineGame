package top.nipuru.minegame.common.processor;

import top.nipuru.minegame.common.message.RequestMessage;
import top.nipuru.minegame.common.message.ResponseMessage;
import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RequestMessage 分发处理
 *
 * @author Nipuru
 * @since 2024/10/24 13:08
 */
@Slf4j
public class RequestDispatcher extends AsyncUserProcessor<RequestMessage> {

    private final Map<String, Handler<?>> handlers = new ConcurrentHashMap<>();

    public void registerHandler(Handler<?> handler) {
        if (handlers.containsKey(handler.interest().getName())) {
            throw new IllegalArgumentException("Handler " + handler.interest() + " is already registered");
        }
        handlers.put(handler.interest().getName(), handler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, RequestMessage request) throws Exception {
        Handler handler = handlers.get(request.getClassName());
        if (handler == null) {
            throw new NullPointerException("Handler " + request.getClassName() + " not exist");
        }
        Object data = request.getData();
        handler.handle(new ResponseSender(asyncCtx), data);
    }

    @Override
    public String interest() {
        return RequestMessage.class.getName();
    }

    public interface Handler<T> {
        void handle(ResponseSender responseSender, T request) throws Exception;
        Class<T> interest();
    }

    public record ResponseSender(AsyncContext asyncCtx) {
        public void sendResponse(Object data) {
            try {
                asyncCtx.sendResponse(ResponseMessage.createResponse(data));
            } catch (CodecException e) {
                log.error(e.getMessage(), e);
                asyncCtx.sendException(e);
            }
        }

        public void sendException(Throwable e) {
            asyncCtx.sendException(e);
        }
    }
}
