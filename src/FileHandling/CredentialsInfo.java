package FileHandling;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CredentialsInfo {

    public static List<String> credentials = new ArrayList<>();

    public static void saveCredentials(String userName, String password){
        File file = new File("Credentials.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Credentials.txt"))) {
            writer.write(userName);
            writer.write("\n"+password);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void setCredentials(){
        String filePath = "Credentials.txt"; // Specify the file path and name

        // Use try-with-resources to automatically close the reader
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                credentials.add(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the file.");
            e.printStackTrace();
        }
    }

}
