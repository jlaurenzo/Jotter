import java.io.*;
import java.util.*;

public class databaseManager {

    File database = new File("notes.txt");

    void createDatabase() {
        try {
            if (database.createNewFile( )) {
                System.out.println("Database Created " + database.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error occurred in creating Database");
        }

    }


}
