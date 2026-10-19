
package com.footwear.service;

import com.footwear.dao.LowStockDAO;
import com.footwear.model.ProductSize;

import java.util.List;

public class LowStockService {

    private final LowStockDAO lowStockDAO;

    public LowStockService() {
        lowStockDAO = new LowStockDAO();
    }

    public List<ProductSize> getLowStockItems() {
        return lowStockDAO.getLowStockItems();
    }
}
