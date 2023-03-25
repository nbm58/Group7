package message;
/**
 * Interface [MessageTypes] Defines the different message types used in the application.
 * Any entity using objects of class Message needs to implement this interface.
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public interface MessageTypes {
    
    public static final int OPEN_TRANSACTION = 1;
    public static final int CLOSE_TRANSACTION = 2;
    public static final int READ_REQUEST = 3;
    public static final int WRITE_REQUEST = 4;
    public static final int TRANSACTION_COMMITTED = 5;
    public static final int TRANSACTION_ABORTED = 5;
}