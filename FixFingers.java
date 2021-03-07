import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class FixFingers extends Thread {

    Node node;
    Random random;
    boolean keepAlive;
    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    FixFingers(Node n) {
        node = n;
        keepAlive = true;
        random = new Random();

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("/home/apurwa/IdeaProjects/ImplementationChord/FixFingers.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        keepAlive = false;
    }

    @SneakyThrows
    public void run() {
        while (keepAlive) {
            int randomNum = random.nextInt(31) + 2;
            InetSocketAddress fingerId = null;
            try {
                fingerId = node.findSuccessor(Auxiliary.getFingerId(node.getNodeId(),randomNum,32));
            } catch (NoSuchAlgorithmException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            node.updateFingerTable(randomNum, fingerId);
            logger.info("updating finger entry for " + node.getPort() + " at " + randomNum + " -> " + fingerId.getPort() );

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
