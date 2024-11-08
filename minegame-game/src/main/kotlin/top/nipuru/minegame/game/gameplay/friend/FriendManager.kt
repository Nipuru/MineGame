package top.nipuru.minegame.game.gameplay.friend

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import top.nipuru.minegame.common.message.database.QueryPlayerRequest
import top.nipuru.minegame.game.gameplay.player.BaseManager
import top.nipuru.minegame.game.gameplay.player.DataInfo
import top.nipuru.minegame.game.gameplay.player.GamePlayer
import top.nipuru.minegame.game.gameplay.player.preload
import top.nipuru.minegame.game.gson
import top.nipuru.minegame.game.now
import java.util.*

private const val moduleName = "friendship"

class FriendManager(player: GamePlayer) : BaseManager(player) {
    /** 好友列表  */
    private val friendships: Int2ObjectMap<FriendshipData> = Int2ObjectOpenHashMap()

    /** 收到的好友请求列表  */
    private val friendRequests: Int2ObjectMap<FriendRequest> = Int2ObjectOpenHashMap()

    fun preload(request: QueryPlayerRequest) {
        request.preload(FriendshipData::class.java)
        request.preload(FriendRequest::class.java)
    }

    fun unpack(dataInfo: DataInfo) {
        for (friendship in dataInfo.unpackList(FriendshipData::class.java)) {
            friendships.put(friendship.friendId, friendship)
        }
        for (friendRequest in dataInfo.unpackList(FriendRequest::class.java)) {
            friendRequests.put(friendRequest.friendId, friendRequest)
        }
    }

    fun pack(dataInfo: DataInfo) {
        for (friendship in friendships.values) {
            dataInfo.pack(friendship)
        }
        for (friendRequest in friendRequests.values) {
            dataInfo.pack(friendRequest)
        }
    }

    fun init() {
//        player.registerOfflineHandler(moduleName, this::handleOfflineData)
    }

    val friends: Collection<FriendshipData>
        get() = Collections.unmodifiableCollection(friendships.values)

    fun isFriend(friendId: Int): Boolean {
        return friendships.containsKey(friendId)
    }

    fun getFriend(friendId: Int): FriendshipData {
        return friendships[friendId]
    }

    val friendCount: Int
        get() = friendships.count()

    fun getFriendRequests(): Collection<FriendRequest> {
        return Collections.unmodifiableCollection(friendRequests.values)
    }

    fun hasFriendRequest(friendId: Int): Boolean {
        return friendRequests.containsKey(friendId)
    }

    fun getFriendRequest(playerId: Int): FriendRequest {
        return friendRequests[playerId]
    }

    fun deleteFriend(friendId: Int, friendDbId: Int) {
        val friendship = friendships.remove(friendId) ?: return
        player.delete(friendship)
        pushOfflineData(friendId, friendDbId, FriendshipOfflineData.DELETE, player.playerId, player.dbId, 0L)
    }

    fun rejectFriend(friendId: Int) {
        val friendRequest = friendRequests.remove(friendId) ?: return
        player.delete(friendRequest)
    }

    fun acceptFriend(friendId: Int, friendDbId: Int) {
        val friendRequest = friendRequests.remove(friendId) ?: return
        player.delete(friendRequest)
        var friendship = friendships[friendId]
        if (friendship != null) return
        friendship = FriendshipData()
        friendship.friendId = friendId
        friendship.createTime = now()
        friendships.put(friendId, friendship)
        player.insert(friendship)
        pushOfflineData(
            friendId,
            friendDbId,
            FriendshipOfflineData.ACCEPT,
            player.playerId,
            player.dbId,
            friendship.createTime
        )
    }

    fun addFriend(friendId: Int, friendDbId: Int) {
        if (friendships.containsKey(friendId)) return
        pushOfflineData(
            friendId,
            friendDbId,
            FriendshipOfflineData.REQUEST,
            player.playerId,
            player.dbId,
            now()
        )
    }

    // 通知请求不要在这里处理，要单独发
    private fun handleOfflineData(json: String, isOnline: Boolean): Boolean {
        val data: FriendshipOfflineData = gson.fromJson(json, FriendshipOfflineData::class.java)
        when (data.cmd) {
            FriendshipOfflineData.REQUEST -> {
                if (friendRequests.containsKey(data.friendId)) return true
                val friendRequest = FriendRequest()
                friendRequest.friendId = data.friendId
                friendRequest.createTime = data.createTime
                friendRequests.put(friendRequest.friendId, friendRequest)
                player.insert(friendRequest)
            }

            FriendshipOfflineData.ACCEPT -> {
                var friendship = friendships[data.friendId]
                if (friendship != null) return true
                friendship = FriendshipData()
                friendship.friendId = data.friendId
                friendship.createTime = data.createTime
                friendships.put(friendship.friendId, friendship)
                player.insert(friendship)
            }

            FriendshipOfflineData.DELETE -> {
                val friendship = friendships.remove(data.friendId) ?: return true
                player.delete(friendship)
            }

            else -> {
                return false
            }
        }
        return true
    }

    private fun pushOfflineData(friendId: Int, friendDbId: Int, cmd: Int, playerId: Int, dbId: Int, createTime: Long) {
        val offlineData = FriendshipOfflineData(cmd, playerId, dbId, createTime)
        val json: String = gson.toJson(offlineData)
        player.pushOfflineDataTo(friendId, friendDbId, moduleName, json)
    }
}
