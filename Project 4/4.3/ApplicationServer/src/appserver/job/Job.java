package appserver.job;

import java.io.Serializable;

/**
 * Class [Job] Wrapper class to encapsulate Job-related information,
 * i.e. the (fully qualified) name of the job's tool class
 * and the parameters the tool instance will take in its message go().
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Job implements Serializable{
    
    String toolName;
    Object parameters;
    
    public Job(String toolName, Object parameters) {
        this.toolName = toolName;
        this.parameters = parameters;
    }
    
    public String getToolName() {
        return toolName;
    }
    
    public Object getParameters() {
        return parameters;
    }
}
