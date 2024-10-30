package top.nipuru.minegame.server.game.friend;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.server.TimeMgr;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;
import top.nipuru.minegame.server.util.GsonHolder;

import java.util.Collection;
import java.util.Collections;

@Slf4j
public class FriendManager extends BaseManager {

    private static final String moduleName = "friendship";

    /** 好友列表 */
    private final Int2ObjectMap<FriendshipData> friendships = new Int2ObjectOpenHashMap<>();
    /** 收到的好友请求列表 */
    private final Int2ObjectMap<FriendRequest> friendRequests = new Int2ObjectOpenHashMap<>();

    public FriendManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, FriendshipData.class);
        preload(request, FriendRequest.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        for (FriendshipData friendship : dataInfo.unpackList(FriendshipData.class)) {
            friendships.put(friendship.friendId(), friendship);
        }
        for (FriendRequest friendRequest : dataInfo.unpackList(FriendRequest.class)) {
            friendRequests.put(friendRequest.friendId(), friendRequest);
        }
    }

    public void pack(DataInfo dataInfo) throws Exception {
        for (FriendshipData friendship : friendships.values()) {
            dataInfo.pack(friendship);
        }
        for (FriendRequest friendRequest : friendRequests.values()) {
            dataInfo.pack(friendRequest);
        }
    }

    public void init() {
        player.registerOfflineHandler(moduleName, this::handleOfflineData);
    }

    public Collection<FriendshipData> getFriends() {
        return Collections.unmodifiableCollection(friendships.values());
    }

    public boolean isFriend(int friendId) {
        return friendships.containsKey(friendId);
    }

    public FriendshipData getFriend(int friendId) {
        return friendships.get(friendId);
    }

    public int getFriendCount() {
        return friendships.size();
    }

    public Collection<FriendRequest> getFriendRequests() {
        return Collections.unmodifiableCollection(friendRequests.values());
    }

    public boolean hasFriendRequest(int friendId) {
        return friendRequests.containsKey(friendId);
    }

    public FriendRequest getFriendRequest(int playerId) {
        return friendRequests.get(playerId);
    }

    public void deleteFriend(int friendId, int friendDbId) {
        FriendshipData friendship = friendships.remove(friendId);
        if (friendship == null) return;
        player.delete(friendship);
        pushOfflineData(friendId, friendDbId, FriendshipOfflineData.DELETE, player.getPlayerId(), player.getDbId(), 0L);
    }

    public void rejectFriend(int friendId) {
        FriendRequest friendRequest = friendRequests.remove(friendId);
        if (friendRequest == null) return;
        player.delete(friendRequest);
    }

    public void acceptFriend(int friendId, int friendDbId) {
        FriendRequest friendRequest = friendRequests.remove(friendId);
        if (friendRequest == null) return;
        player.delete(friendRequest);
        FriendshipData friendship = friendships.get(friendId);
        if (friendship != null) return;
        friendship = new FriendshipData();
        friendship.friendId = friendId;
        friendship.createTime = TimeMgr.now();
        friendships.put(friendId, friendship);
        player.insert(friendship);
        pushOfflineData(friendId, friendDbId, FriendshipOfflineData.ACCEPT, player.getPlayerId(), player.getDbId(), friendship.createTime);
    }

    public void addFriend(int friendId, int friendDbId) {
        if (friendships.containsKey(friendId)) return;
        pushOfflineData(friendId, friendDbId, FriendshipOfflineData.REQUEST, player.getPlayerId(), player.getDbId(), TimeMgr.now());
    }

    // 通知请求不要在这里处理，要单独发
    private boolean handleOfflineData(String json, boolean isOnline) {
        FriendshipOfflineData data = GsonHolder.GSON.fromJson(json, FriendshipOfflineData.class);
        switch (data.cmd) {
            // 对方发送好友请求 新增一条未处理好友
            case FriendshipOfflineData.REQUEST -> {
                if (friendRequests.containsKey(data.friendId)) return true;
                FriendRequest friendRequest = new FriendRequest();
                friendRequest.friendId = data.friendId;
                friendRequest.createTime = data.createTime;
                friendRequests.put(friendRequest.friendId, friendRequest);
                player.insert(friendRequest);
            }
            // 对方接受好友请求 新增一条好友
            case FriendshipOfflineData.ACCEPT -> {
                FriendshipData friendship = friendships.get(data.friendId);
                if (friendship != null) return true;
                friendship = new FriendshipData();
                friendship.friendId = data.friendId;
                friendship.createTime = data.createTime;
                friendships.put(friendship.friendId, friendship);
                player.insert(friendship);
            }
            // 对方删除好友
            case FriendshipOfflineData.DELETE -> {
                FriendshipData friendship = friendships.remove(data.friendId);
                if (friendship == null) return true;
                player.delete(friendship);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private void pushOfflineData(int friendId, int friendDbId, int cmd, int playerId, int dbId, long createTime) {
        FriendshipOfflineData offlineData = new FriendshipOfflineData(cmd, playerId, dbId, createTime);
        String json = GsonHolder.GSON.toJson(offlineData);
        player.pushOfflineDataTo(friendId, friendDbId, moduleName, json);
    }
}
