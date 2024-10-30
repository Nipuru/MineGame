package top.nipuru.minegame.common.message.shared;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListingMessage implements Serializable {
    /** 主键id */
    long listingId;

    /** 出售者uuid */
    int sellerId;

    /** 购买者uuid */
    int buyerId;

    /** 交易时间 */
    long dealTime;

    /** 过期时间 */
    long expireTime;

    /** 分组 */
    int category;

    /** 价格 */
    int price;

    /** 物品名称 */
    String name;

    /** 物品数据 */
    byte[] item;
}
