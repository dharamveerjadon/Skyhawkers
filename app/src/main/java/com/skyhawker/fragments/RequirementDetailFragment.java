package com.skyhawker.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.SkyhawkerApplication;


public class RequirementDetailFragment extends BaseFragment {

    private TextView title, description, budget, yearOfExperience, category;
    private LinearLayout lnrSkills;
    private MyJobsModel item;
    private Session session;
    private MainActivity activity;
    private SpinnerView spinnerView;

    public static RequirementDetailFragment newInstance(String title, MyJobsModel model) {
        RequirementDetailFragment fragment = new RequirementDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_requirement_detail, container, false);
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
        spinnerView = view.findViewById(R.id.spinnerView);
    }

    private void registerListener() {

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
}
