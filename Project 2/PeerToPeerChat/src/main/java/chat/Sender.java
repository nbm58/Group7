package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.Scanner;

import utils.*;


public class Sender extends Thread implements MessageTypes
{
    Socket serverConnection = null;
    Scanner userInput = new Scanner(System.in);
    String inputLine = null;
    boolean hasJoined;  // flag indicating if we have joined chat


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
                    System.err.println("{SENDER} You have already joined a chat");
                    continue;
                }

                // Read server information the user has provided, with JOIN command
                String[] connectivityInfo = inputLine.split("[ ]+");

                // If there is any info that may override the connectivity information,
                // that was provided through the properties
                try
                {
                    ChatClient.serverNodeInfo = new NodeInfo(connectivityInfo[1], Integer.parseInt(connectivityInfo[2]));
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    // Don't do anything
                }
                
                if (ChatClient.serverNodeInfo == null)
                {
                    System.err.println("{Sender}.run No server connectivity");
                    continue;
                }
                
                try
                {
                    // Open connection to server
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());

                    // Open object streams
                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    // Send JOIN message
                    writeToNet.writeObject(new Message(JOIN, ChatClient.myNodeInfo));

                    // close connection
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    // Log failure to connect
                    System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
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
                    System.err.println("You have not joined a chat yet.");
                    continue;
                }
                try
                {
                    // Open connection to server
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());

                    // Open object streams
                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    // Send LEAVE message
                    writeToNet.writeObject(new Message(LEAVE, ChatClient.myNodeInfo));

                    // Close connection
                    serverConnection.close();
                }
                catch(IOException ex)
                {
                    // Log failure to connect to server, or open/write/read stream, or closing connection
                    System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
                    continue;
                }  
                // we are out
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
                    System.err.println("You have not joined a chat yet.");
                    continue;
                }
                
                // send SHUTDOWN_ALL message
                try
                {
                    // Open connection to server
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());

                    // Open object streams
                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    // Send SHUTDOWN_ALL message
                    writeToNet.writeObject(new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo));

                    // Close connection
                    serverConnection.close();
                }
                catch(IOException ex)
                {
                    // Log shutdown failure
                    System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
                }  
                System.out.println("Sent shutdown all request...\n");
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

                        // Send SHUTDOWN message
                        writeToNet.writeObject(new Message(SHUTDOWN, ChatClient.myNodeInfo));

                        // Close connection
                        serverConnection.close();
                        
                        System.out.println("Left chat...");
                    }
                    catch(IOException ex)
                    {
                        // Log shutdown failure
                        System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
                    }  
                    System.out.println("Exiting...");
                    System.exit(0);
                }   
            }
            
            
            else // sending a NOTE
            {
                if(hasJoined == false)
                {
                    //Log Join chat first!
                    System.err.println("You must join a chat before you can send notes!");   
                    continue;
                }
                
                // send a note
                try
                {
                    // Open connection to server
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getAddress(), ChatClient.serverNodeInfo.getPort());	

                    // Open object streams
                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    // Send NOTE message
                    writeToNet.writeObject(new Message(NOTE, ChatClient.myNodeInfo));

                    // Close connection
                    serverConnection.close();

                    System.out.println("Message Sent...");
                }
                catch(IOException ex)
                {
                    // Log Note send failure
                    System.err.println("{SENDER} Error connecting to server, creating object streams, or closing connection" + ex);
                    continue;
                } 
            }
        }
    }
}
