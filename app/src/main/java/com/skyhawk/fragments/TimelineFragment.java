package com.skyhawk.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.skyhawk.R;
import com.skyhawk.models.Session;
import com.skyhawk.utils.AppPreferences;

import org.jetbrains.annotations.NotNull;

public class TimelineFragment extends BaseFragment {
    private int mSelectedSubIndex = 0;
    private ViewPager mViewPager;
    private Session session;



    public static TimelineFragment newInstance(String title) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        // Set title
        setToolbarTitle(getTitle());
        View view =  inflater.inflate(R.layout.fragment_timeline, container, false);
        viewById(view);

/*
        if (mSelectedSubIndex > 0) {
            mSelectedSubIndex = Math.min(mSelectedSubIndex, mSubMenuItems.size() - 1);
            mViewPager.setCurrentItem(mSelectedSubIndex);
        }*/

        /*getdata();*/
        return view;
    }


    private void viewById(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(new Pager(getChildFragmentManager()));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(mViewPager);


    }

    /**
     * get selected item in view pager
     *
     * @return
     */
    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    /**
     * Set current item in view pager
     *
     * @param item index
     */
    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item);
    }

    private class Pager extends FragmentStatePagerAdapter {

        private Pager(FragmentManager fm) {
            super(fm);
        }

        //Overriding method getItem
        @NotNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position ==  0)
                fragment = WaitingFragment.newInstance("Waiting");

            if(position == 1)
                fragment = ClosedFragment.newInstance("Closed");

            if(position == 2)
                fragment = AllRequirementFragment.newInstance("All Requirement");


       return fragment;
        }

        //Overridden method getCount to get the number of tabs
        @Override
        public int getCount() {
            session = AppPreferences.getSession();
            return session.isAdmin() ? 3 : 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
                title = "WAITING";

             if (position == 1)
                title = "CLOSED";

            if (position == 2)
                title = "ALL";

            return title;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppPreferences.IsCongratulationDone()){
            AppPreferences.setIsCongratulationDone(AppPreferences.IsCONGRATULATIONACTIONDONE, false);
            mViewPager.postDelayed(() -> mViewPager.setCurrentItem(1), 100);
        }

    }
}
