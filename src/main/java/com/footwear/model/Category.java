
package com.footwear.model;

public class Category {

    private int categoryId;
    private String categoryName;
    private boolean active;

    public Category() {
    }

    public Category(int categoryId, String categoryName, boolean active) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.active = active;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
