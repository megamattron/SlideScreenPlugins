package com.larvalabs.googlevoiceplugin;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * User: matt
 * Date: Nov 15, 2010
 * Time: 3:38:41 PM
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                // Trigger content provider as a test
                String[] columns = new String[]{"_ID"};
                Cursor cursor = getContentResolver().query(GoogleVoiceContentProvider.CONTENT_URI, null, null, null, null);

                return null;
            }
        };
        task.execute(null);
    }
}
