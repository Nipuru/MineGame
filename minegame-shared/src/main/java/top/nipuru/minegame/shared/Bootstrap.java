package top.nipuru.minegame.shared;

public class Bootstrap {

    public static void main(String[] args) throws Exception {
        SharedServer sharedServer = new SharedServer();
        sharedServer.startup();
        Runtime.getRuntime().addShutdownHook(new Thread(sharedServer::shutdown));
    }

}
