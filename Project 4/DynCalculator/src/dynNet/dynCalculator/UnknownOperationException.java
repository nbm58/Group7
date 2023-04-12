package dynNet.dynCalculator;



/**
 * Class [UnknownOperationException]
 * <p>
 * Occurs, when an unknown operation is encountered
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version May 2002
 */
class UnknownOperationException extends Exception{

	String errorMessage = null;
	
	public UnknownOperationException(){
		errorMessage = "This operation is not implemented";
	}
	
	public UnknownOperationException(String s){
		errorMessage = "Operation \"" + s + "\" is not implemented";
	}

	public String toString(){
		return errorMessage;
	}
}
