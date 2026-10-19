
package com.footwear.service;

import com.footwear.dao.BrandDAO;
import com.footwear.model.Brand;

import java.util.List;

public class BrandService {

    private final BrandDAO brandDAO;

    public BrandService() {
        brandDAO = new BrandDAO();
    }

    public void addBrand(String brandName) {
        validateBrandName(brandName);
        brandDAO.addBrand(brandName.trim());
    }

    public List<Brand> getAllActiveBrands() {
        return brandDAO.getAllActiveBrands();
    }

    public void updateBrand(Brand brand) {
        if (brand == null || brand.getBrandId() <= 0) {
            throw new IllegalArgumentException("Invalid brand selected.");
        }

        validateBrandName(brand.getBrandName());
        brandDAO.updateBrand(brand);
    }

    public void deactivateBrand(int brandId) {
        if (brandId <= 0) {
            throw new IllegalArgumentException("Invalid brand selected.");
        }

        brandDAO.deactivateBrand(brandId);
    }

    private void validateBrandName(String brandName) {
        if (brandName == null || brandName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Brand name cannot be empty.");
        }

        if (brandName.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Brand name cannot exceed 100 characters.");
        }
    }
}
