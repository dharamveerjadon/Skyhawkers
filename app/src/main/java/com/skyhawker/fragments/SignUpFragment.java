package com.skyhawker.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.skyhawker.R;
import com.skyhawker.activities.DeveloperEntryActivity;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.Session;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

public class SignUpFragment extends BaseFragment implements View.OnClickListener {

    private EditText mEdtEmailId, mEdtCreatePassword, mEdtRepeatPassword, mEdtMobileNumber;
    private Button mBtnRegister;
    private SpinnerView spinnerView;

    public static SignUpFragment newInstance(@SuppressWarnings("SameParameterValue") String title) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);


        findViewById(rootView);
        registerListenerId();
        return rootView;
    }

    private void findViewById(View view) {
        mEdtEmailId = view.findViewById(R.id.edt_email_id);
        mEdtCreatePassword = view.findViewById(R.id.edt_create_password);
        mEdtRepeatPassword = view.findViewById(R.id.edt_repeat_password);
        mEdtMobileNumber = view.findViewById(R.id.edt_mobile_number);
        mBtnRegister = view.findViewById(R.id.btn_register);
        spinnerView = view.findViewById(R.id.progress_bar);
    }

    private void registerListenerId() {

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_register:
                if (isValidation()) {
                    spinnerView.setVisibility(View.VISIBLE);
                    String userId = mEdtMobileNumber.getText().toString().trim();
                    String emailId = mEdtEmailId.getText().toString().trim();
                    final String contactNumber = mEdtMobileNumber.getText().toString().trim();
                    String loginId = mEdtEmailId.getText().toString().trim();
                    String password = mEdtCreatePassword.getText().toString().trim();
                    String repeatPassword = mEdtRepeatPassword.getText().toString().trim();
                    boolean isAdmin = false;
                    final Session session = new Session(userId, emailId, password, repeatPassword, contactNumber, new UserModel(), AppPreferences.getFcmToken(),false);
                    DatabaseReference signUpReference = SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(contactNumber);
                    if(signUpReference != null) {
                        signUpReference.setValue(session)
                                .addOnSuccessListener(aVoid -> {
                                    AppPreferences.setSession(session);
                                        spinnerView.setVisibility(View.GONE);
                                        Intent intent = new Intent(getActivity(), DeveloperEntryActivity.class);
                                        intent.putExtra(Keys.MOBILE_NUMBER,contactNumber);
                                        intent.putExtra("isFirst",true);
                                        startActivity(intent);
                                        getActivity().finish();

                                })
                                .addOnFailureListener(e -> spinnerView.setVisibility(View.GONE));
                    }

                }

                break;
        }
    }

    /**
     * Add the developer fragment
     */
    private void addMyDeveloperFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DeveloperEntryFragment fragment = DeveloperEntryFragment.newInstance(getString(R.string.string_profile), null);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private boolean isValidation() {

        if (TextUtils.isEmpty(mEdtEmailId.getText().toString().trim())) {
            mEdtEmailId.setError("Enter email address here..");
            mEdtEmailId.requestFocus();
            return false;
        } else {
            mEdtEmailId.setError(null);
        }
        if (TextUtils.isEmpty(mEdtCreatePassword.getText().toString().trim())) {
            mEdtCreatePassword.setError("Enter password here..");
            mEdtCreatePassword.requestFocus();
            return false;
        } else {
            mEdtCreatePassword.setError(null);
        }
        if (TextUtils.isEmpty(mEdtRepeatPassword.getText().toString().trim())) {
            mEdtRepeatPassword.setError("Enter repeat password here..");
            mEdtRepeatPassword.requestFocus();
            return false;
        } else {
            mEdtRepeatPassword.setError(null);
        }
        if (TextUtils.isEmpty(mEdtMobileNumber.getText().toString().trim())) {
            mEdtMobileNumber.setError("Enter mobile number here..");
            mEdtMobileNumber.requestFocus();
            return false;
        } else {
            mEdtMobileNumber.setError(null);
        }

        if(!mEdtRepeatPassword.getText().toString().trim().equals(mEdtCreatePassword.getText().toString().trim())) {
            mEdtRepeatPassword.setError("Enter correct repeat password here..");
            mEdtRepeatPassword.requestFocus();
            return false;
        }else {
            mEdtRepeatPassword.setError(null);
        }
        return true;
    }
}
