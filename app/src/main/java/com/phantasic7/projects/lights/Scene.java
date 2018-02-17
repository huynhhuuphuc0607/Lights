package com.phantasic7.projects.lights;

import android.graphics.Color;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Scene {
    private long mSceneID;
    private String name;
    private String mColor;
    private int mBrightness;

    public Scene(long sceneID, String name, String color, int brightness) {
        mSceneID = sceneID;
        this.name = name;
        mColor = color;
        mBrightness = brightness;
    }

    public long getSceneID() {
        return mSceneID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public int getBrightness() {
        return mBrightness;
    }

    public void setBrightness(int brightness) {
        mBrightness = brightness;
    }
}
