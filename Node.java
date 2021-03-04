import lombok.Builder;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;


@Builder
public class Node {
    private BigInteger nodeId;
    private int port;
    private InetSocketAddress socketAddress;
    private InetSocketAddress predecessor;
    private InetSocketAddress successor;
    private FingerTable fingerTable;

    private Listener listener;

    public void setNodeId(BigInteger nodeId) {
        this.nodeId = nodeId;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void setPredecessor(InetSocketAddress predecessor) {
        this.predecessor = predecessor;
    }

    public void setSuccessor(InetSocketAddress successor) {
        this.successor = successor;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public BigInteger getNodeId() {
        return nodeId;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public InetSocketAddress getPredecessor() {
        return predecessor;
    }

    public InetSocketAddress getSuccessor() {
        return successor;
    }

    public Listener getListener() {
        return listener;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(FingerTable fingerTable) {
        this.fingerTable = fingerTable;
    }

    public InetSocketAddress findSuccessor(BigInteger keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

        System.out.println("Inside FindSuccessor");
        InetSocketAddress  successor = getSuccessor();

        //find the predecessor of the keyId
        InetSocketAddress predecessor = findPredecessor(keyId);
        if(predecessor != getSocketAddress()) {
            successor = Auxiliary.requestAddress(predecessor, "GET-SUCCESSOR");
        }
        // no other node is present
        if(successor == null) {
            successor = getSocketAddress();
        }

        return successor;
    }

    public InetSocketAddress closest_preceding_finger(BigInteger keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

        BigInteger relativeKeyId = Auxiliary.getRelativeId(keyId, nodeId);

        for(int i = 32; i > 0; i--) {
            InetSocketAddress fingerAddress = fingerTable.getFingerEntry(i);
            if(fingerAddress == null)
                continue;

            BigInteger fingerId = Auxiliary.getHashAddress(fingerAddress);
            BigInteger relativeFingerId = Auxiliary.getRelativeId(fingerId, nodeId);

            //relativeFingerId > 0 && relativeFingerId < relativeKeyId
            if (relativeFingerId.compareTo(BigInteger.ZERO) > 0 && relativeFingerId.compareTo(relativeKeyId) < 0)  {
                String response  = Auxiliary.sendRequest(fingerAddress, "KEEP");
                if (response!=null &&  response.equals("ALIVE")) {
                    return fingerAddress;
                }

                // remove it from finger table
                else {
                    fingerTable.deleteFingerEntry(fingerAddress);
                }
            }

        }
        return socketAddress;
    }

    public InetSocketAddress findPredecessor(BigInteger keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

        System.out.println("Entered findPredecessor");

        InetSocketAddress n = getSocketAddress();
        InetSocketAddress nSuccessor = getSuccessor();
        BigInteger relative_successor_id = Auxiliary.getRelativeId(Auxiliary.getHashAddress(nSuccessor), nodeId);
        BigInteger relative_key_id = Auxiliary.getRelativeId(keyId, nodeId);
        InetSocketAddress most_recently_alive = n;


        System.out.println("relative_key_id " + relative_key_id);
        System.out.println("relative_successor_id " + relative_successor_id);

        //check this condition properly
        while(!(relative_key_id.compareTo(BigInteger.ZERO) > 0 && relative_successor_id.compareTo(relative_key_id) > 0)) {

            InetSocketAddress current = n;

            if(n == getSocketAddress()) {
                n = closest_preceding_finger(keyId);
            } else {
                String key = new String(keyId.toByteArray());
                String request = "CLOSEST-FINGER_" + key;
                InetSocketAddress result = Auxiliary.requestAddress(n, request);

                // if fail to get response, set n to most recently
                if (result == null) {
                    n = most_recently_alive;
                    nSuccessor = Auxiliary.requestAddress(n, "GET-SUCCESSOR");
                    //if nSuccessor is null
                    if (nSuccessor == null) {
                        return getSocketAddress();
                    }
                    continue;
                }

                // if n's closest is itself, return n
            else if (result == n)
                    return result;

                    // else n's closest is other node "result"
                else {
                    most_recently_alive = n;
                    nSuccessor = Auxiliary.requestAddress(result, "GET-SUCCESSOR");;
                    // if we can get its response, then "result" must be our next n
                    if (nSuccessor != null) {
                        n = result;
                    } else {
                        nSuccessor = Auxiliary.requestAddress(n, "GET-SUCCESSOR");
                    }
                }

                // compute relative ids for while loop judgement
                relative_successor_id = Auxiliary.getRelativeId(Auxiliary.getHashAddress(nSuccessor), Auxiliary.getHashAddress(n));
                relative_key_id = Auxiliary.getRelativeId(keyId, Auxiliary.getHashAddress(n));

            }

            if(current == n)
                break;

        }

        return n;
    }

}
