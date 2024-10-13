package FileHandling;

import Main.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ProductsInfo {

    public static void writeProductsToFile(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Products"))) {

            // Write the header
            writer.write("Product ID | Name           | Category   | Price   | Quantity | Reorder Threshold | Created At           | Updated At          ");
            writer.newLine();
            writer.write("------------------------------------------------------------------------------------------------------------");
            writer.newLine();

            // Write each product's details
            for (Product product : products) {
                String line = String.format("%-11d | %-14s | %-10s | %-7.2f | %-8d | %-17d | %-19s | %-19s",
                        product.getProductId(), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity(),
                        product.getReorderThreshold(), product.getCreatedAt(), product.getUpdatedAt());
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Products exported successfully to " + "Products.txt");

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}
