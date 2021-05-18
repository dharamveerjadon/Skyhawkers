package com.skyhawker.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.SkyhawkerApplication;


public class CongratulationFragment extends BaseFragment implements View.OnClickListener {

    private TextView title, description, budget, yearOfExperience, category;
    private LinearLayout lnrSkills;
    private MyJobsModel item;
    private ImageView mAccept, mDecline, mSavedForLater;
    private SpinnerView spinnerView;
    private Session session;
    private MainActivity activity;

    public static CongratulationFragment newInstance(String title, MyJobsModel model) {
        CongratulationFragment fragment = new CongratulationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putParcelable("item", model);
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
        View view = inflater.inflate(R.layout.fragment_congratulation, container, false);
        session = AppPreferences.getSession();
        findViewById(view);

        registerListener();

        if (getArguments() != null) {
            item = getArguments().getParcelable("item");
        }

        setData(item);
        return view;
    }

    private void findViewById(View view) {
        spinnerView = view.findViewById(R.id.progress_bar);
        title = view.findViewById(R.id.txt_job_name);
        description = view.findViewById(R.id.txt_job_description);
        budget = view.findViewById(R.id.txt_budgets);
        yearOfExperience = view.findViewById(R.id.txt_year_of_experience);
        lnrSkills = view.findViewById(R.id.lnr_skill);
        category = view.findViewById(R.id.txt_job_category);
        mAccept = view.findViewById(R.id.accept);
        mDecline = view.findViewById(R.id.decline);
        mSavedForLater = view.findViewById(R.id.saved_later);
        spinnerView.setVisibility(View.VISIBLE);
    }

    private void registerListener() {
        mAccept.setOnClickListener(this);
        mDecline.setOnClickListener(this);
        mSavedForLater.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                sendDataToFirebase("Accept");
                break;
            case R.id.decline:
                sendDataToFirebase("Decline");
                break;
            case R.id.saved_later:
                sendDataToFirebase("Saved for later");
                break;
        }
    }

    private void setData(MyJobsModel item) {

        title.setText(item.getTitle());
        description.setText(item.getDescription());
        budget.setText(item.getBudgets());
        yearOfExperience.setText(item.getYearOfExperience());
        category.setText(item.getJobType());
        setSkills(item.getSkills());
        spinnerView.setVisibility(View.GONE);
    }

    private void setSkills(String skills) {
        String[] strSkills = skills.split(",");
        for (String value : strSkills) {
            addTextSkills(value);
        }
    }

    private void addTextSkills(String value){
        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.skills_item, null);
        TextView skills = view.findViewById(R.id.txt_name);
        skills.setText(value.trim());
        lnrSkills.addView(skills);
    }

    private void sendDataToFirebase(String action) {
        spinnerView.setVisibility(View.VISIBLE);
        ApplyJob applyJob = new ApplyJob(action, session);
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(item.getKey()).child("status").child(session.getMobileNumber()).setValue(applyJob)
                .addOnSuccessListener(aVoid -> {
                    spinnerView.setVisibility(View.GONE);
                    AppPreferences.setSession(session);
                    getActivity().onBackPressed();
                })
                .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
    }
}
