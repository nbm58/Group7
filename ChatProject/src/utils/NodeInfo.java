package utils;
import java.io.Serializable;

public class NodeInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	String ipAddress;
    int portNumber;
    String name = null;

    public NodeInfo(String ipAddress, int portNumber, String name)
    {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.name = name;
    }

    public NodeInfo(String ipAddress, int portNumber)
    {
        this(ipAddress, portNumber, null);
    }

    String getAddress()
    {
        return this.ipAddress;
    }

    int getPort()
    {
        return this.portNumber;
    }

    String getName()
    {
        return this.name;
    }
}