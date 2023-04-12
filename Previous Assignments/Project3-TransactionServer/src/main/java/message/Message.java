package message;

import java.io.Serializable;

/**
 * Class [Message] Defines a generic Message that has a message type and content.
 * Instances of this class can be sent over a network, using object streams.
 * Message types are defined in MessageTypes
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Message implements MessageTypes, Serializable {
    
    // type of message, types are defined in interface MessageTypes
    protected int type;
    // content that is specific to a certain message type
    protected Object content;

    
    // constructor
    public Message(int type, Object content) {
        this.type = type;
        this.content = content;
    }
    
    public Message(int type) {
        this.type = type;
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
}