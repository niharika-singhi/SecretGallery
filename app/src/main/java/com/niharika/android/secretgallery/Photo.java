package com.niharika.android.secretgallery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.atomic.AtomicInteger;

public class Photo implements Parcelable {
    private static final AtomicInteger count=new AtomicInteger(0);//count variable to create ids
    private String fileName;
    private String name;
    private int id;


    public Photo(String fileName) {
        id=count.incrementAndGet();
        this.fileName = fileName;
    }

    protected Photo(Parcel in) {
        fileName = in.readString();
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(name);
        dest.writeInt(id);
    }
}
