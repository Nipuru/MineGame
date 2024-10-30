package top.nipuru.minegame.common.message.database;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class SaveFileRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7426980022234470930L;
    private final String filename;
    private final byte[] data;

    public SaveFileRequest(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;
    }
}
