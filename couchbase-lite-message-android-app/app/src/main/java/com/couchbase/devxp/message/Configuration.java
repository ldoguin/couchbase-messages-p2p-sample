package com.couchbase.devxp.message;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.couchbase.lite.Database;
import com.couchbase.lite.listener.LiteListener;
import com.couchbase.lite.util.Log;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ldoguin on 13/02/15.
 */
public class Configuration {

    public static final String SERVICE_TYPE = "_cblite._http._tcp.local.";

    public static final String SERVICE_DESCRIPTION = "I am a message service demo";

    public final String serviceName;

    private JmDNS jmdns;
    private Database database;
    android.net.wifi.WifiManager.MulticastLock lock;

    public Configuration(WifiManager wifi, Database database, String serviceName) throws IOException {
        this.database = database;
        this.serviceName = serviceName;

        WifiInfo wifiinfo = wifi.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();
        byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException e1) {
            throw new RuntimeException(e1);
        }

        lock = wifi.createMulticastLock("mylockthereturn");
        lock.setReferenceCounted(true);
        lock.acquire();
        try {
            jmdns = JmDNS.create(addr);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
