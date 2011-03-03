package com.larvalabs.pluginexample;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import com.larvalabs.slidescreen.PluginUtils;

import java.util.ArrayList;
import java.util.Calendar;

import static com.larvalabs.slidescreen.PluginConstants.*;

/**
 * @author John Watkinson
 */
public class SlideDemoContentProvider extends ContentProvider {

    private static final String TAG = SlideDemoContentProvider.class.getName();

    public static final Uri CONTENT_URI = Uri.parse("content://com.larvalabs.pluginexample");

    public boolean onCreate() {
        Log.d(TAG, "* CREATED.");
        return true;
    }

    public Cursor query(Uri uri, String[] fields, String s, String[] strings1, String s1) {
        if (fields == null || fields.length == 0) {
            fields = FIELDS_ARRAY;
        }
        Log.d(TAG, "* QUERY Called.");
        for (String string : fields) {
            Log.d(TAG, "  ARG: " + string);
        }
        MatrixCursor cursor = new MatrixCursor(fields);
        for (int i = 1; i <= 2; i++) {
            MatrixCursor.RowBuilder builder = cursor.newRow();
            long time = Calendar.getInstance().getTime().getTime();
            for (String field : fields) {
                if (FIELD_ID.equals(field)) {
                    builder.add("" + i);
                } else if (FIELD_TITLE.equals(field)) {
                    builder.add("Title #" + i);
                } else if (FIELD_LABEL.equals(field)) {
                    builder.add("LABEL");
                } else if (FIELD_TEXT.equals(field)) {
                    builder.add("Hello and welcome to item #" + i + ".");
                } else if (FIELD_DATE.equals(field)) {
                    builder.add(time - i);
                } else if (FIELD_PRIORITY.equals(field)) {
                    builder.add(time - i);
                } else if (FIELD_INTENT.equals(field)) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setType("vnd.android-dir/mms-sms");
                    builder.add(PluginUtils.encodeIntents(intent));
                } else {
                    builder.add("");
                }
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    public void sendUpdatedNotification() {
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
    }
}
