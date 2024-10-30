package top.nipuru.minegame.database;

public class Bootstrap {

    public static void main(String[] args) throws Exception {
        DatabaseServer databaseServer = new DatabaseServer();
        databaseServer.startup();
        Runtime.getRuntime().addShutdownHook(new Thread(databaseServer::shutdown));
    }

}
