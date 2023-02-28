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

    String getAddress()
    {
        return this.address;
    }

    int getPort()
    {
        return this.port;
    }

    String getName()
    {
        return this.name;
    }
    
 // Compare two nodes based on their IP address, port number, & name
    public boolean equals(NodeInfo firstNode, NodeInfo secondNode)
    {
    	return firstNode.getAddress() == secondNode.getAddress()
    			&& firstNode.getPort() == secondNode.getPort()
    			&& firstNode.getName() == secondNode.getName();
    }
    
    
}