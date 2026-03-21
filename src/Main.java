import java.util.Scanner;

public class Main {

    public static void main (String args[]) {

        String tryAgain = "yes";

        Scanner inputScanner = new Scanner(System.in);
        NoteManager newManager = new NoteManager();

        while (tryAgain.equalsIgnoreCase("yes")) {

        System.out.println("---------- JOTTER ----------");
        System.out.println("1. Create New Note");
        System.out.println("2. View All Notes");
        System.out.println("3. Read Note");
        System.out.println("4. Edit Note");
        System.out.println("5. Delete Note");
        System.out.println("6. Exit");
        endLine();
        System.out.print("Enter Choice: ");
        int choice = inputScanner.nextInt();
        inputScanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Create New Note");
                    newManager.CreateNewNote();
                    break;
                case 2:
                    System.out.println("View All Notes");
                    newManager.DisplayAllNote();
                    break;
                case 3:
                    System.out.println("Read Note");
                    newManager.ReadNote();
                    break;
                case 4:
                    System.out.println("Edit Note");

                    break;
                case 5:
                    System.out.println("Delete Note");

                    break;
                default:
                    System.out.println("Error!");

            }

            System.out.print("Try again? (yes / no): ");
            tryAgain = inputScanner.nextLine();

        }

    }


    static void endLine() {
        System.out.println("----------------------------");
    }


}
