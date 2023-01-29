package utils;
import java.io.IOException;
import java.net.ServerSocket;

public class Receiver extends Thread{

	//client-side
	//receives messages from the chat server
	
	
	//init receiver ServerSocket
	static ServerSocket receiverSocket = null;
	//init client username
	static String userName = null;
	
	//Constructor
	public Receiver()
	{
	 try
	 {
	  receiverSocket = new ServerSocket(ChatClient.myNodeInfo.getPort());
	  //Display Socket creation success, listening on port #
	 }
	 catch (IOException ex)
	 {
	  //Log failure to create socket
	 }
	 //display listening on message, port
	 System.out.println(ChatClient.myNodeInfo.getName() + " listening on " + ChatClient.myNodeInfo.getAddress() + ":" + ChatClient.myNodeInfo.getPort());
	}
	
	@Override
	public void run()
	{
	 //run server loop
	 while(true)
	 {
	  try
	  {
	   (new ReceiverWorker(receiverSocket.accept())).start();
	  }
	  catch (IOException e)
	  {
	   //Log failure to create Socket Thread Object "error accepting client"
	  }
	 }
	}    
}
