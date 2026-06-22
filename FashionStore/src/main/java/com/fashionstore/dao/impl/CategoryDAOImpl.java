package com.fashionstore.dao.impl;

import java.sql.*;
import java.util.*;

import com.fashionstore.dao.CategoryDAO;
import com.fashionstore.model.Category;
import com.fashionstore.util.DBConnection;

public class CategoryDAOImpl implements CategoryDAO {

    private Connection conn = DBConnection.getConnection();

    @Override
    public boolean addCategory(Category category) {
        try {
            String sql = "INSERT INTO categories(name) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM categories";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Category getCategoryById(int id) {
        try {
            String sql = "SELECT * FROM categories WHERE category_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setName(rs.getString("name"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateCategory(Category category) {
        try {
            String sql = "UPDATE categories SET name=? WHERE category_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setInt(2, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteCategory(int id) {
        try {
            String sql = "DELETE FROM categories WHERE category_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}