package utils;
public class ChatServer 
{
 public static void main(String[] args)
 {
    switch()
    {
        case JOIN:
            //add participant to list
            NodeInfo newParticipantInfo = (NodeInfo)message.getContent();
            ChatServer.participants.add(newParticipantInfo);
            System.out.println(newParticipantInfo.getName() + " joined. Participants: ");

            //print out all participants
            Iterator<NodeInfo> participantsIterator = ChatServer.participants.iterator();
            while(participantsIterator.hasNext())
            {
                NodeInfo participantInfo = participantsIterator.next();
                System.out.println(participantInfo.getName() + " ");
            }
            System.out.println();

            break;
        
        case LEAVE:
            case SHUTDOWN:
                //remove participant from list
                NodeInfo leavingParticipantInfo = (NodeInfo)message.getContent();
                if (ChatServer.participants.remove(leavingParticipantInfo))
                {
                    System.err.println(leavingParticipantInfo.getName() + " removed.");
                }
                else
                {
                    System.err.println(leavingParticipantInfo.getName() + " not found.");
                }

                //show who left
                System.out.println(leavingParticipantInfo.getName() + " left. Remaining participants: ");

                //print out all remaining participants
                participantsIterator = ChatServer.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();
                    System.out.println(participantInfo.getName() + " ");
                }
                System.out.println();

                break;
            
            case SHUTDOWN_ALL:
                participantsIterator = ChatServer.participants.iterator();
                while(participantsIterator.hasNext())
                {
                    participantInfo = participantsIterator.next();

                    try {
                        chatConnection = newSocket(participantInfo.address, participantInfo.port);

                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                        writeToNet.writeObject(new Message(SHUTDOWN, null));

                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(ChatServerWorker.class.getName()).log(Level.SEVERE, "[ChatServerWorker].run", ex);
                    }
                }

                System.out.println("Shut down all clients, exiting ...");

                System.exit(0);
            
            case NOTE:
                System.out.println((String) message.getContent());

                participantsIterator = ChatServer.participants.iterator();
                while(participantsIterator.hastNext())
                {
                    participantInfo = participantsIterator.next();

                    try {
                        chatConnection = newSocket(participantInfo.address, participantInfo.port);

                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                        writeToNet.writeObject(message);

                        chatConnection.close();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(ChatServerWorker.class.getName()).log(Level.SEVERE, "[ChatServerWorker].run", ex);
                    }
                }

                break;

            default:
                //Log unknown message type
                break;
    }
 }   
}
