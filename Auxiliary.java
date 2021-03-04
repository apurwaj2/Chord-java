import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Auxiliary {

    public static BigInteger getHashAddress(InetSocketAddress socketAddress) throws NoSuchAlgorithmException {
        System.out.println("Socket Address is: " + socketAddress);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(BigInteger.valueOf(socketAddress.hashCode()).toByteArray());
        BigInteger num = new BigInteger(1, messageDigest);
        return num;
    }

    public static BigInteger getHashKey(int key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = md.digest(BigInteger.valueOf(key).toByteArray());
        BigInteger num = new BigInteger(1, messageDigest);
        return num;
    }

    public static BigInteger getRelativeId(BigInteger a, BigInteger b) {
        BigInteger c = a.subtract(b);
        if(c.compareTo(BigInteger.ZERO) < 0) {
            c = c.add(BigDecimal.valueOf(Math.pow(2, 32)).toBigInteger());
        }
        return c;
    }

    public static String sendRequest(InetSocketAddress server, String request) throws ClassNotFoundException {

        String response = null;

        try (Socket socket = new Socket("127.0.0.1", server.getPort())) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            oos.writeObject(request);
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            response = (String) ois.readObject();
            System.out.println("Response: " + response);
            //close resources
            ois.close();
            oos.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static InetSocketAddress requestAddress(InetSocketAddress server, String request) throws ClassNotFoundException {

        System.out.println("Entered requestAddress");
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
