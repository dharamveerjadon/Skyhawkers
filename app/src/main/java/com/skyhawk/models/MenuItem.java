package com.skyhawk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.skyhawk.utils.SkyhawkerApplication;

/**
 * Created on 7/10/17.
 */
public class MenuItem implements Parcelable {

    public final int textResId;
    public final int imageResId;
    public final String name;

    public MenuItem(int textResId, int imageResId) {
        this.textResId = textResId;
        this.imageResId = imageResId;
        this.name = SkyhawkerApplication.sharedInstance().getString(textResId);
    }

    private MenuItem(Parcel in) {
        textResId = in.readInt();
        imageResId = in.readInt();
        name = in.readString();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(textResId);
        dest.writeInt(imageResId);
        dest.writeString(name);
    }
}
