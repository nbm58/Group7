package utils;
import java.io.Serializable;
import java.util.ArrayList;

public class NodeInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	String ipAddress;
    int portNumber;
    String name = null;

    // Constructors
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

    // Getters
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
    			nodeList.remove(i);
    			
    			return nodeList.get(i);
    		}
    	}
    	
    	return null;
    }
}