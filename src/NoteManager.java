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
        System.out.println("----------------------------");
        System.out.print("Enter Title: ");
        String title = userInput.nextLine();
        System.out.print("Enter Note: ");
        String note = userInput.nextLine();
        System.out.println("----------------------------");

        int noteId = GetNextId() ;

        try (FileWriter noteWriter = new FileWriter("notes.txt", true)){
            noteWriter.write(noteId + "#" + " , " + newNote.createdDate + " , " + title + " , " + note + " , " + "\n");
            System.out.println("Note Successfully Created!");
        } catch (IOException e) {
            System.out.println("An Error Occurred, Try Again later!");
        }
    }

    int GetNextId() {
        int noteLines = 0;

        try (BufferedReader noteReader = new BufferedReader(new FileReader("notes.txt"))) {
            while(noteReader.readLine() != null) {
                noteLines++;
            }

        } catch (IOException e) {
            System.out.println("Error in accessing (notes.txt)!");
        }
        return  noteLines;
    }

    void DeleteNote() {

    }

    void ReadNote() {
        Scanner userInput = new Scanner(System.in);

        System.out.println("----------------------------");
        System.out.println("Read Note");
        DisplayAllNote();
        System.out.println("Enter Note Id: ");
        int nNote = userInput.nextInt();

        try (BufferedReader noteReader = new BufferedReader(new FileReader("notes.txt"))) {

            String noteLine;

            while ((noteLine = noteReader.readLine()) != null) {
                String[] noteCheck = noteLine.split(" , ");
                int id = Integer.parseInt(noteCheck[0].replace("#", ""));

                if (id == nNote) {
                    System.out.println("");
                    System.out.println("ID: " + id);
                    System.out.println("Date Created: " + noteCheck[1]);
                    System.out.println("Title: " + noteCheck[2]);
                    System.out.println("------------------------------");
                    System.out.println(noteCheck[3]);
                    System.out.println("------------------------------");
                }
                System.out.println("Error!");
            }

        } catch (IOException e) {
            System.out.println("Error in accessing (notes.txt)!");
        }

    }

    void UpdateNote() {

    }

    void DisplayAllNote() {
        System.out.println("Your Notes:");

        try (BufferedReader noteReader = new BufferedReader(new FileReader("notes.txt"))) {

            String noteLine;

            while ((noteLine = noteReader.readLine()) != null) {
                String separator[] = noteLine.split(", ");

                String noteId = separator [0].replace("#", "");
                String title = separator [2];

                System.out.println("ID: "+ noteId + "|" + " Title: " + title);
            }

        } catch (IOException e) {
            System.out.println("Error in accessing (notes.txt)!");
        }



    }
}
