package com.phantasic7.projects.lights;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.HueHTTPRequest;
import com.philips.lighting.hue.sdk.wrapper.connection.HueHTTPResponse;
import com.philips.lighting.hue.sdk.wrapper.connection.RequestCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuynhHuu on 09-Mar-18.
 */

public class LibraryLoader extends Application {
    static {
        // Load the huesdk native library before calling any SDK method
        System.loadLibrary("huesdk");
    }

    public static Bridge mBridge;
    public static String mUsername = "";
    public static BridgeConnection mBridgeConnection;
    public static String TAG = "Phantastic_Lights";
    @Override
    public void onCreate() {
        super.onCreate();

        // Configure the storage location and log level for the Hue SDK
        Persistence.setStorageLocation(getFilesDir().getAbsolutePath(), "HueQuickStart");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);

    }

    public static void authorizeUser()
    {
        String s = "/lights/3/state";
        String s1 = "{\"on\": true,\"bri\": 200}";
        LibraryLoader.mBridgeConnection.doPut(s, s1, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                Log.i("Phantastic Lights", hueHTTPResponse.getBody());
            }
        });
    }

    public static List<Group> getGroups()
    {
        String s = "/groups";
        final List<Group> groups = new ArrayList<>();
        LibraryLoader.mBridgeConnection.doGet(s, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                Log.i("Phantastic Lights", hueHTTPResponse.getBody());
                try {
                    JSONObject jsonObject = new JSONObject(hueHTTPResponse.getBody());
                    int size = mBridge.getBridgeState().getGroups().size() + 1;
                    for(int i = 1; i< size; i++) {
                        JSONObject groupJSON = jsonObject.getJSONObject(i + "");
                        JSONArray lightsJSONArray = groupJSON.getJSONArray("lights");
                        List<String> lights = new ArrayList<>();
                        int length = lightsJSONArray.length();
                        for(int j = 0; j < length; j ++)
                        {
                            lights.add((String)lightsJSONArray.get(j));
                        }
                        JSONObject actionJSON = groupJSON.getJSONObject("action");
                        JSONArray xyJSONArray = actionJSON.getJSONArray("xy");

                        Group group = new Group(i,groupJSON.getString("name"),lights,
                                actionJSON.getBoolean("on"), actionJSON.getInt("bri"),
                                actionJSON.getInt("hue"), actionJSON.getInt("sat"),
                                (float)xyJSONArray.getDouble(0),(float)xyJSONArray.getDouble(1),groupJSON.getString("class"));
                        groups.add(group);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return groups;
    }

    public static Group getGroup(final int groupID)
    {
        String s = "/groups/" +groupID;
        final Group group = new Group(groupID);

        mBridgeConnection.doGet(s, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                JSONObject groupJSON = null;
                try {
                    groupJSON = new JSONObject(hueHTTPResponse.getBody());
                    JSONArray lightsJSONArray = groupJSON.getJSONArray("lights");
                    List<String> lights = new ArrayList<>();
                    int length = lightsJSONArray.length();
                    for(int j = 0; j < length; j ++)
                    {
                        lights.add((String)lightsJSONArray.get(j));
                    }
                    JSONObject actionJSON = groupJSON.getJSONObject("action");
                    JSONArray xyJSONArray = actionJSON.getJSONArray("xy");
                    group.setName(groupJSON.getString("name"));
                    group.setLightIDs(lights);
                    group.setOn(actionJSON.getBoolean("on"));
                    group.setBrightness(actionJSON.getInt("bri"));
                    group.setHue(actionJSON.getInt("hue"));
                    group.setSat(actionJSON.getInt("sat"));
                    group.setXCor((float)xyJSONArray.getDouble(0));
                    group.setYCor((float)xyJSONArray.getDouble(1));
                    group.setType(groupJSON.getString("class"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return group;
    }

    public static void toggleGroup(final int groupID, final boolean newState)
    {
        String s = "/groups/" + groupID + "/action";
        String s1;
        if(newState)
            s1 = "{\"on\":true}";
        else
            s1 = "{\"on\":false}";

        mBridgeConnection.doPut(s, s1, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                Log.i(TAG,"New state " + newState + ". Turn on/off group " + groupID);
                Log.i(TAG, hueHTTPResponse.getBody());
            }
        });
    }

    public static void changeGroupName(final int groupID, final String newName)
    {
        String s = "/groups/" + groupID;
        String s1 = "{\"name\":\"" + newName + "\"}";
        mBridgeConnection.doPut(s, s1, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                Log.i(TAG, "Change to " + newName);
                Log.i(TAG,hueHTTPResponse.getBody());
            }
        });
    }

    public static void changeGroupType(final int groupID, final String newClass)
    {
        String s = "/groups/" + groupID;
        String s1 = "{\"class\":\"" + newClass + "\"}";
        mBridgeConnection.doPut(s, s1, new RequestCallback() {
            @Override
            public void onCallback(List<HueError> list, HueHTTPResponse hueHTTPResponse) {
                Log.i(TAG, "Change to " + newClass);
                Log.i(TAG,hueHTTPResponse.getBody());
            }
        });
    }

    public static List<LightPoint> getLights(List<String> lightIds)
    {
        List<LightPoint>lights = new ArrayList<>();
        int size = lightIds.size();
        for (int i = 0; i < lightIds.size(); i++)
            lights.add(LibraryLoader.mBridge.getBridgeState().getLightPoint(lightIds.get(i) + ""));
        return lights;
    }
}
