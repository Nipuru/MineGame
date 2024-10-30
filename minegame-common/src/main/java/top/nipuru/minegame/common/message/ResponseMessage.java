package top.nipuru.minegame.common.message;

public final class ResponseMessage extends BaseMessage {
    public ResponseMessage(String className, int serializer, byte[] data) {
        super(className, serializer, data);
    }
}
