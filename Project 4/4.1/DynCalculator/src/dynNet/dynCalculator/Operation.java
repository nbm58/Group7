package dynNet.dynCalculator;

/**
 * Interface [Operation]
 * <p>
 * Defines the interface, which has to be implemented by concrete
 * operations. These classes can be loaded from anywhere.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version May 2002
 */
public interface Operation{
	
	float calculate(float firstOperand, float secondOperand);
}
