package com.larvalabs.googlevoiceplugin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * User: matt
 * Date: Nov 24, 2010
 * Time: 2:13:16 PM
 */
public class GoogleVoicePluginPreferences extends PreferenceActivity {
    public static final String SETTING_USERNAME = "voiceUsername";
    public static final String SETTING_AUTH_TOKEN = "voiceAuthToken";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final GoogleAuthPreferenceDialog loginDialog = new GoogleAuthPreferenceDialog(this, null, "Login", "Username and password information.",
                SETTING_USERNAME, SETTING_AUTH_TOKEN, "grandcentral", GoogleAuthPreferenceDialog.AuthType.AUTH);
        root.addPreference(loginDialog);

        setPreferenceScreen(root);
    }
}
