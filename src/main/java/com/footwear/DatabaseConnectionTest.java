
package com.footwear;

import com.footwear.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        String sql = "SELECT brand_id, brand_name FROM brands ORDER BY brand_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Brands available in the database:");

            while (resultSet.next()) {
                System.out.println(
                        resultSet.getInt("brand_id") + " - " +
                                resultSet.getString("brand_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
