import java.net.InetSocketAddress;
import java.util.HashMap;

public class FingerTable {

    private HashMap<Integer, InetSocketAddress> fingerTable;

    FingerTable() {
        fingerTable = new HashMap<Integer, InetSocketAddress>();
        for(int i = 1; i <= 32;  i++) {
            updateFingerTable(i, null);
        }
    }

    public InetSocketAddress getFingerEntry(Integer i) {
        return fingerTable.get(i);
    }

    public void updateFingerTable(Integer i, InetSocketAddress socketAddress) {
        fingerTable.put(i, socketAddress);
    }

    public void deleteFingerEntry(InetSocketAddress address) {
        for (int i = 32; i > 0; i--) {
            InetSocketAddress fingerAddress = fingerTable.get(i);
            if(fingerAddress == address)
                fingerTable.put(i, null);
        }
    }
}
