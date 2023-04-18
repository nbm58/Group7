package dynNet.operationsImpl;

import dynNet.dynCalculator.Operation;

/**
 * Class [MultiplicationOperation]
 * <p>
 * This is a concrete operation class, that implements the
 * interface <code>Operation</code>.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version May 20002
 */
public class MultiplicationOperation implements Operation{
	
	public float calculate(float firstNumber, float secondNumber){
		return firstNumber * secondNumber;
	}
}