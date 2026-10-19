
package com.footwear.service;

import com.footwear.dao.CategoryDAO;
import com.footwear.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        categoryDAO = new CategoryDAO();
    }

    public void addCategory(String categoryName) throws SQLException {
        validateCategoryName(categoryName);
        categoryDAO.addCategory(categoryName.trim());
    }

    public List<Category> getAllActiveCategories()
            throws SQLException {
        return categoryDAO.getAllActiveCategories();
    }

    public void updateCategory(int categoryId, String categoryName)
            throws SQLException {

        if (categoryId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid category.");
        }

        validateCategoryName(categoryName);
        categoryDAO.updateCategory(categoryId, categoryName.trim());
    }

    public void deactivateCategory(int categoryId)
            throws SQLException {

        if (categoryId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid category.");
        }

        categoryDAO.deactivateCategory(categoryId);
    }

    private void validateCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Category name cannot be empty.");
        }

        if (categoryName.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Category name cannot exceed 100 characters.");
        }
    }
}
