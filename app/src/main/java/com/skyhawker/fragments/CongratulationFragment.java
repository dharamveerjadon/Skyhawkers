package com.skyhawker.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
    private Session session;
    private MainActivity activity;
    private SpinnerView spinnerView;

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
        title = view.findViewById(R.id.txt_job_name);
        description = view.findViewById(R.id.txt_job_description);
        budget = view.findViewById(R.id.txt_budgets);
        yearOfExperience = view.findViewById(R.id.txt_year_of_experience);
        lnrSkills = view.findViewById(R.id.lnr_skill);
        category = view.findViewById(R.id.txt_job_category);
        mAccept = view.findViewById(R.id.accept);
        mDecline = view.findViewById(R.id.decline);
        mSavedForLater = view.findViewById(R.id.saved_later);
        spinnerView = view.findViewById(R.id.spinnerView);
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
                showActionDialog("Accepted");
                break;
            case R.id.decline:
                showActionDialog("Declined");
                break;
            case R.id.saved_later:
                showActionDialog("Saved for later");
                break;
        }
    }

    private void setData(MyJobsModel item) {

        title.setText(item.getTitle());
        description.setText(item.getDescription());
        budget.setText("₹ "+item.getBudgets());
        yearOfExperience.setText(item.getYearOfExperience()+ " Yrs");
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
        ApplyJob applyJob = new ApplyJob(action, session, 0);
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(item.getKey()).child("applyJob").child(session.getMobileNumber()).setValue(applyJob)
                .addOnSuccessListener(aVoid -> {
                    spinnerView.setVisibility(View.GONE);
                    AppPreferences.setIsCongratulationDone(AppPreferences.IsCONGRATULATIONACTIONDONE, true);
                    activity.onBackPressed();
                })
                .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
    }

    private void showActionDialog(String action) {

        final Dialog dialog = new Dialog(activity, R.style.Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_confirm);
        dialog.setCancelable(false);
        final TextView txtNo = dialog.findViewById(R.id.txt_no);
        final TextView txtYes = dialog.findViewById(R.id.txt_yes);

        txtYes.setOnClickListener(view -> {
            sendDataToFirebase(action);
            dialog.dismiss();
        });

        txtNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }
}
