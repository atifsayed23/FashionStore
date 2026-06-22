package com.fashionstore.dao.impl;

import com.fashionstore.dao.ProductVariantDAO;
import com.fashionstore.model.ProductVariant;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.*;

public class ProductVariantDAOImpl implements ProductVariantDAO {

    @Override
    public List<ProductVariant> getVariantsByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapVariant(rs));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT * FROM product_variants WHERE variant_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapVariant(rs);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ProductVariant> getVariantsBySize(int productId, String size) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND size = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, size);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapVariant(rs));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ProductVariant> getVariantsByColor(int productId, String color) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND color = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, color);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapVariant(rs));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean isVariantInStock(int variantId) {
        String sql = "SELECT stock FROM product_variants WHERE variant_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stock") > 0;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateVariantStock(int variantId, int quantity) {
        String sql = "UPDATE product_variants SET stock = stock - ? WHERE variant_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, variantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAvailableSizes(int productId) {
        List<String> sizes = new ArrayList<>();
        String sql = "SELECT DISTINCT size FROM product_variants WHERE product_id = ? AND stock > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) sizes.add(rs.getString("size"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return sizes;
    }

    @Override
    public List<String> getAvailableColors(int productId) {
        List<String> colors = new ArrayList<>();
        String sql = "SELECT DISTINCT color FROM product_variants WHERE product_id = ? AND stock > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) colors.add(rs.getString("color"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return colors;
    }

    private ProductVariant mapVariant(ResultSet rs) throws SQLException {
        ProductVariant v = new ProductVariant();
        v.setVariantId(rs.getInt("variant_id"));
        v.setProductId(rs.getInt("product_id"));
        v.setSize(rs.getString("size"));
        v.setColor(rs.getString("color"));
        v.setStock(rs.getInt("stock"));
        return v;
    }
}