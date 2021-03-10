import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;


public class Commands {

    private static Node node;


    public static boolean create(int port) throws NoSuchAlgorithmException {
        //create socketaddress
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        long id = Auxiliary.getHashAddress(socketAddress);

        //create node and related
        FingerTable ft = new FingerTable();
        node = new Node(port, socketAddress, id, ft);

        //Start Listener
        Listener listener = new Listener(node);
        node.setListener(listener);
        listener.start();

        //Start FixFingers
        FixFingers fixFingers = new FixFingers(node);
        node.setFixFingers(fixFingers);
        fixFingers.start();

        //Start Stabilize
        Stabilize stabilize = new Stabilize(node);
        node.setStabilize(stabilize);
        stabilize.start();

        //Start CheckPredecessor
        CheckPredecessor checkPredecessor = new CheckPredecessor(node);
        node.setCheckPredecessor(checkPredecessor);
        checkPredecessor.start();

        //set successor and predecessor
        node.setPredecessor(null);

        Main.masterLookup.put(port, node);

        return true;
    }

    public static void join(Node node, int portMain) throws NoSuchAlgorithmException, ClassNotFoundException {

        // Trying to join an existing ring
        InetSocketAddress address = new InetSocketAddress(portMain);
        String id = String.valueOf(node.getNodeId());
     //   System.out.println("Join node hash id value is : " + id);
        String request = "FIND-SUCCESSOR_" + id;
        InetSocketAddress successor = Auxiliary.requestAddress(address, request);
        if(successor == null) {
            System.out.println("Cannot find the node to join the ring");
            return;
        }

        node.updateFingerTable(1, successor);


    }

    public static void delete(int port) {

        Node node = Main.masterLookup.get(port);
        node.getListener().stopThread();
        node.getCheckPredecessor().stopThread();
        node.getFixFingers().stopThread();
        node.getStabilize().stopThread();
        Main.masterLookup.remove(port);
    }

    public static Collection<Node> list() {
        return Main.masterLookup.values();
    }

    public static void getKey(int port, int key) throws ClassNotFoundException, NoSuchAlgorithmException {

        long keyId = Auxiliary.getHashKey(key);
        String keyHash =  String.valueOf(keyId);

      //  System.out.println("Hash value is : " + keyHash);

        String request = "FIND-SUCCESSOR_" + keyHash;
        InetSocketAddress result = Auxiliary.requestAddress(node.getSocketAddress(), request);

        // if fail to send request, local node is disconnected, exit
        if (result == null) {
            System.out.println("Unable to contact node " + node.getPort());
            return;
        }
        // print out response
        System.out.println("Response from node : " + node.getPort() + "  Key present at node " + result.getPort());

    }
}
