package top.nipuru.minegame.database.file;

import top.nipuru.minegame.database.DatabaseServer;

public class FileManager {

    private final DatabaseServer server;

    public FileManager(DatabaseServer server) {
        this.server = server;
    }

    public byte[] getFile(String fileName) {
        // todo
        return null;
    }

    public void saveFile(String fileName, byte[] data) {
        // todo
    }
}
