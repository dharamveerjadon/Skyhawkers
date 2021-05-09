package com.skyhawker.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.skyhawker.R;
import com.skyhawker.activities.BaseActivity;

/**
 * Created on 7/03/17.
 * Base class for all the fragment
 */

public abstract class BaseFragment extends Fragment {

    public interface ProgressDialogInteraction {
        void showProgressDialog(String message);

        void dismissProgressDialog();
    }

    static final String ARG_TITLE = "title";

    @Nullable
    private String mTitle;

    boolean mIsViewCreatedFirstTime = true;

    private ProgressDialogInteraction mProgressDialogInteraction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mIsViewCreatedFirstTime) {
            getActivity().invalidateOptionsMenu();
        }

        mIsViewCreatedFirstTime = false;
    }

    /**
     * update the title on actionbar/toolbar
     *
     * @param title to be set on action bar
     */
    public void setToolbarTitle(String title) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    /**
     * @return get the fragment title
     */
    @Nullable
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title set the fragment title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param resId    The resource id of the string resource to use.  Can be formatted text.
     * @param duration duration
     */
    protected void showToast(int resId, int duration) {
        View view = getView();
        if (view == null) {
            Toast.makeText(getActivity(), resId, Toast.LENGTH_LONG).show();
        } else {
            Snackbar.make(getView(), resId, duration).show();
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param resId The resource id of the string resource to use.  Can be formatted text.
     */
    protected void showToast(int resId) {
        View view = getView();
        if (view == null) {
            Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getView(), resId, Snackbar.LENGTH_SHORT).show();
        }
    }


    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param text     The text to show.  Can be formatted text.
     * @param duration duration
     */
    void showToast(CharSequence text, @SuppressWarnings("SameParameterValue") int duration) {
        View view = getView();
        if (view == null) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
        } else {
            Snackbar.make(getView(), text, duration).show();
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param text The text to show.  Can be formatted text.
     */
    protected void showToast(CharSequence text) {
        View view = getView();
        if (view == null) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Hide the toolbar
     */
    protected void hideToolbar() {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.hideToolbar();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProgressDialogInteraction) {
            mProgressDialogInteraction = (ProgressDialogInteraction) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProgressDialogInteraction = null;
    }

    /**
     * Show the progress dialog
     *
     * @param message message
     */
    void showProgressDialog(String message) {
        if (mProgressDialogInteraction != null) {
            mProgressDialogInteraction.showProgressDialog(message);
        }
    }

    /**
     * dismiss the progress dialog
     */
    void dismissProgressDialog() {
        if (mProgressDialogInteraction != null) {
            mProgressDialogInteraction.dismissProgressDialog();
        }
    }

    /**
     * Push the new fragment in the activity
     *
     * @param fragment fragment
     */
    void pushFragment(Fragment fragment, boolean animated) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (animated) {
            ft.setCustomAnimations(
                    R.anim.slide_in_from_right, 0,
                    0, R.anim.slide_out_to_right);
        }
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    /**
     * remove the current fragment
     */
    void popFragment() {
        getActivity().onBackPressed();
    }

    /**
     * pop all the fragment except the first in the stack
     */
    void popToRootFragment() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreenName();
    }

    /**
     * Track screen name
     */
    void trackScreenName() {
        setCurrentScreen(getTitle());
    }

    /**
     * Set current screen name
     *
     * @param currentScreen current screen name
     */
    void setCurrentScreen(String currentScreen) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setCurrentScreen(currentScreen, this.getClass().getName());
        } else {
            if (!TextUtils.isEmpty(currentScreen)) {
                FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, currentScreen, this.getClass().getName());
            }
        }
    }
}
