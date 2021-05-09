package com.skyhawker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skyhawker.R;
import com.skyhawker.fragments.SignInFragment;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;

public class SignUpActivity extends BaseActivity {

    private static String TAG = SignUpActivity.class.toString();
    //toolbar
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

// Set Tool bar to the screen
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        AppPreferences.setFcmToken(token);
                    }
                });


        if (savedInstanceState == null) {
            // get fragment manager
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, SignInFragment.newInstance(null));
            ft.commit();
        }

    }

    @Override
    protected void syncActionBarArrowState() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mToolbar.setNavigationIcon(null);
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
