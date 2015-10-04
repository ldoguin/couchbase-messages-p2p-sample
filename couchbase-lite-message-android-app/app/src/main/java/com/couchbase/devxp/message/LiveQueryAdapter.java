package com.couchbase.devxp.message;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;

/**
 * Created by phil on 04/11/14.
 *
 * Used as an Adapter for the list view, so our LiveQuery behaves like an ArrayList which
 * can directly be used as a Source for our ListView
 *
 */
public abstract class LiveQueryAdapter extends BaseAdapter {

    private LiveQuery query;
    private QueryEnumerator enumerator;
    private Context context;

    public LiveQueryAdapter(LiveQuery query, Context context) {
        this.query = query;
        this.context = context;
        // Everytime the query returns a new result we notify the ListView of this fact and update the UI
        query.addChangeListener(new LiveQuery.ChangeListener() {
            @Override
            public void changed(final LiveQuery.ChangeEvent changeEvent) {
                ((Activity) LiveQueryAdapter.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enumerator = changeEvent.getRows();
                        notifyDataSetChanged();
                    }
                });
            }
        });
        query.start();
    }

    @Override
    public int getCount() {
        return (enumerator == null) ? 0 : enumerator.getCount();
    }

    @Override
    public Object getItem(int i) {
        return enumerator != null ? enumerator.getRow(i).getDocument() : null;
    }

    @Override
    public long getItemId(int i) {
        return enumerator.getRow(i).getSequenceNumber();
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void invalidate() {
        if (query != null) {
            query.stop();
        }
    }
}
