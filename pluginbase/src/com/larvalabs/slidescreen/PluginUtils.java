package com.larvalabs.slidescreen;

import android.content.Intent;
import android.os.Parcel;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * User: matt
 * Date: Nov 30, 2010
 * Time: 11:21:19 AM
 */
public class PluginUtils {

    private static final String TAG = PluginUtils.class.getName();

    public static final String SEPERATOR = "!";

    public static String combineStrings(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string).append(SEPERATOR);
        }
        return sb.substring(0, sb.length()-1);
    }

    public static String encodeIntents(Intent... intents) {
        return encodeIntents(Arrays.asList(intents));
    }

    public static String encodeIntents(List<Intent> intents) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        try {
            out.writeInt(intents.size());
            for (Intent intent : intents) {
                Parcel parcel = Parcel.obtain();
                intent.writeToParcel(parcel, 0);
                byte[] data = parcel.marshall();
                final String s = Base64Util.encodeBytes(data);
                out.writeUTF(s);
                parcel.recycle();
            }
            out.close();
            byte[] result = bout.toByteArray();
            return Base64Util.encodeBytes(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readAll(InputStream stream) throws IOException {
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