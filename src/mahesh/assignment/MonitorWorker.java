package mahesh.assignment;

import java.io.IOException;
import java.net.Socket;

/**
 * Connects to service and updates timestamp of the last successful attempt
 *     In case if waiting threshold is exceeded - an event of ServerUp or ServerDown is fired
 */
public class MonitorWorker implements Runnable {
   
    protected ServiceConfiguration configuration;

    private void connectionOk() {
    	 System.out.println(String.format("Connection OK for %s", configuration.getName()));

        long timestamp = System.currentTimeMillis();
        configuration.setTimestampLastRun(timestamp);
        boolean stateChanged = configuration.getState().markAsRunning(timestamp, configuration.getGraceInterval());
        if (stateChanged) {
            configuration.fireEventServiceUp(timestamp);
        }
    }

    private void connectionNotOk() {
    	 System.out.println(String.format("Connection NOT OK for %s", configuration.getName()));

        long timestamp = System.currentTimeMillis();
        configuration.setTimestampLastRun(timestamp);
        boolean stateChanged = configuration.getState().markAsNotRunning(timestamp, configuration.getGraceInterval());
        if (stateChanged) {
            configuration.fireEventServiceDown(timestamp);
        }
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(configuration.getHost(), configuration.getPort());
            if (socket.isConnected()) {
                connectionOk();
            } else {
                connectionNotOk();
            }
        } catch (Exception e) {
            connectionNotOk();
            System.out.println("Unexpected exception on socket connection level");
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
            	 System.out.println("Exception on closing socket");
            }
        }
    }

    public MonitorWorker(String name) throws IOException {        
        configuration = Configurator.getInstance().getConfiguration(name);
    }
}
