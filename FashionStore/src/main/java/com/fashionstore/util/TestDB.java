package com.fashionstore.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class TestDB {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String sql = "DESCRIBE product_variants";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("Column: " + rs.getString("Field"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
