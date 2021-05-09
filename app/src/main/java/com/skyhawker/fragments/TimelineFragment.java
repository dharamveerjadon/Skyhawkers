package com.skyhawker.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.skyhawker.R;
import com.skyhawker.adapters.WatingAdapter;
import com.skyhawker.models.MyJobsModel;

import org.jetbrains.annotations.NotNull;

public class TimelineFragment extends BaseFragment implements WatingAdapter
        .OnItemClickListener{
    private int mSelectedSubIndex = 0;
    private ViewPager mViewPager;



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

    @Override
    public void onItemClick(MyJobsModel item) {
        pushFragment(UserListFragment.newInstance("Developers", item.getTitle(), item.getDescription()), true);
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
            if (position ==  0) {
                fragment = WaitingFragment.newInstance("My Job");
            }

            else if(position == 1) {
                fragment = ClosedFragment.newInstance("My Job");
            }
       return fragment;
        }

        //Overridden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
            {
                title = "WAITING";
            }
            else if (position == 1)
            {
                title = "CLOSED";
            }
            return title;
        }
    }

}
