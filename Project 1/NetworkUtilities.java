package utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * Utility class for networking.
 */
public class NetworkUtilities {

    /**
     * Helper class to retrieve ones own IPv4.
     * @see <a href="https://stackoverflow.com/questions/8083479/java-getting-my-ip-address">original source</a>
     * 
     * @return my own IPv4 address
     */
    public static String getMyIP() {
    try {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual() || networkInterface.isPointToPoint()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                final String myIP = address.getHostAddress();
                if (Inet4Address.class == address.getClass()) {
                    return myIP;
                }
            }
        }
    } catch (SocketException e) {
        throw new RuntimeException(e);
    }
    return null;
    }
}
