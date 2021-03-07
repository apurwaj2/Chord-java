import io.github.woodenbell.pprint.PrettyPrintable;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;


public class Node implements PrettyPrintable {
    private long nodeId;
    private int port;
    private InetSocketAddress socketAddress;
    private InetSocketAddress predecessor;
    private InetSocketAddress successor;
    private FingerTable fingerTable;

    private Listener listener;
    private FixFingers fixFingers;
    private Stabilize stabilize;
    private CheckPredecessor checkPredecessor;

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public synchronized void setPredecessor(InetSocketAddress predecessor) {
        System.out.println("setting predecessor of " + this.port + ":" + ((predecessor==null)? " ":predecessor.getPort()));
        this.predecessor = predecessor;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public long getNodeId() {
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
        if (fingerTable != null && fingerTable.size() > 0) {
            return fingerTable.getFingerEntry(1);
        }
        return null;
    }

    public Listener getListener() {
        return listener;
    }

    public FixFingers getFixFingers() {
        return fixFingers;
    }

    public void setFixFingers(FixFingers fixFingers) {
        this.fixFingers = fixFingers;
    }

    public Stabilize getStabilize() {
        return stabilize;
    }

    public void setStabilize(Stabilize stabilize) {
        this.stabilize = stabilize;
    }

    public CheckPredecessor getCheckPredecessor() {
        return checkPredecessor;
    }

    public void setCheckPredecessor(CheckPredecessor checkPredecessor) {
        this.checkPredecessor = checkPredecessor;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(FingerTable fingerTable) {
        this.fingerTable = fingerTable;
    }

    Node(int p, InetSocketAddress address, long id, FingerTable ft) {
        setPort(p);
        setNodeId(id);
        setSocketAddress(address);
        setFingerTable(ft);
    }

    public InetSocketAddress findSuccessor(long keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

      //  System.out.println("Inside FindSuccessor");
        InetSocketAddress  succ = getSuccessor();

        //find the predecessor of the keyId
        InetSocketAddress pred = findPredecessor(keyId);
        if(!pred.equals(getSocketAddress())) {
            succ = Auxiliary.requestAddress(pred, "GET-SUCCESSOR");
        }
        // no other node is present
        if(succ == null) {
            succ = getSocketAddress();
        }

        return succ;
    }

    public InetSocketAddress closest_preceding_finger(long keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

        long relativeKeyId = Auxiliary.getRelativeId(keyId, nodeId);

        for(int i = 32; i > 0; i--) {
            InetSocketAddress fingerAddress = fingerTable.getFingerEntry(i);
            if(fingerAddress == null)
                continue;

            long fingerId = Auxiliary.getHashAddress(fingerAddress);
            long relativeFingerId = Auxiliary.getRelativeId(fingerId, nodeId);

            //relativeFingerId > 0 && relativeFingerId < relativeKeyId
            if (relativeFingerId > 0 && relativeFingerId <relativeKeyId)  {
//                System.out.println("hello..................................................");
                String response  = Auxiliary.sendRequest(fingerAddress, "KEEP");
                if (response!=null &&  response.equals("ALIVE")) {
                    return fingerAddress;
                }

                // remove it from finger table
                else {
                    modifyFingerEntries(-2, fingerAddress);
                }
            }

        }
        return socketAddress;
    }

    public InetSocketAddress findPredecessor(long keyId) throws NoSuchAlgorithmException, ClassNotFoundException {

       // System.out.println("Entered findPredecessor");

        InetSocketAddress n = socketAddress;
        InetSocketAddress nSuccessor = getSuccessor();
        long relative_successor_id = Auxiliary.getRelativeId(Auxiliary.getHashAddress(nSuccessor), nodeId);
        long relative_key_id = Auxiliary.getRelativeId(keyId, nodeId);
        InetSocketAddress most_recently_alive = n;


//        System.out.println("relative_key_id " + relative_key_id);
//        System.out.println("relative_successor_id " + relative_successor_id);

        //check this condition properly
        while(!(relative_key_id > 0 && relative_key_id <= relative_successor_id)) {

            InetSocketAddress current = n;

            if(n.equals(getSocketAddress())) {
                n = closest_preceding_finger(keyId);
            } else {
//                System.out.printl!=n("inside while");
                String key = String.valueOf(keyId);
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
            else if (result.equals(n))
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

            if(current.equals(n))
                break;

        }

        return n;
    }

    public String notifySuccessor(InetSocketAddress S) {
        if (S != null && !S.equals(getSocketAddress())) {
            String port = String.valueOf(getPort());
            String request = "TELL-SUCCESSOR_" + port;
            return Auxiliary.sendRequest(S, request);
        } else {
            return null;
        }
    }


    public void handleNotification (InetSocketAddress newPredecessor) throws NoSuchAlgorithmException, ClassNotFoundException {
        System.out.println("handle notification : " + getPort() + " -> " + (newPredecessor==null?" ":newPredecessor.getPort()));
        if (getPredecessor() == null || getPredecessor().equals(newPredecessor)) {
            setPredecessor(newPredecessor);
        }
        else {

            String response  = Auxiliary.sendRequest(getPredecessor(), "KEEP");
            System.out.println("Response for check KEEP for " + getPredecessor() + "->" + response);
            if (response == null || !response.equals("ALIVE")) {
                setPredecessor(newPredecessor);
                return;
            }

            long oldPredecessor = Auxiliary.getHashAddress(getPredecessor());
            long oldPredecessorRelativeId = Auxiliary.getRelativeId(nodeId, oldPredecessor);
            long newPredecessorRelativeId = Auxiliary.getRelativeId(Auxiliary.getHashAddress(newPredecessor), oldPredecessor);
            System.out.println("newrelative " + newPredecessorRelativeId + " oldrelative " + oldPredecessorRelativeId);
            if (newPredecessorRelativeId > 0 && newPredecessorRelativeId < oldPredecessorRelativeId)
                setPredecessor(newPredecessor);
            else {
                System.out.println("Failed to set predecessor");
            }
        }
    }

    public void fillSuccessor() {
        InetSocketAddress succ = getSuccessor();
        if (succ == null || succ.equals(socketAddress)) {
            for (int i = 2; i <= 32; i++) {
                InetSocketAddress fingerId = fingerTable.getFingerEntry(i);
                if (fingerId != null && !fingerId.equals(socketAddress)) {
                    for (int j = i-1; j >= 1; j--) {
                        updateFingerTable(j, fingerId);
                    }
                    break;
                }
            }
        }

        //Setting predecessor as successor -> just two nodes
        if ((succ == null || succ.equals(socketAddress)) &&
        predecessor != null && !predecessor.equals(socketAddress)) {
            updateFingerTable(1, predecessor);
        }
    }

    public void deleteSuccessor(){

        // Already null
        InetSocketAddress succ = getSuccessor();
        if (succ == null)
            return;

        // get last entry of successor
        int i = 32;
        for (i = 32; i > 0; i--) {
            InetSocketAddress fingerId = fingerTable.getFingerEntry(i);
            if (fingerId != null && fingerId.equals(succ))
                break;
        }

        // delete it
        for (int j = i; j >= 1 ; j--) {
            updateFingerTable(j, null);
        }

        // if predecessor is successor, delete it
        if (predecessor!= null && predecessor.equals(succ)) {
            setPredecessor(null);
        }
        // try to fill successor
        fillSuccessor();

        // if successor is still null or local node,
        // and the predecessor is another node, keep asking
        // it's predecessor until find local node's new successor
        if ((succ == null || succ.equals(getSuccessor())) &&
            predecessor != null && !predecessor.equals(socketAddress)) {

            InetSocketAddress p = predecessor;
            InetSocketAddress p_pre = null;
            while (true) {
                p_pre = Auxiliary.requestAddress(p, "GET-PREDECESSOR");
                if (p_pre == null)
                    break;

                // if p's predecessor is node is just deleted,
                // or itself (nothing found in p), or local address,
                // p is current node's new successor, break
                if (p_pre.equals(p) || p_pre.equals(socketAddress) || p_pre.equals(succ)) {
                    break;
                }

                // else, keep asking
                else {
                    p = p_pre;
                }
            }

            // update successor
            updateFingerTable(1, p);
        }
    }

    public void updateFingerTable(int i, InetSocketAddress address){
        fingerTable.updateFingerEntry(i, address);

        if (i == 1 && address != null && !socketAddress.equals(address)) {
            notifySuccessor(address);
        }
    }

    public synchronized void modifyFingerEntries(int position, InetSocketAddress address) {

        if (position > 0 && position <= 32) {
            updateFingerTable(position, address);
        } else if (position == -1) {
            deleteSuccessor();
        } else if (position == -2) {
            fingerTable.deleteFingerEntry(address);
        } else if (position == -3) {
            fillSuccessor();
        } else {
            System.out.println("Invalid option for modifyFingerEntries");
        }

    }


    @Override
    public boolean ppIsRecursive() {
        return true;
    }

    @Override
    public boolean ppHasKeys() {
        return true;
    }

    @Override
    public Object[] ppGetKeys() {
        return new String[] {"Current Node","Successor", "Predecessor", "FingerTable"};
    }

    @Override
    public Object[] ppGetValues() {
        return new Object[] {getPort(), (getSuccessor() == null ? "NULL": getSuccessor().getPort()), (getPredecessor() == null ? "NULL": getPredecessor().getPort()), getFingerTable()};
    }
}
