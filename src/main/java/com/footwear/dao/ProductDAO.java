
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;
import com.footwear.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {

    // Add a new product
    public boolean addProduct(Product product) {

        String sql = "INSERT INTO products " +
                "(product_code, product_name, brand_id, category_id, " +
                "image_path, description, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getProductCode());
            statement.setString(2, product.getProductName());
            statement.setInt(3, product.getBrandId());
            statement.setInt(4, product.getCategoryId());
            statement.setString(5, product.getImagePath());
            statement.setString(6, product.getDescription());
            statement.setBoolean(7, product.isActive());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setProductId(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get all products
    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.product_id, p.product_code, " +
                "p.product_name, p.brand_id, b.brand_name, " +
                "p.category_id, c.category_name, p.image_path, " +
                "p.description, p.active, p.created_at " +
                "FROM products p " +
                "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "ORDER BY p.product_id";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Product product = new Product();

                product.setProductId(
                        resultSet.getInt("product_id"));

                product.setProductCode(
                        resultSet.getString("product_code"));

                product.setProductName(
                        resultSet.getString("product_name"));

                product.setBrandId(
                        resultSet.getInt("brand_id"));

                product.setCategoryId(
                        resultSet.getInt("category_id"));

                product.setBrandName(
                        resultSet.getString("brand_name"));

                product.setCategoryName(
                        resultSet.getString("category_name"));

                product.setImagePath(
                        resultSet.getString("image_path"));

                product.setDescription(
                        resultSet.getString("description"));

                product.setActive(
                        resultSet.getBoolean("active"));

                Timestamp createdAt =
                        resultSet.getTimestamp("created_at");

                product.setCreatedAt(createdAt);

                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // Get active brands for the Add Product dropdown
    public Map<Integer, String> getActiveBrands() {

        Map<Integer, String> brands = new LinkedHashMap<>();

        String sql = "SELECT brand_id, brand_name " +
                "FROM brands " +
                "WHERE active = TRUE " +
                "ORDER BY brand_name";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                brands.put(
                        resultSet.getInt("brand_id"),
                        resultSet.getString("brand_name")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not load active brands.", e);
        }

        return brands;
    }

    // Get active categories for the Add Product dropdown
    public Map<Integer, String> getActiveCategories() {

        Map<Integer, String> categories = new LinkedHashMap<>();

        String sql = "SELECT category_id, category_name " +
                "FROM categories " +
                "WHERE active = TRUE " +
                "ORDER BY category_name";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.put(
                        resultSet.getInt("category_id"),
                        resultSet.getString("category_name")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not load active categories.", e);
        }

        return categories;
    }


    public boolean updateProduct(Product product) {

        String sql = "UPDATE products SET " +
                "product_code = ?, " +
                "product_name = ?, " +
                "brand_id = ?, " +
                "category_id = ?, " +
                "image_path = ?, " +
                "description = ?, " +
                "active = ? " +
                "WHERE product_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, product.getProductCode());
            statement.setString(2, product.getProductName());
            statement.setInt(3, product.getBrandId());
            statement.setInt(4, product.getCategoryId());
            statement.setString(5, product.getImagePath());
            statement.setString(6, product.getDescription());
            statement.setBoolean(7, product.isActive());
            statement.setInt(8, product.getProductId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not update product.", e);
        }
    }

}
