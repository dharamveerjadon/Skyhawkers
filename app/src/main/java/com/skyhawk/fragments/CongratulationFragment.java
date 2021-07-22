package com.skyhawk.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skyhawk.R;
import com.skyhawk.activities.MainActivity;
import com.skyhawk.activities.WebViewActivity;
import com.skyhawk.customview.SpinnerView;
import com.skyhawk.models.ApplyJob;
import com.skyhawk.models.MyJobsModel;
import com.skyhawk.models.Session;
import com.skyhawk.models.Upload;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.SkyhawkerApplication;
import com.skyhawk.utils.Utils;

import static android.app.Activity.RESULT_OK;


public class CongratulationFragment extends BaseFragment implements View.OnClickListener {
    private static final int DOCUMENT_CODE = 07;
    private TextView title, description, budget, yearOfExperience, category;
    private LinearLayout lnrSkills;
    private MyJobsModel item;
    private ImageView mAccept, mDecline, mSavedForLater;
    private LinearLayout mLnrAccept, mLnrSavedLater, mLnrDecline;
    private Session session;
    private MainActivity activity;
    private SpinnerView spinnerView;
    private String resumeUrl;
    private String PathHolder;
    private ProgressDialog dialog;
    private Uri imageuri;
    private Upload upload;
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

        getData();
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
        mLnrAccept = view.findViewById(R.id.lnr_accept);
        mLnrDecline = view.findViewById(R.id.lnr_decline);
        mLnrSavedLater = view.findViewById(R.id.lnr_saved_for_later);
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

    private void getData() {
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(item.getKey()).child("applyJob").child(session.getMobileNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {

                    ApplyJob applyJob = snapshot.getValue(ApplyJob.class);
                    if("Declined".equalsIgnoreCase(applyJob.getActionType()))
                        mLnrDecline.setVisibility(View.GONE);
                    else if("Saved for later".equalsIgnoreCase(applyJob.getActionType()))
                        mLnrSavedLater.setVisibility(View.GONE);
                    else if("Accepted".equalsIgnoreCase(applyJob.getActionType()))
                        mLnrAccept.setVisibility(View.GONE);

                }else {
                    mLnrAccept.setVisibility(View.VISIBLE);
                    mLnrSavedLater.setVisibility(View.VISIBLE);
                    mLnrDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setData(MyJobsModel item) {

        title.setText(item.getTitle());
        description.setText(item.getDescription());
        budget.setText("₹ "+item.getBudgets());
        yearOfExperience.setText(item.getYearOfExperience()+ "+ Yrs");
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

    private void sendDataToFirebase(String action, String noticePeriod, String expectedCtc) {
        spinnerView.setVisibility(View.VISIBLE);
        ApplyJob applyJob = new ApplyJob(action,0, session,noticePeriod, expectedCtc );
        SkyhawkerApplication.sharedDatabaseInstance().child("MyJobs").child(item.getKey()).child("applyJob").child(session.getMobileNumber()).setValue(applyJob)
                .addOnSuccessListener(aVoid -> {
                    spinnerView.setVisibility(View.GONE);
                    AppPreferences.setIsCongratulationDone(AppPreferences.IsCONGRATULATIONACTIONDONE, true);
                    if(!("Declined".equalsIgnoreCase(action) || "Saved for later".equalsIgnoreCase(action))) {
                    showFinishDialog();
                    }else {
                        activity.onBackPressed();
                    }

                })
                .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
    }

    private void showActionDialog(String action) {

        final Dialog dialog = new Dialog(activity, R.style.Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_confirm);
        dialog.setCancelable(false);
        final LinearLayout lnrNotice = dialog.findViewById(R.id.lnr_notice);
        final LinearLayout lnrExpectedCTC = dialog.findViewById(R.id.lnr_expected);
        final LinearLayout lnrResume = dialog.findViewById(R.id.lnr_resume);
        final TextView txtResumeYes = dialog.findViewById(R.id.re_upload_resume);
        final ImageView imgResume = dialog.findViewById(R.id.img_resume);
        final EditText notice = dialog.findViewById(R.id.edt_notice_period);
        final EditText expectedCTC = dialog.findViewById(R.id.edt_expected_ctc);

        resumeUrl = AppPreferences.getSession().getUserModel().getUpload().url;
        if("Declined".equalsIgnoreCase(action) || "Saved for later".equalsIgnoreCase(action)) {
            lnrNotice.setVisibility(View.GONE);
            lnrExpectedCTC.setVisibility(View.GONE);
            lnrResume.setVisibility(View.GONE);
        }
        final TextView txtNo = dialog.findViewById(R.id.txt_no);
        final TextView txtYes = dialog.findViewById(R.id.txt_yes);

        imgResume.setOnClickListener(v -> {
            Intent intentWeb = new Intent(getContext(), WebViewActivity.class);
            intentWeb.putExtra("resume_url", resumeUrl);
            startActivity(intentWeb);
        });
        txtResumeYes.setOnClickListener(v -> {
            String[] supportedMimeTypes = {"application/pdf", "application/msword"};
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(supportedMimeTypes.length == 1 ? supportedMimeTypes[0] : "*/*");
                if (supportedMimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);
                }
            } else {
                String mimeTypes = "";
                for (String mimeType : supportedMimeTypes) {
                    mimeTypes += mimeType + "|";
                }
                intent.setType(mimeTypes.substring(0, mimeTypes.length() - 1));
            }
            startActivityForResult(intent, DOCUMENT_CODE);
        });
        txtYes.setOnClickListener(view -> {
            if(!("Declined".equalsIgnoreCase(action) || "Saved for later".equalsIgnoreCase(action))) {
                if(TextUtils.isEmpty(notice.getText().toString().trim())) {
                    notice.setError(getString(R.string.error_validation_field_required));
                    return;
                }else {
                    notice.setError(null);
                }
                if(TextUtils.isEmpty(expectedCTC.getText().toString().trim())) {
                    expectedCTC.setError(getString(R.string.error_validation_field_required));
                    return;
                }else {
                    expectedCTC.setError(null);
                }
            }

            sendDataToFirebase(action, notice.getText().toString(), expectedCTC.getText().toString());
            dialog.dismiss();
        });

        txtNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void showFinishDialog() {

        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_confirm_finish);

        dialog.show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();
            activity.onBackPressed();
        }, 6000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case DOCUMENT_CODE:

                if (resultCode == RESULT_OK) {

                    PathHolder = data.getData().getPath();
                    // Here we are initialising the progress dialog box
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Uploading");
                    dialog.setCancelable(false);

                    // this will show message uploading
                    // while pdf is uploading
                    dialog.show();

                    imageuri = data.getData();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final String messagePushID = AppPreferences.getSession().getMobileNumber();

                    // Here we are uploading the pdf in firebase storage with the name of current time
                    final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
                    PathHolder = filepath.getName();
                    filepath.putFile(imageuri).continueWithTask((Continuation) task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            // After uploading is done it progress
                            // dialog box will be dismissed
                            dialog.dismiss();
                           /* mUploadFile.setText(PathHolder);*/
                            Uri uri = task.getResult();
                            upload = new Upload(filepath.getName(), uri.toString());
                            resumeUrl = uri.toString();
                            SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(session.getMobileNumber()).child("userModel").child("upload").setValue(upload);
                            Utils.showToast(getActivity().getApplicationContext(), getActivity().findViewById(R.id.fragment_container), "Uploaded Successfully");
                        } else {
                            dialog.dismiss();
                            Utils.showToast(getActivity().getApplicationContext(), getActivity().findViewById(R.id.fragment_container), "Upload Failed");
                        }
                    });
                }
                break;

        }
    }

}
