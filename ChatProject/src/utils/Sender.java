package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread implements MessageTypes
{
	// Sender processes user input, translates to messages
	// and sends to chat server
	Socket serverConnection = null;
	Scanner userInput = new Scanner(System.in);
	String inputLine = null;
	boolean hasJoined;
	
	
   // Constructor
	public Sender()
	{
		userInput = new Scanner(System.in);
		hasJoined = false;
	}
	
	@Override
	public void run()
	{
		System.out.println("{SENDER} Boot");
		ObjectOutputStream writeToNet;
		ObjectInputStream readFromNet;
	 
		// Loop until user runs SHUTDOWN or SHUTDOWN_ALL
		while(true)
		{
			System.out.println("{SENDER} Enter Command:");
			
			// Get user input
			inputLine = userInput.nextLine(); 
	  
			// JOIN functionality
			if (inputLine.startsWith("JOIN"))
			{
				System.out.println("{SENDER} command: JOIN");   
				if (hasJoined == true)
				{
					// Log Chat already joined
					System.out.println("{SENDER} You have already joined a chat");
					continue;
				}
	
				// Read server information the user has provided, with JOIN command
				String[] connectivityInfo = inputLine.split("[ ]+");
	
				// If there is any info that may override the connectivity information,
				// that was provided through the properties
				try
				{
					ChatClient.serverNodeInfo = new NodeInfo(connectivityInfo[1],
			                            Integer.parseInt(connectivityInfo[2]));
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					// Don't do anything
				}
   
				try
				{
					// Open connection to server
					serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), 
			                              ChatClient.serverNodeInfo.getPort());
	
					// Open object streams
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	
					// Send join request
					writeToNet.writeObject(new Message(JOIN, ChatClient.myNodeInfo));
					
					// close connection
					serverConnection.close();
				}
				catch (IOException ex)
				{
					// Log failure to connect
					System.err.println("{SENDER} Failed to join server");
					continue;
				}
				hasJoined = true;   
				System.out.println("{SENDER} Joined chat...");
			}
			
			// LEAVE functionality
			else if (inputLine.startsWith("LEAVE"))
			{
				System.out.println("{SENDER} command: LEAVE");
				if (hasJoined == false)
				{
					// Log chat not joined yet
					System.out.println("You have not joined the chat yet.");
					continue;
				}
				try
				{
					// Open connection to server
					serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), 
			                              ChatClient.serverNodeInfo.getPort());
	
					// Open object streams
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
	
					// Send leave request
					writeToNet.writeObject(new Message(LEAVE, ChatClient.myNodeInfo));
	
					// Close connection
					serverConnection.close();
				}
				catch(IOException ex)
				{
					// Log failure to connect to server, or open/write/read stream, or closing connection
					System.err.println("{SENDER} Failure to write LEAVE");
					continue;
				}  
				hasJoined = false;
				System.out.println("Left chat...");
			}
			
			// SHUTDOWN_ALL functionality
			else if (inputLine.startsWith("SHUTDOWN_ALL"))
			{
				System.out.println("{SENDER} command: SHUTDOWN_ALL");	  
				if (hasJoined == false)
				{
					// Log join a chat before shutting it down
					System.out.println("You have not joined the chat yet.");
					continue;
				}
				try
				{
					// Open connection to server
					serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
					
					// Open object streams
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					// Send shutdown_all request
					writeToNet.writeObject(new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo));
	 
					// Close connection
					serverConnection.close();
				}
				catch(IOException ex)
				{
					// Log shutdown failure
					System.err.println("{SENDER} Failure to write SHUTDOWN_ALL");
					continue;
				}  
				System.out.println("Sent shutdown all request...");
			}
			
			// SHUTDOWN functionality
			else if (inputLine.startsWith("SHUTDOWN"))
			{
				System.out.println("{SENDER} command: SHUTDOWN");
				// Leave if participant, before shutdown
				
				if (hasJoined == true)
				{
					// Send leave request
					try
					{
						// Open connection to server
						serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());
						
						// Open object streams
						readFromNet = new ObjectInputStream(serverConnection.getInputStream());
						writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());	
						
						// Send shutdown_all request
						writeToNet.writeObject(new Message(SHUTDOWN, ChatClient.myNodeInfo));
	 
						// Close connection
						serverConnection.close();
						System.out.println("Left chat...");
					}
					catch(IOException ex)
					{
						// Log shutdown failure
						System.err.println("{SENDER} Failure to write SHUTDOWN");
						continue;
					}  
					System.out.println("Exiting...");
					System.exit(0);
				}   
			}
			
			// NOTE functionality
			else
			{
				if(hasJoined == false)
				{
					//Log Join chat first!
					System.out.println("You must join a chat before you can post notes!");   
					continue;
				}
				try
				{
					System.out.println("{SENDER} command: NOTE");
	
					// Open connection to server
					serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());	
					
					// Open object streams
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());	
	
					// Send shutdown_all request
					writeToNet.writeObject(new Message(NOTE, ChatClient.myNodeInfo));
	
					// Close connection
					serverConnection.close();
	
					System.out.println("Message Sent...");
				}
				catch(IOException ex)
				{
					// Log Note send failure
					System.err.println("{SENDER} Failure to write NOTE");
					continue;
				} 
			}
		}
	}   
}
