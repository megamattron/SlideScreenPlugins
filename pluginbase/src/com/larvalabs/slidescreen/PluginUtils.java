package com.larvalabs.slidescreen;

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
}