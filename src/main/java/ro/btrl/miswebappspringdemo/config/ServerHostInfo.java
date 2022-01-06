package ro.btrl.miswebappspringdemo.config;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

public class ServerHostInfo {

    private static volatile ServerHostInfo singleton;
    private InitialContext ctx;
    private String weblogicDomainName;
    private String weblogicVersion;
    private String machineName;
    private String serverName;
    private String rootDirectory;
    private String host;
    private int port;

    private ServerHostInfo() {
        try {
            ctx = new InitialContext();
            MBeanServer beanServer = (MBeanServer)ctx.lookup("java:comp/env/jmx/runtime");

            // Get RuntimeServiceMBean
            ObjectName runtimeService =
                    new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");

            // Retrieve DomainConfiguration MBean from RuntimeServiceMBean
            ObjectName domainCFG = (ObjectName)beanServer.getAttribute(runtimeService, "DomainConfiguration");

            // Get Weblogic DomainName *attribute) from DomainConfiguration MBean
            setWeblogicDomainName((String)beanServer.getAttribute(domainCFG, "Name"));

            setRootDirectory((String)beanServer.getAttribute(domainCFG, "RootDirectory"));
            setWeblogicVersion((String)beanServer.getAttribute(domainCFG, "DomainVersion"));

            // Retrieve ServerConfiguration MBean from RuntimeService MBean
            ObjectName serverCFG = (ObjectName)beanServer.getAttribute(runtimeService, "ServerConfiguration");

            // Retrieve Machine MBean from ServerConfiguration MBean
            ObjectName machineCFG = (ObjectName)beanServer.getAttribute(serverCFG, "Machine");

            // Retrieve Machine Name from Machine MBean
            setMachineName((String)beanServer.getAttribute(machineCFG, "Name"));

            // Retrieve ManagedServer Name from ServerConfiguration MBean
            setServerName((String)beanServer.getAttribute(serverCFG, "Name"));

        } catch (Exception e) {
            setWeblogicDomainName("Necunoscut");
            setWeblogicVersion("Necunoscut");
            setMachineName("Necunoscut");
            setServerName("Necunoscut");
            setRootDirectory("Necunoscut");
            e.printStackTrace();
        }
    }

    public static ServerHostInfo getInstance() {
        if (singleton == null) {
            singleton = new ServerHostInfo();
        }
        return singleton;
    }

    public String getWeblogicDomainName() {
        return weblogicDomainName;
    }

    public void setWeblogicDomainName(String weblogicDomainName) {
        this.weblogicDomainName = weblogicDomainName;
    }

    public String getWeblogicVersion() {
        return weblogicVersion;
    }

    public void setWeblogicVersion(String weblogicVersion) {
        this.weblogicVersion = weblogicVersion;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
