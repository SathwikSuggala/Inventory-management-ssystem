package Main;

import DataBase.CustomerFunctions;
import FileHandling.LogDetails;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerManagement {

    CustomerFunctions customerFunctions = new CustomerFunctions();
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public void startMenu(){

        Scanner sc = new Scanner(System.in);
        int menuOption;
        while(true) {

            System.out.println("Select an option from menu");
            System.out.println("1.Create new customer");
            System.out.println("2.View all customers");
            System.out.println("3.Search customer");
            System.out.println("4.Update customer");

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
                case 1 -> createCustomer();
                case 2 -> viewAllCustomers();
                case 3 -> searchCustomer();
                case 4 -> updateCustomer();
                default -> System.out.println("\nselect an option from menu only\n");
            }
        }
    }

    private void updateCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter customer id you want to modify");

        int customerId;
        try {
            customerId = sc.nextInt();
            sc.nextLine();
        }catch (Exception e){

            sc.nextLine();
            System.out.println("\nWrong input. try again\n");
            return;
        }
        System.out.println("Enter new name");
        String customerName = sc.nextLine();
        System.out.println("Enter new Email");
        String newEmail = sc.nextLine();
        System.out.println("Enter new phone number");
        String newPhoneNumber = sc.nextLine();

        try {
            if(customerFunctions.updateCustomer(customerId,customerName,newEmail,newPhoneNumber)){
                LogDetails.log("Customer with id " + customerId + " has been updated");
                System.out.println("Customer updated successfully");
            }else{
                System.out.println("\nCustomer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("\nError adding new customer");
        }catch (NullPointerException ne){
            System.out.println("\nThere are no customers registered.");
        }
    }

    private void searchCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name or email to get details");
        String nameOrEmail = sc.nextLine();

        try {
            printCustomers(customerFunctions.searchCustomer(nameOrEmail));
        } catch (SQLException e) {
            System.out.println("There is not customer available with this name or email.");
        }catch (NullPointerException ne){
            System.out.println("\nThere are no customers registered. \n");
        }
    }

    private void viewAllCustomers() {

        try {
            printCustomers(customerFunctions.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }catch (NullPointerException ne){
            System.out.println("\nThere are no customers registered. \n");
        }
    }

    public void createCustomer(){

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter new name");
        String customerName = sc.nextLine();
        System.out.println("Enter new Email");
        String newEmail = sc.nextLine();

        //checking email.
        if(!isValidEmail(newEmail)){
            System.out.println("Email is not valid try again.");
            return;
        }
        System.out.println("Enter new phone number");
        String newPhoneNumber = sc.nextLine();

        //checking phone number.
        for(char i : newPhoneNumber.toCharArray()){
            if(!Character.isDigit(i)){
                System.out.println("Wrong input. try again");
                return;
            }
        }
        if(newPhoneNumber.length() != 10){
            System.out.println("Wrong input. try again");
            return;
        }



        try {
            if(customerFunctions.createCustomer(customerName,newEmail,newPhoneNumber)){
                LogDetails.log("New customer has been created");
                System.out.println("Customer created successfully");
            }else{
                System.out.println("Error creating new customer");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding new customer");
        } catch (RuntimeException re){
            LogDetails.log("tried to add duplicate customer");
            System.out.println("Error adding new customer. Duplicate entry");
        }
    }

    public void printCustomers(List<Customer> customers) {
        // Define column headers
        String format = "| %-12s | %-20s | %-25s | %-15s | %-20s |%n";

        // Print table headers
        System.out.format("+--------------+----------------------+---------------------------+-----------------+----------------------+%n");
        System.out.format("| Customer ID  | Name                 | Email                     | Phone Number    | Created At           |%n");
        System.out.format("+--------------+----------------------+---------------------------+-----------------+----------------------+%n");

        // Print customer data
        for (Customer customer : customers) {
            System.out.format(format,
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getCreatedAt());
        }

        // Print table footer
        System.out.format("+--------------+----------------------+---------------------------+-----------------+----------------------+%n");
    }

    public boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}

