package com.skyhawker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.adapters.AllRequirementAdapter;
import com.skyhawker.adapters.WatingAdapter;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class AllRequirementFragment extends BaseFragment implements AllRequirementAdapter.OnItemClickListener {

    private AllRequirementAdapter mAdapter;
    private ListView listRequirement;
    private SpinnerView spinnerView;
    private ImageView noRecordFound;


    public static AllRequirementFragment newInstance(String title) {
        AllRequirementFragment fragment = new AllRequirementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_waiting, container, false);

        viewById(view);

        if (mAdapter == null) {
            mAdapter = new AllRequirementAdapter(this.getContext(), this);
        }

        listRequirement.setAdapter(mAdapter);

        getdata();

        return view;
    }

    private void viewById(View view) {
        listRequirement = view.findViewById(R.id.listView);
        spinnerView = view.findViewById(R.id.progress_bar);
        noRecordFound = view.findViewById(R.id.no_record_found);


    }


    private void getdata() {
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
                        myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, "InProcess",key));

                        if (myJob.size() > 0) {
                            listRequirement.setVisibility(View.VISIBLE);
                            noRecordFound.setVisibility(GONE);
                            mAdapter.setItems(myJob, 10);
                        } else {
                            listRequirement.setVisibility(GONE);
                            noRecordFound.setVisibility(View.VISIBLE);
                        }


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
        pushFragment(UserListFragment.newInstance("Users", item), true);
    }
}
