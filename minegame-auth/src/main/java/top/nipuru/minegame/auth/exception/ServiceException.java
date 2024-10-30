package top.nipuru.minegame.auth.exception;

import top.nipuru.minegame.auth.constant.HttpStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 业务异常
 */
@Getter
@Setter
@Accessors(chain = true)
public final class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final ServiceException ERROR = new ServiceException("操作失败");

    /**
     * 错误码
     */
    private Integer code = HttpStatus.ERROR;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}