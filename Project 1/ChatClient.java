package utils;

import java.io.IOException;
import java.util.Properties;

//packages
//imports
//imports form other packages (local)

public class ChatClient implements Runnable {


	/*initialize variables/references
	  initialize receiver
	  reacts to things from the network
	  notes
	  shutdown request
	*/
	static Receiver receiver = null;
	
	/*initialize sender
	  connects to server (sockets(ip/port number), etc) 
	  user command line output, join, leave, shutdown, notes
	*/
	static Sender sender = null;
	
	//initialize client connectivity info
	//initialize nodeinfo
	public static NodeInfo myNodeInfo = null;
	//initialize serverinfo
	public static NodeInfo serverNodeInfo = null;
	
	//constructor
		//try to get properties (server etc)
		//catch
	public ChatClient(String propertiesFile)
	{
	 //Get properties from properties file
	 Properties properties = null;
	 try
	 {
	  properties = new PropertyHandler(propertiesFile);
	 }
	 catch(IOException ex)
	 {
	  //Log failure to open Properties File
	  System.exit(1);
	 }
	 	 
	 //get my receiver port number
	 int myPort = 0;
	 try
	 {
	  myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
	 }
	 catch(NumberFormatException ex)
	 {
	  //Log Failure to read port
		 System.exit(1);
	 }
	
	 //Get my name
	 String myName = properties.getProperty("MY_NAME");
	 if(myName == null)
	 {
	  //Log Failure to read name
	  System.exit(1);
	 }
	 
	 //create my own node info
	 myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myName);
	 
	 //get server default port
	 int serverPort = 0;
	 try
	 {
      serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
	 }
	 catch (NumberFormatException ex)
	 {
	  //Log failure to read server port
	 }
	 
	 //Get server default IP
	 String serverIP = null;
	 serverIP = properties.getProperty("SERVER_IP");
	 if(serverIP == null)
	 {
	  //Log failure to read server IP
	 }
	 
	 //create server default connectivity information
	 if(serverPort != 0 && serverIP != null)
	 {
	  serverNodeInfo = new NodeInfo(serverIP, serverPort);
	 }
	 
	}
	
	
	
	@Override
	public void run() {
		//run
		//receiver start
		(receiver = new Receiver()).start();
		
		//sender start
		(sender = new Sender()).start();
	}
	
	//main
		//get properties
		//create new chat client with properties
		public static void main(String[] args)
		{
		 String propertiesFile = null;
		 
		 try
		 {
		  propertiesFile = args[0];
		 }
		 catch (ArrayIndexOutOfBoundsException ex)
		 {
		  propertiesFile = "config/ChatNodeDefault.properties";
		 }
		 
		 (new ChatClient(propertiesFile)).run();
		 
		}

}