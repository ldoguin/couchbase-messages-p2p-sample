package org.couchbase.devex;

import com.couchbase.lite.Database;
import com.couchbase.lite.listener.LiteListener;
import com.couchbase.lite.replicator.Replication;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.URL;

/**
 * Created by ldoguin on 13/02/15.
 */
public class DiscoveryListener implements ServiceListener {

    private Database database;
    private JmDNS jmdns;
    private String serviceName;

    public DiscoveryListener(Database database, JmDNS jmdns, String serviceName) {
        this.database = database;
        this.jmdns = jmdns;
        this.serviceName = serviceName;
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        if (! serviceName.equals(event.getName())){
            jmdns.requestServiceInfo(event.getType(), event.getName(), 10);
        }
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        System.out.println(event.getName() + " removed");
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        System.out.println("RESOLVED");
        String[] serviceUrls = event.getInfo().getURLs();
        for (String url : serviceUrls) {
            System.out.println(url);
            setupSync(database, url + "/messages");
        }

    }

    public void setupSync(Database database, String syncUrl) {
        try {
            URL url = new URL(syncUrl);
            Replication pullReplication = database.createPullReplication(url);

            pullReplication.setContinuous(true);
            pullReplication.start();

            Replication pushReplication = database.createPushReplication(url);
            pushReplication.setContinuous(true);
            pushReplication.start();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
