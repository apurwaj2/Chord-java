import java.net.InetSocketAddress;


public class CheckPredecessor extends Thread {

    Node node;
    boolean keepAlive;

    CheckPredecessor(Node n) {
        node = n;
        keepAlive = true;
    }

    public void stopThread() {
        keepAlive = false;
    }

    public void run() {
        while (keepAlive) {
            InetSocketAddress predecessor = node.getPredecessor();
            if (predecessor != null){
                String response = null;
                response = Auxiliary.sendRequest(predecessor, "KEEP");
                if (response == null || !response.equals("ALIVE")) {
                    node.setPredecessor(null);
                }
            }
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
