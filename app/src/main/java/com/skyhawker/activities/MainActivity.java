package com.skyhawker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.customview.TabBar;
import com.skyhawker.fragments.CongratulationFragment;
import com.skyhawker.fragments.MyJobsFragment;
import com.skyhawker.fragments.MyProfileFragment;
import com.skyhawker.fragments.TimelineFragment;
import com.skyhawker.interfaces.MenuItemInteraction;
import com.skyhawker.models.MenuItem;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.skyhawker.utils.AppPreferences.SELECTED_HOME_SCREEN;

public class MainActivity extends BaseActivity implements MenuItemInteraction {
    //toolbar
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    private boolean isFirstEntry;
    private String mobileNumber;
    private ActionBar actionBar;
    private LinearLayout lnrEditIconProfile;
    //Reference to drawer layout to open or hide the menu
    private DrawerLayout mDrawerLayout;

    // This is drawer listener to close and open the drawer
    private ActionBarDrawerToggle mDrawerToggle;
    //bottom tab bar
    private TabBar mTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set Tool bar to the screen
        mToolbar = findViewById(R.id.toolbar);
        toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);
        lnrEditIconProfile = mToolbar.findViewById(R.id.lnr_edit);
        toolbarTitle.setText(mToolbar.getTitle());
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Session session = AppPreferences.getSession();

        if (getIntent() != null) {
            mobileNumber = getIntent().getStringExtra(Keys.MOBILE_NUMBER);
            isFirstEntry = getIntent().getBooleanExtra("isFirst", false);
        }


        mobileNumber = TextUtils.isEmpty(session.getMobileNumber()) ? mobileNumber : session.getMobileNumber();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                /* host Activity */
                this,
                /* DrawerLayout object */
                mDrawerLayout,
                0, 0) {
            public void onDrawerClosed(View view) {
                syncActionBarArrowState();
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mTabBar = findViewById(R.id.tab_bar);
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem(R.string.string_timeline, R.drawable.ic_timeline_grey));
        items.add(new MenuItem(R.string.string_my_job, R.drawable.ic_job_grey));
        items.add(new MenuItem(R.string.string_profile, R.drawable.ic_profile_grey));
        mTabBar.setMenuItems(items);


        mTabBar.setOnMenuClickListener(new MenuItemInteraction() {
            @Override
            public void onMenuClick(MenuItem menuItem) {
                switch (menuItem.textResId) {
                    case R.string.string_timeline:
                        addTimeLineFragment();
                        break;
                    case R.string.string_my_job:
                        addMyJobFragment();
                        break;
                    case R.string.string_profile:
                        addMyProfileFragment();
                        break;
                    default:
                        mTabBar.setSelectedIndex(0, false);
                        addTimeLineFragment();
                }
            }

            @Override
            public void onPopClick() {
                MainActivity.this.onPopClick();
            }
        });

        if (savedInstanceState == null) {
                mTabBar.setSelectedIndex(AppPreferences.getSelectedHomeScreen(), false);
        }

        new Handler().postDelayed(() -> onHandleNotification(getIntent()), 1000);


        getdata();

        lnrEditIconProfile.setOnClickListener(v -> {
            Session editSession = AppPreferences.getSession();
            AppPreferences.setSelectedHomeScreen(AppPreferences.SELECTED_HOME_SCREEN, 2);
            Intent intent = new Intent(MainActivity.this, DeveloperEntryActivity.class);
            intent.putExtra(Keys.MOBILE_NUMBER, editSession.getMobileNumber());
            startActivity(intent);
        });

    }

    @Override
    protected void syncActionBarArrowState() {
        mDrawerToggle.setDrawerIndicatorEnabled(getSupportFragmentManager().getBackStackEntryCount() == 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onHandleNotification(intent);
    }

    private void getdata() {

        DatabaseReference databaseReference = SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(mobileNumber);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Session value = snapshot.getValue(Session.class);
                AppPreferences.setSession(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Add the more fragment
     */
    private void openCongratulationScreen(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    /**
     * handle notifications
     *
     * @param intent intent
     */

    private void onHandleNotification(Intent intent) {
        if (intent.hasExtra(Keys.NOTIFICATION)) {
            if (intent.getSerializableExtra(Keys.NOTIFICATION) instanceof HashMap) {
                @SuppressWarnings("unchecked") HashMap<String, String> map = (HashMap<String, String>) intent
                        .getSerializableExtra(Keys.NOTIFICATION);
                if (map != null) {
                    String type = map.get(Keys.TYPE);
                    if (!TextUtils.isEmpty(type)) {
                        switch (type) {
                            case Keys.TYPE_ADD_JOB:
                                String jobname = map.get("job_name");
                                String description = map.get("job_description");
                                String date = map.get("job_date");
                                String budgets = map.get("job_budgets");
                                String yearofexperience = map.get("job_experience");
                                String category = map.get("job_category");
                                String skills = map.get("skills_required");
                                String key = map.get("key");


                                MyJobsModel model = new MyJobsModel(jobname, description, date, category, yearofexperience, skills, budgets, "", key);
                                if (!TextUtils.isEmpty(jobname) && !TextUtils.isEmpty(description)) {
                                    openCongratulationScreen(CongratulationFragment.newInstance("Congratulation", model));
                                }
                                break;

                            case Keys.TYPE_PROFILE_SELECTED:
                                addMyJobFragment();

                                break;

                        }
                    }
                }
            }
        }
    }

    /**
     * logout the user
     */
    private void logout() {
        //show the confirmation dialog
        Utils.showConfirmDialog(this, getString(R.string.string_logout), getString(R.string.logout_message), android.R
                .string.yes, android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawers();
        } else {
            onBackPressed();
        }
        return true;
    }

    /**
     * close drawer on screen layout
     */
    private void closeDrawers() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    /**
     * handles the back button pressed functionality
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawers();
            return;
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (mTabBar.getSelectedIndex() != 0) {
                AppPreferences.setSelectedHomeScreen(SELECTED_HOME_SCREEN, 0);
                mTabBar.setSelectedIndex(0, false);
                return;
            } else {
                Fragment fragment = getTopFragment();
                if (fragment != null && fragment instanceof MyJobsFragment) {
                    mTabBar.setSelectedIndex(1, false);
                } else if (fragment != null && fragment instanceof MyProfileFragment) {
                    mTabBar.setSelectedIndex(2, false);
                } else {
                    mTabBar.setSelectedIndex(0, false);
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onMenuClick(MenuItem item) {
//close the drawers
        closeDrawers();

        switch (item.textResId) {
            case R.string.string_timeline:
                mTabBar.setSelectedIndex(0, false);
                addTimeLineFragment();
                break;

            case R.string.string_my_job:
                mTabBar.setSelectedIndex(1, false);
                addMyJobFragment();
                break;

            case R.string.string_logout:
                redirectToLoginActivity(this);
                break;
        }

    }

    private void redirectToLoginActivity(Context context) {
        AppPreferences.logout();
        context.startActivity(new Intent(context, SignUpActivity.class));
        ((Activity) context).finish();
    }

    /**
     * Add the timeline fragment
     */
    private void addTimeLineFragment() {
        lnrEditIconProfile.setVisibility(View.INVISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        TimelineFragment fragment = TimelineFragment.newInstance(getString(R.string.string_timeline));
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Add the job fragment
     */
    private void addMyJobFragment() {
        lnrEditIconProfile.setVisibility(View.INVISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MyJobsFragment fragment = MyJobsFragment.newInstance(getString(R.string.string_my_job));
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Add the profile fragment
     */
    private void addMyProfileFragment() {
        lnrEditIconProfile.setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MyProfileFragment fragment = MyProfileFragment.newInstance(getString(R.string.string_profile));
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPopClick() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbarTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int index = AppPreferences.getSelectedHomeScreen();
        if (index >= 0 && index < 3) {
            MenuItem menuItem = mTabBar.getMenuItem(index);
            setCurrentScreen(menuItem.name, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setSelectedHomeScreen(SELECTED_HOME_SCREEN, 0);
    }
}
