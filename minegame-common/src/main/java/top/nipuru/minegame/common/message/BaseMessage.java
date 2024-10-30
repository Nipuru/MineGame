package top.nipuru.minegame.common.message;

import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import lombok.Getter;

import java.io.Serializable;

public sealed class BaseMessage implements Serializable permits RequestMessage, ResponseMessage {

    @Getter private final String className;
    private final int serializer;
    private final byte[] data;

    public BaseMessage(String className, int serializer, byte[] data) {
        this.className = className;
        this.serializer = serializer;
        this.data = data;
    }

    public <T> T getData() {
        try {
            return SerializerManager.getSerializer(serializer).deserialize(data, className);
        } catch (CodecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestMessage createRequest(Object request) throws CodecException {
        byte serializer = ConfigManager.serializer();
        Serializer serializer1 = SerializerManager.getSerializer(serializer);
        byte[] bytes = serializer1.serialize(request);
        return new RequestMessage(request.getClass().getName(), serializer, bytes);
    }

    public static ResponseMessage createResponse(Object response) throws CodecException {
        byte serializer = ConfigManager.serializer();
        Serializer serializer1 = SerializerManager.getSerializer(serializer);
        byte[] bytes = serializer1.serialize(response);
        return new ResponseMessage(response.getClass().getName(), serializer, bytes);
    }

}
