import lombok.SneakyThrows;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import static java.lang.Thread.sleep;

public class Stabilize implements Runnable {

    Node node;
    boolean keepAlive;
    private Thread worker;

    Stabilize(Node n) {
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

    @SneakyThrows
    public void run() {
        while (keepAlive) {
            InetSocketAddress successor = node.getSuccessor();
            if (successor == null || successor.equals(node.getSocketAddress())) {
                node.fillSuccessor();
            }


            successor = node.getSuccessor();
            if (successor != null && !successor.equals(node.getSocketAddress())) {

                // get predecessor
                InetSocketAddress x = null;
                try {
                    x = Auxiliary.requestAddress(successor, "GET-PREDECESSOR");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (x == null) {
                    try {
                        node.deleteSuccessor();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                // else if successor's predecessor is not itself
                else if (!x.equals(successor)) {
//                    System.out.println("REached HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    long successorRelativeId = 0;
                    try {
                        successorRelativeId = Auxiliary.getRelativeId(Auxiliary.getHashAddress(successor), node.getNodeId());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    long xRelativeId = 0;
                    try {
                        xRelativeId = Auxiliary.getRelativeId(Auxiliary.getHashAddress(x), node.getNodeId());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    if (xRelativeId > 0 && xRelativeId < successorRelativeId) {
                        node.updateFingerTable(1, x);
                    }
                }
                // successor's predecessor is successor itself, then notify successor
                else {
                    try {
                        node.notifySuccessor(successor);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                sleep(240);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
