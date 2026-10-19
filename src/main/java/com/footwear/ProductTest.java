package com.footwear;

import com.footwear.model.Product;

public class ProductTest {

    public static void main(String[] args) {

        Product product = new Product();

        product.setProductCode("CH-001");
        product.setProductName("Men's Casual Slipper");
        product.setBrandId(1);
        product.setCategoryId(1);
        product.setActive(true);

        System.out.println("Product Code: "
                + product.getProductCode());

        System.out.println("Product Name: "
                + product.getProductName());

        System.out.println("Brand ID: "
                + product.getBrandId());

        System.out.println("Category ID: "
                + product.getCategoryId());

        System.out.println("Active: "
                + product.isActive());
    }
}

