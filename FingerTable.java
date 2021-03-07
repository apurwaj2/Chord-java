import io.github.woodenbell.pprint.PrettyPrintable;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class FingerTable implements PrettyPrintable {

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
        return "" + fingerTable;
    }

    public int size() {
        return fingerTable.size();
    }

    @Override
    public boolean ppIsRecursive() {
        return false;
    }

    @Override
    public boolean ppHasKeys() {
        return true;
    }

    @Override
    public Object[] ppGetKeys() {
        Object[] k = new Object[fingerTable.size()];

        int i = 0;
        for (Integer key: fingerTable.keySet()) {
            k[i] = key;
            i++;
        }
        return k;
    }

    @Override
    public Object[] ppGetValues() {
        Object[] k = new Object[fingerTable.size()];

        int i = 0;
        for (Integer key: fingerTable.keySet()) {
            k[i] = fingerTable.get(key);
            i++;
        }
        return k;
    }
}
