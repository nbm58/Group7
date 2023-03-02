package utils;

import java.io.Serializable;

public class NodeInfo implements Serializable
{
    String address;
    int port;
    String name = null;

    public NodeInfo(String address, int port, String name)
    {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public NodeInfo(String address, int port)
    {
        this(address, port, null);
    }

    public String getAddress()
    {
        return this.address;
    }

    public int getPort()
    {
        return this.port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }

    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public boolean equals (Object other)
    {
        String otherIP = ( (NodeInfo) other).getAddress();
        int otherPort = ( (NodeInfo) other).getPort();
        
        return otherIP.equals(this.address) && (otherPort == this.port);
    }
}