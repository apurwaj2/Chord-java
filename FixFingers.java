import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static java.lang.Thread.sleep;

public class FixFingers implements Runnable {

        Node node;
        Random random;
        boolean keepAlive;
        private Thread worker;

        FixFingers(Node n) {
            node = n;
            keepAlive = true;
            random = new Random();
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
                int randomNum = random.nextInt(31) + 2;
                InetSocketAddress fingerId = null;
                try {
                    fingerId = node.findSuccessor(Auxiliary.getFingerId(node.getNodeId(),randomNum,32));
                } catch (NoSuchAlgorithmException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                node.updateFingerTable(randomNum, fingerId);


                try {
                    sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
}
