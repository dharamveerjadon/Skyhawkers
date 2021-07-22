package com.skyhawk.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skyhawk.R;
import com.skyhawk.activities.MainActivity;
import com.skyhawk.customview.SpinnerView;
import com.skyhawk.customview.Tag;
import com.skyhawk.customview.TagView;
import com.skyhawk.models.MyJobsModel;
import com.skyhawk.models.NotificationModel;
import com.skyhawk.models.Session;
import com.skyhawk.models.UserModel;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.Constants;
import com.skyhawk.utils.Keys;
import com.skyhawk.utils.MySingleton;
import com.skyhawk.utils.SkyhawkerApplication;
import com.skyhawk.utils.Utils;

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
        activity = (MainActivity) context;
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

        final DatabaseReference databaseReference = SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs");
        final String key = databaseReference.push().getKey();

        MyJobsModel model = new MyJobsModel(title, description, dateTime, jobType, yearOfExperience, skills, budget, "", key);
        showPostingViewDialog(databaseReference, key, model);
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
                        String title = "Skyhawk has posted a new job";
                        String message = "Please check and apply if matches your profile";


                        notificationModels.add(new NotificationModel(title, message, name, description, budget, yearofexperience, category, skills, date, Keys.TYPE_DETAIL, topic, key));
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
                    if (innerModel.size() == 0) {
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


    private void showPostingViewDialog(DatabaseReference databaseReference, String key, MyJobsModel model) {

        final Dialog dialog = new Dialog(activity, R.style.FullScreenDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_posting_view);
        dialog.setCancelable(false);
        final TextView title = dialog.findViewById(R.id.txt_title);
        final TextView txtDate = dialog.findViewById(R.id.txt_date);
        final TextView jobType = dialog.findViewById(R.id.txt_job_type);
        final TagView tagGroup = dialog.findViewById(R.id.tag_group);
        final TextView TxtYearOfExperience = dialog.findViewById(R.id.txt_year_of_experience);
        final TextView mBudget = dialog.findViewById(R.id.txt_tentative_budget);
        final TextView mTxtStatus = dialog.findViewById(R.id.txt_status);

        title.setText(model.getTitle());
        txtDate.setText(model.getDate());
        jobType.setText(model.getJobType());
        setTags(getActivity(), tagGroup, model.getSkills());
        TxtYearOfExperience.setText(model.getYearOfExperience() + "+ Yrs experience");
        mBudget.setText("₹ " + model.getBudgets());
        mTxtStatus.setText("In Progress");

        final TextView txtNo = dialog.findViewById(R.id.txt_no);
        final TextView txtYes = dialog.findViewById(R.id.txt_yes);

        txtYes.setOnClickListener(view -> {
            spinnerView.setVisibility(View.VISIBLE);
            databaseReference.child(key).setValue(model)
                    .addOnSuccessListener(aVoid -> new Handler().postDelayed(() ->
                            pushNotification(model.getTitle(), model.getDescription(), model.getDate(), model.getYearOfExperience(), model.getBudgets(), model.getJobType(), model.getSkills(), key), 3000))
                    .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
            dialog.dismiss();
        });

        txtNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void setTags(Context context, TagView tagGroup, String skills) {
        List<Tag> tagList = new ArrayList<>();

        String[] strSkills = skills.split(",");
        for (String value : strSkills) {
            Tag tag;
            tag = new Tag(context, value);
            tag.radius = 10f;
            tag.layoutColor = tag.layoutBorderColor;
            tag.isDeletable = false;
            tagList.add(tag);
        }
        tagGroup.addTags(tagList);
    }
}
