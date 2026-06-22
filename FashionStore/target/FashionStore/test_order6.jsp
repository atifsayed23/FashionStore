<%@ page import="java.sql.*, java.io.PrintWriter, com.fashionstore.util.DBConnection" %>
<%
    response.setContentType("application/json");
    PrintWriter outWriter = response.getWriter();
    outWriter.println("[");
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement()) {
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM order_items WHERE order_id=6");
        boolean first = true;
        while (rs.next()) {
            if (!first) outWriter.println(",");
            first = false;
            outWriter.println("{ \"order_id\": " + rs.getInt("order_id") + ", \"variant_id\": " + rs.getInt("variant_id") + "}");
        }
        
    } catch (Exception e) {
        outWriter.println("{\"error\": \"" + e.getMessage() + "\"}");
    }
    outWriter.println("]");
%>
