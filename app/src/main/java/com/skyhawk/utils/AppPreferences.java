package com.skyhawk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.skyhawk.models.Session;

public class AppPreferences {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String SESSION = "session";
    private static final String IS_PREFERENCE_ADDED = "is_preference_added";
    public static final String SELECTED_HOME_SCREEN = "selected_home_screen";
    public static final String IsCONGRATULATIONACTIONDONE = "is_congratulation_done";

    private static final String IS_FCM_TOKEN = "IS_FCM_TOKEN";

    private static SharedPreferences mSharedPreferences;
    //ref to the last updated session
    private static Session userSession;

    private AppPreferences() {
        //private constructor to enforce Singleton pattern
    }

    /**
     * initialize the reference to shared preferences, this need to be initialize before any function call to this class
     *
     * @param context application context
     */
    static void init(@NonNull Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    /**
     * put boolean value with key
     *
     * @param key   key
     * @param value value
     */
    private static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get the selected home screen in the setting
     *
     * @return int value
     */
    public static int getSelectedHomeScreen() {
        return mSharedPreferences.getInt(SELECTED_HOME_SCREEN, 0);
    }

    public static void setSelectedHomeScreen(String key, @Nullable int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean IsCongratulationDone() {
        return mSharedPreferences.getBoolean(IsCONGRATULATIONACTIONDONE, false);
    }

    public static void setIsCongratulationDone(String key, @Nullable boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * get boolean value for key
     *
     * @param key      key
     * @param defValue default value
     * @return the value
     */
    @SuppressWarnings("SameParameterValue")
    private static boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * put long value with key
     *
     * @param key   key
     * @param value value
     */
    static void putLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * get value for key
     *
     * @param key      key
     * @param defValue default value
     * @return the value
     */
    @SuppressWarnings("SameParameterValue")
    static long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    /**
     * remove the keys that are required to be removed on logout
     */
    public static void logout() {

        //reset the session
        userSession = new Session();

        //clear all preferences
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Provides LogIn Status
     *
     * @return boolean
     */
    public static boolean isLoggedIn() {

        return !(TextUtils.isEmpty(userSession.getMobileNumber()) || userSession == null );

    }

    /**
     * put int value with key
     *
     * @param key   key
     * @param value value
     */
    private static void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * get value for key
     *
     * @param key      key
     * @param defValue default value
     * @return the value
     */
    @SuppressWarnings("SameParameterValue")
    private static int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * get the string value for key
     *
     * @param key      key
     * @param defValue default value
     * @return the value for key
     */
    @SuppressWarnings("SameParameterValue")
    public static String getString(String key, @Nullable String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * put long value with key
     *
     * @param key   key
     * @param value value
     */
    public static void putString(String key, @Nullable String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Get the reference to last updated session
     *
     * @return the session
     */
    public static Session getSession() {
                if (userSession == null) {
                    String json = mSharedPreferences.getString(SESSION, null);
                    if (TextUtils.isEmpty(json)) {
                        userSession = new Session();
                    } else {
                        userSession = new Gson().fromJson(json, Session.class);
                    }
        }
        return userSession;
    }

    /**
     * Save the current session
     *
     * @param session session
     */
    public static void setSession(Session session) {
        userSession = session;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SESSION, new Gson().toJson(session));
        editor.apply();
    }

    /**
     * Return true if init preference setup is done
     *
     * @return true if init preference setup is done
     */
    public static boolean isPreferenceAdded() {
        return mSharedPreferences.getBoolean(IS_PREFERENCE_ADDED, false);
    }

    /**
     * mark the preference setup is done
     */
    public static void markPreferenceAdded() {
        putBoolean(IS_PREFERENCE_ADDED, true);
    }


    /**
     * set incremented count
     *
     * @param key Key
     */
    public static void incrementViewCount(String key, boolean isShown) {
        // isShown will be true when pop up has been shown for session otherwise false
        if (isShown) {
            putInt("notification_" + key, -1);
        } else {
            // if already shown then don't increase the count
            if (getViewCount(key) >= 0) {
                putInt("notification_" + key, getViewCount(key) + 1);
            }
        }
    }

    /**
     * get incremented count
     *
     * @param key Key
     * @return Value of count
     */
    public static int getViewCount(String key) {
        return mSharedPreferences.getInt("notification_" + key, 0);
    }

    /**
     * is fcm token send on server
     *
     * @return true/false
     */
    public static String getFcmToken() {
        return mSharedPreferences.getString(IS_FCM_TOKEN, null);
    }

    /**
     * mark fcm token send on server
     */
    public static void setFcmToken(String token) {
        putString(IS_FCM_TOKEN, token);
    }
}
