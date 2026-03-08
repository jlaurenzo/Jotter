import java.util.Scanner;

public class Main {

    public static void main (String args[]) {

        Scanner inputScanner = new Scanner(System.in);
        NoteManager newManager = new NoteManager();


        System.out.println("---------- JOTTER ----------");
        System.out.println("1. Create New Note");
        System.out.println("2. View All Notes");
        System.out.println("3. Search Notes");
        System.out.println("4. Edit Note");
        System.out.println("5. Delete Note");
        System.out.println("6. Exit");
        endLine();
        System.out.print("Enter Choice: ");
        int choice = inputScanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Create New Note");
                newManager.CreateNewNote();
                break;
            case 2:
                break;
            case 3:
                break;




        }





    }

    static void endLine() {
        System.out.println("----------------------------");
    }


}
