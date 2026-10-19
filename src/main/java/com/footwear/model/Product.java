
package com.footwear.model;

import java.sql.Timestamp;

public class Product {

    private int productId;
    private String productCode;
    private String productName;
    private int brandId;
    private int categoryId;
    private String imagePath;
    private String description;
    private boolean active;
    private Timestamp createdAt;
    private String brandName;
    private String categoryName;

    // Default constructor
    public Product() {
    }

    // Parameterized constructor
    public Product(int productId, String productCode,
                   String productName, int brandId,
                   int categoryId, String imagePath,
                   String description, boolean active,
                   Timestamp createdAt) {

        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.imagePath = imagePath;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
