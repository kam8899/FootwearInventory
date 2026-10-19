
package com.footwear.service;

import com.footwear.dao.ProductDAO;
import com.footwear.model.Product;

import java.util.List;
import java.util.Map;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public boolean addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null.");
        }

        if (product.getProductCode() == null
                || product.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product code is required.");
        }

        if (product.getProductName() == null
                || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name is required.");
        }

        return productDAO.addProduct(product);
    }

    public Map<Integer, String> getActiveBrands() {
        return productDAO.getActiveBrands();
    }

    public Map<Integer, String> getActiveCategories() {
        return productDAO.getActiveCategories();
    }

    public boolean updateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (product.getProductCode() == null
                || product.getProductCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Product code is required.");
        }

        if (product.getProductName() == null
                || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }

        return productDAO.updateProduct(product);
    }
}
