package com.phantasic7.projects.lights;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BridgeEstablishingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static {
        // Load the huesdk native library before calling any SDK method
        System.loadLibrary("huesdk");
    }

    private static final String TAG = "Phantastic Lights";
    //  private Bridge mBridge;
    private BridgeDiscovery mBridgeDiscovery;
    private List<BridgeDiscoveryResult> mBridgeDiscoveryResults;

    private ListView brideDiscoveryListView;
    private TextView searchTextView;
    private ProgressBar searchProgressBar;
    private TextView timeTextView;
    private ProgressBar timeProgressBar;
    private CardView pushlinkCardView;
    private Button searchButton;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_establishing);

        searchTextView = findViewById(R.id.searchTextView);
        searchProgressBar = findViewById(R.id.searchProgressBar);
        brideDiscoveryListView = findViewById(R.id.brideDiscoveryListView);
        brideDiscoveryListView.setOnItemClickListener(this);
        pushlinkCardView = findViewById(R.id.pushlinkCardView);
        timeTextView = findViewById(R.id.timeTextView);
        timeProgressBar = findViewById(R.id.timeProgressBar);
        searchButton = findViewById(R.id.searchButton);

        Persistence.setStorageLocation(getFilesDir().getAbsolutePath(), "HueQuickStart");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);

        String bridgeID = getLastUsedBridgeIp();
        if (bridgeID != null)
            connectToBridge(bridgeID);
        else
            startBridgeDiscovery();
    }

    private void startBridgeDiscovery() {
        //This app only supports one bridge at a time
        disconnectFromBridge();
        mBridgeDiscovery = new BridgeDiscovery();
        mBridgeDiscovery.search(BridgeDiscovery.BridgeDiscoveryOption.ALL, bridgeDiscoveryCallback);
        updateUI("Scanning for bridges");
        searchProgressBar.setVisibility(View.VISIBLE);
    }

    private BridgeDiscoveryCallback bridgeDiscoveryCallback = new BridgeDiscoveryCallback() {
        @Override
        public void onFinished(final List<BridgeDiscoveryResult> list, ReturnCode returnCode) {
            //set mBridgeDiscovery to null to prevent stopBridgeDiscovery from stopping it
            mBridgeDiscovery = null;
            if (returnCode == ReturnCode.SUCCESS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchButton.setEnabled(true);
                        searchProgressBar.setVisibility(View.GONE);
                        searchTextView.setText("Found " + list.size() + " bridge(s)");
                        brideDiscoveryListView.setAdapter(
                                new BridgeAdapter(BridgeEstablishingActivity.this, R.layout.one_bridge_row, list));
                    }
                });

                mBridgeDiscoveryResults = list;
            } else if (returnCode == ReturnCode.STOPPED) {
                Log.i(TAG, "Bridge discovery stopped.");
            } else {
                updateUI("Error doing bridge discovery: " + returnCode);
            }
        }
    };

    private void disconnectFromBridge() {
        if (LibraryLoader.mBridge != null) {
            LibraryLoader.mBridge.disconnect();
            LibraryLoader.mBridge = null;
        }
    }

    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
            Log.i(TAG, "Connection event: " + connectionEvent);

            switch (connectionEvent) {
                case LINK_BUTTON_NOT_PRESSED:
                    break;

                case COULD_NOT_CONNECT:
                    updateUI("Could not connect.");
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
                Log.e(TAG, "Connection error: " + error.toString());
            }
        }
    };

    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            Log.i(TAG, "Bridge state updated event: " + bridgeStateUpdatedEvent);
            switch (bridgeStateUpdatedEvent) {
                case INITIALIZED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCountDownTimer != null) {
                                mCountDownTimer.cancel();
                                mCountDownTimer = null;
                            }
                            triggerPushlinkCardViewAnimation(false);
                            searchTextView.setText("Connection established");
                            searchProgressBar.setVisibility(View.GONE);
                            startActivity(new Intent(BridgeEstablishingActivity.this, MainControllerActivity.class));
                        }
                    });

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

    private void connectToBridge(final String bridgeID) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                stopBridgeDiscovery();
                //disconnect if it is connected to another bridge
                disconnectFromBridge();

                LibraryLoader.mBridge = new BridgeBuilder("Lights", "device name")
                        .setIpAddress(bridgeID)
                        .setConnectionType(BridgeConnectionType.LOCAL)
                        .setBridgeConnectionCallback(bridgeConnectionCallback)
                        .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                        .build();
                LibraryLoader.mBridgeConnection = LibraryLoader.mBridge.getBridgeConnection(BridgeConnectionType.LOCAL);

                LibraryLoader.mBridge.connect();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchTextView.setText("Tap the button on the bridge");
                        // searchProgressBar.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                triggerPushlinkCardViewAnimation(true);
                                mCountDownTimer = new CountDownTimer(30000, 1000) {
                                    @Override
                                    public void onTick(long l) {
                                        timeTextView.setText(String.format("00:%02d", (int) l / 1000));
                                        timeProgressBar.setProgress((int) l / 1000);
                                    }

                                    @Override
                                    public void onFinish() {
                                    }
                                }.start();
                            }
                        }, 3000);
                    }
                });
            }
        });
    }

    private void stopBridgeDiscovery() {
        if (mBridgeDiscovery != null) {
            mBridgeDiscovery.stop();
            mBridgeDiscovery = null;
        }
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

    private void updateUI(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Status: " + status);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        connectToBridge(mBridgeDiscoveryResults.get(i).getIP());
    }

    public void searchForBridges(View v) {
        searchButton.setEnabled(false);
        searchProgressBar.setVisibility(View.VISIBLE);
        searchTextView.setText("Searching for bridges...");
        brideDiscoveryListView.setAdapter(null);
        startBridgeDiscovery();
    }


    public void triggerPushlinkCardViewAnimation(boolean enabled) {
        if (enabled) {
            pushlinkCardView.startAnimation(
                    AnimationUtils.loadAnimation(BridgeEstablishingActivity.this, R.anim.pop_up));
        } else {
            pushlinkCardView.startAnimation(
                    AnimationUtils.loadAnimation(BridgeEstablishingActivity.this, R.anim.shrink));
        }
    }

    public void abortConnection(View v) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        triggerPushlinkCardViewAnimation(false);
        LibraryLoader.mBridge.disconnect();
        searchTextView.setText("Connection aborted");
        searchProgressBar.setVisibility(View.GONE);
    }
}
