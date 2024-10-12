package Main;

import DataBase.OrderFunctions;
import FileHandling.LogDetails;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OrderManagement {

    private final OrderFunctions orderService;

    public OrderManagement() {
        orderService = new OrderFunctions(); // Backend service object
    }

    // Function to interact with the user and place an order
    public void placeOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Customer ID:");
        int customerId = scanner.nextInt();

        Map<Integer, Integer> productQuantities = new HashMap<>();

        // Input loop for adding products to the order
        while (true) {
            System.out.println("Enter Product ID (or 0 to stop):");
            int productId = scanner.nextInt();
            if (productId == 0) break;

            System.out.println("Enter quantity:");
            int quantity = scanner.nextInt();

            productQuantities.put(productId, quantity);
        }

        // Call the backend to place the order
        try {
            boolean orderPlaced = orderService.placeOrder(customerId, productQuantities);
            if (orderPlaced) {
                LogDetails.log("New order has been placed");
                System.out.println("Order placed successfully!");
            } else {
                System.out.println("Order failed. Check stock or product details.");
            }
        } catch (SQLException e) {
            System.out.println("Error placing order: customer or product is not registered.");
        }
    }

    // Function to display the order result
    public void displayOrderResult(boolean orderPlaced) {
        if (orderPlaced) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println("Order failed.");
        }
    }

}
