package mahesh.assignment;

/** 
 * server up and server down
 */
public interface ServiceListener {
    void serviceUp(String name, long timestamp);
    void serviceDown(String name, long timestamp);
}
