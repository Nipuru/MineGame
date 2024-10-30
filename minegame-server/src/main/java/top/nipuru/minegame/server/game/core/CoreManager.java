package top.nipuru.minegame.server.game.core;


import com.alipay.remoting.exception.RemotingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.nipuru.minegame.common.message.database.QueryPlayerRequest;
import top.nipuru.minegame.common.message.shared.PlayerInfoMessage;
import top.nipuru.minegame.common.message.shared.PlayerInfoUpdateNotify;
import top.nipuru.minegame.server.LogServer;
import top.nipuru.minegame.server.Router;
import top.nipuru.minegame.server.TimeMgr;
import top.nipuru.minegame.server.constants.ItemTypes;
import top.nipuru.minegame.server.game.player.BaseManager;
import top.nipuru.minegame.server.game.player.GamePlayer;
import top.nipuru.minegame.server.game.player.DataInfo;

import java.util.Arrays;

@Getter
@Slf4j
public class CoreManager extends BaseManager {

    private PlayerData playerData;
    private long playedTimeUpdateTime;
    private boolean updateShared;

    public CoreManager(GamePlayer player) {
        super(player);
    }

    public static void preload(QueryPlayerRequest request) throws Exception {
        preload(request, PlayerData.class);
    }

    public void unpack(DataInfo dataInfo) throws Exception {
        playerData = dataInfo.unpack(PlayerData.class);
        if (playerData == null) {
            playerData = new PlayerData();
            playerData.createTime = TimeMgr.now();
            player.insert(playerData);
            updateShared = true;
        }
    }

    public void pack(DataInfo dataInfo) throws Exception {
        dataInfo.pack(playerData);
    }

    // 更新玩家在线时间
    public void tick(long systemTimeMills) {
        updatePlayedTime(systemTimeMills, false);
        updatePublic();
    }

    public void onQuit() {
        updatePlayedTime(System.currentTimeMillis(), true);
    }

    /** 用于传输 或者显示给其他玩家 */
    public PlayerInfoMessage getPlayerInfoMessage() {
        return new PlayerInfoMessage()
                .setPlayerId(player.getPlayerId())
                .setName(player.getName())
                .setDbId(player.getDbId())
                .setCoin(playerData.coin())
                .setRankId(playerData.rankId())
                .setCreateTime(playerData.createTime())
                .setLastLogoutTime(playerData.lastLogoutTime())
                .setPlayedTime(playerData.playedTime());
    }

    public boolean isOnline() {
        return playerData.lastLogoutTime == 0L;
    }

    public void setOnline(boolean isOnline) {
        if (isOnline) {
            setLastLogoutTime(0L);
        } else {
            setLastLogoutTime(TimeMgr.now());
        }
    }

    public void subtractCoin(int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("subtract invalid coin amount: {}", amount);
            return;
        }
        playerData.coin -= amount;
        player.update(playerData, PlayerData::coin);
        updateShared = true;
        LogServer.logAddItem(player.getPlayerId(), ItemTypes.ITEM_PROP, ItemTypes.PROP_COIN, amount, way);
    }

    public void addCoin(int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("add invalid coin amount: {}", amount);
            return;
        }
        playerData.coin += amount;
        player.update(playerData, PlayerData::coin);
        updateShared = true;
        LogServer.logAddItem(player.getPlayerId(), ItemTypes.ITEM_PROP, ItemTypes.PROP_COIN, amount, way);
    }

    public void subtractPoints(int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("subtract invalid points amount: {}", amount);
            return;
        }
        playerData.points -= amount;
        player.update(playerData, PlayerData::coin);
        LogServer.logAddItem(player.getPlayerId(), ItemTypes.ITEM_PROP, ItemTypes.PROP_POINTS, amount, way);
    }

    public void addPoints(int amount, int way) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("add invalid coin points: {}", amount);
            return;
        }
        playerData.points += amount;
        player.update(playerData, PlayerData::points);
        LogServer.logAddItem(player.getPlayerId(), ItemTypes.ITEM_PROP, ItemTypes.PROP_POINTS, amount, way);
    }

    public void setRankId(int rankId) {
        if (playerData.rankId == rankId) return;
        playerData.rankId = rankId;
        player.update(playerData, PlayerData::rankId);
        updateShared = true;
    }

    public void setMedalId(int medalId) {
        if (playerData.medalId == medalId) return;
        playerData.medalId = medalId;
        player.update(playerData, PlayerData::medalId);
        updateShared = true;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        if (playerData.lastLogoutTime == lastLogoutTime) return;
        playerData.lastLogoutTime = lastLogoutTime;
        player.update(playerData, PlayerData::lastLogoutTime);
        updateShared = true;
    }

    public void setResetTime(long resetTime) {
        if (playerData.resetTime == resetTime) return;
        playerData.resetTime = resetTime;
        player.update(playerData, PlayerData::resetTime);
    }

    public void setPlayedTime(long playedTime) {
        if (playerData.playedTime == playedTime) return;
        playerData.playedTime = playedTime;
        player.update(playerData, PlayerData::playedTime);
        updateShared = true;
    }

    public void setBirthday(int month, int day) {
        int[] birthday = {month, day};
        if (Arrays.equals(playerData.birthday, birthday)) return;
        playerData.birthday = birthday;
        player.update(playerData, PlayerData::birthday);
        updateShared = true;
    }

    private void updatePlayedTime(long systemTimeMills, boolean force) {
        if (!isOnline()) return;  // 不在线直接退出
        long updateTime = playedTimeUpdateTime;
        long playedTime = playerData.playedTime();
        if (updateTime == 0L) {
            playedTimeUpdateTime = systemTimeMills;
            setPlayedTime(playedTime);
            return;
        }
        long delay = 60 * 1000; // 满一分钟执行一次
        if (!force && (systemTimeMills - updateTime + playedTime) / delay ==  playedTime / delay) return;
        if (log.isDebugEnabled()) {
            log.info("Update playedTime from {} to {} for GamePlayer {}", playedTime, playedTime + (systemTimeMills - updateTime), player.getName());
        }
        playedTimeUpdateTime = systemTimeMills;
        playedTime += systemTimeMills - updateTime;
        setPlayedTime(playedTime);
    }

    // 更新玩家的公共玩家信息
    private void updatePublic() {
        if (!updateShared) return;
        updateShared = false;
        PlayerInfoMessage info = getPlayerInfoMessage();
        if (log.isDebugEnabled()) {
            log.debug("Update PlayerInfo to SharedServer for GamePlayer: {}", info.getName());
        }
        PlayerInfoUpdateNotify notify = new PlayerInfoUpdateNotify(info);
        player.getPlugin().getBizThread().execute(() -> {
            try {
                Router.sharedNotify(notify);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }


}
