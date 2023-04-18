package dynNet.operationsImpl;

import dynNet.dynCalculator.Operation;

/**
 * Class [SubtractionOperation]
 * <p>
 * This is a concrete operation class, that implements the
 * interface <code>Operation</code>.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version May 20002
 */
public class SubtractionOperation implements Operation{
	
	public float calculate(float firstNumber, float secondNumber){
		return firstNumber - secondNumber;
	}
}