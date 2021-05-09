package com.skyhawker.utils;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SkyhawkerApplication extends Application {

    /**
     * The default socket timeout in milliseconds
     */
    private static final int DEFAULT_TIMEOUT_MS = 10000;

    private final String TAG = SkyhawkerApplication.class.getSimpleName();

    private static SkyhawkerApplication mSharedInstance;
    private static DatabaseReference mDatabase;


    public static SkyhawkerApplication sharedInstance() {
        return mSharedInstance;
    }
    public static DatabaseReference sharedDatabaseInstance() {
        return mDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedInstance = this;
        AppPreferences.init(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
