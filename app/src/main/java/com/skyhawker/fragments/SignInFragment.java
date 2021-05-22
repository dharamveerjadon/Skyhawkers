package com.skyhawker.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skyhawker.R;
import com.skyhawker.activities.MainActivity;
import com.skyhawker.activities.SignUpActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.models.Session;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private Button mBtnSignUp, mBtnLogin;
    private EditText mEdtLoginId, mEdtPassword;
    private SpinnerView spinnerView;
    private  Session session;
    private SignUpActivity activity;

    public static SignInFragment newInstance(@SuppressWarnings("SameParameterValue") String title) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SignUpActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        findViewById(rootView);
        registerListenerId();
        return rootView;
    }


    private void findViewById(View view) {
        mEdtLoginId = view.findViewById(R.id.edt_login_id);
        mEdtPassword = view.findViewById(R.id.edt_password);
        mBtnSignUp = view.findViewById(R.id.btn_sign_up);
        mBtnLogin = view.findViewById(R.id.btn_login);
        spinnerView = view.findViewById(R.id.progress_bar);
    }

    private void registerListenerId() {
        mBtnSignUp.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

        mEdtLoginId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getUserInformation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getUserInformation(String mobileNumber) {

        DatabaseReference signInReference = SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(mobileNumber);
        if(signInReference != null) {
            signInReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // this method is call to get the realtime
                    // updates in the data.
                    // this method is called when the data is
                    // changed in our Firebase console.
                    // below line is for getting the data from
                    // snapshot of our database.
                    session = snapshot.getValue(Session.class);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                pushFragment(SignUpFragment.newInstance(null), true);
                break;
            case R.id.btn_login:
                if(isValidation()) {
                    spinnerView.setVisibility(View.VISIBLE);

                    if(session != null && session.getCreatePassword() != null && mEdtPassword.getText().toString().equals(session.getCreatePassword())) {
                        new Handler().postDelayed(() -> {
                            spinnerView.setVisibility(View.GONE);
                            AppPreferences.setSession(session);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            activity.finish();
                        },3000);
                    }else {
                        new Handler().postDelayed(() -> {
                            spinnerView.setVisibility(View.GONE);
                            Utils.showToast(activity, activity.findViewById(R.id.fragment_container),"Invalid credentials");
                        },3000);

                    }


                }
                break;
        }
    }

    private boolean isValidation() {

        if (TextUtils.isEmpty(mEdtLoginId.getText().toString().trim())) {
            mEdtLoginId.setError("Enter mobile number here..");
            mEdtLoginId.requestFocus();
            return false;
        } else {
            mEdtLoginId.setError(null);
        }
        if (TextUtils.isEmpty(mEdtPassword.getText().toString().trim())) {
            mEdtPassword.setError("Enter password here..");
            mEdtPassword.requestFocus();
            return false;
        } else {
            mEdtPassword.setError(null);
        }

        return true;
    }
}
