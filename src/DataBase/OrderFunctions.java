package DataBase;
import FileHandling.CredentialsInfo;

import java.sql.*;
import java.util.Map;

public class OrderFunctions {

    String URL = "jdbc:mysql://localhost:3306/InventoryManagement";
    final static String USER = CredentialsInfo.credentials.getFirst();
    final static String PASSWORD = CredentialsInfo.credentials.get(1);
    // Function to calculate total price and validate stock
    public double calculateTotalAmountAndCheckStock(Connection connection, Map<Integer, Integer> productQuantities) throws SQLException {
        double totalAmount = 0.0;
        String checkStockSQL = "SELECT price, quantity FROM products WHERE product_id = ?";

        try (PreparedStatement checkStockStmt = connection.prepareStatement(checkStockSQL)) {
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                checkStockStmt.setInt(1, productId);
                ResultSet rs = checkStockStmt.executeQuery();
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    int availableQuantity = rs.getInt("quantity");

                    if (availableQuantity < quantity) {
                        return -1; // Error condition: insufficient stock
                    }
                    totalAmount += price * quantity;
                } else {
                    return -1; // Error condition: product not found
                }
            }
        }
        return totalAmount;
    }

    // Function to insert a new order and return the order ID
    public int insertOrder(Connection connection, int customerId, double totalAmount) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";

        try (PreparedStatement orderStmt = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            orderStmt.setInt(1, customerId);
            orderStmt.setDouble(2, totalAmount);
            orderStmt.executeUpdate();

            ResultSet orderKeys = orderStmt.getGeneratedKeys();
            if (orderKeys.next()) {
                return orderKeys.getInt(1); // Return the generated order ID
            } else {
                return -1; // Error condition: failed to insert order
            }
        }
    }

    // Function to insert order items and update the product inventory
    public boolean insertOrderItems(Connection connection, int orderId, Map<Integer, Integer> productQuantities) throws SQLException {
        String insertOrderItemSQL = "INSERT INTO order_items (order_id, product_id, quantity, price_at_order) VALUES (?, ?, ?, ?)";
        String updateProductSQL = "UPDATE products SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";

        try (PreparedStatement orderItemStmt = connection.prepareStatement(insertOrderItemSQL);
             PreparedStatement updateProductStmt = connection.prepareStatement(updateProductSQL)) {

            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();

                String getProductPriceSQL = "SELECT price FROM products WHERE product_id = ?";
                try (PreparedStatement productPriceStmt = connection.prepareStatement(getProductPriceSQL)) {
                    productPriceStmt.setInt(1, productId);
                    ResultSet rs = productPriceStmt.executeQuery();
                    if (rs.next()) {
                        double priceAtOrder = rs.getDouble("price");

                        // Insert order item
                        orderItemStmt.setInt(1, orderId);
                        orderItemStmt.setInt(2, productId);
                        orderItemStmt.setInt(3, quantity);
                        orderItemStmt.setDouble(4, priceAtOrder);
                        orderItemStmt.executeUpdate();

                        // Update product inventory
                        updateProductStmt.setInt(1, quantity);
                        updateProductStmt.setInt(2, productId);
                        updateProductStmt.setInt(3, quantity);
                        updateProductStmt.executeUpdate();
                    }
                }
            }
            return true;
        }
    }

    // Function to place an order
    public boolean placeOrder(int customerId, Map<Integer, Integer> productQuantities) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);

        try {
            double totalAmount = calculateTotalAmountAndCheckStock(connection, productQuantities);
            if (totalAmount < 0) return false;

            int orderId = insertOrder(connection, customerId, totalAmount);
            if (orderId <= 0) {
                connection.rollback();
                return false;
            }

            boolean success = insertOrderItems(connection, orderId, productQuantities);
            if (!success) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e; // Re-throw the exception to notify the frontend
        } finally {
            connection.close();
        }
    }
}

