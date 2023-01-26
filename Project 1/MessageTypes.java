package message;

/**
 * Interface [MessageTypes] Defines the different message types used in the application.
 * Any entity using objects of class Message needs to implement this interface.
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public interface MessageTypes {
    
    public static final int JOIN         = 1;
    public static final int LEAVE        = 2;
    public static final int NOTE         = 3;
    public static final int SHUTDOWN     = 4;
    public static final int SHUTDOWN_ALL = 5;    
}