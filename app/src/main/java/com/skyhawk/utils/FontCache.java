package com.skyhawk.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 7/03/17.
 */
public class FontCache {

    @SuppressWarnings("CanBeFinal")
    @NonNull
    private static Map<String, Typeface> sFontCache = new HashMap<>();

    private FontCache() {

    }

    /**
     * get the typeface using font name
     *
     * @param name name
     * @param context context
     * @return typeface
     */
    public static Typeface getTypeface(String name, @NonNull Context context) {

        Typeface typeface = sFontCache.get(name);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + name);
            } catch (Exception e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
                return null;
            }

            sFontCache.put(name, typeface);
        }

        return typeface;
    }
}
