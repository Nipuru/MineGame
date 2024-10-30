package top.nipuru.minegame.auth.exception;

import java.io.Serial;

/**
 * 工具类异常
 *
 * @author Nipuru
 * @since 2023/10/07 21:24
 */
public class UtilException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2124379970044640619L;

    public UtilException(Throwable e) {
        super(e.getMessage(), e);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable throwable) {
        super(message, throwable);
    }
}