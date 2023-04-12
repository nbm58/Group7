package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread implements MessageTypes
{

 //sender processes user input, translates to messages
 //and sends to chat server
 Socket serverConnection = null;
 Scanner userInput = new Scanner(System.in);
 String inputLine = null;
 boolean hasJoined;
	
	
 /*
   Constructor
 */
 public Sender()
 {
  userInput = new Scanner(System.in);
  hasJoined = false;
 }
	
 @Override
 public void run()
 {
  ObjectOutputStream writeToNet;
  ObjectInputStream readFromNet;
	 
  //until SHUTDOWN/SHUTDOWN_ALL
  while(true)
  {
   //get user input
   inputLine = userInput.nextLine(); 
	  
   if (inputLine.startsWith("JOIN"))
   {
	if(hasJoined == true)
	{
     //Log Chat already joined
	 continue;
	}
	   
   String[] connectivityInfo = inputLine.split("[]+");
	   
   try
   {
	//open connection to server
	serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
	
	//open object streams
	readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	
	//send join request
	writeToNet.writeObject(new Message(JOIN, ChatClient.myNodeInfo));
   }
   catch(IOException ex)
   {
	//Log failure to connect to server, or open/write/read obest stream/ or closing connection
	continue;
   }
   hasJoined = true;   
   System.out.println("Joined chat...");
  }
  else if (inputLine.startsWith("LEAVE"))
  {
   if (hasJoined == false)
   {
    //Log chat not joined yet
    continue;
   }
   try
   {
	//open connection to server
	serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
	
	//open object streams
	readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	
	//send leave request
	writeToNet.writeObject(new Message(LEAVE, ChatClient.myNodeInfo));
	
	//close connection
	serverConnection.close();
   }
   catch(IOException ex)
   {
	//Log failure to connect to server, or open/write/read obest stream/ or closing connection
	continue;
   }  
   hasJoined = false;
   System.out.println("Left chat...");
  }
  else if (inputLine.startsWith("SHUTDOWN_ALL"))
  {
   if (hasJoined == false)
   {
    //Log join a chat before shutting it down
    continue;
   }
   try
    {
	 //open connection to server
	 serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
	 //open object streams
	 readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	 writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	 //send shutdown_all request
	 writeToNet.writeObject(new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo));
	 //close connection
	 serverConnection.close();
    }
   catch(IOException ex)
   {
	//Log shutdown failure
	continue;
   }  
   System.out.println("Sent shutdown all request...");
  }
  else if (inputLine.startsWith("SHUTDOWN"))
  {
   //Leave if participant, before shutdown
   if (hasJoined == true)
   {
    //Send leave request
    try
    {
	 //open connection to server
	 serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
	 //open object streams
	 readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	 writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());	
 	 //send shutdown_all request
  	 writeToNet.writeObject(new Message(SHUTDOWN, ChatClient.myNodeInfo));
	 //close connection
	 serverConnection.close();
	 System.out.println("Left chat...");
    }
    catch(IOException ex)
    {
	 //Log shutdown failure
	 continue;
    }  
    System.out.println("Exiting...");
    System.exit(0);
   }   
  }
  else
  {
   if(hasJoined == false)
   {
    //Log Join chat first!
	continue;
   }
   try
   {
	//open connection to server
	serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());	
	//open object streams
	readFromNet = new ObjectInputStream(serverConnection.getInputStream());
	writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());	
	//send shutdown_all request
	writeToNet.writeObject(new Message(NOTE, ChatClient.myNodeInfo));	
	//close connection
	serverConnection.close();
	System.out.println("Message Sent...");
   }
   catch(IOException ex)
   {
	//Log Note send failure
    continue;
   } 
  }
 }
}   
}
