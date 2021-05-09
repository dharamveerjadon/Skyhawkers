package com.skyhawker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ApplyJob implements Parcelable {
    private String actionType;
    private Session session;
    private boolean developerSelected;

    protected ApplyJob(Parcel in) {
        actionType = in.readString();
        session = in.readParcelable(Session.class.getClassLoader());
        developerSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionType);
        dest.writeParcelable(session, flags);
        dest.writeByte((byte) (developerSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApplyJob> CREATOR = new Creator<ApplyJob>() {
        @Override
        public ApplyJob createFromParcel(Parcel in) {
            return new ApplyJob(in);
        }

        @Override
        public ApplyJob[] newArray(int size) {
            return new ApplyJob[size];
        }
    };

    public boolean isDeveloperSelected() {
        return developerSelected;
    }

    public ApplyJob() {
    }

    public String getActionType() {
        return actionType;
    }

    public Session getSession() {
        return session;
    }

    public ApplyJob(String actionType, Session session) {
        this.actionType = actionType;
        this.session = session;
    }
}
