package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceiverWorker extends Thread{
	//try 
	//read from net, new input stream, use serverconnection.getinput
	//write to net, new output stream, use serverconnection.getoutput
	//catch
	//error
	ObjectInputStream readFromNet;
	ObjectOutputStream writeToNet;
	Message message = null;
	public ReceiverWorker(Socket serverConnection)
	{
	 try
	 {
	  readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	  writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	 }
	 catch(IOException ex)
	 {
	  //Log Failure to open object streams.
	 }
	}

	@Override
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
		 System.exit(1);
	 }
	 
	 //decide what to do depending on the type of message received.
	 switch(message.getType())
	 {
	 //switch statement, based on message, getType
	 //case shutdown
	 //join
	 //etc	 
	 }
	}

 
}
