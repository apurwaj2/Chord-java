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
            System.out.println();

            option = input.nextLine();
            System.out.println("Entered option : " + option);

            if (option.equals("create")) {

                //port number
                System.out.println("Enter the port number for creating node:");
                System.out.println();

                int port = input.nextInt();
                if(Commands.create(port)) {
                    System.out.println("Chord ring created successfully");
                } else {
                    System.out.println("Could not create chord ring");
                }

            } else if (option.equals("join")) {

                System.out.println("Enter the port number for joining node:");
                System.out.println();
                int port1 = input.nextInt();

                System.out.println("Enter the port number of the node to join the ring:");
                System.out.println();
                int port2 = input.nextInt();

                Commands.create(port1);
                Node n = masterLookup.get(port1);
                Commands.join(n, port2);

            } else if (option.equals("delete")) {

                System.out.println("Enter the port number for deleting node:");
                System.out.println();
                int port = input.nextInt();
                Commands.delete(port);

            } else if (option.equals("list")) {

                System.out.println("Listing nodes");
                for(Node node: Commands.list())
                {
                    System.out.println(node.getNodeId() + " : " + node.getPort());
                    System.out.println("Predecessor: " + node.getPredecessor());
                    System.out.println("Successor: " + node.getSuccessor());
                    System.out.println("Finger Table: " + node.getFingerTable());
                }
            }
            else if (option.equals("getkey")) {

                System.out.println("Enter the port number to search key :");
                System.out.println();
                int port = input.nextInt();
                System.out.println("Enter the key:");
                System.out.println();
                int key = input.nextInt();
                Commands.getKey(port, key);

            } else if (option.equals("quit")) {
                System.out.println("Ending the program..");
                for(Node node: Commands.list())
                {
                    Commands.delete(node.getPort());
                }
                System.exit(0);
            } else {
                System.out.println("Entered wrong option, please try again.");
            }

            System.out.println();
            System.out.println("--------------------------------------------------------");
        }

    }
}
