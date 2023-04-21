package appserver.job;

/**
 * Interface [Tool]
 * This is the interface that any tool needs to implement. It defines the call back method,
 * that entities executing the tool, i.e. doing the job, will call, e.g. satellite servers.
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public interface Tool {
    public Object go(Object parameters);
}
