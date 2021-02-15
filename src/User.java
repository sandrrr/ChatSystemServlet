import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class User {
    private String username;
    private InetAddress addressIP;
    private String addressMAC;
    private Socket socket;
    private ObjectOutputStream out;

    public User(String username, InetAddress addressIP, String addressMAC) {
        this.username = username;
        this.addressIP = addressIP;
        this.addressMAC = addressMAC;
        try {
            //DEBUG
        	int port = 53000;
            if (addressMAC.charAt(0) == 'G') {
                port++;
            }
            
            socket = new Socket();
            socket.connect(new InetSocketAddress(addressIP, port), 3000);;
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getAddressIP() {
        return addressIP;
    }

    public String getAddressMAC() {
        return addressMAC;
    }

    public void write(String message) {
    	if (out == null) return;
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
        	socket.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
    	try {
			return addressMAC + "@" + username + "@" + addressIP.toString() + "@" + Boolean.toString(addressIP.isReachable(2000));
		} catch (IOException e) {
			return addressMAC + "@" + username + "@" + addressIP.toString();
		}
    }
}
