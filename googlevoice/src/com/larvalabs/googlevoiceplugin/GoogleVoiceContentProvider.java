package com.larvalabs.googlevoiceplugin;

import android.content.*;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.larvalabs.slidescreen.PluginUtils;
import com.techventus.server.voice.Voice;
import com.techventus.server.voice.exception.AuthenticationException;
import com.techventus.server.voice.util.JSONContants;
import com.techventus.server.voice.util.ParsingUtil;
import com.techventus.server.voice.util.SMSParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.larvalabs.slidescreen.PluginConstants.*;

public class GoogleVoiceContentProvider extends ContentProvider {

    private static final String TAG = GoogleVoiceContentProvider.class.getName();

    public static final Uri CONTENT_URI = Uri.parse("content://com.larvalabs.googlevoiceplugin");

    public boolean onCreate() {
        Log.d(getClass().getName(), "* CREATED.");
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!preferences.contains(GoogleVoicePluginPreferences.SETTING_USERNAME)) {
            Log.w(TAG, "No username set, telling SlideScreen we need to show settings entry.");
            return makeSettingsNeededCursor();
        }

        String username = preferences.getString(GoogleVoicePluginPreferences.SETTING_USERNAME, "nousername");
        String authToken = preferences.getString(GoogleVoicePluginPreferences.SETTING_AUTH_TOKEN, "");

        try {
            Voice voice = null;
            try {
                voice = new Voice(username, authToken, false);
            } catch (AuthenticationException e) {
                Log.e(TAG, "Login failure, telling SlideScreen we need to show settings entry: " + e.getMessage(), e);
                return makeSettingsNeededCursor();
            }

            String inbox = voice.getInbox();
            String jsonResponse = ParsingUtil.removeUninterestingParts(inbox,
                    SMSParser.FilterResponse.JSON_BEGIN, SMSParser.FilterResponse.JSON_END, false);
            JSONObject json = new JSONObject(jsonResponse);
            JSONObject messages = json.getJSONObject(JSONContants.MESSAGES);
            JSONArray names = messages.names();
            MatrixCursor cursor = new MatrixCursor(fields);
            for (int i = 0; i < names.length(); i++) {
                JSONObject msgobj = messages.getJSONObject(names.getString(i));
                String id = msgobj.has(JSONContants.ID) ? msgobj.getString(JSONContants.ID) : "";
                String dispNum = msgobj.has(JSONContants.DISPLAY_NUMBER) ? msgobj.getString(JSONContants.DISPLAY_NUMBER) : "";
                long startTime = msgobj.has(JSONContants.START_TIME) ? msgobj.getLong(JSONContants.START_TIME) : 0;
//                    String note = jsonSmsThread.has(JSONContants.NOTE) ? jsonSmsThread.getString(JSONContants.NOTE) : "";
                boolean isRead = msgobj.has(JSONContants.IS_READ) ? msgobj.getBoolean(JSONContants.IS_READ) : false;
                if (!isRead) {
                    JSONArray labels = msgobj.getJSONArray(JSONContants.LABELS);
                    String subj = "";
                    for (int j = 0; j < labels.length(); j++) {
                        String label = labels.getString(j);
                        if (JSONContants.LABEL_SMS.equals(label)) {
                            subj += "SMS ";
                        } else if (JSONContants.LABEL_VOICEMAIL.equals(label)) {
                            subj += "VOICEMAIL ";
                        } else if (JSONContants.LABEL_MISSED.equals(label)) {
                            subj += "MISSED ";
                        }
                    }
                    MatrixCursor.RowBuilder builder = cursor.newRow();
                    for (String field : fields) {
                        if (FIELD_ID.equals(field)) {
                            builder.add("" + id);
                        } else if (FIELD_TITLE.equals(field)) {
                            builder.add(dispNum);
                        } else if (FIELD_LABEL.equals(field)) {
                            builder.add("");
                        } else if (FIELD_TEXT.equals(field)) {
                            builder.add(subj);
                        } else if (FIELD_DATE.equals(field)) {
                            builder.add(startTime);
                        } else if (FIELD_PRIORITY.equals(field)) {
                            builder.add(startTime);
                        } else if (FIELD_INTENT.equals(field)) {
                            ArrayList<String> intents = new ArrayList<String>();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setComponent(new ComponentName("com.google.android.apps.googlevoice", "com.google.android.apps.googlevoice.SplashActivity"));
                            intents.add(intent.toURI());
                            intents.add(new Intent(Intent.ACTION_VIEW, Uri.parse("https://voice.google.com")).toURI());
                            builder.add(PluginUtils.combineStrings(intents));
                        } else {
                            builder.add("");
                        }
                    }
                }

                Log.d(TAG, "    " + dispNum + ": " + isRead);
//                    boolean isStarred = jsonSmsThread.has(JSONContants.STARRED) ? jsonSmsThread.getBoolean(JSONContants.STARRED) : false;
//                    SMSThread smsThread = new SMSThread(id, note, new Date(startTime), null, isRead, isStarred);
//                    result.put(id, smsThread);
            }
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;


            /*
//            Log.d(TAG, "  Inbox: " + inbox);
            String jsonRep = ParsingUtil.removeUninterestingParts(inbox, SMSParser.FilterResponse.JSON_BEGIN, SMSParser.FilterResponse.JSON_END, false);
//            Log.d(TAG, "  JSON Rep: " + jsonRep);
            JSONObject jsonTop = new JSONObject(jsonRep);
            Log.d(TAG, "Created json object.");
            JSONArray jsonArray = jsonTop.getJSONArray("messages");
            Log.d(TAG, "  Messages array: " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject message = jsonArray.getJSONObject(i);
                Log.d(TAG, "    " + message.getString("displayNumber") + ": " + message.getBoolean("isRead"));
            }
            */
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;

        /*
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
                    builder.add("AWESOME");
                } else if (FIELD_TEXT.equals(field)) {
                    builder.add("Hello and welcome to item #" + i + ".");
                } else if (FIELD_DATE.equals(field)) {
                    builder.add(time - i);
                } else if (FIELD_PRIORITY.equals(field)) {
                    builder.add(time - i);
                } else {
                    builder.add("");
                }
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
        */
    }

    private Cursor makeSettingsNeededCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{FIELD_SETTINGS_NEEDED_MESSAGE});
        MatrixCursor.RowBuilder builder = cursor.newRow();
        builder.add("Login failed, press here to login.");
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return TYPE_ENTRY;
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
