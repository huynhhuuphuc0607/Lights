package com.phantasic7.projects.lights;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Group {
    private long mGroupID;
    private String mName;
    private String mColor;
    private long mSceneID;
    private List<String> mLightIDs;
    private int mBrightness;

    public Group(int groupID, String name) {
        mGroupID = groupID;
        mName = name;

        mLightIDs = new ArrayList<>();
    }

    //completed constructor
    public Group(long groupID, String name, String color, long sceneID, List<String> lightIDs, int brightness) {
        mGroupID = groupID;
        mName = name;
        mColor = color;
        mSceneID = sceneID;
        mLightIDs = lightIDs;
        mBrightness = brightness;
    }

    public long getGroupID() {
        return mGroupID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
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
}
