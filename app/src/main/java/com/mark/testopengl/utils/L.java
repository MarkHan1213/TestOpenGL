package com.mark.testopengl.utils;

import android.util.Log;

public class L {

    private static final String TAG = L.class.getSimpleName();
    private static final boolean DE_BUG = true;


    public static void e(Object o) {
        if (DE_BUG) {
            Log.e(TAG, o == null ? "null" : o.toString());
        }
    }


}
