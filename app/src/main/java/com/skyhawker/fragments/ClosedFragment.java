package com.skyhawker.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.adapters.ClosedAdapter;
import com.skyhawker.adapters.WatingAdapter;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.MyJobsModel;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Constants;
import com.skyhawker.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class ClosedFragment extends BaseFragment implements ClosedAdapter.OnItemClickListener {

    private ClosedAdapter mAdapter;
    private ListView listRequirement;
    private ImageView noRecordFound;
    private MainActivity activity;
    private SpinnerView spinnerView;
    private String checkedValue = Constants.SHOW_ALL;

    public static ClosedFragment newInstance(String title) {
        ClosedFragment fragment = new ClosedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
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
        View view = inflater.inflate(R.layout.fragment_my_closed, container, false);
        viewById(view);

        if (mAdapter == null) {
            mAdapter = new ClosedAdapter(activity, this);
        }

        listRequirement.setAdapter(mAdapter);

        getdata(true, Constants.SHOW_ALL);

        return view;
    }

    private void viewById(View view) {
        listRequirement = view.findViewById(R.id.listView);
        spinnerView = view.findViewById(R.id.spinnerView);
        noRecordFound = view.findViewById(R.id.no_record_found);

        view.findViewById(R.id.fab).setOnClickListener(v -> {
            showDialog(checkedValue);
        });
    }


    private void getdata(final boolean isFilter, final String filterKey) {
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
                        ApplyJob applyJob = ds.child("applyJob/" + session.getMobileNumber()).getValue(ApplyJob.class);
                        if (applyJob != null)
                            if (!TextUtils.isEmpty(applyJob.getSession().getMobileNumber())) {
                                if (session.getMobileNumber().equalsIgnoreCase(applyJob.getSession().getMobileNumber()))
                                    if (isFilter) {
                                        if (filterKey.equalsIgnoreCase("all")) {
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType(), key, applyJob));

                                        } else if (filterKey.equalsIgnoreCase(applyJob.getActionType())) {
                                            myJob.add(new MyJobsModel(title, description, date, jobType, yearOfExperience, skills, budgets, applyJob.getActionType(), key, applyJob));
                                        }
                                    }
                            }

                    }
                    if (myJob.size() > 0) {
                        listRequirement.setVisibility(View.VISIBLE);
                        noRecordFound.setVisibility(GONE);
                        Collections.sort(myJob);
                        mAdapter.setItems(myJob, 10);
                    } else {
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
    public void onItemClick(MyJobsModel item) {

    }

    @Override
    public void onDeveloperAppliedUser(MyJobsModel item) {
        pushFragment(TimeLineDeveloperListFragment.newInstance("Applied Developer List", item), true);
    }

    public void showDialog(String Value) {

        final Dialog d = new Dialog(activity, R.style.DialogSlideAnim);
        Window window = d.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setGravity(Gravity.BOTTOM);

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_job_order);

        final CheckBox checkBoxAll = d.findViewById(R.id.chk_all);
        final CheckBox checkBoxAccepted = d.findViewById(R.id.chk_accepted);
        final CheckBox checkBoxDeclined = d.findViewById(R.id.chk_declined);
        final CheckBox checkBoxSaved = d.findViewById(R.id.chk_saved_for_later);
        final TextView clear = d.findViewById(R.id.txt_clear);
        final TextView cancel = d.findViewById(R.id.txt_cancel);
        final TextView apply = d.findViewById(R.id.txt_apply);

        clear.setOnClickListener(v -> {
            checkBoxAll.setChecked(true);
            checkBoxAccepted.setChecked(false);
            checkBoxDeclined.setChecked(false);
            checkBoxSaved.setChecked(false);
            checkedValue = Constants.SHOW_ALL;
        });

        cancel.setOnClickListener(v -> {
            d.dismiss();
        });

        apply.setOnClickListener(v -> {
            d.dismiss();
            getdata(true, checkedValue);
        });

        if (Constants.SHOW_ALL.equalsIgnoreCase(Value))
            checkBoxAll.setChecked(true);
        else if (Constants.ACCEPT.equalsIgnoreCase(Value))
            checkBoxAccepted.setChecked(true);
        else if (Constants.DECLINE.equalsIgnoreCase(Value))
            checkBoxDeclined.setChecked(true);
        else if (Constants.SAVED_FOR_LATER.equalsIgnoreCase(Value))
            checkBoxSaved.setChecked(true);

        checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxAccepted.setChecked(false);
                checkBoxDeclined.setChecked(false);
                checkBoxSaved.setChecked(false);
                handlecheckBoxState(R.id.chk_all);
            }
        });

        checkBoxAccepted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxSaved.setChecked(false);
                checkBoxDeclined.setChecked(false);
                checkBoxAll.setChecked(false);
                handlecheckBoxState(R.id.chk_accepted);
            }
        });

        checkBoxDeclined.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxAccepted.setChecked(false);
                checkBoxSaved.setChecked(false);
                checkBoxAll.setChecked(false);
                handlecheckBoxState(R.id.chk_declined);
            }
        });

        checkBoxSaved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxAll.setChecked(false);
                checkBoxAccepted.setChecked(false);
                checkBoxDeclined.setChecked(false);
                handlecheckBoxState(R.id.chk_saved_for_later);
            }
        });


        d.show();
        d.setCanceledOnTouchOutside(true);
    }

    private void handlecheckBoxState(int checkboxId) {
        switch (checkboxId) {
            case R.id.chk_all:
                checkedValue = Constants.SHOW_ALL;
                break;
            case R.id.chk_accepted:
                checkedValue = Constants.ACCEPT;
                break;
            case R.id.chk_declined:
                checkedValue = Constants.DECLINE;
                break;
            case R.id.chk_saved_for_later:
                checkedValue = Constants.SAVED_FOR_LATER;

                break;
        }
    }
}
