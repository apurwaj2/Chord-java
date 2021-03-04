import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

public class Auxiliary {

    Node node;

    public static boolean create(int port) throws NoSuchAlgorithmException {
        //create socketaddress
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(BigInteger.valueOf(socketAddress.hashCode()).toByteArray());
        BigInteger no = new BigInteger(1, messageDigest);

        //create node and related
        Node node = Node.builder().socketAddress(socketAddress).port(port).nodeId(no).build();

        //Start Listener
        Listener listener = new Listener(node);
        node.setListener(listener);
        listener.start();

        Main.masterLookup.put(port, node);

        return true;
    }

    public static boolean delete(int port) {

        Node node = Main.masterLookup.get(port);
        node.getListener().stopListner();
        Main.masterLookup.remove(port);
        return true;
    }

    public static Collection<Node> list() {
        return Main.masterLookup.values();
    }

    public static void getKey(int port, int key) throws ClassNotFoundException {

        try (Socket socket = new Socket("127.0.0.1", port)) {

//            OutputStream output = socket.getOutputStream();
//            PrintWriter writer = new PrintWriter(output, true);
//            writer.println(key);
//            InputStream input = socket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

//            String time = reader.readLine();
//            System.out.println(time);


            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            oos.writeObject(String.valueOf(key));
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            //close resources
            ois.close();
            oos.close();


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
