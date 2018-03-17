package com.phantasic7.projects.lights;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.HueHTTPResponse;
import com.philips.lighting.hue.sdk.wrapper.connection.RequestCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class MainControllerActivity extends AppCompatActivity {

    LinearLayout groupsLinearLayout;
    List<Group> groupsList;
    DBHelper dbHelper;
    FloatingActionButton fab;
    RecyclerView groupRecyclerView;
    RecyclerView.LayoutManager mGroupRecyclerViewLayoutManager;

    private BridgeDiscovery bridgeDiscovery;
    private List<BridgeDiscoveryResult> bridgeDiscoveryResults;


    private GroupAdapter mGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincontroller);

        groupRecyclerView = findViewById(R.id.groupRecyclerView);
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
                switch (position) {
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
        groupsList = LibraryLoader.getGroups();

        Utility.changeStatusBarIconColor(this, true);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupAdapter = new GroupAdapter(this, R.layout.one_group_row, groupsList);
        groupRecyclerView.setAdapter(mGroupAdapter);
    }

    public void gotoColorController(View v)
    {
        Intent intent = new Intent(MainControllerActivity.this,ColorControllerActivity.class);
        String tag = (String)v.getTag();
        String[] minitags = tag.split("\\|");
        if(Boolean.parseBoolean(minitags[2])) {
            intent.putExtra("position", Integer.parseInt(minitags[0]));
            intent.putExtra("groupid", Integer.parseInt(minitags[1]));

            intent.putExtra("group?", true);
            startActivityForResult(intent, ColorControllerActivity.GROUP_REQUEST_CODE);
        }
        else
            new AlertDialog.Builder(MainControllerActivity.this)
                    .setTitle("Help").setMessage("Turn it on before changing color.")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ColorControllerActivity.GROUP_REQUEST_CODE && resultCode == RESULT_OK)
        {
            int position = data.getIntExtra("position",0);

            groupsList.get(position).setColor(data.getIntExtra("color",0));
            mGroupAdapter.notifyDataSetChanged();
        }
    }
}