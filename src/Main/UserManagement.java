package Main;

import DataBase.userFunctions;
import FileHandling.LogDetails;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class UserManagement {
    private String username;
    private String passWord;
    private String role;

    public String getUsername() {
        return username;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getRole() {
        return role;
    }

    public boolean setUser(String userName, String password){

        String result = "";
        try {
            result = userFunctions.getRole(userName, password);
        }catch (RuntimeException re){

            System.out.println("User name or password is incorrect");
        }
        catch (SQLException se){

            System.out.println(se.getMessage());
        }
        if(result.isEmpty()){
            return false;
        }
        role = result;
        this.username = userName;
        this.passWord = password;
        return true;

    }

    protected void startMenu() {

        Scanner sc = new Scanner(System.in);
        while(true) {
            int menuOption;
            System.out.println("\n\nSelect an option from menu");
            System.out.println("1.Change user name");
            System.out.println("2.Change password.");

            if(getRole().equals("ADMIN")) System.out.println("3.Edit users");

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
                    System.out.println("Enter the new user name");
                    String newUserName = sc.nextLine();
                    System.out.println("Confirm the new user name");
                    String newUserNameCheck = sc.nextLine();

                    if(newUserName.isEmpty()){
                        System.out.println("Empty user name is not allowed");
                        break;
                    }
                    if(!newUserName.equals(newUserNameCheck)){
                        System.out.println("\nInput doesn't match, try again");
                        break;
                    }

                    try {
                        userFunctions.modifyUser("username", newUserName, getUsername());
                        LogDetails.log("user name " + getUsername() + " changed to "+ newUserName);
                    } catch (SQLException e) {
                        LogDetails.log(e.getMessage());
                    }

                    System.out.println("Terminating the application.");
                    System.out.println("\n\nrestart the application.");
                    System.exit(0);
                    break;

                case 2:
                    System.out.println("Enter the new password");
                    String newPassword = sc.nextLine();
                    System.out.println("Confirm password");
                    String newPasswordCheck = sc.nextLine();

                    if(newPassword.isEmpty()){
                        System.out.println("Empty password is not allowed");
                        break;
                    }
                    if(!newPassword.equals(newPasswordCheck)){
                        System.out.println("\nInput doesn't match. try again");
                        break;
                    }

                    try {
                        userFunctions.modifyUser("password", newPassword, getPassWord());
                        LogDetails.log("user with user name " + getUsername() + " changed password");
                    } catch (SQLException e) {
                        LogDetails.log(e.getMessage());
                    }

                    System.out.println("Terminating the application.");
                    System.out.println("\n restart th e application.");
                    System.exit(0);
                    break;

                case 3:
                    if (getRole().equals("ADMIN")) {
                        startAdminMenu();
                        break;
                    }

                default:
                    System.out.println("Select an option from menu only.");
            }
         }
    }

    private void startAdminMenu(){
        Scanner sc = new Scanner(System.in);
        int menuOption;
        while(true) {
            System.out.println("Select an option from menu");
            System.out.println("1.show all the users and roles");
            System.out.println("2.Delete user");
            System.out.println("3.Add new user");

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
                    try (Stream<Map.Entry<Integer, List<String>>> stream = userFunctions.getUsersDetails().entrySet().stream()) {
                        System.out.format("/n%-10s %-15s %-20s %-10s%n", "User Id", "Created On", "User Name", "Role");
                        //using streams to directly print the users list without storing it.

                        stream.forEach(entry ->
                                System.out.format("%-10d %-15s %-20s %-10s%n",
                                        entry.getKey(),
                                        entry.getValue().get(2),  // Created On
                                        entry.getValue().getFirst(),  // User Name
                                        entry.getValue().get(1))  // Role
                        );
                        System.out.println();
                    } catch (RuntimeException re) {

                        System.out.println(re.getMessage());
                    }

                    break;
                case 2:
                    System.out.println("Enter User Name to delete");
                    String deleteUserName = sc.nextLine();
                    try {
                        userFunctions.deleteUser(deleteUserName);
                        LogDetails.log("deleted user with user name : " + deleteUserName);
                    } catch (RuntimeException re) {

                        LogDetails.log(re.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Enter new User Name : ");
                    String newUserName = sc.nextLine();
                    System.out.println("Enter Password : ");
                    String newPassword = sc.nextLine();
                    System.out.println("Choose the role : \n 1) ADMIN \n 2)EMPLOYEE");
                    int roleOption;
                    if(newUserName.isEmpty() || newPassword.isEmpty()){
                        System.out.println("Empty input is not allowed");
                        break;
                    }
                    try {
                        roleOption= sc.nextInt();
                        sc.nextLine();
                    }catch (Exception e){
                        sc.nextLine();
                        System.out.println("\nWrong input. try again\n");
                        break;
                    }
                    String newRole;
                    if (roleOption == 1) {

                        newRole = "ADMIN";
                    } else {

                        newRole = "EMPLOYEE";
                    }
                    try {
                        userFunctions.addUser(newUserName, newPassword, newRole);
                    }catch (Exception e){
                        LogDetails.log("Error while creating new user");
                        System.out.println("cannot create user with these credentials. Duplicate Entry");
                    }
                    LogDetails.log("created new account with " + newUserName + " user name as " + newRole);
                    break;
                default:
                    System.out.println("Select an option from menu only");
            }
        }


    }

}
