package top.nipuru.minegame.common.message.shared

import java.io.Serializable

class ListingMessage : Serializable {
    /** 主键id  */
    var listingId: Long = 0

    /** 出售者uuid  */
    var sellerId: Int = 0

    /** 购买者uuid  */
    var buyerId: Int = 0

    /** 交易时间  */
    var dealTime: Long = 0

    /** 过期时间  */
    var expireTime: Long = 0

    /** 分组  */
    var category: Int = 0

    /** 价格  */
    var price: Int = 0

    /** 物品名称  */
    var name: String = ""

    /** 物品数据  */
    var item: ByteArray = ByteArray(0)
}
