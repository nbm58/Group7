package chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import utils.*;

public class ChatClientWorker extends Thread implements MessageTypes
{
    Socket chatConnection = null;
    ObjectInputStream readFromNet;
    ObjectOutputStream writeToNet;
    Message message = null;
    
    public ChatClientWorker(Socket chatConnection)
    {
        this.chatConnection = chatConnection;
    }
    
    @Override
    public void run()
    {
        NodeInfo participantInfo = null;
        Iterator <NodeInfo> participantsIterator;
        
        try
        {
            // get object stream
            writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
            readFromNet = new ObjectInputStream(chatConnection.getInputStream());
            
            // read message
            message = (Message) readFromNet.readObject();
            
            chatConnection.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.err.println("{ChatClientWorker.run} Failed to open object streams or message could not be read");
            
            System.exit(1);
        }
        
        //processing message
        switch (message.getType())
        {
            case JOIN:
                // read participant's NodeInfo
                NodeInfo joiningParticipantNodeInfo = (NodeInfo) message.getContent();
                
                // add this client to list of participants
                ChatClient.participants.add(joiningParticipantNodeInfo);
                
                // show who joined
                System.out.print(joiningParticipantNodeInfo.getName() + " joined. All current participants: ");
                
                //print out all current participants
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();
                    System.out.print(participantInfo.getName() + " ");
                }
                System.out.println();

                break;
                
                
            case LEAVE:
            case SHUTDOWN:
                // remove this participant's info
                NodeInfo leavingParticipantInfo = (NodeInfo) message.getContent();
                if (ChatClient.participants.remove(leavingParticipantInfo))
                {
                    System.err.println(leavingParticipantInfo.getName() + " removed.");
                }
                else
                {
                    System.err.println(leavingParticipantInfo.getName() + " not found.");
                }
                
                // show who left
                System.out.print(leavingParticipantInfo.getName() + " left. Remaining participants: ");
                
                // print out all remaining participants
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();
                    System.out.print(participantInfo.getName() + " ");
                }
                System.out.println();
                
                break;
                
                
            case SHUTDOWN_ALL:
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
                        System.err.println("{ChatClientWorker}.run Error connecting, creating object streams, or sending a message" + ex);
                    }
                }
                
                System.out.println("Shutdown all clients, exiting ...");
                
                // now exit myself
                System.exit(0);
                
                
            case NOTE:
                // display NOTE
                System.out.println((String) message.getContent());
                
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
                        writeToNet.writeObject(message);
                        
                        // close connection
                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        System.err.println("{ChatClientWorker}.run Error connecting, creating object streams, or sending a message" + ex);
                    }
                }
                
                break;
                
            default:
                // cannot occur
        }
    }
}