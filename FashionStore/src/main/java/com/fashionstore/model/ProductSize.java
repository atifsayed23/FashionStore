package com.fashionstore.model;

public class ProductSize {

    private int sizeId;
    private int productId;
    private String size;
    private int stock;

    public ProductSize() {}

    public ProductSize(int sizeId, int productId, String size, int stock) {
        this.sizeId = sizeId;
        this.productId = productId;
        this.size = size;
        this.stock = stock;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}