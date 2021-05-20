package com.skyhawker.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.adapters.UserListAdapter;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Constants;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.MySingleton;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class UserListFragment extends BaseFragment  implements UserListAdapter
        .OnItemClickListener{
    private UserListAdapter mAdapter;
    private ListView listRequirement;
    private SpinnerView spinnerView;
    private ImageView noRecordFound;
    private MyJobsModel model;

    public static UserListFragment newInstance(String title, MyJobsModel item) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelable("item", item);
        fragment.setArguments(args);
        return fragment;
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
            mAdapter = new UserListAdapter(this.getContext(), this);
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
        Session session = AppPreferences.getSession();

        if (session != null) {
            SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(model.getKey()).child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Session> requirementModels = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String actionType = ds.child("actionType").getValue(String.class);
                        Session session1 =  ds.child("session").getValue(Session.class);
                        if(session1 != null && "Accept".equalsIgnoreCase(actionType))
                        requirementModels.add(session1);
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
                    Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onItemClick(Session item) {

        pushFragment(UserProfileFragment.newInstance("Developer Profile", model, item), true);
    }

}
