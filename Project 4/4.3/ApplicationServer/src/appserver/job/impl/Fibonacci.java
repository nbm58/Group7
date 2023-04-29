package appserver.job.impl;

import appserver.job.Tool;

/**
 *
 * @author Tyler M.
 */
public class Fibonacci implements Tool
{
    @Override
    public Object go(Object parameters) {
        Integer number = (Integer) parameters;
        
        return fibonacci(number);
    }
    
    public Integer fibonacci(int number)
    {
        if (number == 1 || number == 0)
        {
            return number;
        }
        else
        {
            return fibonacci(number - 1) + fibonacci(number - 2);
        }
    }
}
