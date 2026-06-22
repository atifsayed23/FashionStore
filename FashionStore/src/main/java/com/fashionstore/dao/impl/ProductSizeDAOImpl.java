package com.fashionstore.dao.impl;

import java.sql.*;
import java.util.*;

import com.fashionstore.dao.ProductSizeDAO;
import com.fashionstore.model.ProductSize;
import com.fashionstore.util.DBConnection;

public class ProductSizeDAOImpl implements ProductSizeDAO {

    private Connection conn = DBConnection.getConnection();

    @Override
    public boolean addProductSize(ProductSize size) {
        try {
            String sql = "INSERT INTO product_sizes(product_id, size, stock) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, size.getProductId());
            ps.setString(2, size.getSize());
            ps.setInt(3, size.getStock());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ProductSize> getSizesByProductId(int productId) {
        List<ProductSize> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM product_sizes WHERE product_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductSize s = new ProductSize();
                s.setSizeId(rs.getInt("size_id"));
                s.setProductId(rs.getInt("product_id"));
                s.setSize(rs.getString("size"));
                s.setStock(rs.getInt("stock"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateProductSize(ProductSize size) {
        try {
            String sql = "UPDATE product_sizes SET size=?, stock=? WHERE size_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, size.getSize());
            ps.setInt(2, size.getStock());
            ps.setInt(3, size.getSizeId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProductSize(int sizeId) {
        try {
            String sql = "DELETE FROM product_sizes WHERE size_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sizeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ProductSize getSizeById(int sizeId) {
        try {
            String sql = "SELECT * FROM product_sizes WHERE size_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sizeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ProductSize s = new ProductSize();
                s.setSizeId(rs.getInt("size_id"));
                s.setProductId(rs.getInt("product_id"));
                s.setSize(rs.getString("size"));
                s.setStock(rs.getInt("stock"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public boolean reduceStock(int sizeId, int quantity) {
        try {
            String sql = "UPDATE product_sizes SET stock = stock - ? WHERE size_id=? AND stock >= ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, sizeId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}