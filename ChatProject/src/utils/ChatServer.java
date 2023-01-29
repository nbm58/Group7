package utils;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ChatServer extends Thread
{
	
	 
	 static ServerSocket serverSocket = null;
	 static Receiver receiver = null;
	 static Sender sender = null;
	 public static NodeInfo myNodeInfo = null;
	 public static NodeInfo serverNodeInfo = null;
	 
	 public ChatServer(String propertiesFile)
	 {
	  //Get properties from properties file
	  Properties properties = null;
	  try
	  {
		  
	   properties = new PropertyHandler(propertiesFile);
	  }
	  catch(IOException ex)
	  {
	   System.out.println("Failure getting properties");
	   System.exit(1); 
	  }
		 	 
	  //Get my name
	  String myName = properties.getProperty("MY_NAME");
	  if(myName == null)
	  {
		  System.out.println("Failure getting name");
	   System.exit(1);
	  }
 
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
	 public void run() 
	 {
	  System.out.println("{SERVER} Boot");
	  try 
	  {
	   serverSocket = new ServerSocket(serverNodeInfo.getPort());
	   System.out.println("{SERVER} server socket created, listening on port " 
			  								+ serverNodeInfo.getPort());
	   System.out.println("{SERVER} Server Waiting for Connections..");
	   (new ChatServerWorker(serverSocket.accept())).start();
	  }  
	  catch (IOException e) 
	  {
	   System.out.println("{SERVER} Failure to create Socket in chatserver run");
	  }
	 }
	
	//main
		 public static void main(String[] args)
		 {
		  
		  String propertiesFile = null;
		  try
		  {
		   propertiesFile = args[0];
		  }
		  catch (ArrayIndexOutOfBoundsException ex)
		  {  
		   propertiesFile = "db.properties";
		  }
		  (new ChatServer(propertiesFile)).run();
		 }
	 
	 
	 
}   
