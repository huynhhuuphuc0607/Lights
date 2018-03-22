package com.phantasic7.projects.lights;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Group {
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
}
