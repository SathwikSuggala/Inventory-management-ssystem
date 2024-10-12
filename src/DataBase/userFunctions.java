package DataBase;

import FileHandling.CredentialsInfo;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class userFunctions {

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

    //this function returns the role of the current user.
    public static String getRole(String userName, String password) throws SQLException {

        connect();

        if(!isUserPresentAndNotDeleted(userName)){

            throw new RuntimeException("User is not there, already deleted.");
        }
            Statement stmt = con.createStatement();
            String query = "select role from users where BINARY username = '" +
                    userName +
                    "' and BINARY password = '" +
                    password +
                    "';";
            ResultSet rs = stmt.executeQuery(query);
            try {
                rs.next();
                return rs.getString(1);
            }catch (SQLException empty){
                throw new RuntimeException();
            }



        //return null;
    }

    //this function with modify the user details.
    public static void modifyUser(String column, String newValue, String oldValue) throws SQLException {

        connect();

        if(isUserNamePresent(userName)){

            throw new RuntimeException("User is not there, already deleted.");
        }
            Statement stmt = con.createStatement();
            String query = "update users " +
                    "set " + column + " = '" + newValue +
                    "' where "+ column + " = '" + oldValue + "';";
            int result = stmt.executeUpdate(query);
            if(result == 0){

                System.out.println("there is an error in the input");
            }
            else{
                System.out.println("Successfully updated");
            }


    }

    public static Map<Integer, List<String>> getUsersDetails() {

        connect();
        try {
            Statement stmt = con.createStatement();
            String query = "select user_id, username, role, created_at from users where is_deleted = '0';";
            ResultSet res = stmt.executeQuery(query);
            Map<Integer, List<String>> usersData = new HashMap<>();
            List<String> usersList;
            while(res.next()){

                usersList = new ArrayList<>();
                usersList.add(res.getString(2));
                usersList.add(res.getString(3));
                usersList.add(res.getDate(4).toString());

                usersData.put(res.getInt(1),usersList);
            }

            if(usersData.isEmpty()){

                throw new RuntimeException("There are no users");
            }
            return usersData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(String userName){

        connect();
        try {

            if(!isUserPresentAndNotDeleted(userName)){

                throw new RuntimeException("No user found with user name : " + userName);
            }
            //Statement stmt = con.createStatement();
            String query = """
                    update users
                    set is_deleted = '1'
                    where username = ?
                    """;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,userName);
            int result = stmt.executeUpdate();

            if(result == 0){

                throw new RuntimeException("No user found with user name : " + userName);
            }else{

                System.out.println("Deleted Successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public static void addUser(String newUserName, String newPassword, String newRole) {

        connect();
        if(isUserNamePresent(userName)){

            throw new RuntimeException("User is not there, already deleted.");
        }
        String query = "insert into users (username, password, role, is_deleted) values(?,?,?,'0');";
        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1,newUserName);
            stmt.setString(2,newPassword);
            stmt.setString(3,newRole);

             stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public static boolean isUserNamePresent(String username) {

        connect();
        String query = "SELECT 1 FROM users WHERE BINARY username = ? LIMIT 1";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // If true, user exists
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        //return false;  // User does not exist
    }

    public static boolean isUserPresentAndNotDeleted(String username) {

        connect();
        String query = "SELECT 1 FROM users WHERE BINARY username = ? AND is_deleted = '0' LIMIT 1";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // If true, user exists and is not deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // User does not exist or is deleted
    }

}
