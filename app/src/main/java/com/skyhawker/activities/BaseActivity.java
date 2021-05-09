package com.skyhawker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.skyhawker.R;
import com.skyhawker.fragments.BaseFragment;
import com.skyhawker.utils.Utils;

import java.util.concurrent.TimeUnit;


/**
 * Created on 7/03/17.
 * Base class for all the activities
 */
public abstract class BaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, BaseFragment.ProgressDialogInteraction {

    //instance to progress dialog
    private ProgressDialog mProgressDialog;

    private boolean mIsStopped = false;

    protected static final long HOURTOMILLIS = TimeUnit.HOURS.toMillis(1);

    private static long mEnterBackgroundTimestamp = 0;

    public boolean isNotificationPopupDisplayedForSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
        syncActionBarArrowState();
        BaseFragment baseFragment = getTopFragment();
        if (baseFragment != null) {
            baseFragment.setToolbarTitle(baseFragment.getTitle());
        }
    }

    /**
     * @return the top fragment in the fragment manager by id fragment_container
     */
    @Nullable
    BaseFragment getTopFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    /**
     * sync the action bar back arrow state based on the size of back stack
     */
    abstract protected void syncActionBarArrowState();

    @Override
    protected void onResume() {
        super.onResume();
        //sync the arrow state of button if activity is restarted or restored
        onBackStackChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsStopped = false;
        if (System.currentTimeMillis() - mEnterBackgroundTimestamp > HOURTOMILLIS) {
            isNotificationPopupDisplayedForSession = false;
        }
    }

    @Override
    protected void onStop() {
        mEnterBackgroundTimestamp = System.currentTimeMillis();
        super.onStop();
        mIsStopped = true;
    }

    @Override
    public void onBackPressed() {
        if (!mIsStopped) {
            super.onBackPressed();
            Utils.hideKeyboard(this);
        }
    }


    /**
     * hide toolbar from screen layout
     */
    public void hideToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    public void showProgressDialog(String message) {
        //create the instance of progress dialog if not created
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
        }

        //set the message and show the dialog
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Set current screen name
     *
     * @param currentScreen current screen name
     */
    public void setCurrentScreen(String currentScreen, String screenClassName) {
        if (!TextUtils.isEmpty(currentScreen)) {
            FirebaseAnalytics.getInstance(this).setCurrentScreen(this, currentScreen, screenClassName);
        }
    }
}
