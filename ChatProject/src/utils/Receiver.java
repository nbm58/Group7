package utils;
import java.io.IOException;
import java.net.ServerSocket;

//receiver can just accept incoming connection from the server, work on it, return back in loop, accept the next one.

public class Receiver extends Thread
{
	 // Client-side
	 // Receives messages from the chat server
	 // Init receiver ServerSocket
	 static ServerSocket receiverSocket = null;
 
	 // Init client username
	 static String userName = null;

	 // Constructor
	 public Receiver()
	 {
		 try
		 {
			 receiverSocket = new ServerSocket(ChatClient.myNodeInfo.getPort());
			 System.out.println("{RECEIVER} Socket created, listening on port " + ChatClient.myNodeInfo.getPort());
		 }
		 catch (IOException ex)
		 {
			 // Log failure to create socket
			 System.out.println("{RECEIVER} Failure to create socket");  
		 }

		 // Display listening on message, port
		 System.out.println("{RECEIVER} " + ChatClient.myNodeInfo.getName() + " listening on " + ChatClient.myNodeInfo.getAddress() + ":" + ChatClient.myNodeInfo.getPort());
	 }

	 @Override
	 public void run()
	 {
		 // Run server loop
		 while(true)
		 {
			 try
			 {
				 System.out.println("{RECEIVER} Waiting for message from server..");
				 (new ReceiverWorker(receiverSocket.accept())).start();
			 }
			 catch (IOException e)
			 {
				 // Log failure to create Socket Thread Object "error accepting client"
				 System.err.println("Error accepting client");
			 }
   
		 }
	 }    
}
