package com.phantasic7.projects.lights;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Group implements Parcelable {
    private int mGroupID;
    private String mName;
    private long mSceneID;
    private List<String> mLightIDs;
    private int mBrightness;
    private boolean mOn;
    private int mHue;
    private int mSat;
    private float mXCor;
    private float mYCor;
    private int mColor;
    private String mType;

    public Group(int groupID) {
        mGroupID = groupID;
    }

    public Group(int groupID, String name) {
        mGroupID = groupID;
        mName = name;

        mLightIDs = new ArrayList<>();
    }

    public Group(int groupID, String name, List<String> lightIDs, boolean on, int brightness, int hue, int sat, float x, float y, String type)
    {
        mGroupID = groupID;
        mName = name;
        mLightIDs = lightIDs;
        mOn = on;
        mBrightness = brightness;
        mHue = hue;
        mSat = sat;
        mXCor = x;
        mYCor = y;

        mColor = Utils.XYZToColor(x,y,brightness);
        mType = type;
    }

    //complete constructor
    public Group(int groupID, String name, String color, long sceneID, List<String> lightIDs, int brightness) {
        mGroupID = groupID;
        mName = name;
       // mColor = color;
        mSceneID = sceneID;
        mLightIDs = lightIDs;
        mBrightness = brightness;
    }

    protected Group(Parcel in) {
        mGroupID = in.readInt();
        mName = in.readString();
        mSceneID = in.readLong();
        mLightIDs = in.createStringArrayList();
        mBrightness = in.readInt();
        mOn = in.readByte() != 0;
        mHue = in.readInt();
        mSat = in.readInt();
        mXCor = in.readFloat();
        mYCor = in.readFloat();
        mColor = in.readInt();
        mType = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public int getGroupID() {
        return mGroupID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public long getSceneID() {
        return mSceneID;
    }

    public void setSceneID(long sceneID) {
        mSceneID = sceneID;
    }

    public List<String> getLightIDs() {
        return mLightIDs;
    }

    public void setLightIDs(List<String> lightIDs) {
        mLightIDs = lightIDs;
    }

    public void addLightID(String lightID)
    {
        mLightIDs.add(lightID);
    }

    public void removeLightID(String lightID)
    {
        mLightIDs.remove(mLightIDs.indexOf(lightID));
    }

    public int getBrightness() {
        return mBrightness;
    }

    public void setBrightness(int brightness) {
        mBrightness = brightness;
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        mOn = on;
    }

    public int getHue() {
        return mHue;
    }

    public void setHue(int hue) {
        mHue = hue;
    }

    public int getSat() {
        return mSat;
    }

    public void setSat(int sat) {
        mSat = sat;
    }

    public float getXCor() {
        return mXCor;
    }

    public void setXCor(float XCor) {
        mXCor = XCor;
    }

    public float getYCor() {
        return mYCor;
    }

    public void setYCor(float YCor) {
        mYCor = YCor;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mGroupID);
        parcel.writeString(mName);
        parcel.writeLong(mSceneID);
        parcel.writeStringList(mLightIDs);
        parcel.writeInt(mBrightness);
        parcel.writeByte((byte) (mOn ? 1 : 0));
        parcel.writeInt(mHue);
        parcel.writeInt(mSat);
        parcel.writeFloat(mXCor);
        parcel.writeFloat(mYCor);
        parcel.writeInt(mColor);
        parcel.writeString(mType);
    }
}
