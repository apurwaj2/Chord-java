import java.io.*;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static Map<Integer, Node> masterLookup = new ConcurrentHashMap<>();

    public static void main (String[] args) throws NoSuchAlgorithmException, ClassNotFoundException {

        String nodeIP = "localhost";
        String option;

        Scanner input = new Scanner(System.in);

        while(true) {

            System.out.println();
            System.out.println("Enter anyone of the following options:");
            System.out.println("create, join, delete, list, getkey, quit");

            option = input.nextLine();
            System.out.println("Entered option : " + option);

            if (option.equals("create")) {
                System.out.println("Create");

                //port number
                System.out.println("Enter the port number for creating node:");
//                System.out.println(input.nextInt());
                int port = input.nextInt();
                if(Auxiliary.create(port)) {
                    System.out.println("Chord ring created successfully");
                } else {
                    System.out.println("Could not create chord ring");
                }

            } else if (option.equals("join")) {
                System.out.println("Join");

            } else if (option.equals("delete")) {
                System.out.println("Delete");
                System.out.println("Enter the port number for deleting node:");
                int port = input.nextInt();
                Auxiliary.delete(port);

            } else if (option.equals("list")) {
                System.out.println("Listing nodes");

                for(Node node: Auxiliary.list())
                {
                    System.out.println(node.getNodeId() + ":" + node.getPort() + ":" + node.getFingerTable());
                }
            }
            else if (option.equals("getkey")) {
                System.out.println("getKey");
                System.out.println("Enter the port number to search key :");
                int port = input.nextInt();
                System.out.println("Enter the key:");
                int key = input.nextInt();
                Auxiliary.getKey(port, key);

            } else if (option.equals("quit")) {
                System.out.println("Ending the program..");
                break;
            } else {
                System.out.println("Entered wrong option, please try again.");
            }

            System.out.println();
            System.out.println("--------------------------------------------------------");
        }

    }
}
