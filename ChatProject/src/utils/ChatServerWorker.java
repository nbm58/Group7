package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatServerWorker extends Thread implements MessageTypes
{
	 
	 Socket serverConnection = null;
	 ObjectInputStream readFromNet;
	 ObjectOutputStream writeToNet;
	 Message message = null;
	 
	 
	 //Constructor (Accepts a Socket)
	 //this socket, is the socket.
	 public ChatServerWorker(Socket serverConnection)
	 {
	  this.serverConnection = serverConnection;
	  System.out.println("{CSW} connection established!");
	  try
	  {
	   System.out.println("{CSW} Dont listen to Eagon, "
	   		+ "Cross the streams, Creating streams..");
	   writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	   readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	   System.out.println("{CSW} streams established..");
	  }
	  catch(IOException  | NullPointerException ex)
	  {
	   //Log Failure to open object streams.
	   System.out.println("{CSW} Failure to open socket / object streams");
	  }
	 }
	 
	 
	 @Override
	 //The meat
	 public void run()
	 {
	  while(true)
	  {
	  System.out.println("{CSW} Run");
	  try
	  {
	   //read message
	   message = (Message)readFromNet.readObject();
	  }
	  catch (IOException | ClassNotFoundException ex)
	  {
	   //Log Failure to read message
	   System.out.println("{CSW} Failure to read message");
	   System.exit(1);
	  }
	  
	  //Do the things
	  switch(message.getType())
	  {	 
	   
	   //join server==============================
	   case JOIN:
	   System.out.println("{CSW} Join request received");
	   
	   
	   break;
	  
	 		 
	   //Leave Server==============================
	   case LEAVE:
	   System.out.println("{CSW} Leave request received");
	   try 
	   {
		//leave the server, replace this closure with leave stuff
		   serverConnection.close();
	   }
	   catch (IOException ex)
	   {
		System.out.println("{CSW} Failure to Leave");
	   }
	   
	   break;
	   
	   //Note  ==============================================
	   case NOTE:
		   System.out.println("{CSW} NOTE received");
		   
		   
		   System.out.println((String) message.getContent());
		   break;
  
  	   //shutdown from server===========================
       case SHUTDOWN:
	   System.out.println("{CSW} Shutdown request received");
	   try 
	   {
		//Shutdown operation   
		   serverConnection.close();
	   }
	   catch (IOException ex)
	   {
		//Do nothing, we are closing
	   }
	   System.exit(0);
	   break;
	 
	   //shutdown from server (shutdown_all)==============
	   case SHUTDOWN_ALL:
	   System.out.println("Shutdown_All request received");
	   try 
	   {
	    //Shutdown All operation.
		   serverConnection.close();
	   }
	   catch (IOException ex)
	   {
	    //Do nothing, we are closing
	   }
	   System.exit(0);
	   break;
	  }//end switch
	  }//end while
	 }//end run	 
}//end class
