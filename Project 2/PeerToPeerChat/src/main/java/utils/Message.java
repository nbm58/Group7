package utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class [Message] Defines a generic Message that has a message type and content.
 * Instances of this class can be sent over a network, using object streams.
 * Message types are defined in MessageTypes
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Message implements MessageTypes, Serializable {
    
    // type of message, types are defined in interface MessageTypes
    int type;
    // content that is specific to a certain message type
    Object content;
    ArrayList<NodeInfo> participants;

    
    // constructor
    public Message(int type, Object content) {
        this.type = type;
        this.content = content;
    }
    
    public Message(int type, ArrayList<NodeInfo> participants) {
        this.type = type;
        this.participants = participants;
    }
    
    // getters
    public int getType() 
    {
        return type;
    }

    public Object getContent()
    {
        return content;
    }
    
    public ArrayList<NodeInfo> getParticipants()
    {
        return participants;
    }
}