package com.phantasic7.projects.lights;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Light {
    private String lightID;
    private String mName;
    private boolean isOn;
    private boolean isReachable;

    public Light(String lightID, String name, boolean isOn, boolean isReachable) {
        this.lightID = lightID;
        mName = name;
        this.isOn = isOn;
        this.isReachable = isReachable;
    }

    public String getLightID() {
        return lightID;
    }

    public void setLightID(String lightID) {
        this.lightID = lightID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }
}
