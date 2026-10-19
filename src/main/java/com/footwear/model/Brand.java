
package com.footwear.model;

public class Brand {

    private int brandId;
    private String brandName;
    private boolean active;

    public Brand() {
    }

    public Brand(int brandId, String brandName, boolean active) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.active = active;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
