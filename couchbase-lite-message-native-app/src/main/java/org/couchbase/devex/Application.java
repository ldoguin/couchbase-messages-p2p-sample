package org.couchbase.devex;

import org.couchbase.devex.controllers.MessagesController;
import org.couchbase.devex.services.MessageDB;

import com.couchbase.lite.Database;

public class Application {

	public static void main(String[] args) throws Exception {
		String serviceName = "My Message Service";
		if (args.length >= 1) {
			serviceName = args[0];
		}
		MessageDB mdb = new MessageDB();
		mdb.establishConnection("messages");
		Database db = mdb.connection("messages");
		new MessagesController(db);
		Configuration configuration = new Configuration(db, serviceName);
		int cbListenerPort = configuration.startCBLiteListener(5432);
		configuration.exposeService(cbListenerPort);
		configuration.listenForService();
	}
}