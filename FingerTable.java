import java.net.InetSocketAddress;
import java.util.HashMap;

public class FingerTable {

    private HashMap<Integer, InetSocketAddress> fingerTable;

    FingerTable() {
        fingerTable = new HashMap<Integer, InetSocketAddress>();
        for(int i = 1; i <= 32;  i++) {
            updateFingerEntry(i, null);
        }
    }

    public InetSocketAddress getFingerEntry(Integer i) {
        return fingerTable.get(i);
    }

    public void updateFingerEntry(Integer i, InetSocketAddress socketAddress) {
        fingerTable.put(i, socketAddress);

    }

    public void deleteFingerEntry(InetSocketAddress address) {
        for (int i = 32; i > 0; i--) {
            InetSocketAddress fingerAddress = fingerTable.get(i);
            if(fingerAddress != null && fingerAddress.equals(address))
                fingerTable.put(i, null);
        }
    }

    @Override
    public String toString() {
        return "FingerTable{" +
                "fingerTable=" + fingerTable +
                '}';
    }

    public int size() {
        return fingerTable.size();
    }
}
