package com.skyhawker.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.activities.WebViewActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.models.Upload;
import com.skyhawker.utils.Constants;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.MySingleton;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserProfileFragment extends BaseFragment implements View.OnClickListener {
    private ImageView docResume;
    private String resumeUrl;
    private TextView logout;
    private ImageView profileImage;
    private Uri imageuri;
    private String PathHolder;
    private Context context;
    private Upload uploadProfile;
    private LinearLayout lnrSkills;
    private MainActivity activity;
    private Session session;
    private MyJobsModel model;
    private String actionType;
    private ApplyJob mApplyJob;
    private SpinnerView spinnerView;
    private TextView mTxtLinkedIn, mTxtEmailId, mTxtName, mTxtContact, mTxtExpectedSalary, mTxtPricePerHour, mTxtLocation, mTxtSkypeId, mTxtYearOfExperience, mTxtSkills, txtReject, txtSelect;

    public static UserProfileFragment newInstance(String title, MyJobsModel item, Session session) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelable("item", item);
        args.putParcelable("session", session);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title
        setToolbarTitle(getTitle());
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        findViewById(rootView);
        registerListener();
        if (getArguments() != null) {
            model = getArguments().getParcelable("item");
            session = getArguments().getParcelable("session");
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity != null && isAdded())
            getdata();
    }

    private void getdata() {
        spinnerView.setVisibility(View.VISIBLE);
        // calling add value event listener method
        // for getting the values from database.
        if (session != null) {
            SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(model.getKey()).child("applyJob").child(session.getMobileNumber()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    mApplyJob = snapshot.getValue(ApplyJob.class);
                    if (mApplyJob != null) {
                        initData(mApplyJob.getSession());
                    }

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

    private void findViewById(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        mTxtName = view.findViewById(R.id.user_name);
        mTxtEmailId = view.findViewById(R.id.txt_email);
        mTxtLinkedIn = view.findViewById(R.id.txt_linkedIn);
        mTxtContact = view.findViewById(R.id.txt_mobile_number);
        mTxtExpectedSalary = view.findViewById(R.id.txt_expected_salary);
        mTxtPricePerHour = view.findViewById(R.id.txt_price_per_hour);
        mTxtLocation = view.findViewById(R.id.txt_location);
        mTxtSkypeId = view.findViewById(R.id.txt_skype_id);
        mTxtYearOfExperience = view.findViewById(R.id.txt_year_of_experience);
        lnrSkills = view.findViewById(R.id.lnr_skill);
        txtReject = view.findViewById(R.id.txt_reject);
        txtSelect = view.findViewById(R.id.txt_select);
        docResume = view.findViewById(R.id.img_resume);
        spinnerView = view.findViewById(R.id.spinnerView);
    }

    private void registerListener() {
        docResume.setOnClickListener(this);
        txtSelect.setOnClickListener(this);
        txtReject.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_resume:
                Intent intentWeb = new Intent(getContext(), WebViewActivity.class);
                intentWeb.putExtra("resume_url", resumeUrl);
                startActivity(intentWeb);
                break;
            case R.id.txt_reject:
                sendActionDataToServer(false);
                break;

            case R.id.txt_select:
                sendActionDataToServer(true);
                break;
        }
    }

    private void initData(Session sessionDeveloper) {
        spinnerView.setVisibility(View.GONE);
        mTxtName.setText(sessionDeveloper.getUserModel().getFirstName() + " " + sessionDeveloper.getUserModel().getLastName());
        mTxtContact.setText(sessionDeveloper.getMobileNumber());
        mTxtExpectedSalary.setText("₹ " + sessionDeveloper.getUserModel().getExpectedCtc() + "L");
        mTxtPricePerHour.setText("₹ " + sessionDeveloper.getUserModel().getPricePerHour());
        mTxtLocation.setText(sessionDeveloper.getUserModel().getLocation());

        mTxtYearOfExperience.setText(sessionDeveloper.getUserModel().getYearOfExperience());
        setTags(sessionDeveloper.getUserModel().getSkills());
        if (TextUtils.isEmpty(sessionDeveloper.getUserModel().getSkypeId()))
            mTxtSkypeId.setText("nA");
        else
            mTxtSkypeId.setText(sessionDeveloper.getUserModel().getSkypeId());

        if (TextUtils.isEmpty(sessionDeveloper.getUserModel().getLinkedInId()))
            mTxtLinkedIn.setText("nA");
        else
            mTxtLinkedIn.setText(sessionDeveloper.getUserModel().getLinkedInId());


        mTxtEmailId.setText(sessionDeveloper.getEmailId());

        if (sessionDeveloper.getUserModel().getProfileImage() != null && !TextUtils.isEmpty(sessionDeveloper.getUserModel().getProfileImage().url)) {


            Glide.with(SkyhawkerApplication.sharedInstance())
                    .load(sessionDeveloper.getUserModel().getProfileImage().url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                                isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                                target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(R.drawable.ic_avatar)
                    .dontAnimate()
                    .into(profileImage);
        }
        if (sessionDeveloper.getUserModel().getUpload() != null && !TextUtils.isEmpty(sessionDeveloper.getUserModel().getUpload().url)) {
            docResume.setVisibility(View.VISIBLE);
            resumeUrl = sessionDeveloper.getUserModel().getUpload().url;
        } else {
            docResume.setVisibility(View.GONE);
        }

    }

    private void setTags(String skills) {
        String[] strSkills = skills.split(",");
        for (String value : strSkills) {
            addTextSkills(value);
        }
    }

    private void addTextSkills(String value) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.skills_item, null);
        TextView skills = view.findViewById(R.id.txt_name);
        skills.setText(value.trim());
        lnrSkills.addView(skills);
    }

    private void sendActionDataToServer(boolean action) {
        spinnerView.setVisibility(View.VISIBLE);
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(model.getKey()).child("applyJob").child(session.getMobileNumber()).child("developerSelected").setValue(action);
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(model.getKey()).child("applyJob").child(session.getMobileNumber()).child("isShowHighlight").setValue(1);
        sendNotificationOnAction(action);

    }

    private void sendNotificationOnAction(boolean action) {
        String topic = session.getUserToken(); //topic must match with what the receiver subscribed to
        String title = "";
        String message = "";
        if (action) {
            title = "Congratulations " + session.getUserModel().getFirstName();
            message = "Your profile just got selected for the interview process";
        } else {
            title = "Sorry " + session.getUserModel().getFirstName();
            message = "Your profile just got rejected to a client Requirement";
        }


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", message);
            notifcationBody.put(Keys.TYPE, Keys.TYPE_PROFILE_SELECTED);

            notification.put("to", topic);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                response -> {
                    spinnerView.setVisibility(View.GONE);
                    activity.onBackPressed();
                    Utils.showToast(activity, activity.findViewById(R.id.fragment_container), "Notification sent");
                },
                error -> {
                    Toast.makeText(activity, "Request error", Toast.LENGTH_LONG).show();
                    spinnerView.setVisibility(View.GONE);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

}
