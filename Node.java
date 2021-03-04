import lombok.Builder;

import java.math.BigInteger;
import java.net.InetSocketAddress;

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
}
