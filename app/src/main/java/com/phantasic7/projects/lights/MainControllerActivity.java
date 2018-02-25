package com.phantasic7.projects.lights;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.wrapper.HueLog;
import com.philips.lighting.hue.sdk.wrapper.Persistence;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.LocalBridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuCloseListener;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;
import uk.co.markormesher.android_fab.SpeedDialMenuOpenListener;

public class MainControllerActivity extends AppCompatActivity {

    static {
        System.loadLibrary("huesdk");
    }

    LinearLayout groupsLinearLayout;
    List<Group> groupsList;
    DBHelper dbHelper;
    FloatingActionButton fab;

    Bridge bridge;
    String bridgeID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure the storage location and log level for the Hue SDK
        Persistence.setStorageLocation(getFilesDir().getAbsolutePath(), "Lights");
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO);

        init();

        setContentView(R.layout.activity_maincontroller);
        groupsLinearLayout = findViewById(R.id.groupsLinearLayout);
        fab = findViewById(R.id.fab);
        fab.setContentCoverEnabled(true);
        fab.setContentCoverColour(0xaaffffff);
        fab.setSpeedDialMenuAdapter(new SpeedDialMenuAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @NotNull
            @Override
            public SpeedDialMenuItem getMenuItem(Context context, int i) {
                SpeedDialMenuItem item = null;
                switch (i) {
                    case 0:
                        item = new SpeedDialMenuItem(context, R.drawable.light, "Add a new light blub");
                        break;
                    case 1:
                        item = new SpeedDialMenuItem(context, R.drawable.group, "Create a group/room");
                        break;
                }
                return item;
            }

            @Override
            public boolean onMenuItemClick(int position) {
                switch (position)
                {
                    case 0:
                        Toast.makeText(MainControllerActivity.this, "Add a new light bulb", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainControllerActivity.this, "Create a group/room", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }

            @Override
            public float fabRotationDegrees() {
                return 45;
            }
        });

        dbHelper = new DBHelper(this);
        groupsList = new ArrayList<>();
        createGroups();
        createGroupCardViews2();
    }

    private void createGroups() {
        dbHelper.deleteAllGroups();
        dbHelper.deleteAllLights();
        dbHelper.deleteAllScenes();

        dbHelper.addLight(new Light("AAAAAA", "Bed","1|2"));
        dbHelper.addLight(new Light("BBBBBB", "Desk","1|2"));
        dbHelper.addLight(new Light("CCCCCC", "Dinner table","2|3"));
        dbHelper.addLight(new Light("DDDDDD", "Sink","4"));

        dbHelper.addGroup(new Group(1, "Bedroom","#FDC010",1, DBHelper.createStringListfromString("AAAAAA|BBBBBB"),75));
        dbHelper.addGroup(new Group(2, "Party", "#09BCD3", 2, DBHelper.createStringListfromString("AAAAAA|BBBBBB|CCCCCC|DDDDDD|AAAAAA"),75));
        dbHelper.addGroup(new Group(3, "Kitchen","#FF1744", 3, DBHelper.createStringListfromString("CCCCCC|DDDDDD"),75));
        dbHelper.addGroup(new Group(4, "Mystery","#8F3E97", 4, DBHelper.createStringListfromString("BBBBBB|DDDDDD"),75));

        dbHelper.addScene(new Scene(1, "Cozy", "#FDC010", 75));
        dbHelper.addScene(new Scene(2,  "Cool Blue", "#09BCD3", 75));
        dbHelper.addScene(new Scene(3, "Cherry Red", "#FF1744", 75));
        dbHelper.addScene(new Scene(4, "Royal Purple", "#8F3E97", 75));

        groupsList = dbHelper.getAllGroups();
    }
    private void createGroupCardViews2() {
        int numCardViews = groupsList.size();

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);

        for (int i = 0; i < numCardViews; i++) {
            Group group = groupsList.get(i);

            CardView cv = new CardView(this);
            cv.setLayoutParams(layoutParams);
            // Set CardView corner radius
            cv.setRadius(9);
            // Set cardView content padding
            cv.setContentPadding(15, 15, 15, 15);
            // Set a background color for CardView
            cv.setCardBackgroundColor(Color.parseColor(group.getColor()));
            // Set CardView elevation
            cv.setCardElevation(15);

            final TextView groupNameTextView = new TextView(this);
            groupNameTextView.setText(group.getName());

            final TextView sceneNameTextView = new TextView(this);
            groupNameTextView.setText(group.getName());

            ProgressBar brightnessProgressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
            brightnessProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            brightnessProgressBar.setBackgroundColor(Color.parseColor(group.getColor()));
            brightnessProgressBar.setProgress(75);

            final Intent groupIntent = new Intent(MainControllerActivity.this, GroupsActivity.class);
            groupIntent.putExtra("GroupID",group.getGroupID());
            groupIntent.putExtra("SceneID",group.getSceneID());
            groupIntent.putExtra("Color",group.getColor());


            LinearLayout ln = new LinearLayout(this);
            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        groupNameTextView.setTransitionName(getString(R.string.transition_name_group));
                        sceneNameTextView.setTransitionName(getString(R.string.transition_name_scene));
                        Pair<View, String> pair1 = Pair.create((View) groupNameTextView, getString(R.string.transition_name_group));
                        Pair<View, String> pair2 = Pair.create((View) sceneNameTextView, getString(R.string.transition_name_scene));
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(MainControllerActivity.this, pair1, pair2);
                        startActivity(groupIntent, options.toBundle());

                    } else
                        startActivity(groupIntent);
                }
            });
            ln.setOrientation(LinearLayout.VERTICAL);
            ln.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutParams.WRAP_CONTENT));

            ln.addView(groupNameTextView);
            ln.addView(sceneNameTextView);
            ln.addView(brightnessProgressBar);
            cv.addView(ln);
            groupsLinearLayout.addView(cv);
        }
    }
    private void createGroupCardViews() {
        int numCardViews = groupsList.size();

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);

        for (int i = 0; i < numCardViews; i++) {
            Group group = groupsList.get(i);

            CardView cv = new CardView(this);
            cv.setLayoutParams(layoutParams);
            // Set CardView corner radius
            cv.setRadius(9);
            // Set cardView content padding
            cv.setContentPadding(15, 15, 15, 15);
            // Set a background color for CardView
            cv.setCardBackgroundColor(Color.parseColor(group.getColor()));
            // Set CardView elevation
            cv.setCardElevation(15);

            final TextView groupName = new TextView(this);
            groupName.setTextAppearance(MainControllerActivity.this, android.R.style.TextAppearance_Large);
            groupName.setLayoutParams(layoutParams);
            groupName.setText(group.getName());
            groupName.setTextColor(Color.WHITE);
            groupName.setTypeface(groupName.getTypeface(),Typeface.BOLD);

            final TextView sceneName = new TextView(this);
            groupName.setTextAppearance(MainControllerActivity.this, android.R.style.TextAppearance_Medium);
            sceneName.setLayoutParams(layoutParams);
            sceneName.setText(dbHelper.getScene(group.getSceneID()).getName());
            sceneName.setTextColor(Color.WHITE);
            sceneName.setTypeface(sceneName.getTypeface(),Typeface.BOLD_ITALIC);

            View horizontalLine = new View(this);
            horizontalLine.setBackgroundColor(Color.BLACK);
            LinearLayout.LayoutParams lineLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            lineLP.setMargins(16, 0, 16, 0);
            horizontalLine.setLayoutParams(lineLP);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                groupName.setTransitionName(getString(R.string.transition_name_group));
                sceneName.setTransitionName(getString(R.string.transition_name_scene));
            }

            LinearLayout ln = new LinearLayout(this);

            final Intent groupIntent = new Intent(MainControllerActivity.this, GroupsActivity.class);
            groupIntent.putExtra("GroupID",group.getGroupID());
            groupIntent.putExtra("SceneID",group.getSceneID());
            groupIntent.putExtra("Color",group.getColor());

            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> pair1 = Pair.create((View) groupName, getString(R.string.transition_name_group));
                        Pair<View, String> pair2 = Pair.create((View) sceneName, getString(R.string.transition_name_scene));
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(MainControllerActivity.this, pair1, pair2);
                        startActivity(groupIntent, options.toBundle());

                    } else
                        startActivity(groupIntent);
                }
            });
            ln.setOrientation(LinearLayout.VERTICAL);
            ln.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, layoutParams.WRAP_CONTENT));

            ln.addView(groupName);
            ln.addView(horizontalLine);
            ln.addView(sceneName);
            cv.addView(ln);
            groupsLinearLayout.addView(cv);
        }
    }

    private void init()
    {
        BridgeDiscovery bridgeDiscovery = new BridgeDiscovery();
        bridgeDiscovery.search(new BridgeDiscoveryCallback() {
            @Override
            public void onFinished(List<BridgeDiscoveryResult>  results, ReturnCode returnCode) {
                if (returnCode == ReturnCode.SUCCESS) {
                    Toast.makeText(MainControllerActivity.this, "Bridge found!", Toast.LENGTH_SHORT).show();

                    // Process results
                    BridgeDiscoveryResult searchResult = results.get(0);
                    bridgeID = searchResult.getUniqueID();

                    bridge = new BridgeBuilder("appname", "devicename")
                            .setConnectionType(BridgeConnectionType.LOCAL)
                            .setIpAddress(searchResult.getIP())
                            .setBridgeId(bridgeID)
                            .addBridgeStateUpdatedCallback(bridgeStateUpdateCallback)
                            .setBridgeConnectionCallback(bridgeConnectionCallback)
                            .build();

                    bridge.connect();
                    //or we can do it this way
//                    LocalBridgeConnection localConnection = (LocalBridgeConnection) bridge.getBridgeConnection(BridgeConnectionType.LOCAL);
//                    localConnection.connect();

                }
                else
                    Toast.makeText(MainControllerActivity.this, "Bridge not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent connectionEvent) {
            switch (connectionEvent) {
                // Authentication
                case COULD_NOT_CONNECT:
                    // Inform user to resolve connectivity issues or connect to other bridge
                    break;
                case CONNECTED:
                    // Inform user a connection has been established
                    Toast.makeText(MainControllerActivity.this, "Connection fully established!", Toast.LENGTH_LONG).show();
                    break;
                case CONNECTION_LOST:
                    // Inform user to resolve connectivity issues or connect to other bridge. This can also happen during pushlinking.
                    Toast.makeText(MainControllerActivity.this, "Connection lost. Reconnecting...", Toast.LENGTH_SHORT).show();
                    LocalBridgeConnection localConnection = (LocalBridgeConnection) bridge.getBridgeConnection(BridgeConnectionType.LOCAL);
                    localConnection.getConnectionOptions().enableFastConnectionMode(bridgeID);
                    localConnection.connect();
                    Toast.makeText(MainControllerActivity.this, "Reconnecting successfully!", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTION_RESTORED:
                    // Inform user that connectivity issues have been solved.
                    break;
                case DISCONNECTED:
                    // Inform user he is disconnected with the bridge. This event will be sent when 'disconnect' is called on the connection
                    break;

                // Authentication
                case NOT_AUTHENTICATED:
                    // Inform user that he has to pushlink with the bridge
                    break;
                case LINK_BUTTON_NOT_PRESSED:
                    // Inform user the pushlink button has not been pressed
                    break;
                case AUTHENTICATED:
                    // Update UI to show authenticated state. No UI updates yet, because we have to wait for the bridge state events
                    break;

                // Add more cases
                default:
                    break;
            }
        }

        @Override
        public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> list) {

        }
    };

    private BridgeStateUpdatedCallback bridgeStateUpdateCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            switch (bridgeStateUpdatedEvent)
            {
                case INITIALIZED:
                    // The initialised event is sent when the bridge state is populated
                    // with the full configuration of the bridge for the first time.
                    // You can now access the bridge state
                    break;

                case LIGHTS_AND_GROUPS:
                    // Update UI with changed lights list
                    break;

                // Add more cases

                default:
                    // ...
                    break;
            }
        }
    };
}
