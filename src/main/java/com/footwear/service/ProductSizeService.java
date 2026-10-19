
package com.footwear.service;

import com.footwear.dao.ProductSizeDAO;
import com.footwear.model.ProductSize;

import java.util.List;

public class ProductSizeService {

    private final ProductSizeDAO productSizeDAO;

    public ProductSizeService() {
        productSizeDAO = new ProductSizeDAO();
    }

    public List<ProductSize> getAllProductSizes() {
        return productSizeDAO.getAllProductSizes();
    }

    public void recordMovement(
            int productSizeId,
            String movementType,
            int quantity,
            String reason,
            String notes) {

        productSizeDAO.recordMovement(
                productSizeId,
                movementType,
                quantity,
                reason,
                notes);
    }
}
