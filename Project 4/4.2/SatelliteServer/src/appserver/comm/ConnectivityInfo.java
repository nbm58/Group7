package appserver.comm;

import java.io.Serializable;

/**
 * Class [ConnectivityInfo] Wraps server connectivity information
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class ConnectivityInfo implements Serializable {

    String host = null;
    int port = 0;
    String name = null;

    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getHost() {
        return host;
    }
 
    public void setHost(String ipAddress) {
        this.host = ipAddress;
    }

    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
}
