package com.skyhawk.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String linkedInId;
    private String skypeId;
    private String location;
    private String yearOfExperience;
    private String pricePerHour;
    private String expectedCtc;
    private String skills;
    private String PathHolder;
    private Uri docPath;
    private Upload profileImage;
    private Upload upload;
    private String profile_image_url;
    /**
     * Get the profile image url
     *
     * @return profile image url
     */
    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public void setProfileImageUrl(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }


    public  UserModel() {}
    public UserModel(String firstName) {
        this.firstName = firstName;
    }


    public UserModel(String firstName, String middleName, String lastName, String linkedInId, String skypeId, String location, String yearOfExperience, String pricePerHour, String expectedCtc, String skills, Upload upload,  Upload profileImage) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.linkedInId = linkedInId;
        this.skypeId = skypeId;
        this.location = location;
        this.yearOfExperience = yearOfExperience;
        this.pricePerHour = pricePerHour;
        this.expectedCtc = expectedCtc;
        this.skills = skills;
        this.upload = upload;
        this.profileImage = profileImage;
    }

    public UserModel(String firstName, String middleName, String lastName, String linkedInId, String skypeId, String location, String yearOfExperience, String pricePerHour, String expectedCtc, String skills, Upload upload) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.linkedInId = linkedInId;
        this.skypeId = skypeId;
        this.location = location;
        this.yearOfExperience = yearOfExperience;
        this.pricePerHour = pricePerHour;
        this.expectedCtc = expectedCtc;
        this.skills = skills;
        this.upload = upload;
    }


    protected UserModel(Parcel in) {
        userId = in.readString();
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        linkedInId = in.readString();
        skypeId = in.readString();
        location = in.readString();
        yearOfExperience = in.readString();
        pricePerHour = in.readString();
        expectedCtc = in.readString();
        skills = in.readString();
        PathHolder = in.readString();
        profile_image_url = in.readString();
        docPath = in.readParcelable(Uri.class.getClassLoader());
    }


    public String getPricePerHour() {
        return pricePerHour;
    }

    public String getExpectedCtc() {
        return expectedCtc;
    }

    public Upload getUpload() {
        return upload;
    }

    public Upload getProfileImage() {
        return profileImage;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public Uri getDocPath() {
        return docPath;
    }

    public String getPathHolder() {
        return PathHolder;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLinkedInId() {
        return linkedInId;
    }

    public String getSkypeId() {
        return skypeId;
    }

    public String getLocation() {
        return location;
    }

    public String getYearOfExperience() {
        return yearOfExperience;
    }


    public String getSkills() {
        return skills;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(linkedInId);
        dest.writeString(skypeId);
        dest.writeString(location);
        dest.writeString(yearOfExperience);
        dest.writeString(pricePerHour);
        dest.writeString(expectedCtc);
        dest.writeString(skills);
        dest.writeString(PathHolder);
        dest.writeString(profile_image_url);
        dest.writeParcelable(docPath, flags);
    }
}
