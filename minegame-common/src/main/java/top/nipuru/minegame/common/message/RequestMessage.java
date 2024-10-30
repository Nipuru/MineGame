package top.nipuru.minegame.common.message;

public final class RequestMessage extends BaseMessage {
    public RequestMessage(String className, int serializer, byte[] data) {
        super(className, serializer, data);
    }
}
