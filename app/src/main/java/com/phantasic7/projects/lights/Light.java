package com.phantasic7.projects.lights;

/**
 * Created by HuynhHuu on 16-Feb-18.
 */

public class Light {
    private String mAddress;
    private String mName;
    private String mGroupIDs;

    public Light(String address) {
        this.mAddress = address;
    }

    public Light(String address, String name) {
        this.mAddress = address;
        this.mName = name;
    }

    //completed constructor
    public Light(String address, String name, String groupIDs) {
        mAddress = address;
        mName = name;
        mGroupIDs = groupIDs;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getGroupIDs() {
        return mGroupIDs;
    }

    public void setGroupIDs(String groupIDs) {
        this.mGroupIDs = groupIDs;
    }
}
