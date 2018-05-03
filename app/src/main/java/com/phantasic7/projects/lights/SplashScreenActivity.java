package com.phantasic7.projects.lights;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    static {
        // Load the huesdk native library before calling any SDK method
        System.loadLibrary("huesdk");
    }

    ImageView lightbulbImageView;
    ImageView whiteCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Persistence.setStorageLocation(getFilesDir().getAbsolutePath(), "HueQuickStart");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lightbulbImageView = findViewById(R.id.lightbulbImageView);
        whiteCircleImageView = findViewById(R.id.whiteCircleImageView);
        lightbulbImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.white_light_slide_up));
        whiteCircleImageView.startAnimation(AnimationUtils.loadAnimation(SplashScreenActivity.this,R.anim.white_spot_spread_out));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String bridgeID = getLastUsedBridgeIp();
                if (bridgeID != null)
                    connectToBridge(bridgeID);
                else {
                    startActivity(new Intent(SplashScreenActivity.this, BridgeEstablishingActivity.class));
                    finish();
                }
            }
        }, 2650);

    }

    private String getLastUsedBridgeIp() {
        List<KnownBridge> bridges = KnownBridges.getAll();

        if (bridges.isEmpty())
            return null;
        else
            return Collections.max(bridges, new Comparator<KnownBridge>() {
                @Override
                public int compare(KnownBridge a, KnownBridge b) {
                    return a.getLastConnected().compareTo(b.getLastConnected());
                }
            }).getIpAddress();
    }

    private void connectToBridge(final String bridgeID) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LibraryLoader.mBridge = new BridgeBuilder("Lights", "device name")
                        .setIpAddress(bridgeID)
                        .setConnectionType(BridgeConnectionType.LOCAL)
                        .setBridgeConnectionCallback(bridgeConnectionCallback)
                        .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                        .build();
                LibraryLoader.mBridgeConnection = LibraryLoader.mBridge.getBridgeConnection(BridgeConnectionType.LOCAL);

                LibraryLoader.mBridge.connect();
            }
        });
    }

    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
            Log.i(LibraryLoader.TAG, "Connection event: " + connectionEvent);

            switch (connectionEvent) {
                case LINK_BUTTON_NOT_PRESSED:
                    break;

                case COULD_NOT_CONNECT:
                    Log.i(LibraryLoader.TAG, "Could not connect");
                    break;

                case CONNECTION_LOST:
                    updateUI("Connection lost. Attempting to reconnect.");
                    break;

                case CONNECTION_RESTORED:
                    updateUI("Connection restored.");
                    break;

                case DISCONNECTED:
                    // User-initiated disconnection.
                    break;

                case NOT_AUTHENTICATED:
                    updateUI("Not authenticated");
                    break;
                case AUTHENTICATED:
                    // Update UI to show authenticated state. No UI updates yet, because we have to wait for the bridge state events
                    updateUI("Authenticated");
                    break;

                case CONNECTED:
                    //  updateUI("Connected");
                    break;
            }
        }

        @Override
        public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> list) {
            for (HueError error : list) {
                Log.e(LibraryLoader.TAG, "Connection error: " + error.toString());
            }
        }
    };

    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            Log.i(LibraryLoader.TAG, "Bridge state updated event: " + bridgeStateUpdatedEvent);
            switch (bridgeStateUpdatedEvent) {
                case INITIALIZED:
                    startActivity(new Intent(SplashScreenActivity.this, MainControllerActivity.class));
                    finish();
                    break;
                case LIGHTS_AND_GROUPS:
                    // updateUI("lights and groups ");
                    break;
                default:
                    //   updateUI("bridgeStateUpdated default");
                    break;
            }
        }
    };

    private void updateUI(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LibraryLoader.TAG, "Status: " + status);
            }
        });
    }
}
