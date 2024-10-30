package top.nipuru.minegame.common.message;

import top.nipuru.minegame.common.RequestMessageContainer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseServerRequest implements RequestMessageContainer, Serializable {
    @Serial
    private static final long serialVersionUID = 2153259716601478748L;
    int dbId;
    RequestMessage requestMessage;
}
