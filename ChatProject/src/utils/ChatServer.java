package utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;

public class ChatServer implements Runnable
{
	 static ServerSocket serverSocket = null;
	 public static NodeInfo myNodeInfo = null;
	 public static NodeInfo serverNodeInfo = null;
	 public static ArrayList<NodeInfo> participants = new ArrayList<NodeInfo>();
	 
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
		        
		 //get my receiver port number
		 int myPort = 0;
		 try
		 {
			 myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
		 }
		 catch(NumberFormatException ex)
		 {
			 System.out.println("Failure getting port");
			 System.exit(1);
		 }
		    
		 //Get my name
		 String myName = properties.getProperty("MY_NAME");
		 if (myName == null)
		 {
			 System.out.println("Failure getting name");
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
			 System.out.println("Failed to get server port.");
		 }
		
		 //Get server default IP
		 String serverIP = null;
		 serverIP = properties.getProperty("SERVER_IP");
		 if (serverIP == null)
		 {
			 System.out.println("Failed to get server IP.");
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
		 try
		 {
			 serverSocket = new ServerSocket(serverNodeInfo.getPort());
			 serverSocket.setReuseAddress(true);
			 
			 while (true)
			 {
				 try
				 {
					 (new ChatServerWorker(serverSocket.accept())).start();
				 }
				 catch (IOException ex)
				 {
					 System.err.println("[ChatServer.run] Warning: Error accepting client.");
				 }
			 } 
		 }
		 catch (IOException e)
		 {
			 System.err.println("Failed to create server socket or accept client-server connection");
		 }
	 }
	
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
		 System.out.println("{SERVER} Boot");
		 (new ChatServer(propertiesFile)).run();
	 }
}