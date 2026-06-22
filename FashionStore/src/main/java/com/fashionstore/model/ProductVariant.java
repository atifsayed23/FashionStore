package com.fashionstore.model;

public class ProductVariant {

    private int variantId;
    private int productId;
    private String size;
    private String color;
    private double extraPrice;
    private int stock;

    public ProductVariant() {}

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getExtraPrice() { return extraPrice; }
    public void setExtraPrice(double extraPrice) { this.extraPrice = extraPrice; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}