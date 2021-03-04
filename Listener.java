import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Listener implements Runnable {

    private Node node;
    private ServerSocket server;
    private AtomicBoolean keepAlive;
    private Thread worker;
    private int interval;


    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        keepAlive.set(false);
    }

    public Listener(Node n) {
        node = n;
        keepAlive = new AtomicBoolean(true);
        try {
            server = new ServerSocket(node.getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port " + node.getPort());
        }

    }

    private String processRequest (String request) throws ClassNotFoundException, NoSuchAlgorithmException {

        System.out.println("Processing the request : " + request);

        InetSocketAddress result = null;
        String ret = null;
        if (request  == null) {
            return null;
        }
        if (request.startsWith("CLOSEST-FINGER")) {
            String[] s = request.split("_");
            BigInteger id = new BigInteger(s[1], 16);
            result = node.closest_preceding_finger(id);
            int port = result.getPort();
            ret = String.valueOf(port);
        }
        else if (request.startsWith("GET-SUCCESSOR")) {
            result = node.getSuccessor();
            if (result != null) {
                int port = result.getPort();
                ret = String.valueOf(port);
            }
            else {
                ret = "NULL";
            }
        }
        else if (request.startsWith("GET-PREDECESSOR")) {
            result = node.getPredecessor();
            if (result != null) {
                int port = result.getPort();
                ret = String.valueOf(port);
            }
            else {
                ret = "NULL";
            }
        } else if (request.startsWith("FIND-SUCCESSOR")) {
            String[] s = request.split("_");
            BigInteger id = new BigInteger(s[1], 16);
            result = node.findSuccessor(id);
            int port = result.getPort();
            ret = String.valueOf(port);

        } else if (request.startsWith("TELL-SUCCESSOR")) {
            String[] s = request.split("_");
            int port = Integer.parseInt(s[1]);
            InetSocketAddress new_pre = new InetSocketAddress(port);
           // node.notifySuccessor(new_pre);
            ret = "NOTIFIED";
        } else if (request.startsWith("KEEP")) {
            ret = "ALIVE";
        }
        return ret;
    }

    @Override
    public void run()
    {
        while(keepAlive.get()) {

            try {
                Socket clientSocket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String request = (String) ois.readObject();
                System.out.println("Message Received on port: " + node.getPort() + "\n request: " + request);

                //Send response
                String response = processRequest(request);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject(response);
                //close resources
                ois.close();
                oos.close();

                clientSocket.close();
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.err.println("Could not accept connection on port " + node.getPort());
            }

        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
