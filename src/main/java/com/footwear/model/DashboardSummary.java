
package com.footwear.model;

public class DashboardSummary {

    private int totalProducts;
    private int totalBrands;
    private long totalStock;
    private int lowStockItems;

    public DashboardSummary(int totalProducts, int totalBrands,
                            long totalStock, int lowStockItems) {
        this.totalProducts = totalProducts;
        this.totalBrands = totalBrands;
        this.totalStock = totalStock;
        this.lowStockItems = lowStockItems;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalBrands() {
        return totalBrands;
    }

    public long getTotalStock() {
        return totalStock;
    }

    public int getLowStockItems() {
        return lowStockItems;
    }
}
