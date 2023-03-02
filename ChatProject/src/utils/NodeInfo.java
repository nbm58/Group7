package utils;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeInfo implements Serializable
{

	String address;
    int port;
    String name = null;

    // Constructors
    public NodeInfo(String ipAddress, int portNumber, String name)
    {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public NodeInfo(String address, int port)
    {
        this(address, port, null);
    }

    // Getters
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
    public boolean equals(NodeInfo otherNode)
    {
    	return this.getAddress() == otherNode.getAddress()
    			&& this.getPort() == otherNode.getPort()
    			&& this.getName() == otherNode.getName();
    }
    
    // Delete a specified node from a specified list; return the deleted node
    public NodeInfo delete(ArrayList<NodeInfo> nodeList)
    {
    	for(int i = 0; i < nodeList.size(); i++)
    	{
    		if (this.equals(nodeList.get(i)))
    		{
    			return nodeList.remove(i);
    		}
    	}
    	
    	return null;
    }
}