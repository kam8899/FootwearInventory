
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    public List<Object[]> getSalesHistory() throws Exception {

        List<Object[]> records = new ArrayList<Object[]>();

        String sql =
                "SELECT sm.movement_id, " +
                        "       p.product_name, " +
                        "       b.brand_name, " +
                        "       c.category_name, " +
                        "       ps.shoe_size, " +
                        "       sm.quantity, " +
                        "       sm.movement_date, " +
                        "       sm.notes " +
                        "FROM stock_movements sm " +
                        "JOIN product_sizes ps " +
                        "  ON sm.product_size_id = ps.product_size_id " +
                        "JOIN products p " +
                        "  ON ps.product_id = p.product_id " +
                        "JOIN brands b " +
                        "  ON p.brand_id = b.brand_id " +
                        "JOIN categories c " +
                        "  ON p.category_id = c.category_id " +
                        "WHERE sm.movement_type = 'OUT' " +
                        "  AND sm.reason = 'SOLD' " +
                        "ORDER BY sm.movement_date DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                records.add(new Object[]{
                        rs.getObject("movement_id"),
                        rs.getString("product_name"),
                        rs.getString("brand_name"),
                        rs.getString("category_name"),
                        rs.getString("shoe_size"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("movement_date"),
                        rs.getString("notes")
                });
            }
        }

        return records;
    }
}
