package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

import utils.*;

public class ReceiverWorker extends Thread implements MessageTypes
{
    Socket chatConnection = null;
    ObjectInputStream readFromNet;
    ObjectOutputStream writeToNet;
    Message message = null;


    public ReceiverWorker(Socket chatConnection)
    {
        this.chatConnection = chatConnection;
        try
        {
            readFromNet = new ObjectInputStream(chatConnection.getInputStream());
            writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
        }
        catch(IOException  | NullPointerException ex)
        {
            // Log Failure to open object streams.
            System.err.println("Failure to open socket / object streams");
        }
    }

    @Override
    public void run()
    {
        NodeInfo participantInfo;
        Iterator <NodeInfo> participantsIterator;
        
        try
        {
            // Read message
            message = (Message)readFromNet.readObject();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            // Log Failure to read message
            System.err.println("Failure to read message" + ex);
            System.exit(1);
        }

        // Decide what to do depending on the type of message received.
        switch(message.getType())
        {
            case SHUTDOWN:
                System.out.println("Shutdown request received, exiting");
                
                try
                {
                    chatConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                System.exit(0);
                
                break;

                
            case NOTE:
                // Display note
                System.out.println((String) message.getContent());
                try {
                    chatConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                break;
                
            case JOIN:
                // read participant's NodeInfo
                NodeInfo joiningParticipantNodeInfo = (NodeInfo) message.getContent();
                
                // add this client to list of participants
                ChatClient.participants.add(joiningParticipantNodeInfo);
                
                // show who joined
                System.out.print(joiningParticipantNodeInfo.getName() + " joined. All current participants: ");
                
                //print out all current participants and update their participants list
                participantsIterator = ChatClient.participants.iterator();
                Socket tempConnection;
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();
                    System.out.print(participantInfo.getName() + " ");
                    
                    // send message to all participants with updated list of participants
                    try
                    {
                        if (!participantInfo.equals(ChatClient.myNodeInfo))
                        {
                            // open connection to client
                            tempConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());

                            // create object streams
                            writeToNet = new ObjectOutputStream(tempConnection.getOutputStream());
                            readFromNet = new ObjectInputStream(tempConnection.getInputStream());

                            // send message
                            writeToNet.writeObject(new Message(UPDATE_PARTICIPANTS, ChatClient.participants));

                            // close connection
                            tempConnection.close();
                        }
                    }
                    catch (IOException ex)
                    {
                        System.err.println("{ReceiverWorker}.run Error connecting, creating object streams, or sending a message " + ex);
                    }
                }
                System.out.println();
                
                try
                {
                    chatConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }

                break;
                
            case LEAVE:
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
                
                try {
                    chatConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                break;
                
            case UPDATE_PARTICIPANTS:
                // update participants list with full list of participants receive from connected chat node
                ChatClient.participants = message.getParticipants();
                
                try {
                    chatConnection.close();
                }
                catch (IOException ex)
                {
                    // Do nothing, we are closing
                }
                
                // show who joined
                System.out.println("A new chatter joined. All current participants: ");
                
                //print out all current participants
                participantsIterator = ChatClient.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();
                    System.out.print(participantInfo.getName() + " ");
                }
                System.out.println();
                
            default:
                // cannot occur
        }
    }
}
