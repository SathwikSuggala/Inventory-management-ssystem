package DataBase;

import FileHandling.CredentialsInfo;
import Main.Product;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Date;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryFunctions {

    static Connection con;
    final static String userName = CredentialsInfo.credentials.getFirst();
    final static String password = CredentialsInfo.credentials.get(1);

    public static void connect(){

        if(con == null) {
            var dataSource = new MysqlDataSource();

            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306);
            dataSource.setDatabaseName("InventoryManagement");

            //System.out.println("trying to connect with data base.....");
            try {
                con = dataSource.getConnection(userName, password);
                //System.out.println("Connection successful");
            } catch (SQLException e) {
                System.out.println("Connection failed : " + e.getMessage());
            }
        }



    }

    public static void addNewProduct(String newProductName, String newProductCategory, double newProductPrice, int newProductQuantity, int newProductReOrderThreshold) throws SQLException {

        connect();
        if(isProductAvailable(newProductName, newProductCategory)){
            throw new RuntimeException("This Product Name is not available");
        }
        String query = """
                INSERT INTO products (name, category, price, quantity, reorder_threshold, created_at, updated_at, is_deleted)
                VALUES
                (?,?,?,?,?, NOW(), NOW(),'0')
                """;

        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setString(1,newProductName);
        stmt.setString(2,newProductCategory);
        stmt.setDouble(3,newProductPrice);
        stmt.setInt(4,newProductQuantity);
        stmt.setInt(5,newProductReOrderThreshold);

        stmt.executeUpdate();


    }

    public static <T> void updateProductById(int productId, String column, T newValue) throws SQLException,RuntimeException {

        connect();
        if (!isProductPresentAndNotDeleted(productId)) {
            throw new RuntimeException("No product available");
        }
        String query = " update products " +
                "set " + column +
                " = ? where product_id = ? ;";

        PreparedStatement stmt = con.prepareStatement(query);
        switch (newValue) {
            case Integer i -> stmt.setInt(1, i);
            case String s -> stmt.setString(1, s);
            case Double v -> stmt.setDouble(1, v);
            case Boolean b -> stmt.setBoolean(1, b);
            case Float v -> stmt.setFloat(1, v);
            case Long l -> stmt.setLong(1, l);
            case Date date -> stmt.setDate(1, date);
            case null, default -> {
                assert newValue != null;
                throw new IllegalArgumentException("Unsupported data type: " + newValue.getClass().getName());
            }
        } //setting value type to add it for prepared statement.
        stmt.setInt(2,productId);
        int result = stmt.executeUpdate();

        if(result != 1){

            throw new SQLException();
        }
    }

    public static void deleteProductById(int productId) throws SQLException {

        connect();
        if (!isProductPresentAndNotDeleted(productId)) {
            throw new RuntimeException("No product available");
        }
        String query = "update products " +
                "set is_deleted = '1' " +
                " where product_id = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1,productId);
        int result = stmt.executeUpdate();
        if(result != 1){

            throw new RuntimeException();
        }
    }

    public static List<Product> getProductsByCategory(String category) throws SQLException {

        connect();

        List<Product> productList = new ArrayList<>();

        // Define the query
        String query = "SELECT * FROM products WHERE category = ? and is_deleted = '0';";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, category); // Set the category parameter

            // Execute the query and get the result set
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create a product object for each row in the result set
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("reorder_threshold"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );

                    // Add the product object to the list
                    productList.add(product);
                }
            }
        }

        if(productList.isEmpty()){

            throw new RuntimeException();
        }
        // Return the list of products
        return productList;
    }

    public static List<Product> getProductsByPriceRange(double minimumAmount, double maximumAmount) throws SQLException {

        connect();
        String query = """
                select * from products where price between ? and ? and is_deleted = '0'
                """;
        List<Product> productList = new ArrayList<>();
        PreparedStatement stmt = con.prepareStatement(query) ;
             // Set the category parameter
        stmt.setDouble(1,minimumAmount);
        stmt.setDouble(2,maximumAmount);
            // Execute the query and get the result set
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Create a product object for each row in the result set
            Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_threshold"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );

            // Add the product object to the list
            productList.add(product);

        }

        if(productList.isEmpty()){
            throw new RuntimeException();
        }
        return productList;
    }

    public static List<Product> getOutOfStockProducts() throws SQLException {

        connect();
        List<Product> productList = new ArrayList<>();
        String query = """
                select * from products where quantity = 0 and is_deleted = '0';
                """;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            // Create a product object for each row in the result set
            Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_threshold"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );

            // Add the product object to the list
            productList.add(product);

        }

        if(productList.isEmpty()){
            throw new RuntimeException();
        }
        return productList;
    }

    public static List<String> getAllCategories() throws SQLException {

        connect();
        List<String> categories = new ArrayList<>();
        String query = """
                select category from products where is_deleted = '0' group by category;
                """;
        Statement stmt = con.createStatement();

        ResultSet res = stmt.executeQuery(query);

        while(res.next()){

            categories.add(res.getString(1));
        }

        if(categories.isEmpty()){

            throw new RuntimeException("No Categories are there");
        }

        return categories;
    }

    public static List<Product> getSortedProductsByColumn(String column, boolean isAscending) throws SQLException {

        connect();
        List<Product> productList = new ArrayList<>();
        String query;
        if(isAscending) {
            query = "select * from products where is_deleted = '0' order by " + column;
        }else{
            query = "select * from products where is_deleted = '0' order by " + column + " DESC;";
        }

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            // Create a product object for each row in the result set
            Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("reorder_threshold"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
            );

            // Add the product object to the list
            productList.add(product);

        }

        if(productList.isEmpty()){
            throw new RuntimeException();
        }
        return productList;
    }

    public static boolean isProductAvailable(String productName, String category) throws SQLException {


        connect();
        String query = "SELECT 1 FROM products WHERE product_id = ? and category = ? LIMIT 1";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, productName);
            preparedStatement.setString(2,category);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();// If true, user exists
            }
        }

    }

    public static boolean isProductPresentAndNotDeleted(int product_id) throws SQLException {

        connect();
        String query = "SELECT 1 FROM products WHERE product_id = ? AND is_deleted = '0' LIMIT 1";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, product_id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // If true, user exists and is not deleted
            }
        }

    }

    public static void addQuantityToProduct(int productId, int quantity) throws SQLException {

        connect();
        if(!isProductPresentAndNotDeleted(productId)){
            throw new RuntimeException("No product available");
        }
        String query = "select quantity from products where product_id = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1,productId);
        ResultSet rs = stmt.executeQuery();
        int oldQuantity = 0;
        while(rs.next()){
            oldQuantity = rs.getInt(1);
        }
        int newQuantity = quantity + oldQuantity;

        query = "update products set quantity = ? where product_id = ?";
        PreparedStatement stmt2 = con.prepareStatement(query);
        stmt2.setInt(1,newQuantity);
        stmt2.setInt(2,productId);
        int result = stmt2.executeUpdate();
        if(result != 1){
            throw new RuntimeException("cannot update product");
        }

    }
}
