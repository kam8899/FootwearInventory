
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;
import com.footwear.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void addCategory(String categoryName) throws SQLException {
        String sql =
                "INSERT INTO categories (category_name, active) " +
                        "VALUES (?, TRUE)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, categoryName);
            statement.executeUpdate();
        }
    }

    public List<Category> getAllActiveCategories()
            throws SQLException {

        String sql =
                "SELECT category_id, category_name, active " +
                        "FROM categories " +
                        "WHERE active = TRUE " +
                        "ORDER BY category_name";

        List<Category> categories = new ArrayList<Category>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();

                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                category.setActive(rs.getBoolean("active"));

                categories.add(category);
            }
        }

        return categories;
    }

    public void updateCategory(int categoryId, String categoryName)
            throws SQLException {

        String sql =
                "UPDATE categories " +
                        "SET category_name = ? " +
                        "WHERE category_id = ? AND active = TRUE";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, categoryName);
            statement.setInt(2, categoryId);

            int updated = statement.executeUpdate();

            if (updated == 0) {
                throw new SQLException(
                        "Category not found or already deactivated.");
            }
        }
    }

    public void deactivateCategory(int categoryId)
            throws SQLException {

        String sql =
                "UPDATE categories " +
                        "SET active = FALSE " +
                        "WHERE category_id = ? AND active = TRUE";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            int updated = statement.executeUpdate();

            if (updated == 0) {
                throw new SQLException(
                        "Category not found or already deactivated.");
            }
        }
    }
}
