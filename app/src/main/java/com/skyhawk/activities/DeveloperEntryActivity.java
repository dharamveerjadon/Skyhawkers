package com.skyhawk.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skyhawk.R;
import com.skyhawk.customview.SpinnerView;
import com.skyhawk.models.Session;
import com.skyhawk.models.Upload;
import com.skyhawk.models.UserModel;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.Keys;
import com.skyhawk.utils.Utils;

import static com.skyhawk.utils.AppPreferences.SELECTED_HOME_SCREEN;

public class DeveloperEntryActivity extends BaseActivity implements View.OnClickListener {
    private static final int DOCUMENT_CODE = 07;
    private EditText mEdtFirstName, mEdtMiddleName, mEdtLastName, mEdtEmailId, mEdtLinkedInId, mEdtSkypeId, mEdtContactNumber,
            mEdtLocation, mEdtYearOfExperience, mEdtPricePerHour, mEdtExpectedSalary, mEdtSkills;
    private Button mBtnSubmit;
    private TextView mUploadFile, txtResume;
    private String PathHolder;
    private Uri imageuri;
    private ProgressDialog dialog;
    private ImageView mImgDocument;
    private static DatabaseReference mDatabase;
    private SpinnerView spinnerView;
    private String mobileNumber;
    private boolean isFirstTime;
    private Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_entry);

        if (getIntent() != null) {
            isFirstTime = getIntent().getBooleanExtra("isFirst", false);
            mobileNumber = getIntent().getStringExtra(Keys.MOBILE_NUMBER);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        findViewId();
        registerListener();
        getdata();
    }

    private void findViewId() {

        mEdtFirstName = findViewById(R.id.edt_first_name);
        mEdtMiddleName = findViewById(R.id.edt_middle_name);
        mEdtLastName = findViewById(R.id.edt_last_name);
        mEdtEmailId = findViewById(R.id.edt_email_id);
        mEdtLinkedInId = findViewById(R.id.edt_linkedin);
        mEdtSkypeId = findViewById(R.id.edt_skype_id);
        mEdtContactNumber = findViewById(R.id.edt_contact_number);
        mEdtLocation = findViewById(R.id.edt_location);
        mEdtYearOfExperience = findViewById(R.id.edt_year_of_experience);
        mEdtPricePerHour = findViewById(R.id.edt_price_per_hour);
        mEdtExpectedSalary = findViewById(R.id.edt_expected_ctc);
        mEdtSkills = findViewById(R.id.edt_skills);
        mUploadFile = findViewById(R.id.upload_file);
        mBtnSubmit = findViewById(R.id.btn_submit);
        mImgDocument = findViewById(R.id.img_document);
        spinnerView = findViewById(R.id.progress_bar);
        txtResume = findViewById(R.id.txt_resume);
    }

    private void registerListener() {
        mUploadFile.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mImgDocument.setOnClickListener(this);
    }

    private void getdata() {
        spinnerView.setVisibility(View.VISIBLE);
        // calling add value event listener method
        // for getting the values from database.

        Session session = AppPreferences.getSession();
        if (session != null) {

            mobileNumber = TextUtils.isEmpty(session.getMobileNumber()) ? mobileNumber : session.getMobileNumber();
            mDatabase.child("Developers").child(mobileNumber).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Session value = snapshot.getValue(Session.class);
                    AppPreferences.setSession(value);

                    initData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    Toast.makeText(DeveloperEntryActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    private boolean isValidation() {

        if (TextUtils.isEmpty(mEdtFirstName.getText().toString().trim())) {
            mEdtFirstName.setError("Enter first name here..");
            mEdtFirstName.requestFocus();
            return false;
        } else {
            mEdtFirstName.setError(null);
        }
        if (TextUtils.isEmpty(mEdtLastName.getText().toString().trim())) {
            mEdtLastName.setError("Enter last name here..");
            mEdtLastName.requestFocus();
            return false;
        } else {
            mEdtLastName.setError(null);
        }

        if (TextUtils.isEmpty(mEdtEmailId.getText().toString().trim())) {
            mEdtEmailId.setError("Enter email id here..");
            mEdtEmailId.requestFocus();
            return false;
        } else {
            mEdtEmailId.setError(null);
        }

        if (TextUtils.isEmpty(mEdtContactNumber.getText().toString().trim())) {
            mEdtContactNumber.setError("Enter contact number here..");
            mEdtContactNumber.requestFocus();
            return false;
        } else {
            mEdtContactNumber.setError(null);
        }

        if (TextUtils.isEmpty(mEdtLocation.getText().toString().trim())) {
            mEdtLocation.setError("Enter location here..");
            mEdtLocation.requestFocus();
            return false;
        } else {
            mEdtLocation.setError(null);
        }

        if (TextUtils.isEmpty(mEdtYearOfExperience.getText().toString().trim())) {
            mEdtYearOfExperience.setError("Enter year of experience here..");
            mEdtYearOfExperience.requestFocus();
            return false;
        } else {
            mEdtYearOfExperience.setError(null);
        }

        if (TextUtils.isEmpty(mEdtPricePerHour.getText().toString().trim())) {
            mEdtPricePerHour.setError("Enter  price per hour here..");
            mEdtPricePerHour.requestFocus();
            return false;
        } else {
            mEdtPricePerHour.setError(null);
        }

        if (TextUtils.isEmpty(mEdtExpectedSalary.getText().toString().trim())) {
            mEdtExpectedSalary.setError("Enter Expected salaray here..");
            mEdtExpectedSalary.requestFocus();
            return false;
        } else {
            mEdtExpectedSalary.setError(null);
        }

        if (TextUtils.isEmpty(mEdtSkills.getText().toString().trim())) {
            mEdtSkills.setError("Enter skills here..");
            mEdtSkills.requestFocus();
            return false;
        } else {
            mEdtSkills.setError(null);
        }

        if (upload == null) {
            Utils.showToast(DeveloperEntryActivity.this, findViewById(R.id.fragment_container), "Please upload resume..");
            return false;
        }

        return true;
    }

    private void initData() {
        spinnerView.setVisibility(View.GONE);
        Session session = AppPreferences.getSession();
        if (session != null) {
            mEdtContactNumber.setText(session.getMobileNumber());
            mEdtEmailId.setText(session.getEmailId());
        }

        if (session != null && session.getUserModel() != null) {
            mEdtFirstName.setText(session.getUserModel().getFirstName());
            mEdtMiddleName.setText(session.getUserModel().getMiddleName());
            mEdtLastName.setText(session.getUserModel().getLastName());

            mEdtLinkedInId.setText(session.getUserModel().getLinkedInId());
            mEdtSkypeId.setText(session.getUserModel().getSkypeId());

            mEdtLocation.setText(session.getUserModel().getLocation());
            mEdtYearOfExperience.setText(session.getUserModel().getYearOfExperience());
            mEdtPricePerHour.setText(session.getUserModel().getPricePerHour());
            mEdtExpectedSalary.setText(session.getUserModel().getExpectedCtc());
            mEdtSkills.setText(session.getUserModel().getSkills());

            upload = session.getUserModel().getUpload();

            if (session.getUserModel().getUpload() != null && !TextUtils.isEmpty(session.getUserModel().getUpload().url)) {
                mUploadFile.setText(session.getUserModel().getUpload().name);
                mImgDocument.setVisibility(View.VISIBLE);
                txtResume.setVisibility(View.VISIBLE);
            } else {
                mImgDocument.setVisibility(View.GONE);
                txtResume.setVisibility(View.GONE);
            }

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isValidation())
                    saveDataToFirebase();
                break;
            case R.id.upload_file:
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
                break;
            case R.id.img_document:
                if (upload != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(upload.url));
                    startActivity(browserIntent);
                }
                break;
        }
    }

    private void saveDataToFirebase() {

        spinnerView.setVisibility(View.VISIBLE);
        String firstName = mEdtFirstName.getText().toString().trim();
        String middleName = mEdtMiddleName.getText().toString().trim();
        String LastName = mEdtLastName.getText().toString().trim();
        String emailId = mEdtEmailId.getText().toString().trim();
        String linkedInId = mEdtLinkedInId.getText().toString().trim();
        String skypeId = mEdtSkypeId.getText().toString().trim();
        final String contactNumber = mEdtContactNumber.getText().toString().trim();
        String location = mEdtLocation.getText().toString().trim();
        String yearOfExperience = mEdtYearOfExperience.getText().toString().trim();
        String expectedSalary = mEdtExpectedSalary.getText().toString().trim();
        String pricePerHour = mEdtPricePerHour.getText().toString().trim();
        String skills = mEdtSkills.getText().toString().trim();

        final Session session = AppPreferences.getSession();
        UserModel userModel = new UserModel(firstName, middleName, LastName, linkedInId, skypeId, location, yearOfExperience, pricePerHour, expectedSalary, skills, upload);
        session.setUserModel(userModel);


        mDatabase.child("Developers").child(contactNumber).setValue(session)
                .addOnSuccessListener(aVoid -> {
                    spinnerView.setVisibility(View.GONE);
                    AppPreferences.setSession(session);
                    if (isFirstTime)
                    AppPreferences.setSelectedHomeScreen(SELECTED_HOME_SCREEN,2);

                    Intent intent = new Intent(DeveloperEntryActivity.this, MainActivity.class);
                    intent.putExtra(Keys.MOBILE_NUMBER,contactNumber );
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        spinnerView.setVisibility(View.GONE));
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
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Uploading");
                    dialog.setCancelable(false);

                    // this will show message uploading
                    // while pdf is uploading
                    dialog.show();

                    imageuri = data.getData();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final String messagePushID = mEdtContactNumber.getText().toString().trim();

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
                            mUploadFile.setText(PathHolder);
                            Uri uri = task.getResult();
                            upload = new Upload(filepath.getName(), uri.toString());
                            mImgDocument.setVisibility(View.VISIBLE);
                            Utils.showToast(DeveloperEntryActivity.this, findViewById(R.id.fragment_container), "Uploaded Successfully");
                        } else {
                            dialog.dismiss();
                            mImgDocument.setVisibility(View.GONE);
                            Utils.showToast(DeveloperEntryActivity.this, findViewById(R.id.fragment_container), "Upload Failed");
                        }
                    });
                }
                break;

        }
    }

    @Override
    protected void syncActionBarArrowState() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}