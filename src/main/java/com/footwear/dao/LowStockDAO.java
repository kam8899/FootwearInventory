
package com.footwear.dao;

import com.footwear.model.ProductSize;

import java.util.ArrayList;
import java.util.List;

public class LowStockDAO {

    private final ProductSizeDAO productSizeDAO;

    public LowStockDAO() {
        productSizeDAO = new ProductSizeDAO();
    }

    public List<ProductSize> getLowStockItems() {

        List<ProductSize> allStock =
                productSizeDAO.getAllProductSizes();

        List<ProductSize> lowStockItems =
                new ArrayList<ProductSize>();

        for (ProductSize item : allStock) {
            if (item.getQuantity() <= item.getMinimumStock()) {
                lowStockItems.add(item);
            }
        }

        return lowStockItems;
    }
}
