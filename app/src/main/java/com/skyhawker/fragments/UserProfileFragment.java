package com.skyhawker.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skyhawker.R;
import com.skyhawker.activities.SignUpActivity;
import com.skyhawker.customview.SpinnerView;
import com.skyhawker.customview.Tag;
import com.skyhawker.customview.TagView;
import com.skyhawker.models.Session;
import com.skyhawker.models.Upload;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.SkyhawkerApplication;
import com.skyhawker.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends BaseFragment implements View.OnClickListener {
    private SpinnerView spinnerView;
    private TextView mBtnEdit;
    private ImageView docResume;
    private String resumeUrl;
    private TextView logout;
    private ImageView userImageEditIcon, profileImage;
    private Uri imageuri;
    private String PathHolder;
    Context context;
    private Upload uploadProfile;
    private TagView tagGroup;
    private ProgressBar progressBar;
    private TextView mTxtLinkedIn, mTxtEmailId, mTxtName, mTxtContact, mTxtExpectedSalary, mTxtPricePerHour, mTxtLocation, mTxtSkypeId, mTxtYearOfExperience, mTxtSkills;

    public static UserProfileFragment newInstance(String title) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set title
        setToolbarTitle(getTitle());
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        context = getActivity();
        findViewById(rootView);
        registerListener();
        getdata();
        return rootView;
    }

    private void getdata() {
        spinnerView.setVisibility(View.VISIBLE);
        // calling add value event listener method
        // for getting the values from database.
        Session session = AppPreferences.getSession();
        if (session != null) {
            SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(session.getMobileNumber()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // this method is call to get the realtime
                    // updates in the data.
                    // this method is called when the data is
                    // changed in our Firebase console.
                    // below line is for getting the data from
                    // snapshot of our database.
                    Session value = snapshot.getValue(Session.class);

                    // after getting the value we are setting
                    // our value to our text view in below line.
                    AppPreferences.setSession(value);

                    initData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // calling on cancelled method when we receive
                    // any error or we are not able to get the data.
                    Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void findViewById(View view) {
        spinnerView = view.findViewById(R.id.progress_bar);
        userImageEditIcon = view.findViewById(R.id.img_edit);
        profileImage = view.findViewById(R.id.profile_image);
        progressBar = view.findViewById(R.id.pb_profile_image);
        mBtnEdit = view.findViewById(R.id.btn_edit);
        mTxtName = view.findViewById(R.id.user_name);
        mTxtEmailId = view.findViewById(R.id.txt_email);
        mTxtLinkedIn = view.findViewById(R.id.txt_linkedIn);
        mTxtContact = view.findViewById(R.id.txt_mobile_number);
        mTxtExpectedSalary = view.findViewById(R.id.txt_expected_salary);
        mTxtPricePerHour = view.findViewById(R.id.txt_price_per_hour);
        mTxtLocation = view.findViewById(R.id.txt_location);
        mTxtSkypeId = view.findViewById(R.id.txt_skype_id);
        mTxtYearOfExperience = view.findViewById(R.id.txt_year_of_experience);
        tagGroup = view.findViewById(R.id.tag_group);
        docResume = view.findViewById(R.id.img_resume);
    }

    private void registerListener() {
        mBtnEdit.setOnClickListener(this);
        docResume.setOnClickListener(this);
        userImageEditIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                pushFragment(DeveloperEntryFragment.newInstance(null, null), true);
                break;
            case R.id.img_resume:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeUrl));
                startActivity(browserIntent);
                break;
            case R.id.img_edit:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case 1:

                if (resultCode == RESULT_OK) {
                    spinnerView.setVisibility(View.VISIBLE);
                    imageuri = data.getData();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final Session session = AppPreferences.getSession();
                    final UserModel userModel = session.getUserModel();
                    final String messagePushID = session.getMobileNumber()+"-profile";

                    // Here we are uploading the pdf in firebase storage with the name of current time
                    final StorageReference filepath = storageReference.child(messagePushID + "." + "png");
                    PathHolder = filepath.getName();
                    filepath.putFile(imageuri).continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // After uploading is done it progress
                                // dialog box will be dismissed
                                spinnerView.setVisibility(View.GONE);
                                final Uri uri = task.getResult();

                                uploadProfile = new Upload(filepath.getName(), uri.toString());
                                UserModel userUpload = new UserModel(userModel.getFirstName(), userModel.getMiddleName(), userModel.getLastName(), userModel.getLinkedInId(), userModel.getSkypeId(), userModel.getLocation(), userModel.getYearOfExperience(), userModel.getExpectedCtc(), userModel.getPricePerHour(), userModel.getSkills(), userModel.getUpload(),uploadProfile);
                                session.setUserModel(userUpload);


                                SkyhawkerApplication.sharedDatabaseInstance().child("Developers").child(session.getMobileNumber()).setValue(session)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                spinnerView.setVisibility(View.GONE);
                                                Glide.with(SkyhawkerApplication.sharedInstance())
                                                        .load(uri.toString())
                                                        .listener(new RequestListener<String, GlideDrawable>() {
                                                            @Override
                                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                                                                    isFirstResource) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                                                                    target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                return false;
                                                            }
                                                        })
                                                        .placeholder(R.drawable.ic_skyhawk_profile_orange)
                                                        .dontAnimate()
                                                        .into(profileImage);
                                                AppPreferences.setSession(session);
                                                Utils.showToast(getActivity(), getActivity().findViewById(R.id.fragment_container), "Uploaded Successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                spinnerView.setVisibility(View.GONE);
                                            }
                                        });

                            } else {
                                spinnerView.setVisibility(View.GONE);
                                Utils.showToast(getActivity(), getActivity().findViewById(R.id.fragment_container), "Upload Failed");
                            }
                        }
                    });

                    //uploadFile(imageuri);
                }
                break;

        }
    }

    private void initData() {
        spinnerView.setVisibility(View.GONE);
        Session session = AppPreferences.getSession();
        mTxtName.setText(session.getUserModel().getFirstName() +" "+session.getUserModel().getLastName());
        mTxtContact.setText(session.getMobileNumber());
        mTxtExpectedSalary.setText(session.getUserModel().getExpectedCtc());
        mTxtPricePerHour.setText(session.getUserModel().getPricePerHour());
        mTxtLocation.setText(session.getUserModel().getLocation());
        mTxtSkypeId.setText(session.getUserModel().getSkypeId());
        mTxtYearOfExperience.setText(session.getUserModel().getYearOfExperience());
        setTags(session.getUserModel().getSkills());
        mTxtLinkedIn.setText(session.getUserModel().getLinkedInId());
        mTxtEmailId.setText(session.getEmailId());

        if (session.getUserModel().getProfileImage() != null && !TextUtils.isEmpty(session.getUserModel().getProfileImage().url)) {


            Glide.with(SkyhawkerApplication.sharedInstance())
                    .load(session.getUserModel().getProfileImage().url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                                isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                                target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .placeholder(R.drawable.ic_skyhawk_profile_orange)
                    .dontAnimate()
                    .into(profileImage);
        }
        if (session.getUserModel().getUpload() != null && !TextUtils.isEmpty(session.getUserModel().getUpload().url)) {
            docResume.setVisibility(View.VISIBLE);
            resumeUrl = session.getUserModel().getUpload().url;
        } else {
            docResume.setVisibility(View.GONE);
        }

    }

    private void setTags(String skills) {
        List<Tag> tagList = new ArrayList<>();

        String[] strSkills = skills.split(",");
        for(String value: strSkills) {
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
