import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Stabilize extends Thread {

    Node node;
    boolean keepAlive;
    Logger logger;
    FileHandler fh;

    Stabilize(Node n) {
        node = n;
        keepAlive = true;
        try {
            logger = Logger.getLogger("MyLog_" + node.getPort());
            // This block configure the logger with handler and formatter
            fh = new FileHandler("/home/apurwa/IdeaProjects/ImplementationChord/Stabilize.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void stopThread() {
        keepAlive = false;
    }

    @SneakyThrows
    public void run() {
        while (keepAlive) {
            logger.info("Response : " + node.getPort());
            InetSocketAddress successor = node.getSuccessor();
            logger.info("Getsuccessor for  " + node.getPort() + "->" + successor);
            if (successor == null || successor.equals(node.getSocketAddress())) {
                logger.info("Succesor is null for  " + node.getPort());
                node.modifyFingerEntries(-3,null);
            }

            successor = node.getSuccessor();
            if (successor != null && !successor.equals(node.getSocketAddress())) {

                // get predecessor
                InetSocketAddress x = null;
                x = Auxiliary.requestAddress(successor, "GET-PREDECESSOR");
                logger.info("Getpredecessor for " +  node.getPort() + " successor  " + successor.getPort());
                if (x == null) {
                    logger.info("Deleting successor as x successor is null " + node.getPort());
                  //  System.out.println("Successor deleted " + node.getPort());
                    node.modifyFingerEntries(-1, null);
                }

                // else if successor's predecessor is not itself
                else if (!x.equals(successor)) {
                    logger.info("predecessor != successor for " +  node.getPort() + " successor  " + successor.getPort() + "->" + x.getPort());
                    long successorRelativeId = 0;
                    try {
                        successorRelativeId = Auxiliary.getRelativeId(Auxiliary.getHashAddress(successor), node.getNodeId());
                    } catch (NoSuchAlgorithmException e) {
                        //e.printStackTrace();
                    }
                    long xRelativeId = 0;
                    try {
                        xRelativeId = Auxiliary.getRelativeId(Auxiliary.getHashAddress(x), node.getNodeId());
                    } catch (NoSuchAlgorithmException e) {
                        //e.printStackTrace();
                    }
                    if (xRelativeId > 0 && xRelativeId < successorRelativeId) {
                        node.updateFingerTable(1, x);
                    }
                }
                // successor's predecessor is successor itself, then notify successor
                else {
                    node.notifySuccessor(successor);
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
