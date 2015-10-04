package com.couchbase.devxp.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;

/**
 * Created by phil on 04/11/14.
 */
public class MessageAdapter extends LiveQueryAdapter {

    public MessageAdapter(LiveQuery query, Context context) {
        super(query, context);
    }

    @Override
    // Display a Message as an Item in the list View
    public View getView(int position, View convertView, ViewGroup parent) {
        // Load the View if not done so already from the view_presentation.xml
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_presentation, null);
        }

        // Get the document currently to be displayed
        final Document document = (Document) getItem(position);

        // make sure this is valid
        if (document == null || document.getCurrentRevision() == null) {
            return convertView;
        }

        // Turn the document into a message model we can operate on
        final Message message = Message.from(document);


        // Fill in all the view items
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        titleView.setText("Message:" + message.getMessage());

        return convertView;

    }


}
