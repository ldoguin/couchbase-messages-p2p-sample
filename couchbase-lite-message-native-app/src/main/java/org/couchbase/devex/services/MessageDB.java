package org.couchbase.devex.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Database;

public class MessageDB {

	public Map<String, Database> connections = new HashMap<String, Database>();

	public Database connection(String databaseName) {
		if (databaseName == null) {
			return connections.values().iterator().next();
		} else {
			return connections.get(databaseName);
		}
	}

	public void establishConnection(String databaseName) throws IOException,
			CouchbaseLiteException {
		if (!com.couchbase.lite.Manager.isValidDatabaseName(databaseName)) {
			throw new IllegalArgumentException(databaseName
					+ "is an invalid databasename");
		}
		Manager manager = new Manager(new JavaContext(),
				Manager.DEFAULT_OPTIONS);
		connections.put(databaseName, manager.getDatabase(databaseName));
	}

}
