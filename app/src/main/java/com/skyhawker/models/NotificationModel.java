package com.skyhawker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationModel implements Parcelable {
    private String title;
    private String message;
    private String jobName;
    private String jobDescription;
    private String budget;
    private String yearOfExperience;
    private String category;
    private String skills;
    private String date;
    private String type;
    private String to;
    private String key;

    public String getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getKey() {
        return key;
    }

    public NotificationModel() {}
    public NotificationModel(String title, String message, String jobName, String jobDescription, String budget, String yearOfExperience, String category, String skills, String date, String type, String to, String key) {
        this.title = title;
        this.message = message;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.budget = budget;
        this.yearOfExperience = yearOfExperience;
        this.category = category;
        this.skills = skills;
        this.date = date;
        this.type = type;
        this.to = to;
        this.key = key;
    }

    protected NotificationModel(Parcel in) {
        title = in.readString();
        message = in.readString();
        jobName = in.readString();
        jobDescription = in.readString();
        budget = in.readString();
        yearOfExperience = in.readString();
        category = in.readString();
        skills = in.readString();
        date = in.readString();
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getBudget() {
        return budget;
    }

    public String getYearOfExperience() {
        return yearOfExperience;
    }

    public String getCategory() {
        return category;
    }

    public String getSkills() {
        return skills;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(jobName);
        dest.writeString(jobDescription);
        dest.writeString(budget);
        dest.writeString(yearOfExperience);
        dest.writeString(category);
        dest.writeString(skills);
        dest.writeString(date);
    }
}
