import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Listener extends Thread {

    private Node node;
    private ServerSocket server;
    private boolean keepAlive;

    Listener(Node n) {
        node = n;
        keepAlive = true;

        try {
            server = new ServerSocket(node.getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port " + node.getPort());
        }

    }


    public void run()
    {
        while(keepAlive) {

            try {
                Socket clientSocket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String request = (String) ois.readObject();
                System.out.println("Message Received on port: " + node.getPort() + "\n request: " + request);

                //Send response
                String response = "Done";
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject("Response " + response);
                //close resources
                ois.close();
                oos.close();

                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
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

    public void stopListner() {
        keepAlive = false;
    }
}
