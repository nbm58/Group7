package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

import java.util.Scanner;

import utils.*;


public class Sender extends Thread implements MessageTypes
{
    Socket chatConnection = null;
    Scanner userInput = new Scanner(System.in);
    String inputLine = null;
    boolean hasJoined;  // flag indicating if we have joined chat
    Message message = null;

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
        
        NodeInfo participantInfo = null;
        Iterator <NodeInfo> participantsIterator;

        // Loop until user runs SHUTDOWN or SHUTDOWN_ALL
        while(true)
        {
            System.out.println("{SENDER} Waiting for input:");

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
                    ChatClient.participants.add(new NodeInfo(connectivityInfo[1], Integer.parseInt(connectivityInfo[2])));
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    // Don't do anything
                }
                
                if (connectivityInfo != null && connectivityInfo.length > 1)
                {
                    try
                    {
                        // Open connection to client
                        chatConnection = new Socket(ChatClient.participants.get(0).getAddress(), ChatClient.participants.get(0).getPort());

                        // Open object streams
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                        // Send JOIN message
                        writeToNet.writeObject(new Message(JOIN, ChatClient.myNodeInfo));

                        // close connection
                        chatConnection.close();
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
                else
                {
                    ChatClient.participants.add(ChatClient.myNodeInfo);
                    
                    hasJoined = true;
                    
                    System.out.println("{SENDER} Created a chat... Waiting for other users.");
                }
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
                
                // run through all participants and send leave message to each one
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    // get next participant
                    participantInfo = participantsIterator.next();
                    
                    try
                    {
                        // open connection to client
                        chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());
                        
                        // open object streams
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());
                        
                        // write message
                        writeToNet.writeObject(new Message(LEAVE, ChatClient.myNodeInfo));
                        
                        // close connection
                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        System.err.println("{Sender}.run Error connecting, creating object streams, or sending a message" + ex);
                    }
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
                
                // run through all the chat participants and shut down each one
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    // get next participant
                    participantInfo = participantsIterator.next();
                    
                    try
                    {
                        // open connection to client
                        chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());
                        
                        // open object streams
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());
                        
                        // send SHUTDOWN message
                        writeToNet.writeObject(new Message(SHUTDOWN, null));
                        
                        // close connection
                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        System.err.println("{Sender}.run Error connecting, creating object streams, or sending a message" + ex);
                    }
                }
                
                System.out.println("Shutdown all clients, exiting ...");
                
                // now exit myself
                System.exit(0);
            }

            // SHUTDOWN functionality
            else if (inputLine.startsWith("SHUTDOWN"))
            {
                System.out.println("{SENDER} command: SHUTDOWN");
                // Leave if participant, before shutdown

                if (hasJoined == true)
                {
                    participantsIterator = ChatClient.participants.iterator();
                    while(participantsIterator.hasNext())
                    {
                        // get next participant
                        participantInfo = participantsIterator.next();

                        try
                        {
                            // open connection to client
                            chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());

                            // open object streams
                            writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                            readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                            // send SHUTDOWN message
                            writeToNet.writeObject(new Message(LEAVE, ChatClient.myNodeInfo));

                            // close connection
                            chatConnection.close();
                        }
                        catch (IOException ex)
                        {
                            System.err.println("{Sender}.run Error connecting, creating object streams, or sending a message" + ex);
                        }
                    }
                }
                
                System.out.println("Exiting...");
                    
                System.exit(0);
            }
            
            
            else // sending a NOTE
            {
                if(hasJoined == false)
                {
                    //Log Join chat first!
                    System.out.println("You must join a chat before you can post notes!");   
                    continue;
                }

                // run through all participants and send the note to each one
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    // get next participant
                    participantInfo = participantsIterator.next();
                    
                    try
                    {
                        // open connection to client
                        chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());
                        
                        // open object streams
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());
                        
                        // write message
                        writeToNet.writeObject(new Message(NOTE, ChatClient.myNodeInfo.getName() + ": " + inputLine));
                        
                        // close connection
                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        System.err.println("{Sender}.run Error connecting, creating object streams, or sending a message " + ex);
                    }
                }
            }
        }
    }
}
