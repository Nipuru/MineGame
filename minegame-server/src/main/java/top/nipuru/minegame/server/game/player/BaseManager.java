package top.nipuru.minegame.server.game.player;

import top.nipuru.minegame.common.message.database.QueryPlayerRequest;

public abstract class BaseManager {

    protected final GamePlayer player;

    protected BaseManager(GamePlayer player) {
        this.player = player;
    }

    protected static void preload(QueryPlayerRequest request, Class<? extends Data> dataClass) throws Exception {
        DataConvertor.preload(request, dataClass);
    }
}
