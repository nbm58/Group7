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
	 
	 
	 // Constructor (Accepts a Socket)
	 // This socket, is the socket.
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
			 // Log Failure to open object streams.
			 System.err.println("{CSW} Failure to open socket / object streams");
		 }
	 }
	 
	 @Override
	 public void run()
	 {
		 while(true)
		 {
			 System.out.println("{CSW} Run");
			 
			 try
			 {
				 // Read message
				 message = (Message)readFromNet.readObject();
			 }
			 catch (IOException | ClassNotFoundException ex)
			 {
				 // Log Failure to read message
				 System.err.println("{CSW} Failure to read message");
				 System.exit(1);
			 }
	  
			 // Do the things
			 switch(message.getType())
			 {	 
	   
			 // JOIN functionality
			 case JOIN:
				 System.out.println("{CSW} Join request received"); 
				 break;
	 
			 // LEAVE functionality
			 case LEAVE:
				 System.out.println("{CSW} Leave request received");
				 try 
				 {
					 // Leave the server
					 // TODO: replace this closure with leave stuff
					 serverConnection.close();
				 }
				 catch (IOException ex)
				 {
					 System.err.println("{CSW} Failure to Leave");
				 }
				 break;
	   
			 // NOTE functionality
			 case NOTE:
				 System.out.println("{CSW} NOTE received");
		   
				 // Print the note
				 System.out.println((String) message.getContent());
				 break;
  
  	   //SHUTDOWN functionality
			 case SHUTDOWN:
				 System.out.println("{CSW} Shutdown request received");
				 try 
				 {
					 // Shutdown operation   
					 serverConnection.close();
				 }
				 catch (IOException ex)
				 {
					 //Do nothing, we are closing
				 }
				 System.exit(0);
				 break;
	 
	   //SHUTDOWN_ALL functionality
			 case SHUTDOWN_ALL:
				 System.out.println("Shutdown_All request received");
				 try 
				 {
					 // TODO: Shutdown All operation.
					 serverConnection.close();
				 }
				 catch (IOException ex)
				 {
					 // Do nothing, we are closing
				 }
				 System.exit(0);
				 break;
			 } //end switch
		 } //end while
	 }
}