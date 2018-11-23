package mahesh.assignment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service Monitor class
 */

public class ServiceMonitor extends Thread {
    public static final int THREAD_INTERVAL = 1000;    

    protected boolean isThreadRunning = true;
    protected Configurator configurator = Configurator.getInstance();

    protected ExecutorService threadPool = Executors.newCachedThreadPool();


    public ServiceMonitor() {        
    	 System.out.println(String.format("Started Monitoring Server at %s", System.currentTimeMillis()));
    }

    protected boolean isTimeToRun(ServiceConfiguration configuration) {
        long timestamp = System.currentTimeMillis();
        return configuration.isRunning()
                && !configuration.isInOutage(timestamp)
                && (timestamp >= configuration.getTimestampLastRun() + configuration.getQueryInterval() * 1000);
    }

    public void run() {
        try {
            while (isThreadRunning) {
                try {
                    for (String serviceName : configurator.getServiceNames()) {
                        ServiceConfiguration configuration = configurator.getConfiguration(serviceName);
                        if (!isTimeToRun(configuration)) {
                            continue;
                        }

                        threadPool.submit(new MonitorWorker(serviceName));
                    }

                    Thread.sleep(THREAD_INTERVAL);
                } catch (Exception e) {
                	 System.out.println("Exception on worker spawning level");
                }
            }
        } catch (Exception e) {
        	 System.out.println("Server side exception.");
        }

        System.out.println(String.format("Monitoring Server Stopped at %s", System.currentTimeMillis()));
    }

    public void stopServer() {
        isThreadRunning = false;
    }

}
