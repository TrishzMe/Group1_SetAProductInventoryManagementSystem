package pckExer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Inventory {
    private Connection conn;
    private List<Product> products; 

    public Inventory() {
        this.products = new ArrayList<>();
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS inventory_db");
            stmt.executeUpdate("USE inventory_db");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (productId INT PRIMARY KEY, productName VARCHAR(100), price DOUBLE, quantity INT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) PRIMARY KEY, password_hash VARCHAR(64))");
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) stmt.executeUpdate("INSERT INTO users VALUES ('admin', SHA2('123', 256))");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Error!");
            System.exit(1);
        }
    }

    public void addProduct(Product product) throws Exception {
        String sql = "INSERT INTO products VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.executeUpdate();
        }
    }

    public void updateProductPrice(int productId, double newPrice) throws Exception {
        String sql = "UPDATE products SET price = ? WHERE productId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, productId);
            if (pstmt.executeUpdate() == 0) throw new Exception("ID not found.");
        }
    }

    public void updateProductQuantity(int productId, int quantity) throws Exception {
        String sql = "UPDATE products SET quantity = ? WHERE productId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            if (pstmt.executeUpdate() == 0) throw new Exception("ID not found.");
        }
    }
    
    public void deleteProduct(int productId) throws Exception {
        String sql = "DELETE FROM products WHERE productId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            if (pstmt.executeUpdate() == 0) throw new Exception("ID not found.");
        }
    }
    
    public String displayProducts() {
        refreshLocalList();
        StringBuilder report = new StringBuilder("--- ALL PRODUCTS ---\n");
        for (Product p : products) {
            report.append(p.toString()).append("\n");
        }
        return report.toString();
    }

    public void refreshLocalList() {
        products.clear();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) products.add(new Product(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<Product> getProductsList() { refreshLocalList(); return products; }
    
    public Product getProduct(int id) throws Exception {
        String sql = "SELECT * FROM products WHERE productId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new Product(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4));
            throw new Exception("Product not found.");
        }
    }

    public boolean authenticateManager(String user, String pass) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = SHA2(?, 256)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user); pstmt.setString(2, pass);
            return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }
}