import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;


public class Commands {

    private static Node node;


    public static boolean create(int port) throws NoSuchAlgorithmException {
        //create socketaddress
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        BigInteger id = Auxiliary.getHashAddress(socketAddress);

        //create node and related
        node = Node.builder().socketAddress(socketAddress).port(port).nodeId(id).fingerTable(new FingerTable()).build();

        //Start Listener
        Listener listener = new Listener(node);
        node.setListener(listener);
        listener.start();

        //set successor and predecessor
        node.setPredecessor(null);
        node.setSuccessor(socketAddress);

        Main.masterLookup.put(port, node);

        return true;
    }

    public static boolean delete(int port) {

        Node node = Main.masterLookup.get(port);
        node.getListener().stop();
        Main.masterLookup.remove(port);
        return true;
    }

    public static Collection<Node> list() {
        return Main.masterLookup.values();
    }

    public static void getKey(int port, int key) throws ClassNotFoundException, NoSuchAlgorithmException {

        BigInteger keyId = Auxiliary.getHashKey(key);
        String keyHash =  keyId.toString(16);

        System.out.println("Hash value is : " + keyHash);

        String request = "FIND-SUCCESSOR_" + keyHash;
        InetSocketAddress result = Auxiliary.requestAddress(node.getSocketAddress(), request);

        // if fail to send request, local node is disconnected, exit
        if (result == null) {
            System.out.println("Unable to contact node!");
            return;
        }
        // print out response
        System.out.println("Response from node : " + node.getPort() + "  Key present at node " + result.getPort());

    }
}
