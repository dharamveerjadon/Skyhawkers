package com.skyhawker.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.adapters.MyJobAdapter;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Constants;
import com.skyhawker.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MyJobsFragment extends BaseFragment implements MyJobAdapter.OnItemClickListener {

    private MyJobAdapter mAdapter;
    private ListView listRequirement;
    private SpinnerView spinnerView;
    private ImageView noRecordFound;
    private Context context;
    private Session session;
    private String checkedValue = Constants.SHOW_ALL;


    public static MyJobsFragment newInstance(String title) {
        MyJobsFragment fragment = new MyJobsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);
        context = getActivity();
        session = AppPreferences.getSession();
        viewById(view);

        if (mAdapter == null) {
            mAdapter = new MyJobAdapter(this.getContext(), this);
        }

        listRequirement.setAdapter(mAdapter);

        getdata(true, Constants.SHOW_ALL);

        return view;
    }

    private void viewById(View view) {
        listRequirement = view.findViewById(R.id.listView);
        spinnerView = view.findViewById(R.id.progress_bar);
        noRecordFound = view.findViewById(R.id.no_record_found);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(checkedValue);
            }
        });

        FloatingActionButton addJob = view.findViewById(R.id.fab_add);

        if (session.isAdmin())
            addJob.setVisibility(View.VISIBLE);
        else
            addJob.setVisibility(GONE);


        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushFragment(AddJobFragment.newInstance("Add Job"), true);
            }
        });
    }


    private void getdata(final boolean isFilter, final String filterKey) {
        spinnerView.setVisibility(View.VISIBLE);
        // calling add value event listener method
        // for getting the values from database.
        final Session session = AppPreferences.getSession();

        if (session != null) {
            SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<MyJobsModel> myJob = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String title = ds.child("title").getValue(String.class);
                        String description = ds.child("description").getValue(String.class);
                        String date = ds.child("date").getValue(String.class);
                        String jobType = ds.child("jobType").getValue(String.class);
                        String skills = ds.child("skills").getValue(String.class);
                        String yearOfExperience = ds.child("yearOfExperience").getValue(String.class);
                        String budgets = ds.child("budgets").getValue(String.class);
                        ApplyJob applyJob = ds.child("status/" + session.getMobileNumber()).getValue(ApplyJob.class);

                        if (applyJob != null && !TextUtils.isEmpty(applyJob.getSession().getMobileNumber())) {
                            if (session.getMobileNumber().equalsIgnoreCase(applyJob.getSession().getMobileNumber()))
                                if (isFilter) {
                                    if (filterKey.equalsIgnoreCase("all")) {
                                        if (applyJob.isDeveloperSelected())
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType()));

                                    } else if (filterKey.equalsIgnoreCase(applyJob.getActionType())) {
                                        if (applyJob.isDeveloperSelected())
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType()));
                                    }
                                }
                        }
                    }
                    if (myJob.size() > 0) {
                        listRequirement.setVisibility(View.VISIBLE);
                        noRecordFound.setVisibility(GONE);
                        mAdapter.setItems(myJob, 10);
                    } else {
                        listRequirement.setVisibility(GONE);
                        noRecordFound.setVisibility(View.VISIBLE);
                    }
                    spinnerView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onItemClick(MyJobsModel item) {

    }

    @Override
    public void callSupport() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + "8447878187"));
        startActivity(callIntent);
    }

    private void showDialog(String checkedValue) {

        final Dialog d = new Dialog(context, R.style.DialogSlideAnim);
        Window window = d.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setGravity(Gravity.BOTTOM);

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_job_order);

        final CheckBox checkBoxAll = d.findViewById(R.id.chk_all);
        final CheckBox checkBoxAccepted = d.findViewById(R.id.chk_accepted);
        final CheckBox checkBoxDeclined = d.findViewById(R.id.chk_declined);
        final CheckBox checkBoxSaved = d.findViewById(R.id.chk_saved_for_later);


        if (Constants.SHOW_ALL.equalsIgnoreCase(checkedValue))
            checkBoxAll.setChecked(true);
        else if (Constants.ACCEPT.equalsIgnoreCase(checkedValue))
            checkBoxAccepted.setChecked(true);
        else if (Constants.DECLINE.equalsIgnoreCase(checkedValue))
            checkBoxDeclined.setChecked(true);
        else if (Constants.SAVED_FOR_LATER.equalsIgnoreCase(checkedValue))
            checkBoxSaved.setChecked(true);


        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    d.dismiss();
                    checkBoxAccepted.setChecked(false);
                    checkBoxDeclined.setChecked(false);
                    checkBoxSaved.setChecked(false);
                    handlecheckBoxState(R.id.chk_all);
                }
            }
        });

        checkBoxAccepted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    d.dismiss();
                    checkBoxSaved.setChecked(false);
                    checkBoxDeclined.setChecked(false);
                    checkBoxAll.setChecked(false);
                    handlecheckBoxState(R.id.chk_accepted);
                }
            }
        });

        checkBoxDeclined.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    d.dismiss();
                    checkBoxAccepted.setChecked(false);
                    checkBoxSaved.setChecked(false);
                    checkBoxAll.setChecked(false);
                    handlecheckBoxState(R.id.chk_declined);
                }
            }
        });

        checkBoxSaved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    d.dismiss();
                    checkBoxAll.setChecked(false);
                    checkBoxAccepted.setChecked(false);
                    checkBoxDeclined.setChecked(false);
                    handlecheckBoxState(R.id.chk_saved_for_later);
                }
            }
        });


        d.show();
        d.setCanceledOnTouchOutside(true);
    }

    private void handlecheckBoxState(int checkboxId) {
        switch (checkboxId) {
            case R.id.chk_all:
                checkedValue = Constants.SHOW_ALL;
                getdata(true, Constants.SHOW_ALL);
                break;
            case R.id.chk_accepted:
                checkedValue = Constants.ACCEPT;
                getdata(true, Constants.ACCEPT);
                break;
            case R.id.chk_declined:
                checkedValue = Constants.DECLINE;
                getdata(true, Constants.DECLINE);
                break;
            case R.id.chk_saved_for_later:
                checkedValue = Constants.SAVED_FOR_LATER;
                getdata(true, Constants.SAVED_FOR_LATER);
                break;
        }
    }
}
