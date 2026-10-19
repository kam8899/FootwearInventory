
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    public List<Object[]> getStockReport() throws SQLException {
        String sql =
                "SELECT p.product_name, b.brand_name, c.category_name, " +
                        "ps.shoe_size, ps.quantity, ps.minimum_stock " +
                        "FROM product_sizes ps " +
                        "JOIN products p ON ps.product_id = p.product_id " +
                        "JOIN brands b ON p.brand_id = b.brand_id " +
                        "JOIN categories c ON p.category_id = c.category_id " +
                        "ORDER BY p.product_name, b.brand_name, ps.shoe_size";

        return executeReport(sql);
    }

    public List<Object[]> getLowStockReport() throws SQLException {
        String sql =
                "SELECT p.product_name, b.brand_name, c.category_name, " +
                        "ps.shoe_size, ps.quantity, ps.minimum_stock " +
                        "FROM product_sizes ps " +
                        "JOIN products p ON ps.product_id = p.product_id " +
                        "JOIN brands b ON p.brand_id = b.brand_id " +
                        "JOIN categories c ON p.category_id = c.category_id " +
                        "WHERE ps.quantity <= ps.minimum_stock " +
                        "ORDER BY ps.quantity ASC, p.product_name";

        return executeReport(sql);
    }

    public List<Object[]> getSalesReport(
            Date startDate, Date endDate) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT sm.movement_id, sm.movement_date, " +
                        "p.product_name, b.brand_name, c.category_name, " +
                        "ps.shoe_size, sm.quantity, sm.notes " +
                        "FROM stock_movements sm " +
                        "JOIN product_sizes ps " +
                        "ON sm.product_size_id = ps.product_size_id " +
                        "JOIN products p ON ps.product_id = p.product_id " +
                        "JOIN brands b ON p.brand_id = b.brand_id " +
                        "JOIN categories c ON p.category_id = c.category_id " +
                        "WHERE sm.movement_type = 'OUT' " +
                        "AND sm.reason = 'SOLD' ");

        List<Object> parameters = new ArrayList<Object>();

        if (startDate != null) {
            sql.append("AND sm.movement_date >= ? ");
            parameters.add(startDate);
        }

        if (endDate != null) {
            // Include the entire end date, up to midnight next day.
            sql.append("AND sm.movement_date < (? + INTERVAL '1 day') ");
            parameters.add(endDate);
        }

        sql.append("ORDER BY sm.movement_date DESC");

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setDate(i + 1, (Date) parameters.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                List<Object[]> rows = new ArrayList<Object[]>();

                while (rs.next()) {
                    rows.add(new Object[] {
                            rs.getLong("movement_id"),
                            rs.getTimestamp("movement_date"),
                            rs.getString("product_name"),
                            rs.getString("brand_name"),
                            rs.getString("category_name"),
                            rs.getString("shoe_size"),
                            rs.getInt("quantity"),
                            rs.getString("notes")
                    });
                }

                return rows;
            }
        }
    }

    private List<Object[]> executeReport(String sql)
            throws SQLException {

        List<Object[]> rows = new ArrayList<Object[]>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[] {
                        rs.getString("product_name"),
                        rs.getString("brand_name"),
                        rs.getString("category_name"),
                        rs.getString("shoe_size"),
                        rs.getInt("quantity"),
                        rs.getInt("minimum_stock")
                });
            }
        }

        return rows;
    }
}
