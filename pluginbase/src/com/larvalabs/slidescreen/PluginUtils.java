package com.larvalabs.slidescreen;

import java.io.*;
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