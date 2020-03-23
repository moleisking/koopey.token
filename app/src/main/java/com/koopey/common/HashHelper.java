package com.koopey.common;

import android.util.Log;
import java.security.MessageDigest;

public class HashHelper {

    private final static String LOG_HEADER = "HASH:HELPER";

    public static String parseMD5(String item) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(item.getBytes());
            item = messageDigest.toString();
        } catch (Exception ex) {
            Log.d(LOG_HEADER + ":MD5", ex.getMessage());
        }
        return item;
    }
}
