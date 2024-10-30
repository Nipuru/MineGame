package top.nipuru.minegame.broker.exception;

import java.io.Serial;

public class IgnoredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7658698968039093285L;
    public static final IgnoredException INSTANCE = new IgnoredException();
    protected IgnoredException() {}
    protected IgnoredException(final String message) {
        super(message);
    }
}
