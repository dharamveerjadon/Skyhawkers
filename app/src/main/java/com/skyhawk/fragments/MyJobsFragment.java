package com.skyhawk.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawk.R;
import com.skyhawk.activities.MainActivity;
import com.skyhawk.adapters.MyJobAdapter;
import com.skyhawk.customview.SpinnerView;
import com.skyhawk.models.ApplyJob;
import com.skyhawk.models.MyJobsModel;
import com.skyhawk.models.Session;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.Constants;
import com.skyhawk.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class MyJobsFragment extends BaseFragment implements MyJobAdapter.OnItemClickListener {

    private MyJobAdapter mAdapter;
    private ListView listRequirement;
    private ImageView noRecordFound;
    private MainActivity activity;
    private Session session;
    private SpinnerView spinnerView;


    public static MyJobsFragment newInstance(String title) {
        MyJobsFragment fragment = new MyJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title
        setToolbarTitle(getTitle());
        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);
        session = AppPreferences.getSession();
        viewById(view);

        if (mAdapter == null) {
            mAdapter = new MyJobAdapter(activity, this);
        }

        listRequirement.setAdapter(mAdapter);

        getdata(true, Constants.SHOW_ALL);

        return view;
    }

    private void viewById(View view) {
        listRequirement = view.findViewById(R.id.listView);
        spinnerView = view.findViewById(R.id.spinnerView);
        noRecordFound = view.findViewById(R.id.no_record_found);

        FloatingActionButton addJob = view.findViewById(R.id.fab_add);

        if (session.isAdmin())
            addJob.setVisibility(View.VISIBLE);
        else
            addJob.setVisibility(GONE);


        addJob.setOnClickListener(v -> pushFragment(AddJobFragment.newInstance("Add Job"), true));
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
                        String key = ds.child("key").getValue(String.class);
                        ApplyJob applyJob = ds.child("applyJob/" + session.getMobileNumber()).getValue(ApplyJob.class);

                        if (applyJob != null && !TextUtils.isEmpty(applyJob.getSession().getMobileNumber())) {
                            if (session.getMobileNumber().equalsIgnoreCase(applyJob.getSession().getMobileNumber()))
                                if (isFilter) {
                                    if (filterKey.equalsIgnoreCase("all")) {
                                        if (applyJob.isDeveloperSelected())
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType(),key, applyJob));

                                    } else if (filterKey.equalsIgnoreCase(applyJob.getActionType())) {
                                        if (applyJob.isDeveloperSelected())
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType(), key, applyJob));
                                    }
                                }
                        }
                    }
                    if (myJob.size() > 0) {
                        listRequirement.setVisibility(View.VISIBLE);
                        noRecordFound.setVisibility(GONE);
                        Collections.sort(myJob);
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
                    Toast.makeText(activity, "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onItemClick(MyJobsModel item) {
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(item.getKey()).child("applyJob").child(session.getMobileNumber()).child("isShowHighlight").setValue(0);
        pushFragment(RequirementDetailFragment.newInstance("Client Requirement", item), true);
    }

    @Override
    public void callSupport() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + "8447878187"));
        startActivity(callIntent);
    }
}
