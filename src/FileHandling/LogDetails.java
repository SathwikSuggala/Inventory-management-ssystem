package FileHandling;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogDetails {

    static LocalDateTime currentDateTime;

    public static void log(String data){

        currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        data = currentDateTime.format(formatter) + " " + data;


        try (FileWriter fileWriter = new FileWriter("logs.txt", true)) {
            fileWriter.write(data + "\n");  // Write user input to the file
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

}
