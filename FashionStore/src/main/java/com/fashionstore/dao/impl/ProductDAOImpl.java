package com.fashionstore.dao.impl;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.model.Product;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    private Connection conn;

    public ProductDAOImpl() {
        conn = DBConnection.getConnection();
    }

    // =========================
    // ADD PRODUCT
    // =========================
    @Override
    public boolean addProduct(Product product) {

        try {
            String sql = "INSERT INTO products(name, description, price, category_id, brand, image_url) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getCategoryId());
            ps.setString(5, product.getBrand());
            ps.setString(6, product.getImageUrl());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // UPDATE PRODUCT
    // =========================
    @Override
    public boolean updateProduct(Product product) {

        try {
            String sql = "UPDATE products SET name=?, description=?, price=?, category_id=?, brand=?, image_url=? WHERE product_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getCategoryId());
            ps.setString(5, product.getBrand());
            ps.setString(6, product.getImageUrl());
            ps.setInt(7, product.getProductId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // DELETE PRODUCT
    // =========================
    @Override
    public boolean deleteProduct(int productId) {

        try {
            String sql = "DELETE FROM products WHERE product_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================
    @Override
    public Product getProductById(int productId) {

        try {
            String sql = "SELECT * FROM products WHERE product_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapProduct(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    @Override
    public List<Product> getAllProducts() {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // CATEGORY FILTER
    // =========================
    @Override
    public List<Product> getProductsByCategory(int categoryId) {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products WHERE category_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // BRAND FILTER
    // =========================
    @Override
    public List<Product> getProductsByBrand(String brand) {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products WHERE brand=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, brand);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // SEARCH PRODUCTS
    // =========================
    @Override
    public List<Product> searchProducts(String keyword) {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // PRICE RANGE FILTER
    // =========================
    @Override
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // PAGINATION
    // =========================
    @Override
    public List<Product> getProductsWithPagination(int offset, int limit) {

        List<Product> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM products LIMIT ? OFFSET ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // COMMON MAPPER METHOD
    // =========================
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setBrand(rs.getString("brand"));
        p.setImageUrl(rs.getString("image_url"));
        try {
            p.setStock(rs.getInt("stock"));
        } catch (Exception e) {
            p.setStock(100);
        }
        try {
            p.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            p.setCreatedAt(null);
        }
        return p;
    }
	@Override
	public List<Product> getFeaturedProducts(int limit) {
		List<Product> list = new ArrayList<>();
		try {
			String sql = "SELECT * FROM products LIMIT ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, limit);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(mapProduct(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean isProductInStock(int productId) {
		try {
			String sql = "SELECT SUM(stock) FROM product_sizes WHERE product_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Product> filterByCategoryAndPrice(int categoryId, double minPrice, double maxPrice) {
		List<Product> list = new ArrayList<>();
		try {
			String sql = "SELECT * FROM products WHERE category_id=? AND price BETWEEN ? AND ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, categoryId);
			ps.setDouble(2, minPrice);
			ps.setDouble(3, maxPrice);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(mapProduct(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Product> filterByPrice(double minPrice, double maxPrice) {
		return getProductsByPriceRange(minPrice, maxPrice);
	}
}