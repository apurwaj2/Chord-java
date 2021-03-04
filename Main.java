import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) {

        String nodeIP = "localhost";
        String option, port1, port2, key;
        Scanner input = new Scanner(System.in);

        while(true) {

            System.out.println();
            System.out.println("Enter anyone of the following options:");
            System.out.println("create, join, delete, getkey, quit");

            option = input.nextLine();
            System.out.println("Entered option : " + option);

            if (option.equals("create")) {
                System.out.println("Create");
            } else if (option.equals("join")) {
                System.out.println("Join");
            } else if (option.equals("delete")) {
                System.out.println("Delete");
            } else if (option.equals("getkey")) {
                System.out.println("getKey");
            } else if (option.equals("quit")) {
                System.out.println("Ending the program..");
                break;
            } else {
                System.out.println("Enetered wrong option, please try again.");
            }

            System.out.println();
            System.out.println("--------------------------------------------------------");
        }

    }
}
