package com.skyhawker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.NotificationModel;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddJobFragment extends BaseFragment {
    //ref to the email view
    private EditText mTitleView;
    //ref to the password view
    private EditText mDescriptionView;
    private Spinner mJobTypeView;
    private EditText mdateView;
    private EditText mYearOfExperienceView;
    private EditText mBudgetsView;
    private EditText mSkillsView;
    private Session session;
    private String mJobType;
    private int count;
    private SpinnerView spinnerView;

    private MainActivity activity;

    public static AddJobFragment newInstance(String title) {
        AddJobFragment fragment = new AddJobFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_add_job, container, false);
        session = AppPreferences.getSession();
        mTitleView = view.findViewById(R.id.edt_job_name);
        mDescriptionView = view.findViewById(R.id.edt_description);
        mJobTypeView = view.findViewById(R.id.spinner);
        mdateView = view.findViewById(R.id.edt_date);
        mYearOfExperienceView = view.findViewById(R.id.edt_year_of_experience);
        mBudgetsView = view.findViewById(R.id.edt_approx_budgets);
        mSkillsView = view.findViewById(R.id.edt_skills);
        spinnerView = view.findViewById(R.id.spinnerView);
        final View loginBtn = view.findViewById(R.id.btn_save);
        loginBtn.setOnClickListener(v -> loginClick());


        final List<String> jobTypeList = new ArrayList<>();
        jobTypeList.add("Part Time");
        jobTypeList.add("Full Time");

        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter(activity,
                R.layout.sector_spinner, jobTypeList);
        jobTypeAdapter.setDropDownViewResource(R.layout.sector_spinner_item);
        mJobTypeView.setAdapter(jobTypeAdapter);
        mJobTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mJobType = jobTypeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSkillsView.setOnFocusChangeListener((v, hasFocus) -> loginBtn.setVisibility(View.VISIBLE));

        mSkillsView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginClick();
                return true;
            }
            return false;
        });

        final String date = getDateTime();
        mdateView.setText(date);

        return view;
    }

    /**
     * On login button click in Login Fragment
     */
    private void loginClick() {

        //validate field
        final String title = mTitleView.getText().toString().trim();
        final String description = mDescriptionView.getText().toString().trim();

        final String jobType = mJobType;
        final String dateTime = mdateView.getText().toString().trim();
        final String yearOfExperience = mYearOfExperienceView.getText().toString().trim();
        final String budget = mBudgetsView.getText().toString().trim();
        final String skills = mSkillsView.getText().toString().trim();

        mTitleView.setError(null);
        mDescriptionView.setError(null);

        if (TextUtils.isEmpty(title)) {
            mTitleView.setError(getString(R.string.error_validation_field_required));
            mTitleView.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_validation_field_required));
            mDescriptionView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(jobType)) {
            Utils.showToast(activity, activity.findViewById(R.id.fragment_container), "Select job type");
            mJobTypeView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dateTime)) {
            mdateView.setError(getString(R.string.error_validation_field_required));
            mdateView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(yearOfExperience)) {
            mYearOfExperienceView.setError(getString(R.string.error_validation_field_required));
            mYearOfExperienceView.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(budget)) {
            mBudgetsView.setError(getString(R.string.error_validation_field_required));
            mBudgetsView.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(skills)) {
            mSkillsView.setError(getString(R.string.error_validation_field_required));
            mSkillsView.requestFocus();
            return;
        }
       spinnerView.setVisibility(View.VISIBLE);
        final DatabaseReference databaseReference = SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs");
        final String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(new MyJobsModel(title, description, dateTime, jobType, yearOfExperience, skills, budget,"", key))
                .addOnSuccessListener(aVoid -> new Handler().postDelayed(() ->
                        pushNotification(title, description, dateTime, yearOfExperience, budget, jobType, skills, key), 3000))
                .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
    }

    private void pushNotification(final String name, final String description, final String date, final String yearofexperience, final String budget, final String category, final String skills, final String key) {
        SkyhawkerApplication.sharedDatabaseInstance().child("Developers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<NotificationModel> notificationModels = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String mobileNumber = ds.child("mobileNumber").getValue(String.class);
                    String userToken = ds.child("userToken").getValue(String.class);
                    UserModel userModel = ds.child("userModel").getValue(UserModel.class);
                    if (userModel != null) {
                        String topic = userToken; //topic must match with what the receiver subscribed to
                        String title = "Congratulations " + userModel.getFirstName();
                        String message = "Your profile just got matched to a client Requirement \n Time to get Work";


                        notificationModels.add(new NotificationModel(title, message, name, description, budget, yearofexperience, category, skills, date,Keys.TYPE_DETAIL, topic, key));
                    }
                }
                sendNotification(notificationModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_EMMMMDDYYYY);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void sendNotification(List<NotificationModel> model) {
        final List<NotificationModel> innerModel = model;
        if (innerModel.size() == 0)
            return;


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", innerModel.get(0).getTitle());
            notifcationBody.put("message", innerModel.get(0).getMessage());
            notifcationBody.put("job_name", innerModel.get(0).getJobName());
            notifcationBody.put("job_description", innerModel.get(0).getJobDescription());
            notifcationBody.put("job_date", innerModel.get(0).getDate());
            notifcationBody.put("job_budgets", innerModel.get(0).getBudget());
            notifcationBody.put("job_experience", innerModel.get(0).getYearOfExperience());
            notifcationBody.put("job_category", innerModel.get(0).getCategory());
            notifcationBody.put("skills_required", innerModel.get(0).getSkills());
            notifcationBody.put("key", innerModel.get(0).getKey());
            notifcationBody.put(Keys.TYPE, Keys.TYPE_ADD_JOB);

            notification.put("to", innerModel.get(0).getTo());
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(Constants.TAG, "onCreate: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification,
                response -> {
                    innerModel.remove(0);
                    sendNotification(innerModel);
                    if(innerModel.size() == 0) {
                        spinnerView.setVisibility(View.GONE);
                        Utils.showToast(activity, activity.findViewById(R.id.fragment_container), "Notification sent");
                        activity.onBackPressed();
                    }
                },
                error -> {
                    Toast.makeText(activity, "Request error", Toast.LENGTH_LONG).show();
                    spinnerView.setVisibility(View.GONE);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", Constants.serverKey);
                params.put("Content-Type", Constants.contentType);
                return params;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }
}
