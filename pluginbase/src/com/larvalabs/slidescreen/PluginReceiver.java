package com.larvalabs.slidescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;

/**
 * User: matt
 * Date: Nov 30, 2010
 * Time: 10:42:18 AM
 */
public abstract class PluginReceiver extends BroadcastReceiver {

    public static final String LOGTAG = "SlidescreenPluginReceiver";

    public static final int API_VERSION = 1;

    public static final String INTENT_ACTION_DISCOVER_PLUGINS = "com.larvalabs.slidescreen.DISCOVER_PLUGINS";
    public static final String INTENT_ACTION_PLUGIN_MARK_READ = "com.larvalabs.slidescreen.PLUGIN_MARK_READ";
    public static final String INTENT_ACTION_REGISTER_PLUGIN = "com.larvalabs.slidescreen.REGISTER_PLUGIN";

    public static final String EXTRA_PLUGIN_API_VERSION = "apiVersion";    // Type: Integer
    public static final String EXTRA_PROVIDER_URI = "provider";    // Type: Uri
    public static final String EXTRA_GROUP_NAME = "groupName";        // Type: String
    public static final String EXTRA_GROUP_COLOR = "color";        // Type: Integer
    public static final String EXTRA_ICON = "icon";                // Type: Byte Array
    public static final String EXTRA_GROUP_INTENT = "groupIntent"; // Type: Intent[]
    public static final String EXTRA_LONG_INTENT = "longIntent";   // Type: Intent[]
    public static final String EXTRA_PREFERENCE_INTENT = "preferenceIntent";   // Type: Intent
    public static final String EXTRA_RECEIVER_CLASS = "receiverClass";   // Type: String

    public static final String EXTRA_MARK_READ_ID = "markReadId";   // Type: String


    @Override
    public final void onReceive(Context context, Intent intent) {
        if (INTENT_ACTION_DISCOVER_PLUGINS.equals(intent.getAction())) {
            Log.d(LOGTAG, "Plugin Discovery broadcast Received.");
            Uri uri = intent.getData();
            Intent response = new Intent(INTENT_ACTION_REGISTER_PLUGIN);
            response.setData(uri);
            response.putExtra(EXTRA_PROVIDER_URI, getContentProviderURI());
            response.putExtra(EXTRA_GROUP_NAME, getName());
            response.putExtra(EXTRA_PLUGIN_API_VERSION, API_VERSION);
            response.putExtra(EXTRA_GROUP_COLOR, getColor());
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), getIconResourceId());
            Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            {
                Canvas canvas = new Canvas(copy);
                Paint paint = new Paint();
                canvas.drawBitmap(bitmap, 0, 0, paint);
            }
            response.putExtra(EXTRA_ICON, copy);
            response.putExtra(EXTRA_GROUP_INTENT, getSingleTapShortcutIntents());
            response.putExtra(EXTRA_LONG_INTENT, getLongpressShortcutIntents());
            response.putExtra(EXTRA_PREFERENCE_INTENT, getPreferenceActivityIntent());
            response.putExtra(EXTRA_RECEIVER_CLASS, this.getClass().getName());
            Log.d(LOGTAG, "Sending response broadcast...");
            context.sendBroadcast(response);
            Log.d(LOGTAG, "Done.");
        } else if (INTENT_ACTION_PLUGIN_MARK_READ.equals(intent.getAction())) {
            String itemId = intent.getExtras().getString(EXTRA_MARK_READ_ID);
            Log.d(LOGTAG, "Recevied mark read intent for id: " + itemId);
            markedAsRead(itemId);
        }


    }

    public abstract Uri getContentProviderURI();

    public abstract String getName();

    public abstract int getColor();

    public abstract int getIconResourceId();

    public abstract Intent[] getSingleTapShortcutIntents();

    public abstract Intent[] getLongpressShortcutIntents();

    public abstract Intent getPreferenceActivityIntent();

    public abstract void markedAsRead(String itemId);
}
