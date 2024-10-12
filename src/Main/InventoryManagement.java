package Main;

import DataBase.InventoryFunctions;
import FileHandling.LogDetails;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.stream.Stream;

public class InventoryManagement {

    public static void startMenu(){

        Scanner sc = new Scanner(System.in);
        int menuOption;

        while(true){
            System.out.println("\n\nSelect an option from menu");
            System.out.println("1.Add new product");
            System.out.println("2.Update product");
            System.out.println("3.Delete product");
            System.out.println("4.View products");
            System.out.println("5.Add quantity to existing products");

            try {
                menuOption = sc.nextInt();
                sc.nextLine();
            }catch (Exception e){
                sc.nextLine();
                System.out.println("Wrong input. try again");
                continue;
            }
            if(menuOption <= 0){
                break;
            }

            switch (menuOption){
                case 1 :
                    addProduct();
                    break;
                case 2 :
                    updateProduct();
                    break;
                case 3:
                    deleteProduct();
                    break;
                case 4:
                    viewProducts();
                    break;
                case 5:
                    addQuantityToProducts();
                    break;
                default:
                    System.out.println("Enter an option from menu only");
            }
        }
    }

    private static void addQuantityToProducts() {
        Scanner sc = new Scanner(System.in);
        try{
            System.out.println("Enter product id");
            int productId = sc.nextInt();
            System.out.println("Enter new Quantity to be added : ");
            int quantity = sc.nextInt();
            sc.nextLine();
            try {
                InventoryFunctions.addQuantityToProduct(productId, quantity);
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }catch (RuntimeException re){
            System.out.println(re.getMessage());
        }
    }

    public static void addProduct(){

        Scanner sc= new Scanner(System.in);

        String newProductName;
        String newProductCategory;
        double newProductPrice;
        int newProductQuantity;
        int newProductReOrderThreshold;
        //user input from console
        try {
            System.out.println("Enter product name");
            newProductName = sc.nextLine();
            System.out.println("Enter category of the product");
            newProductCategory = sc.next();
            System.out.println("Enter the price of the product");
            newProductPrice = sc.nextDouble();
            System.out.println("Enter the initial quantity of the product");
            newProductQuantity = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter the reorder threshold of the product");
            newProductReOrderThreshold = sc.nextInt();
            sc.nextLine();

            //trying to add data to the database.
            InventoryFunctions.addNewProduct(newProductName, newProductCategory, newProductPrice, newProductQuantity, newProductReOrderThreshold);
            LogDetails.log("New product has been added into the data base with product name " + newProductName + ", and category as "+ newProductCategory);

            System.out.println("Successfully added the product.");
        }catch (RuntimeException re){

            LogDetails.log("Error while adding new product");
            System.out.println("\nThere is an error in the input type, try again\n");
        }
        catch (SQLException e){

            LogDetails.log("Error while adding new product");
            System.out.println("\ncannot add product there is an error in the input\n");
        }catch (Exception e){
            LogDetails.log("Error while adding new product");
            System.out.println("\nWrong input. try again\n");
        }

    }

    public static void updateProduct(){

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter product id you want to update");
            int productId = sc.nextInt();
            sc.nextLine();
            System.out.println("Select the attribute you want to update");
            System.out.println(" 1.Name             2.Category \n 3.Price             4.Quantity \n 5.Reorder threshold");
            int menuOption;
            try {
                menuOption = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                sc.nextLine();
                System.out.println("\n Wrong input try again\n");
                return;
            }

            String column = switch (menuOption) {
                case 1 -> "name";
                case 2 -> "category";
                case 3 -> "price";
                case 4 -> "quantity";
                case 5 -> "reorder_threshold";
                default -> throw new RuntimeException("\nUnexpected value: " + menuOption + "\n");
            };


            System.out.println("Enter new " + column + " value : ");
            String newValue = sc.nextLine();
            try {
                InventoryFunctions.updateProductById(productId, column, newValue);
                LogDetails.log("product with product id " + productId + " has been updated with new " + column);
                System.out.println("\n Successfully Update the details.\n");

            } catch (RuntimeException re) {

                System.out.println("\nThere is an error in the input\n");
            } catch (SQLException se) {

                System.out.println("\nThere is an error in the sql syntax " + se.getMessage() + "\n");
            }

        }catch (Exception e){

            System.out.println("\nWrong input. try again\n");
        }
    }

    private static void deleteProduct() {

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the product id you want to delete");
            int productId = sc.nextInt();
            sc.nextLine();

            try {
                InventoryFunctions.deleteProductById(productId);
                LogDetails.log("product with product id " + productId + " was deleted");
                System.out.println("Successfully deleted product with product id : " + productId);
            } catch (SQLException se) {

                System.out.println(se.getMessage());
            } catch (RuntimeException re) {

                System.out.println("There is no product with given product id, try again.");
            }
        }catch (Exception e){

            System.out.println("\nWrong input. try again\n");
        }
    }

    private static void viewProducts() {

        Scanner sc = new Scanner(System.in);
        while(true) {
        System.out.println("\n\nSelect an option from menu\n");
        System.out.println("1.Category wise products");
        System.out.println("2.Products in a price range");
        System.out.println("3.Out of stock products");
        System.out.println("4.List all categories");
        System.out.println("5.View all products");

        int menuOption;

            try {
                menuOption = sc.nextInt();
                sc.nextLine();
            }catch (Exception e){
                sc.nextLine();
                System.out.println("Wrong input. try again");
                continue;
            }
            if(menuOption <= 0){
                break;
            }


            switch (menuOption) {

                case 1:
                    filterProductsByCategory();
                    break;
                case 2:
                    viewProductsByPriceRange();
                    break;
                case 3:
                    viewOutOfStockProducts();
                    break;
                case 4:
                    viewAllCategories();
                    break;
                case 5:
                    try {
                        searchProducts();
                    } catch (RuntimeException re) {
                        System.out.println("\nWrong input. try again");
                    }
                    break;

                default:
                    throw new RuntimeException();
            }
        }
    }

    private static void filterProductsByCategory() {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the category you want");
        String category = sc.nextLine();
        try (Stream<Product> productStream = InventoryFunctions.getProductsByCategory(category).stream()) {

            // Print table header, rows, and footer in a single function
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
            System.out.printf("| %-10s | %-15s | %-12s | %-10s | %-8s | %-15s | %-19s | %-19s |%n",
                    "Product ID", "Name", "Category", "Price", "Quantity", "Reorder Threshold", "Created At", "Updated At");
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");

            // Print each product row
            productStream.forEach(product -> System.out.printf("| %-10d | %-15s | %-12s | %-10.2f | %-8d | %-15d | %-19s | %-19s |%n",
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getReorderThreshold(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()));

            // Print table footer
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
        }catch (SQLException se){
            System.out.println("there is an exception");
        }catch (RuntimeException re){
            System.out.println("\n No products available in the given category. \n");
        }
    }

    private static void viewProductsByPriceRange() {

        Scanner sc = new Scanner(System.in);

        double minimumAmount;
        double maximumAmount;
        try {
            System.out.println("Enter the minimum amount : ");
            minimumAmount = sc.nextDouble();
            System.out.println("Enter the maximum amount : ");
            maximumAmount = sc.nextDouble();
        }catch (Exception e){

            sc.nextLine();
            System.out.println("\nWrong input. try again\n");
            return;
        }

        ;
        try (Stream<Product> productStream = InventoryFunctions.getProductsByPriceRange(minimumAmount, maximumAmount).stream()) {

            // Print table header, rows, and footer in a single function
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
            System.out.printf("| %-10s | %-15s | %-12s | %-10s | %-8s | %-15s | %-19s | %-19s |%n",
                    "Product ID", "Name", "Category", "Price", "Quantity", "Reorder Threshold", "Created At", "Updated At");
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");

            // Print each product row
            productStream.forEach(product -> System.out.printf("| %-10d | %-15s | %-12s | %-10.2f | %-8d | %-15d | %-19s | %-19s |%n",
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getReorderThreshold(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()));

            // Print table footer
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
        }catch (SQLException se){
            System.out.println("there is an exception");
        }catch (RuntimeException re){
            System.out.println("\n No Products Available in the specified range");
        }
    }

    private static void viewOutOfStockProducts() {

        //Scanner sc = new Scanner(System.in);

        try (Stream<Product> productStream = InventoryFunctions.getOutOfStockProducts().stream()) {

            // Print table header, rows, and footer in a single function
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
            System.out.printf("| %-10s | %-15s | %-12s | %-10s | %-8s | %-15s | %-19s | %-19s |%n",
                    "Product ID", "Name", "Category", "Price", "Quantity", "Reorder Threshold", "Created At", "Updated At");
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");

            // Print each product row
            productStream.forEach(product -> System.out.printf("| %-10d | %-15s | %-12s | %-10.2f | %-8d | %-15d | %-19s | %-19s |%n",
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getReorderThreshold(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()));

            // Print table footer
            //System.out.println("+------------+-----------------+--------------+------------+----------+-----------------+---------------------+---------------------+");
        }catch (SQLException se){
            System.out.println("there is an exception");
        }catch (RuntimeException re){
            System.out.println("\n All products are in stock");
        }

    }

    private static void viewAllCategories() {

        //Scanner sc = new Scanner(System.in);
        try(Stream<String> categoriesStream = InventoryFunctions.getAllCategories().stream()) {

            System.out.println("\nAvailable Categories : ");
            categoriesStream.forEach(System.out::println);
        }
        catch (SQLException e) {

            System.out.println(e.getMessage());
        }
        catch (RuntimeException re){

            System.out.println("No categories are there.");
        }
    }

    private static void searchProducts() {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n Select an option from menu to sort the product to view : \n");
        System.out.println("1.Product Id");
        System.out.println("2.Price");
        System.out.println("3.Quantity");
        System.out.println("4.Date created");
        System.out.println("5.Date updated");

        int menuOption;
        try {
            menuOption = sc.nextInt();
        }catch (Exception e){
            sc.nextLine();
            System.out.println("\nWrong input\n");
            return;
        }

        System.out.println("\n 1.Ascending");
        System.out.println(" 2.Descending");

        int menuOptionForSorting;
        try {
            menuOptionForSorting = sc.nextInt();
            sc.nextLine();
        }catch (Exception e){

            sc.nextLine();
            System.out.println("\nWrong input. try again\n");
            return;
        }

        boolean isAscending;

        if (menuOptionForSorting == 1) {

            isAscending = true;
        } else if (menuOptionForSorting == 2) {

            isAscending = false;
        } else {

            System.out.println(("\nWrong input. try again\n"));
            return;
        }

        String column = switch (menuOption) {
            case 1 -> "product_id";
            case 2 -> "price";
            case 3 -> "quantity";
            case 4 -> "created_at";
            case 5 -> "updated_at";
            default -> throw new RuntimeException("Invalid input for sorting menu.");
        };

        try (Stream<Product> productStream = InventoryFunctions.getSortedProductsByColumn(column, isAscending).stream()) {
            // Print table header, rows, and footer in a single function
            System.out.printf("| %-10s | %-15s | %-12s | %-10s | %-8s | %-15s | %-19s | %-19s |%n",
                    "Product ID", "Name", "Category", "Price", "Quantity", "Reorder Threshold", "Created At", "Updated At");

            // Print each product row
            productStream.forEach(product -> System.out.printf("| %-10d | %-15s | %-12s | %-10.2f | %-8d | %-15d | %-19s | %-19s |%n",
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getReorderThreshold(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()));
        } catch (SQLException se) {
            System.out.println("there is an exception");
        } catch (RuntimeException re) {
            System.out.println("\n There are no products available");
        }
    }

}
