package com.skyhawker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ApplyJob implements Parcelable {
    private String actionType;
    private int isShowHighlight = 1;
    private Session session;
    private boolean developerSelected;

    protected ApplyJob(Parcel in) {
        actionType = in.readString();
        isShowHighlight = in.readInt();
        session = in.readParcelable(Session.class.getClassLoader());
        developerSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionType);
        dest.writeInt(isShowHighlight);
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

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getIsShowHighlight() {
        return isShowHighlight;
    }

    public void setIsShowHighlight(int isShowHighlight) {
        this.isShowHighlight = isShowHighlight;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setDeveloperSelected(boolean developerSelected) {
        this.developerSelected = developerSelected;
    }

    public ApplyJob(String actionType, Session session, int isShowValue) {
        this.actionType = actionType;
        this.session = session;
        this.isShowHighlight = isShowValue;
    }

    public ApplyJob(String actionType, boolean action, Session session, int isShowValue) {
        this.actionType = actionType;
        this.developerSelected = action;
        this.session = session;
        this.isShowHighlight = isShowValue;
    }
}
