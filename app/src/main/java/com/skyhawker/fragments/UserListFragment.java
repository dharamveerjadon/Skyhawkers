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

    private String requirementTitle;
    private String requirementDescription;

    public static UserListFragment newInstance(String title,String arg1, String arg2) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(Keys.TITLE, arg1);
        args.putString(Keys.DESCRIPTION, arg2);
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
            requirementTitle = getArguments().getString(Keys.TITLE);
            requirementDescription = getArguments().getString(Keys.DESCRIPTION);
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
            SkyhawkerApplication.sharedDatabaseInstance().child("Developers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Session> requirementModels = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String mobileNumber = ds.child("mobileNumber").getValue(String.class);
                        String userToken = ds.child("userToken").getValue(String.class);
                        UserModel userModel =  ds.child("userModel").getValue(UserModel.class);
                        if(userModel != null)
                        requirementModels.add(new Session(mobileNumber,userModel, userToken));
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

        spinnerView.setVisibility(View.VISIBLE);
        String topic = item.getUserToken(); //topic must match with what the receiver subscribed to
        String title = "Congratulations "+item.getUserModel().getFirstName();
        String message = "Your profile just got matched to a client Requirement \n Time to get Work";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notifcationBody.put("requirement_title", requirementTitle);
            notifcationBody.put("requirement_description", requirementDescription);
            notifcationBody.put(Keys.TYPE, Keys.TYPE_DETAIL);

            notification.put("to", topic);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spinnerView.setVisibility(View.GONE);
                        Utils.showToast(getActivity(), getActivity().findViewById(R.id.fragment_container), "Notification sent");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Request error", Toast.LENGTH_LONG).show();
                        spinnerView.setVisibility(View.GONE);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}
