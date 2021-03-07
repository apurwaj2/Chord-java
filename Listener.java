import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Listener extends Thread{

    private Node node;
    private ServerSocket server;
    private boolean keepAlive;

    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;


    public void stopThread() {
        keepAlive = false;
    }

    public Listener(Node n) {
        node = n;
        keepAlive = true;
        try {
            server = new ServerSocket(node.getPort());
        } catch (IOException e) {
            System.out.println("Could not listen on port " + node.getPort());
        }

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("/home/apurwa/IdeaProjects/ImplementationChord/MyLogFile.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        private String processRequest (String request) throws ClassNotFoundException, NoSuchAlgorithmException {

        if(request.startsWith("FIND") || request.startsWith("TELL"))
        System.out.println("Processing the request at : " + node.getPort() + " -> " + request);

        if(!request.startsWith("KEEP"))
        logger.info("Processing the request at : " + node.getPort() + " -> " + request);

        InetSocketAddress result = null;
        String ret = null;
        if (request  == null) {
            return null;
        }
        if (request.startsWith("CLOSEST-FINGER")) {
            String[] s = request.split("_");
            long id = Long.parseLong(s[1]);
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
            long id = Long.parseLong(s[1]);
            result = node.findSuccessor(id);
            int port = result.getPort();
            ret = String.valueOf(port);

        } else if (request.startsWith("TELL-SUCCESSOR")) {
            String[] s = request.split("_");
            int port = Integer.parseInt(s[1]);
            InetSocketAddress new_pre = new InetSocketAddress(port);
            node.handleNotification(new_pre);
            ret = "NOTIFIED";
        } else if (request.startsWith("KEEP")) {
            ret = "ALIVE";
        }

        if(request.startsWith("FIND") || request.startsWith("TELL"))
            System.out.println("Response : " + node.getPort() + " -> " + ret);

        if(!request.startsWith("KEEP"))
        logger.info("Response : " + node.getPort() + " -> " + ret);

        return ret;
    }

    @Override
    public void run()
    {
        while(keepAlive) {

            try {
                Socket clientSocket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String request = (String) ois.readObject();
//                System.out.println("Message Received on port: " + node.getPort() + "\n request: " + request);

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
                System.out.println("Could not accept connection on port " + node.getPort());
                throw new RuntimeException("Cannot accept connection request", e);
            }

        }
    }
}
