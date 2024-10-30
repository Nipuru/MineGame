package top.nipuru.minegame.common.message.database;

import lombok.Getter;

@Getter
public class LoadFileRequest {
    private final String filename;

    public LoadFileRequest(String filename) {
        this.filename = filename;
    }
}
