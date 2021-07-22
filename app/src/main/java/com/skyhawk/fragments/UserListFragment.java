package com.skyhawk.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawk.R;
import com.skyhawk.activities.MainActivity;
import com.skyhawk.adapters.UserListAdapter;
import com.skyhawk.customview.SpinnerView;
import com.skyhawk.models.ApplyJob;
import com.skyhawk.models.MyJobsModel;
import com.skyhawk.models.Session;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class UserListFragment extends BaseFragment  implements UserListAdapter
        .OnItemClickListener{
    private UserListAdapter mAdapter;
    private ListView listRequirement;
    private ImageView noRecordFound;
    private MyJobsModel model;
    private MainActivity activity;
    private SpinnerView spinnerView;

    public static UserListFragment newInstance(String title, MyJobsModel item) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelable("item", item);
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

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_list, container, false);
        viewById(view);

        if(getArguments() != null) {
            model = getArguments().getParcelable("item");

        }

        if (mAdapter == null) {
            mAdapter = new UserListAdapter(activity, this);
        }

        listRequirement.setAdapter(mAdapter);

        getdata();
        return view;
    }

    private void viewById(View view) {
        listRequirement = view.findViewById(R.id.listView);
        spinnerView = view.findViewById(R.id.spinnerView);
        noRecordFound = view.findViewById(R.id.no_record_found);
    }

    private void getdata() {
        spinnerView.setVisibility(View.VISIBLE);
        // calling add value event listener method
        // for getting the values from database.
        Session session = AppPreferences.getSession();

        if (session != null) {
            SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(model.getKey()).child("applyJob").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<ApplyJob> requirementModels = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String actionType = ds.child("actionType").getValue(String.class);
                        String noticePeriod = ds.child("noticePeriod").getValue(String.class);
                        String expectedCTC = ds.child("expectedCTC").getValue(String.class);
                        Session session1 =  ds.child("session").getValue(Session.class);
                        if(session1 != null && "Accepted".equalsIgnoreCase(actionType))
                        requirementModels.add(new ApplyJob(session1, noticePeriod, expectedCTC));
                    }
                    if(requirementModels.size() > 0) {
                        listRequirement.setVisibility(View.VISIBLE);
                        noRecordFound.setVisibility(GONE);
                        mAdapter.setItems(requirementModels, 10);
                    }else {
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
    public void onItemClick(ApplyJob item) {

        pushFragment(UserProfileFragment.newInstance("Developer Profile", model, item.getSession()), true);
    }

}
