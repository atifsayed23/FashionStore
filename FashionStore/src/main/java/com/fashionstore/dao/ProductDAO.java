package com.fashionstore.dao;

import java.util.List;
import com.fashionstore.model.Product;

public interface ProductDAO {

    boolean addProduct(Product product);

    boolean updateProduct(Product product);

    boolean deleteProduct(int productId);

    Product getProductById(int productId);

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(int categoryId);

    List<Product> getProductsByBrand(String brand);

    List<Product> searchProducts(String keyword);

    List<Product> getProductsByPriceRange(double minPrice, double maxPrice);

    List<Product> getProductsWithPagination(int offset, int limit);

	List<Product> getFeaturedProducts(int i);

	boolean isProductInStock(int productId);

	List<Product> filterByCategoryAndPrice(int categoryId, double minPrice, double maxPrice);

	List<Product> filterByPrice(double minPrice, double maxPrice);
}