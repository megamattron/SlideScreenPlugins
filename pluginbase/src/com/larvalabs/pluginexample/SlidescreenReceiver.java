package com.larvalabs.pluginexample;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import com.larvalabs.slidescreen.PluginReceiver;

/**
 * @author John Watkinson
 */
public class SlidescreenReceiver extends PluginReceiver {

    

    @Override
    public int getColor() {
        return Color.rgb(255, 0, 0);
    }

    @Override
    public Uri getContentProviderURI() {
        return SlideDemoContentProvider.CONTENT_URI;
    }

    @Override
    public String getName() {
        return "Example Plugin";
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.group_icon;
    }

    @Override
    public Intent[] getSingleTapShortcutIntents() {
        Intent groupIntent = new Intent(Intent.ACTION_MAIN);
        groupIntent.setType("vnd.android-dir/mms-sms");
        return new Intent[]{groupIntent};
    }

    @Override
    public Intent[] getLongpressShortcutIntents() {
        Intent longIntent = new Intent(Intent.ACTION_MAIN);
        longIntent.setComponent(new ComponentName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity"));
        return new Intent[]{longIntent};
    }

    @Override
    public Intent getPreferenceActivityIntent() {
        // Replace this with an Intent to launch your app's preferences activity
        return null;
    }
}
