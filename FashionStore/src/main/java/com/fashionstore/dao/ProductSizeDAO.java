package com.fashionstore.dao;

import java.util.List;
import com.fashionstore.model.ProductSize;

public interface ProductSizeDAO {

    boolean addProductSize(ProductSize size);

    boolean updateProductSize(ProductSize size);

    boolean deleteProductSize(int sizeId);

    ProductSize getSizeById(int sizeId);

    List<ProductSize> getSizesByProductId(int productId);

    boolean reduceStock(int sizeId, int quantity);
}