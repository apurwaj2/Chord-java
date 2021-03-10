import io.github.woodenbell.pprint.CollectionPrint;
import io.github.woodenbell.pprint.ObjectPrint;
import io.github.woodenbell.pprint.Util;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static Map<Integer, Node> masterLookup = new ConcurrentHashMap<>();

    public static void main (String[] args) throws NoSuchAlgorithmException, ClassNotFoundException {

        int mainPort = 0;
        String option, str[];

        Scanner input = new Scanner(System.in);

        while(true) {

            System.out.println();
            System.out.println("Enter anyone of the following options:");
            System.out.println("create <port>, join <port>, delete <port>, list, getkey <key> <port>, quit");
            System.out.println();

            option = input.nextLine();
            System.out.println("Entered option : " + option);

            if (option.startsWith("create")) {
                //to extract port number
                str = option.split(" ");

                if(str.length == 1) {
                    System.out.println("Correct usage: create <port>");
                    continue;
                }

                int port = Integer.parseInt(str[1]);

                if(Commands.create(port)) {
                    System.out.println("Chord ring created successfully");
                } else {
                    System.out.println("Could not create chord ring");
                }

                mainPort = port;
            } else if (option.startsWith("join")) {

                //to extract port number
                str = option.split(" ");

                if(str.length == 1) {
                    System.out.println("Correct usage: join <port>");
                    continue;
                }

                int port = Integer.parseInt(str[1]);

                Commands.create(port);
                Node n = masterLookup.get(port);
                Commands.join(n, mainPort);

            } else if (option.startsWith("delete")) {

                //to extract port number
                str = option.split(" ");
                if(str.length == 1) {
                    System.out.println("Correct usage: delete <port>");
                    continue;
                }
                int port = Integer.parseInt(str[1]);

                Commands.delete(port);

            } else if (option.equals("list")) {

                System.out.println("Listing nodes");
                for(Node node: Commands.list())
                {
                    ObjectPrint.pprint(node, true, true, Util.TableFormat.UNDERSCORE);
//                    System.out.println(node.getNodeId() + " : " + node.getPort());
//                    System.out.println("Predecessor: " + node.getPredecessor());
//                    System.out.println("Successor: " + node.getSuccessor());
//                    System.out.println("Finger Table: " + node.getFingerTable());
                }
//                CollectionPrint.pprint(Collections.singletonList(Commands.list()), true);
            }
            else if (option.startsWith("getkey")) {

                //to extract port number
                str = option.split(" ");
                if(str.length < 3) {
                    System.out.println("Correct usage: getkey <key> <port>");
                    continue;
                }
                int key = Integer.parseInt(str[1]);
                int port = Integer.parseInt(str[2]);

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
