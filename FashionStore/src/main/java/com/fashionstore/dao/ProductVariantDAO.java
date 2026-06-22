package com.fashionstore.dao;

import com.fashionstore.model.ProductVariant;
import java.util.List;

public interface ProductVariantDAO {
    List<ProductVariant> getVariantsByProductId(int productId);
    ProductVariant getVariantById(int variantId);
    List<ProductVariant> getVariantsBySize(int productId, String size);
    List<ProductVariant> getVariantsByColor(int productId, String color);
    boolean isVariantInStock(int variantId);
    boolean updateVariantStock(int variantId, int quantity);
    List<String> getAvailableSizes(int productId);
    List<String> getAvailableColors(int productId);
}