package mahesh.assignment;

import junit.framework.TestCase;
import mahesh.assignment.ServiceMonitor;
import mahesh.assignment.ServiceConfiguration;
import mahesh.assignment.ServiceListener;


/**
 * unit test Service Monitor
 */
public class ConnectionTester extends TestCase {
    public static final String SERVICE_ONE = "service1";
    public static final String SERVICE_TWO = "service2";
    public static final String SERVICE_THREE = "service3";
    

    private ServiceMonitor server;

    ServiceListener listener = new ServiceListener() {
        @Override
        public void serviceUp(String name, long timestamp) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            System.out.println((String.format("%s service @ %s:%s is UP at %s",
                    configuration.getName(),
                    configuration.getHost(),
                    configuration.getPort(),
                    String.valueOf(timestamp))));
        }

        @Override
        public void serviceDown(String name, long timestamp) {
            ServiceConfiguration configuration = server.configurator.getConfiguration(name);
            System.out.println((String.format("%s service @ %s:%s is DOWN at %s",
                    configuration.getName(),
                    configuration.getHost(),
                    configuration.getPort(),
                    String.valueOf(timestamp))));
        }
    };

    @Override
    protected void setUp() throws Exception {
        server = new ServiceMonitor();
    }

    @Override
    protected void tearDown() throws Exception {
        if (server != null) {
            server.configurator.reset();
            server.stopServer();
            server = null;
        }
    }


    public void test_Connections() throws Exception {
       
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ONE, "www.google.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_TWO, "www.yahoo.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_THREE, "www.wrongaddresstest.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        // register listener
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration sc = server.configurator.getConfiguration(name);
            sc.addListener(listener);
        }
        server.start();
        Thread.sleep(20*1000);
    }

    public void testConnectionsSwitch() throws Exception {
        
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ONE, "www.google.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        configuration = new ServiceConfiguration(SERVICE_TWO, "www.yahoo.com", 80, true, 1, 3);
        server.configurator.addConfiguration(configuration);

        // register listener
        for (String name : server.configurator.getServiceNames()) {
            ServiceConfiguration sc = server.configurator.getConfiguration(name);
            sc.addListener(listener);
        }
        server.start();
        Thread.sleep(20*1000);

        configuration = server.configurator.getConfiguration(SERVICE_TWO);
        configuration.setHost("www.wrongaddresstest.com");
        configuration.setPort(80);
        server.configurator.addConfiguration(configuration);

        Thread.sleep(20*1000);
    }

    public void testGraceThreshold() throws Exception {
        ServiceConfiguration configuration = new ServiceConfiguration(SERVICE_ONE, "www.google.com", 80, true, 1, 3);
        configuration.setQueryInterval(10);
        configuration.setGraceInterval(5);
        assertEquals(configuration.getQueryInterval(), 5);
    }

    public static void main(String[] args) {
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        org.junit.runner.JUnitCore.main(className);
    }
}
