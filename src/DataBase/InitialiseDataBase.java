package DataBase;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import FileHandling.CredentialsInfo;

public class InitialiseDataBase {

    static Connection con;
    String userName;
    String password;

    public  void connect(){

        if(con == null) {
            var dataSource = new MysqlDataSource();

            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306);
            dataSource.setDatabaseName("InventoryManagement");

            System.out.println("trying to connect with data base.....");
            try {
                con = dataSource.getConnection(userName, password);
                System.out.println("Connection successful");
            } catch (SQLException e) {
                System.out.println("Connection failed : " + e.getMessage());
            }
        }

    }

    public void createDataBase() throws SQLException, NullPointerException {

        Connection connection;
        Statement statement;

            Scanner sc = new Scanner(System.in);

            File file = new File("Credentials.txt");
            if (!file.exists()) {
                // 1. Connect to MySQL Server (No specific database selected yet)
                System.out.println("Enter user name for mysql");
                userName = sc.nextLine();
                System.out.println("Enter password for mysql");
                password = sc.nextLine();
                CredentialsInfo.saveCredentials(userName, password);

            }
            CredentialsInfo.setCredentials();
            userName = CredentialsInfo.credentials.getFirst();
            password = CredentialsInfo.credentials.get(1);

            //System.out.println(userName+" " + password);
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", userName, password);
            // 2. Create a Statement
            statement = connection.createStatement();
            // 3. Execute SQL to Create a Database
//            System.out.println("Enter data base name");
//            String myDataBase = sc.next();
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS InventoryManagement";
            statement.executeUpdate(createDatabaseSQL);
            System.out.println("\nDatabase found successfully!");


    }

    public void createTables() throws SQLException {

        connect();
        String usersSchema = """
                CREATE TABLE users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_deleted ENUM('0', '1') NOT NULL DEFAULT '0'
                );
                
                """;

        String productsSchema = """
                CREATE TABLE products (
                    product_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    price DECIMAL(10, 2) NOT NULL,
                    quantity INT NOT NULL,
                    reorder_threshold INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    is_deleted ENUM('0', '1') NOT NULL DEFAULT '0'
                );
                
                """;

        String customersSchema = """
                CREATE TABLE customers (
                    customer_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    phone_number VARCHAR(15) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
                """;

        String ordersSchema = """
                CREATE TABLE orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    customer_id INT,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_amount DECIMAL(10, 2) NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
                );
                
                """;

        String orderItemsSchema = """
                CREATE TABLE order_items (
                    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT,
                    product_id INT,
                    quantity INT NOT NULL,
                    price_at_order DECIMAL(10, 2) NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
                );
                
                """;

            Statement stmt = con.createStatement();

            try {
                stmt.execute(customersSchema);
            } catch (SQLException cs) {
                System.out.println();
            }
            try {
                stmt.execute(ordersSchema);
            } catch (SQLException cs) {
                System.out.println();
            }
            try {
                stmt.execute(usersSchema);
                Scanner sc = new Scanner(System.in);
                System.out.println("\nEnter new Admin user name");
                String newAdminUsername = sc.nextLine();
                System.out.println("Enter new password");
                String newAdminPassword = sc.nextLine();
                PreparedStatement pstmt = con.prepareStatement("insert into users(username,password,role) values (?,?,'ADMIN');");
                pstmt.setString(1,newAdminUsername);
                pstmt.setString(2,newAdminPassword);
                pstmt.executeUpdate();
            } catch (SQLException cs) {
                System.out.println();
            }
            try {
                stmt.execute(productsSchema);
            } catch (SQLException cs) {
                System.out.println();
            }
            try {
                stmt.execute(orderItemsSchema);
            } catch (SQLException cs) {
                System.out.println();
            }

            System.out.println("\nSuccessfully created the schema.\n");
            System.out.println("\nlogin into the system\n");


    }

}
