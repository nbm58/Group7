package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

public class ChatServerWorker extends Thread implements MessageTypes
{
	 Socket chatConnection = null;
	 ObjectInputStream readFromNet;
	 ObjectOutputStream writeToNet;
	 Message message = null;
	 
	 
	 
	 // Constructor (Accepts a Socket)
	 // This socket, is the socket.
	 public ChatServerWorker(Socket chatConnection)
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
			 // get object streams
			 writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
			 readFromNet = new ObjectInputStream(chatConnection.getInputStream());
			 
			 // read message
			 message = (Message) readFromNet.readObject();
			 
			 chatConnection.close();
		 }
		 catch (IOException | ClassNotFoundException e)
		 {
			 // Log Failure to read message
			 System.err.println("{CSW} Failed to open object streams or message could not be read");
			 
			 System.exit(1);
		 }
  
		 // switch for message type
		 switch(message.getType())
		 {	 
   
		 // JOIN functionality
		 case JOIN:
			 // read participant's NodeInfo
			 NodeInfo joiningParticipantNodeInfo = (NodeInfo) message.getContent();
			 
			 // add this client to list of participants
			 ChatServer.participants.add(joiningParticipantNodeInfo);
			 
			 // show who joined
			 System.out.print(joiningParticipantNodeInfo.getName() + " joined. All current participants: ");
			 
			 // print out all current participants
			 participantsIterator = ChatServer.participants.iterator();
			 while(participantsIterator.hasNext())
			 {
				 participantInfo = participantsIterator.next();
				 System.out.print(participantInfo.name + " ");
			 }
			 System.out.println();
			 
			 break;
   
		 // NOTE functionality
		 case NOTE:
			 // just display note
			 System.out.println(message.getContent());
			 
			 // run through all participants and send the note to each one
			 participantsIterator = ChatServer.participants.iterator();
			 while(participantsIterator.hasNext())
			 {
				 // get next participant
				 participantInfo = participantsIterator.next();
				 
				 try
				 {
					 // open socket to one chat client at a time
					 chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());
					 
					 // open object streams
					 writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
					 readFromNet = new ObjectInputStream(chatConnection.getInputStream());
					 
					 //write message
					 writeToNet.writeObject(message);
					 
					 // close connection to this client
					 chatConnection.close();
				 }
				 catch (IOException ex)
				 {
					 System.err.println("Error sending NOTE");
					 }
				 }
				 
				 break;
  
		 //SHUTDOWN functionality
		 case LEAVE:
		 case SHUTDOWN:
			 // remove this participant's info
			 NodeInfo leavingParticipantInfo = (NodeInfo) message.getContent();
			 if (leavingParticipantInfo.delete(ChatServer.participants) != null)
			 {
				 System.out.println(leavingParticipantInfo.getName() + " removed");
			 }
			 else
			 {
				 System.err.println(leavingParticipantInfo.getName() + " not found");
			 }
			 
			 // show who left
			 System.out.print(leavingParticipantInfo.getName() + " left. Remaining participants: ");
			 
			 // print out all remaining participants
			 participantsIterator = ChatServer.participants.iterator();
			 while (participantsIterator.hasNext())
			 {
				 participantInfo = participantsIterator.next();
				 System.out.print(participantInfo.name + " ");
			 }
			 System.out.println();
			 
			 break;
 
		 //SHUTDOWN_ALL functionality
		 case SHUTDOWN_ALL:
			 // run through all the participants and shut down each single one
			 participantsIterator = ChatServer.participants.iterator();
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
					 
					 // send shutdown message
					 writeToNet.writeObject(new Message(SHUTDOWN, null));
					 
					 // close connection
					 chatConnection.close();
				 }
				 catch (IOException ex)
				 {
					 System.err.println("Could not process SHUTDOWN_ALL request.");
				 }
			 }
			 
			 System.out.println("Shut down all clients, exiting ...");
			 
			 // new exit myself
			 System.exit(0);
			 
			 break;
		 } //end switch
	 }
}
