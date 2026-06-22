<%@ page import="java.sql.*, com.fashionstore.util.DBConnection" %>
<%
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement()) {
        
        String alterSql = "ALTER TABLE orders ADD COLUMN order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        stmt.executeUpdate(alterSql);
        out.println("SUCCESS_ORDER_DATE_ADDED");
        
    } catch (Exception e) {
        out.println("ERROR: " + e.getMessage());
        e.printStackTrace();
    }
%>
