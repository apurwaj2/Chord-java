import java.net.InetSocketAddress;
import java.util.Map;

public class FingerTable {

    private Map<String, InetSocketAddress> entries;

    public InetSocketAddress getFingerEntry(String nodeId) {
        return entries.get(nodeId);
    }

    public void updateFingerTable(String nodeId, InetSocketAddress socketAddress) {
        entries.put(nodeId, socketAddress);
    }

    public void deleteFingerEntry(String nodeId) {
        entries.remove(nodeId);
    }
}
