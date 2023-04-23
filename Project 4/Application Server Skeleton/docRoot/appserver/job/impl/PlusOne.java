package appserver.job.impl;

import appserver.job.Tool;

/**
 * Class [PlusOne] Simple POC class that implements the Tool interface
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class PlusOne implements Tool{

    PlusOneAux helper = null;
    
    @Override
    public Object go(Object parameters) {
        
        helper = new PlusOneAux((Integer) parameters);
        return helper.getResult();
    }
}
