package com.skyhawker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.skyhawker.R;
import com.skyhawker.models.Session;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    private final int INTERVAL_TIME = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Session session = AppPreferences.getSession();
                if(session != null && !TextUtils.isEmpty(session.getCreatePassword())) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(SplashActivity.this, SignUpActivity.class);
                    startActivity(i);
                }

                finish();
            }

        }, INTERVAL_TIME);*/

        final Intent intent = getIntent();
        if (Utils.isAppUrl(intent.getDataString()) || intent.hasExtra(Keys.NOTIFICATION)) {
            //if the app opens using link or notification then don't delay
            takeDecisionBasedOnSession(intent);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    takeDecisionBasedOnSession(intent);
                }
            }, INTERVAL_TIME);
        }
    }

    /**
     * /**
     * Take decision based on session to show the login screen or auto login to app
     *
     * @param intent intent
     */
    private void takeDecisionBasedOnSession(Intent intent) {
        Session session = AppPreferences.getSession();
        Intent newIntent = new Intent();
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (AppPreferences.isLoggedIn()) {
            // user is already loggedIn open the MainActivity
            newIntent.setClass(this, MainActivity.class);
            newIntent.putExtra(Keys.NOTIFICATION, intent.getSerializableExtra(Keys.NOTIFICATION));
            if (Utils.isAppUrl(intent.getDataString())) {
                //if app is open using reset password link from email and user is already login then show the message
                Utils.showToast(this, null, getString(R.string.already_logged_in));
            }

        } else {
            //open the login activity
            newIntent.putExtras(intent);
            newIntent.setData(intent.getData());
            newIntent.setClass(this, SignUpActivity.class);
        }

        startActivity(newIntent);
        finish();
    }

}
