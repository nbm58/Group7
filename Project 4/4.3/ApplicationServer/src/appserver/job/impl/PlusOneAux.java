package appserver.job.impl;

/**
 * Class [PlusOneAux] Helper class for PlusOne to demonstrate that dependent classes are loaded automatically
 * when a class is loaded (in this case PlusOne)
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class PlusOneAux {
    
    Integer number = null;
    
    public PlusOneAux(Integer number) {
        this.number = number;
    }
    
    public Integer getResult() {
        return number + 1;
    }
}
