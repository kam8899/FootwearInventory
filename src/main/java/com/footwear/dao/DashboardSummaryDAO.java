
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;
import com.footwear.model.DashboardSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDAO {

    public DashboardSummary getSummary() {
        String sql =
                "SELECT " +
                        "(SELECT COUNT(*) FROM products) AS total_products, " +
                        "(SELECT COUNT(*) FROM brands) AS total_brands, " +
                        "(SELECT COALESCE(SUM(quantity), 0) FROM product_sizes) AS total_stock, " +
                        "(SELECT COUNT(*) FROM product_sizes " +
                        " WHERE quantity <= minimum_stock) AS low_stock_items";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new DashboardSummary(
                        resultSet.getInt("total_products"),
                        resultSet.getInt("total_brands"),
                        resultSet.getLong("total_stock"),
                        resultSet.getInt("low_stock_items")
                );
            }

            throw new IllegalStateException(
                    "Dashboard summary could not be retrieved.");

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to load dashboard summary: "
                            + e.getMessage(), e);
        }
    }

    // Returns the available stock grouped by product category.
    public List<Object[]> getStockByCategory() {
        List<Object[]> records = new ArrayList<Object[]>();

        String sql =
                "SELECT c.category_name, " +
                        "       COALESCE(SUM(ps.quantity), 0) AS total_stock " +
                        "FROM categories c " +
                        "LEFT JOIN products p " +
                        "       ON c.category_id = p.category_id " +
                        "LEFT JOIN product_sizes ps " +
                        "       ON p.product_id = ps.product_id " +
                        "GROUP BY c.category_id, c.category_name " +
                        "ORDER BY total_stock DESC, c.category_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                records.add(new Object[]{
                        rs.getString("category_name"),
                        rs.getLong("total_stock")
                });
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to load stock by category: "
                            + e.getMessage(), e);
        }

        return records;
    }

    // Returns actual sold quantities for the last seven calendar days.
    // Days without sales are included with a quantity of zero.
    public List<Object[]> getSalesLastSevenDays() {
        List<Object[]> records = new ArrayList<Object[]>();

        String sql =
                "SELECT day_series.sale_day::date AS sale_date, " +
                        "       COALESCE(SUM(sm.quantity), 0) AS sold_quantity " +
                        "FROM generate_series(" +
                        "       CURRENT_DATE - 6, " +
                        "       CURRENT_DATE, " +
                        "       INTERVAL '1 day') AS day_series(sale_day) " +
                        "LEFT JOIN stock_movements sm " +
                        "       ON sm.movement_date >= day_series.sale_day " +
                        "      AND sm.movement_date < " +
                        "          day_series.sale_day + INTERVAL '1 day' " +
                        "      AND sm.movement_type = 'OUT' " +
                        "      AND sm.reason = 'SOLD' " +
                        "GROUP BY day_series.sale_day::date " +
                        "ORDER BY sale_date";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                records.add(new Object[]{
                        rs.getDate("sale_date").toLocalDate()
                                .getDayOfWeek()
                                .getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                java.util.Locale.ENGLISH),
                        rs.getLong("sold_quantity")
                });
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to load weekly sales: "
                            + e.getMessage(), e);
        }

        return records;
    }
}