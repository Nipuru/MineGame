package top.nipuru.minegame.shared.auction

import top.nipuru.minegame.common.message.shared.ListingMessage
import top.nipuru.minegame.shared.SharedServer
import top.nipuru.minegame.shared.dataSource
import java.sql.SQLException
import java.sql.Statement
import java.util.concurrent.ConcurrentHashMap

object ListingManager {

    private val createTable = """
            CREATE TABLE IF NOT EXISTS tb_listing (
                listing_id  BIGSERIAL    NOT NULL,
                seller_id   INTEGER      NOT NULL,
                buyer_id    INTEGER      NOT NULL,
                deal_time   BIGINT       NOT NULL,
                expire_time BIGINT       NOT NULL,
                category    INTEGER      NOT NULL,
                price       INTEGER      NOT NULL,
                name        VARCHAR(255) NOT NULL,
                item        BYTEA        NOT NULL,
                CONSTRAINT  pkey_tb_listing PRIMARY KEY (listing_id)
            );
            
            """.trimIndent()

    private const val selectAll =
        "SELECT listing_id,seller_id,buyer_id,deal_time,expire_time,category,price,name,item FROM tb_listing;"

    private const val insert =
        "INSERT INTO tb_listing (seller_id, buyer_id, deal_time, expire_time, category, price, name, item) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

    private const val delete = "DELETE FROM tb_listing WHERE listing_id=?"

    private val listings: MutableMap<Long, ListingMessage> = ConcurrentHashMap()

    fun init() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(createTable)
                stmt.executeQuery(selectAll).use { rs ->
                    while (rs.next()) {
                        val listing = ListingMessage()
                        listing.listingId = rs.getLong(1)
                        listing.sellerId = rs.getInt(2)
                        listing.buyerId = rs.getInt(3)
                        listing.dealTime = rs.getLong(4)
                        listing.expireTime = rs.getLong(5)
                        listing.category = rs.getInt(6)
                        listing.price = rs.getInt(7)
                        listing.name = rs.getString(8)
                        listing.item = rs.getBytes(9)
                        listings[listing.listingId] = listing
                    }
                }
            }
        }
    }

    fun insertListing(sellerId: Int, expireTime: Long, category: Int, price: Int, name: String, item: ByteArray) {
        val listing = ListingMessage()
        listing.sellerId = sellerId
        listing.expireTime = expireTime
        listing.category = category
        listing.price = price
        listing.name = name
        listing.item = item

        dataSource.connection.use { conn ->
            conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                pstmt.setInt(1, listing.sellerId)
                pstmt.setInt(2, listing.buyerId)
                pstmt.setLong(3, listing.dealTime)
                pstmt.setLong(4, listing.expireTime)
                pstmt.setInt(5, listing.category)
                pstmt.setInt(6, listing.price)
                pstmt.setString(7, listing.name)
                pstmt.setBytes(8, listing.item)
                pstmt.executeUpdate()
                pstmt.generatedKeys.use { rs ->
                    rs.next()
                    listing.listingId = rs.getLong(1)
                }
            }
        }
        listings[listing.listingId] = listing
    }

    fun deleteListing(listingId: Long): Boolean {
        val listing = listings.remove(listingId) ?: return false
        dataSource.connection.use { conn ->
            conn.prepareStatement(delete).use { pstmt ->
                pstmt.setLong(1, listingId)
                pstmt.executeUpdate()
            }
        }
        return true
    }

}
