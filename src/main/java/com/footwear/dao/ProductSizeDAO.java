
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;
import com.footwear.model.ProductSize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductSizeDAO {

    public List<ProductSize> getAllProductSizes() {

        List<ProductSize> productSizes = new ArrayList<>();

        String sql =
                "SELECT ps.product_size_id, ps.product_id, " +
                        "p.product_code, p.product_name, " +
                        "b.brand_name, c.category_name, " +
                        "ps.shoe_size, ps.quantity, ps.minimum_stock " +
                        "FROM product_sizes ps " +
                        "JOIN products p ON ps.product_id = p.product_id " +
                        "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                        "LEFT JOIN categories c ON p.category_id = c.category_id " +
                        "ORDER BY p.product_name, ps.shoe_size";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                ProductSize productSize = new ProductSize();

                productSize.setProductSizeId(
                        resultSet.getInt("product_size_id"));

                productSize.setProductId(
                        resultSet.getInt("product_id"));

                productSize.setProductCode(
                        resultSet.getString("product_code"));

                productSize.setProductName(
                        resultSet.getString("product_name"));

                productSize.setBrandName(
                        resultSet.getString("brand_name"));

                productSize.setCategoryName(
                        resultSet.getString("category_name"));

                productSize.setShoeSize(
                        resultSet.getString("shoe_size"));

                productSize.setQuantity(
                        resultSet.getInt("quantity"));

                productSize.setMinimumStock(
                        resultSet.getInt("minimum_stock"));

                productSizes.add(productSize);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not load stock records.", e);
        }

        return productSizes;
    }

    /**
     * Records a stock IN or OUT movement.
     * Stock quantity and movement history are updated atomically.
     */
    public void recordMovement(
            int productSizeId,
            String movementType,
            int quantity,
            String reason,
            String notes) {

        if (productSizeId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid product size.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        if (!"IN".equals(movementType)
                && !"OUT".equals(movementType)) {
            throw new IllegalArgumentException(
                    "Movement type must be IN or OUT.");
        }

        List<String> validReasons = Arrays.asList(
                "PURCHASE",
                "SOLD",
                "DAMAGED",
                "RETURN_TO_SUPPLIER",
                "OTHER"
        );

        if (reason == null || !validReasons.contains(reason)) {
            throw new IllegalArgumentException(
                    "Please select a valid movement reason.");
        }

        // A purchase adds stock; a sale removes stock.
        if ("IN".equals(movementType)
                && !("PURCHASE".equals(reason)
                || "OTHER".equals(reason))) {
            throw new IllegalArgumentException(
                    "Stock In supports PURCHASE or OTHER.");
        }

        if ("OUT".equals(movementType)
                && "PURCHASE".equals(reason)) {
            throw new IllegalArgumentException(
                    "PURCHASE is a Stock In reason.");
        }

        String lockSql =
                "SELECT quantity FROM product_sizes " +
                        "WHERE product_size_id = ? FOR UPDATE";

        String updateInSql =
                "UPDATE product_sizes " +
                        "SET quantity = quantity + ? " +
                        "WHERE product_size_id = ?";

        String updateOutSql =
                "UPDATE product_sizes " +
                        "SET quantity = quantity - ? " +
                        "WHERE product_size_id = ?";

        String insertSql =
                "INSERT INTO stock_movements " +
                        "(product_size_id, movement_type, quantity, " +
                        "reason, notes, movement_date) " +
                        "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            int currentQuantity;

            // Lock the selected size so concurrent movements
            // cannot change its quantity during this transaction.
            try (PreparedStatement statement =
                         connection.prepareStatement(lockSql)) {

                statement.setInt(1, productSizeId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        throw new IllegalArgumentException(
                                "Selected product size was not found.");
                    }

                    currentQuantity = resultSet.getInt("quantity");
                }
            }

            if ("OUT".equals(movementType)
                    && quantity > currentQuantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock. Available quantity: "
                                + currentQuantity);
            }

            String updateSql =
                    "IN".equals(movementType)
                            ? updateInSql
                            : updateOutSql;

            try (PreparedStatement statement =
                         connection.prepareStatement(updateSql)) {

                statement.setInt(1, quantity);
                statement.setInt(2, productSizeId);

                int updatedRows = statement.executeUpdate();

                if (updatedRows != 1) {
                    throw new SQLException(
                            "Could not update stock quantity.");
                }
            }

            // movement_id is an IDENTITY column.
            // PostgreSQL generates it automatically.
            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {

                statement.setInt(1, productSizeId);
                statement.setString(2, movementType);
                statement.setInt(3, quantity);
                statement.setString(4, reason);

                if (notes == null || notes.trim().isEmpty()) {
                    statement.setNull(
                            5, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(5, notes.trim());
                }

                statement.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {

            rollbackTransaction(connection, e);

            throw new RuntimeException(
                    "Could not record stock movement.", e);

        } catch (RuntimeException e) {

            rollbackTransaction(connection, e);

            throw e;

        } finally {

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // The connection is being closed.
                    e.printStackTrace();
                }
            }
        }
    }

    private void rollbackTransaction(
            Connection connection,
            Exception originalException) {

        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                originalException.addSuppressed(
                        rollbackException);
            }
        }
    }
}
