package org.couchbase.devex.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.View;

public class Message {

	public static String VIEW_NAME = "messages-view";
	public static String DOC_TYPE = "message";

	public static Query findAllByDate(Database database) {
		View view = database.getView(VIEW_NAME);
			view.setMap(new Mapper() {
				@Override
				public void map(Map<String, Object> document, Emitter emitter) {
					if (DOC_TYPE.equals(document.get("type"))){
						emitter.emit(document.get("created_at"),document);
					}
				}
			}, "1");
		return view.createQuery();
	}

	public static void createMessage(Database database, String message) throws CouchbaseLiteException {
		Document doc = database.createDocument();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("message", message);
		properties.put("channels", "all");
		properties.put("type", DOC_TYPE);
		properties.put("created_at", new Date());
		doc.putProperties(properties);
	}

}
