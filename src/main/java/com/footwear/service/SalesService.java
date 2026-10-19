
package com.footwear.service;

import com.footwear.dao.ProductSizeDAO;
import com.footwear.dao.SalesDAO;

import java.util.List;

public class SalesService {

    private final ProductSizeDAO productSizeDAO;
    private final SalesDAO salesDAO;

    public SalesService() {
        productSizeDAO = new ProductSizeDAO();
        salesDAO = new SalesDAO();
    }

    public void recordSale(int productSizeId, int quantity, String notes)
            throws Exception {

        if (productSizeId <= 0) {
            throw new IllegalArgumentException("Please select a valid product.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity sold must be greater than zero.");
        }

        productSizeDAO.recordMovement(
                productSizeId,
                "OUT",
                quantity,
                "SOLD",
                notes
        );
    }

    public List<Object[]> getSalesHistory() throws Exception {
        return salesDAO.getSalesHistory();
    }
}
