package org.couchbase.devex;

import com.couchbase.lite.Database;
import com.couchbase.lite.listener.LiteListener;
import org.couchbase.devex.controllers.MessagesController;
import org.couchbase.devex.services.MessageDB;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;

/**
 * Created by ldoguin on 13/02/15.
 */
public class Configuration {

    public static final String SERVICE_TYPE = "_cblite._http._tcp.local.";

    public static final String SERVICE_DESCRIPTION = "I am a message service demo";

    public final String serviceName;

    private JmDNS jmdns;
    private Database database;

    public Configuration(Database database, String serviceName) throws IOException {
        this.database = database;
        this.serviceName = serviceName;
        InetAddress addr = InetAddress.getLocalHost();
        String hostname = InetAddress.getByName(addr.getHostName()).toString();
        this.jmdns = JmDNS.create(addr, hostname);
    }

    public void exposeService(int port) throws IOException {
        ServiceInfo sInfos = ServiceInfo.create(SERVICE_TYPE, serviceName, port, SERVICE_DESCRIPTION);
        jmdns.registerService(sInfos);
    }

    public void listenForService(){
        jmdns.addServiceListener(SERVICE_TYPE, new DiscoveryListener(database, jmdns, serviceName));
    }

    public int startCBLiteListener(int port) {
        LiteListener ls = new LiteListener(database.getManager(), port, null);

        Thread thread = new Thread(ls);
        thread.start();
        return ls.getListenPort();
    }
}
