package com.larvalabs.googlevoiceplugin;

/**
 * User: matt
 * Date: Dec 1, 2010
 * Time: 1:24:29 PM
 */

import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.app.AlertDialog;
import org.apache.http.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;

/**
 * A preferences login dialog for Google services.
 * User: matt
 * Date: Nov 23, 2009
 * Time: 1:22:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleAuthPreferenceDialog extends DialogPreference {

    private static final String TAG = GoogleVoiceContentProvider.class.getName();

    public enum AuthType {
        SID("SID="),
        LSID("LSID="),
        AUTH("Auth=");

        private String keyInResult;

        AuthType(String key) {
            keyInResult = key;
        }

        public String getKeyInResult() {
            return keyInResult;
        }
    }

    private String title;
    private String usernamePrefsKey;
    private String authPrefsKey;
    private String serviceName;
    private EditText username;
    private EditText password;
    private TextView checkResults;
    private AuthType authType;

    public GoogleAuthPreferenceDialog(Context context, AttributeSet attrs, String title, String summary, String usernamePrefsKey, String authPrefsKey, String serviceName, AuthType authType) {
        super(context, attrs);
        this.title = title;
        this.usernamePrefsKey = usernamePrefsKey;
        this.authPrefsKey = authPrefsKey;
        this.serviceName = serviceName;
        this.authType = authType;

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        setTitle(title);
        setSummary(summary);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(title);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String usernameStr = prefs.getString(usernamePrefsKey, null);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View settingsViews = inflater.inflate(R.layout.loginpreferences, null);
        View checkButton = settingsViews.findViewById(R.id.check_button);
        checkResults = (TextView) settingsViews.findViewById(R.id.check_results);
        this.username = (EditText) settingsViews.findViewById(R.id.login_username);
        this.username.setText(usernameStr);
        this.password = (EditText) settingsViews.findViewById(R.id.login_password);
        checkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkResults.setText("Trying login...");
                AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
                    @Override
                    protected String doInBackground(Object... params) {
                        try {
                            String username = GoogleAuthPreferenceDialog.this.username.getText().toString();
                            String password = GoogleAuthPreferenceDialog.this.password.getText().toString();
                            String authKey = getAuthKey(username, password);
                            if (authKey != null) {
                                return "Login successful.";
                            } else {
                                return "Login failed.";
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (result == null) {
                            checkResults.setText("Login failed.");
                            return;
                        }
                        checkResults.setText(result);
                    }
                };
                task.execute();
            }
        });
        builder.setView(settingsViews);
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String username = this.username.getText().toString();
            String password = this.password.getText().toString();
            Log.d(TAG, "Setting username to " + username);
            Toast toast = Toast.makeText(getContext(), "Saving login info.", 1000);
            toast.show();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            checkResults.setText("Storing authorization...");
            String authToken = getAuthKey(username, password);
            Log.d(TAG, "Storing override auth key for google service " + serviceName);
            prefs.edit().putString(authPrefsKey, authToken).commit();
            prefs.edit().putString(usernamePrefsKey, username).commit();
        }
        super.onDialogClosed(positiveResult);
    }

    public String getAuthKey(String username, String password) {
        try {
            Log.d(TAG, serviceName + " Trying to log in");
            long startTime = SystemClock.uptimeMillis();
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("accountType", "HOSTED_OR_GOOGLE"));
            params.add(new BasicNameValuePair("accountType", "GOOGLE"));
            params.add(new BasicNameValuePair("Email", username));
            params.add(new BasicNameValuePair("Passwd", password));
            params.add(new BasicNameValuePair("service", serviceName));
            params.add(new BasicNameValuePair("source", "SlidescreenGoogleVoicePlugin"));
            String reply;
            HttpPost post = new HttpPost("https://www.google.com/accounts/ClientLogin");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(post);
            reply = readAll(response.getEntity().getContent());
            Log.d(TAG, serviceName + " login response: " + reply);

//                            reply = readAll(doPost("https://www.google.com/accounts/ClientLogin", params, false));
            long now = SystemClock.uptimeMillis();
//                        if (Config.LOGD) Log.d(TAG, String.format("Login request took %dms", now - startTime));
            Log.d(TAG, "  Extracting auth type " + authType.getKeyInResult());
            int index = reply.indexOf(authType.getKeyInResult());
            if (index > -1) {
                int end = reply.indexOf('\n', index);
                if (end > index + authType.getKeyInResult().length()) {
                    String authToken = reply.substring(index + authType.getKeyInResult().length(), end);
                    Log.d(TAG, serviceName + " auth token is " + authToken);
                    return authToken;
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private String readAll(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 16 * 1024);
        StringWriter sw = new StringWriter();
        char[] buf = new char[32 * 1024];
        try {
            while (true) {
                int len = reader.read(buf);
                if (len == -1)
                    break;
                sw.write(buf, 0, len);
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return sw.toString();
    }

}