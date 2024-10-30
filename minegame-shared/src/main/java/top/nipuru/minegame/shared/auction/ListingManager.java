package top.nipuru.minegame.shared.auction;

import top.nipuru.minegame.common.message.shared.ListingMessage;
import top.nipuru.minegame.shared.SharedServer;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListingManager {

    private static final String createTable = """
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
            """;

    private static final String selectAll
            = "SELECT listing_id,seller_id,buyer_id,deal_time,expire_time,category,price,name,item FROM tb_listing;";

    private static final String insert
            = "INSERT INTO tb_listing (seller_id, buyer_id, deal_time, expire_time, category, price, name, item) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String delete
            = "DELETE FROM tb_listing WHERE listing_id=?";

    
    private final SharedServer server;
    public final Map<Long, ListingMessage> listings = new ConcurrentHashMap<>();

    public ListingManager(SharedServer server) {
        this.server = server;
    }

    public void init() throws SQLException {
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
                // 缓存所有数据
                try (ResultSet rs = stmt.executeQuery(selectAll)) {
                    while (rs.next()) {
                        ListingMessage listing = new ListingMessage();
                        listing.setListingId(rs.getLong(1));
                        listing.setSellerId(rs.getInt(2));
                        listing.setBuyerId(rs.getInt(3));
                        listing.setDealTime(rs.getLong(4));
                        listing.setExpireTime(rs.getLong(5));
                        listing.setCategory(rs.getInt(6));
                        listing.setPrice(rs.getInt(7));
                        listing.setName(rs.getString(8));
                        listing.setItem(rs.getBytes(9));
                        listings.put(listing.getListingId(), listing);
                    }
                }
            }
        }
    }

    public void insertListing(int sellerId, long expireTime, int category, int price, String name , byte[] item) throws SQLException {
        ListingMessage listing = new ListingMessage();
        listing.setSellerId(sellerId);
        listing.setExpireTime(expireTime);
        listing.setCategory(category);
        listing.setPrice(price);
        listing.setName(name);
        listing.setItem(item);

        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, listing.getSellerId());
            pstmt.setInt(2, listing.getBuyerId());
            pstmt.setLong(3, listing.getDealTime());
            pstmt.setLong(4, listing.getExpireTime());
            pstmt.setInt(5, listing.getCategory());
            pstmt.setInt(6, listing.getPrice());
            pstmt.setString(7, listing.getName());
            pstmt.setBytes(8, listing.getItem());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                listing.setListingId(rs.getLong(1));
            }
        }
        listings.put(listing.getListingId(), listing);
    }
    
    public boolean deleteListing(long listingId) throws SQLException {
        ListingMessage listing = listings.remove(listingId);
        if (listing == null) return false;
        try (Connection conn = server.getDataSourceProvider().getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(delete)) {
            pstmt.setLong(1, listingId);
            pstmt.executeUpdate();
        }
        return true;
    }
}
