import java.net.InetSocketAddress;

import static java.lang.Thread.sleep;

public class CheckPredecessor implements Runnable {

    Node node;
    boolean keepAlive;
    private Thread worker;

    CheckPredecessor(Node n) {
        node = n;
        keepAlive = true;
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stopThread() {
        keepAlive = false;
    }

    public void run() {
        while (keepAlive) {
            InetSocketAddress predecessor = node.getPredecessor();
            if (predecessor != null){
                String response = null;
                try {
                    response = Auxiliary.sendRequest(predecessor, "KEEP");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (response == null || !response.equals("ALIVE")) {
                    node.setPredecessor(null);
                }
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
