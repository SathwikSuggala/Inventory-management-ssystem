package DataBase;

import FileHandling.CredentialsInfo;
import Main.Customer;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerFunctions {
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

    public boolean createCustomer(String name, String email, String phoneNumber) throws SQLException {
        String insertCustomerSQL = "INSERT INTO customers (name, email, phone_number) VALUES (?, ?, ?)";

        // Establishing the database connection
        connect();
        try (
             PreparedStatement preparedStatement = con.prepareStatement(insertCustomerSQL)) {

            // Set parameters for the query
            preparedStatement.setString(1, name);          // Set name
            preparedStatement.setString(2, email);         // Set email
            preparedStatement.setString(3, phoneNumber);   // Set phone number

            // Execute the query and return success
            int affectedRows = preparedStatement.executeUpdate();

            // If affected rows > 0, the insertion was successful
            return affectedRows > 0;

        }
    }

    public  List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customerList = new ArrayList<>();
        String selectCustomersSQL = "SELECT customer_id, name, email, phone_number, created_at FROM customers";

        try (
             PreparedStatement preparedStatement = con.prepareStatement(selectCustomersSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate through the result set and populate the list
            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                long phoneNumber = resultSet.getLong("phone_number");
                java.sql.Date createdAt = resultSet.getDate("created_at");

                // Create a Customer object and add it to the list
                Customer customer = new Customer(customerId, name, email, phoneNumber, createdAt);
                customerList.add(customer);
            }

        }

        if(customerList.isEmpty()){

            throw new RuntimeException("No customers are there.");
        }

        return customerList; // Return the list of customers
    }

    public List<Customer> searchCustomer(String query) throws SQLException {
        List<Customer> customerList = new ArrayList<>();
        String searchSQL = "SELECT customer_id, name, email, phone_number, created_at FROM customers WHERE name LIKE ? OR email LIKE ?";

        try (
             PreparedStatement preparedStatement = con.prepareStatement(searchSQL)) {

            preparedStatement.setString(1, "%" + query + "%");  // Use wildcards for partial matching
            preparedStatement.setString(2, "%" + query + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                long phoneNumber = resultSet.getLong("phone_number");
                java.sql.Date createdAt = resultSet.getDate("created_at");

                Customer customer = new Customer(customerId, name, email, phoneNumber, createdAt);
                customerList.add(customer);
            }
        }

        if(customerList.isEmpty()){

            throw new RuntimeException("No customer available with the provided details.");
        }

        return customerList;
    }

    public boolean updateCustomer(int customerId, String newName, String newEmail, String newPhoneNumber) throws SQLException {
        connect();
        String updateSQL = "UPDATE customers SET name = ?, email = ?, phone_number = ? WHERE customer_id = ?";

        try (
             PreparedStatement preparedStatement = con.prepareStatement(updateSQL)) {

            // Set the new values
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPhoneNumber);
            preparedStatement.setInt(4, customerId);

            // Execute update
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                //System.out.println("Customer updated successfully.");
                return true;
            } else {

                return false;
            }

        }
    }





}
