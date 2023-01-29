package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceiverWorker extends Thread implements MessageTypes
{
 Socket serverConnection = null;
 ObjectInputStream readFromNet;
 ObjectOutputStream writeToNet;
 Message message = null;
 
 
 public ReceiverWorker(Socket serverConnection)
 {
  System.out.println("{RECEIVER} Server Message Incoming!");
  this.serverConnection = serverConnection;
  try
  {
   readFromNet = new ObjectInputStream(serverConnection.getInputStream());
   writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
  }
  catch(IOException  | NullPointerException ex)
  {
   //Log Failure to open object streams.
   System.out.println("Failure to open socket / object streams");
  }
 }

 @Override
 //need to react to things from the network
 public void run()
 {
  try
  {
   //read message
   message = (Message)readFromNet.readObject();
  }
  catch (IOException | ClassNotFoundException ex)
  {
   //Log Failure to read message
   System.out.println("Failure to read message");
   System.exit(1);
  }
 
  //decide what to do depending on the type of message received.
  switch(message.getType())
  {
  case SHUTDOWN:
	  System.out.println("Shutdown request received");
	  try {
		  serverConnection.close();
	  }
	  catch (IOException ex)
	  {
		  //Do nothing, we are closing
	  }
	  System.exit(0);
	  break;
	  
	  
  case NOTE:
	  //display message
	  System.out.println((String) message.getContent());
	  try {
		  serverConnection.close();
	  }
	  catch (IOException ex)
	  {
		  //Do nothing, we are closing
	  }
	  System.exit(0);
	  break; 
  }
 }
}