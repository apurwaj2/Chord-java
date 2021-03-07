import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Auxiliary {

    public static long powerN(
            int number,
            int power
    ) {
        if(power == 0) return 1;
        int result = number;

        while(power > 1) {
            result*=number;
            power--;
        }

        return (long)result;
    }

    public static long getFingerId(long nodeID, int i, int m) {
        long a = powerN(2, i-1);
        long b = 8589934592L;
        long value = (nodeID + a) % b;

        return value;
    }

    public static long getHashAddress(InetSocketAddress socketAddress) throws NoSuchAlgorithmException {
       // System.out.println("Socket Address is: " + socketAddress);
        if(socketAddress != null) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(BigInteger.valueOf(socketAddress.hashCode()).toByteArray());
//            long num = toLong(messageDigest);
           // BigInteger num = new BigInteger(1, messageDigest);
            return socketAddress.getPort();
        }

        return 0;
    }

    public static long getHashKey(int key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(BigInteger.valueOf(key).toByteArray());
//        long num = toLong(messageDigest);
        return key;
    }

    public static long getRelativeId(long a, long b) {
        long c = a-b;
        if(c < 0) {
            c = c + 8589934592L;
        }
        return c;
    }

    public static String sendRequest(InetSocketAddress server, String request){

        String response = null;

        try (Socket socket = new Socket("127.0.0.1", server.getPort())) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
          //  System.out.println("Sending request to Socket Server");
            oos.writeObject(request);

            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //read the server response message
            try {
//                input = talkSocket.getInputStream();
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Cannot get input stream from "+server.toString()+"\nRequest is: "+ois+"\n");
            }

            if(ois != null) {
                response = (String) ois.readObject();
                ois.close();
            }

            oos.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static InetSocketAddress requestAddress(InetSocketAddress server, String request) {

        //  System.out.println("Entered requestAddress");
        String response = null;

        response = sendRequest(server, request);

        if (response == null) {
            return null;
        } else if (response.startsWith("NOTHING")) {
            return server;
        } else {
            InetSocketAddress ret =  new InetSocketAddress(Integer.parseInt(response));
            return ret;
        }
    }

}
