package com.larvalabs.googlevoiceplugin;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import com.larvalabs.slidescreen.PluginReceiver;

/**
 * @author John Watkinson
 */
public class GoogleVoicePluginReceiver extends PluginReceiver {

    @Override
    public int getColor() {
        return Color.rgb(255, 255, 255);
    }

    @Override
    public Uri getContentProviderURI() {
        return GoogleVoiceContentProvider.CONTENT_URI;
    }

    @Override
    public String getName() {
        return "Google Voice";
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.group_icon;
    }

    @Override
    public Intent[] getSingleTapShortcutIntents() {
        Intent[] intents = new Intent[2];
        Intent groupIntent = new Intent(Intent.ACTION_MAIN);
        groupIntent.setComponent(new ComponentName("com.google.android.apps.googlevoice", "com.google.android.apps.googlevoice.SplashActivity"));
        intents[0] = groupIntent;
        intents[1] = new Intent(Intent.ACTION_VIEW, Uri.parse("https://voice.google.com"));
        return intents;
    }

    @Override
    public Intent[] getLongpressShortcutIntents() {
        // Note this is the same as the short press intents right now, will hopefully replace later
        Intent[] intents = new Intent[2];
        Intent groupIntent = new Intent(Intent.ACTION_MAIN);
        groupIntent.setComponent(new ComponentName("com.google.android.apps.googlevoice", "com.google.android.apps.googlevoice.SplashActivity"));
        intents[0] = groupIntent;
        intents[1] = new Intent(Intent.ACTION_VIEW, Uri.parse("https://voice.google.com"));
        return intents;
    }

    @Override
    public Intent getPreferenceActivityIntent() {
        Intent prefsIntent = new Intent(Intent.ACTION_MAIN);
        prefsIntent.setComponent(new ComponentName("com.larvalabs.googlevoiceplugin", "com.larvalabs.googlevoiceplugin.GoogleVoicePluginPreferences"));
        return prefsIntent;
    }
}
