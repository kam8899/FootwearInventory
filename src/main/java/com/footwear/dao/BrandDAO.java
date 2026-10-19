
package com.footwear.dao;

import com.footwear.config.DatabaseConnection;
import com.footwear.model.Brand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public void addBrand(String brandName) {
        String sql =
                "INSERT INTO brands (brand_name, active) VALUES (?, TRUE)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, brandName.trim());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    getDatabaseErrorMessage(e), e);
        }
    }

    public List<Brand> getAllActiveBrands() {
        List<Brand> brands = new ArrayList<Brand>();

        String sql =
                "SELECT brand_id, brand_name, active " +
                        "FROM brands WHERE active = TRUE " +
                        "ORDER BY brand_name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Brand brand = new Brand(
                        resultSet.getInt("brand_id"),
                        resultSet.getString("brand_name"),
                        resultSet.getBoolean("active")
                );

                brands.add(brand);
            }

            return brands;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to retrieve brands: " + e.getMessage(), e);
        }
    }

    public void updateBrand(Brand brand) {
        String sql =
                "UPDATE brands SET brand_name = ? " +
                        "WHERE brand_id = ? AND active = TRUE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, brand.getBrandName().trim());
            statement.setInt(2, brand.getBrandId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException(
                        "Brand not found or already inactive.");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    getDatabaseErrorMessage(e), e);
        }
    }

    public void deactivateBrand(int brandId) {
        String sql =
                "UPDATE brands SET active = FALSE " +
                        "WHERE brand_id = ? AND active = TRUE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, brandId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException(
                        "Brand not found or already inactive.");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to deactivate brand: " + e.getMessage(), e);
        }
    }

    private String getDatabaseErrorMessage(SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return "This brand name already exists.";
        }

        if ("23503".equals(e.getSQLState())) {
            return "This brand is referenced by other records.";
        }

        return "Database operation failed: " + e.getMessage();
    }
}
