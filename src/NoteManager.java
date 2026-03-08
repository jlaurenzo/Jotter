import java.util.*;
import java.io.*;
public class NoteManager {

    void CreateNewNote() {

        databaseManager newManager = new databaseManager();
        if (!newManager.database.exists()) {
            newManager.createDatabase();
        }
        Note newNote = new Note();

        Scanner userInput = new Scanner(System.in);

        System.out.print("Enter Title: ");
        String title = userInput.nextLine();
        System.out.print("Enter Note: ");
        String note = userInput.nextLine();
        userInput.close();

        try (FileWriter noteWriter = new FileWriter("notes.txt", true)){
            noteWriter.write("#" + newNote.getPrivateId() + " , " + newNote.createdDate + " , " + title + " , " + note + " , " + "\n");
            System.out.println("Note Successfully Created!");
        } catch (IOException e) {
            System.out.println("An Error Occurred, Try Again later!");
        }
    }

    void DeleteNote() {

    }

    void ReadNote() {

    }

    void UpdateNote() {

    }

    void DisplayAllNote() {

    }






}
