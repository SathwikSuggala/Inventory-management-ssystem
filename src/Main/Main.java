package Main;

import DataBase.InitialiseDataBase;
import FileHandling.LogDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    static UserManagement currentUser  = new UserManagement();
    CustomerManagement customerManagement = new CustomerManagement();
    OrderManagement orderManagement = new OrderManagement();
    static boolean isLoggedIn = false;

    public static void main(String[] args) throws IOException {

        InitialiseDataBase initialiseDataBase = new InitialiseDataBase();
        try{
        initialiseDataBase.createDataBase();
        }catch (SQLException se){

            System.out.println("cannot find data base or mysql credentials were wrong.");
            LogDetails.log("data base initialisation failed. may be credentials were wrong.");
        }
        catch (NullPointerException ne){

            System.out.println("Wrong credentials entered for mysql data base");
        }
        try {
            initialiseDataBase.createTables();
        } catch (SQLException e) {
            System.out.println("\nError in creating schema");
        }catch (NullPointerException ne){

            System.out.println("\nWrong credentials entered for mysql data base. deleting old credentials so try again.");
            try {
                Files.delete(Path.of("Credentials.txt"));
            }catch (IOException ie){

                System.out.println("File not found.");
            }
            return;
        }
        Main mainFunction = new Main();

        if(isLoggedIn()) {
            //after the login is success.
            System.out.println("\nSuccessfully logged in\n");
            mainFunction.startMainMenu();
        }else{
            System.out.println("Login failed");
        }

    }

    public static boolean isLoggedIn(){

        Scanner sc = new Scanner(System.in);
        //Looping until user is successfully logged in.
        while(!isLoggedIn) {
            System.out.println("Enter user name : ");
            String userName = sc.nextLine();

            System.out.println("Enter password : ");
            String password = sc.nextLine();

            isLoggedIn = currentUser.setUser(userName, password);
            if(!isLoggedIn){
                System.out.println("try again.");
            }else{

                LogDetails.log("\nUser logged in with " + currentUser.getUsername() +" user name");
            }
        }

        return true;
    }

    public void startMainMenu(){

        Scanner sc = new Scanner(System.in);
        int menuOption;

        while(true) {

            System.out.println("Select an option from menu");
            System.out.println("1.Account and User Management");
            System.out.println("2.Inventory Management");
            System.out.println("3.Customer Management");
            System.out.println("4.Place order");
            try {
                menuOption = sc.nextInt();
                sc.nextLine();
            }catch (Exception e){
                sc.nextLine();
                System.out.println("Wrong input. try again");
                continue;
            }

            /* checking the menu option
            if it is less than or equal to 0 we will exit this menu.*/
            if(menuOption <= 0){
                System.out.println("Exiting the menu");
                break;
            }
            switch(menuOption){

                case 1 -> currentUser.startMenu();
                case 2 -> InventoryManagement.startMenu();
                case 3 -> customerManagement.startMenu();
                case 4 -> orderManagement.placeOrder();

                default -> System.out.println("Wrong input");
            }
        }
    }

}
