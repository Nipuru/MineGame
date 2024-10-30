package top.nipuru.minegame.server.game.friend;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FriendshipOfflineData {
    public static final int REQUEST = 1;
    public static final int ACCEPT = 2;
    public static final int DELETE = 4;

    public final int cmd;
    public final int friendId;
    public final int friendDbId;
    public final long createTime;
}
