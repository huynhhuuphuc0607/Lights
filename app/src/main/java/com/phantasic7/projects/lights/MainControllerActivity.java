package com.phantasic7.projects.lights;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.Alert;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class MainControllerActivity extends AppCompatActivity {

    List<Group> groupsList;
    FloatingActionButton fab;
    RecyclerView groupRecyclerView;
    RecyclerView unreachableLightsRecyclerView;
    CardView unreachableLightsCardView;

    private BridgeDiscovery bridgeDiscovery;
    private List<BridgeDiscoveryResult> bridgeDiscoveryResults;

    private GroupAdapter mGroupAdapter;
    private LightAdapter mLightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincontroller);

        unreachableLightsCardView = findViewById(R.id.unreachableLightsCardView);
        unreachableLightsRecyclerView = findViewById(R.id.unreachableLightsRecyclerView);
        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        fab = findViewById(R.id.fab);

        fab.setContentCoverEnabled(true);
        fab.setContentCoverColour(0xffffffff);
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
                        Intent intent = new Intent(MainControllerActivity.this, RoomEditControllerActivity.class);
                        intent.putExtra("drawableRes", R.drawable.ic_living);
                        intent.putExtra("name", "RenameYourGroup");
                        intent.putExtra("new", true);
                        startActivity(intent);
                        break;
                }
                return true;
            }

            @Override
            public float fabRotationDegrees() {
                return 45;
            }
        });

        groupsList = LibraryLoader.getGroups();

        Utils.changeStatusBarIconColor(this, true);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupAdapter = new GroupAdapter(this, R.layout.one_group_row, groupsList);
        groupRecyclerView.setAdapter(mGroupAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUnreachableLights();
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_switch:
                boolean isOn = false;
                for (Group group : groupsList)
                    if (group.isOn()) {
                        isOn = true;
                        break;
                    }
                for (Group group : groupsList) {
                    LibraryLoader.toggleGroup(group.getGroupID(), !isOn);
                    group.setOn(!isOn);
                }
                mGroupAdapter.notifyDataSetChanged();
                break;

            case R.id.menu_reload:
                loadUnreachableLights();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadUnreachableLights() {
        List<LightPoint> lights = LibraryLoader.getLights();
        List<LightPoint> unreachables = new ArrayList<>();

        for (LightPoint light : lights) {
            if (!light.getLightState().isReachable())
                unreachables.add(light);
        }

        if (unreachables.size() > 0) {
            unreachableLightsCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_up));
            unreachableLightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mLightAdapter = new LightAdapter(this, R.layout.one_light_row, unreachables, Color.BLACK);
            unreachableLightsCardView.setVisibility(View.VISIBLE);
            unreachableLightsRecyclerView.setAdapter(mLightAdapter);
        } else {
            if (unreachableLightsCardView.getVisibility() == View.VISIBLE)
                unreachableLightsCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shrink));
            unreachableLightsCardView.setVisibility(View.GONE);
        }
    }

    public void gotoColorController(View v) {
        Intent intent = new Intent(MainControllerActivity.this, ColorControllerActivity.class);
        String tag = (String) v.getTag();
        String[] minitags = tag.split("\\|");
        if (Boolean.parseBoolean(minitags[2])) {
            intent.putExtra("position", Integer.parseInt(minitags[0]));
            intent.putExtra("groupid", Integer.parseInt(minitags[1]));
            intent.putExtra("color", Integer.parseInt(minitags[3]));
            intent.putExtra("group?", true);
            startActivityForResult(intent, ColorControllerActivity.GROUP_REQUEST_CODE);
        } else
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
        if (requestCode == ColorControllerActivity.GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", 0);
            groupsList.get(position).setColor(data.getIntExtra("color", 0));
            mGroupAdapter.notifyDataSetChanged();
        } else if (requestCode == RoomEditControllerActivity.EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra("new", false)) {
                groupsList = LibraryLoader.getGroups();
            } else {
                int position = data.getIntExtra("position", 0);
                groupsList.get(position).setName(data.getStringExtra("name"));
                String type = data.getStringExtra("type");
                if (type != null)
                    groupsList.get(position).setType(type);
            }
            mGroupAdapter.notifyDataSetChanged();
        }
    }

    public void gotoLightController(View v) {
        Intent intent = new Intent(MainControllerActivity.this, LightControllerActivity.class);
        String tag = (String) v.getTag();
        String[] minitags = tag.split("\\|");
        intent.putExtra("position", Integer.parseInt(minitags[0]));
        intent.putExtra("groupid", Integer.parseInt(minitags[1]));
        intent.putExtra("color", Integer.parseInt(minitags[3]));
        intent.putExtra("group?", true);
        intent.putExtra("brightness", Integer.parseInt(minitags[4]));
        startActivity(intent);
    }

    public void gotoRoomEditController(View v) {
        Intent intent = new Intent(MainControllerActivity.this, RoomEditControllerActivity.class);
        String tag = (String) v.getTag();
        String[] minitags = tag.split("\\|");
        intent.putExtra("position", Integer.parseInt(minitags[0]));
        intent.putExtra("groupid", Integer.parseInt(minitags[1]));
        intent.putExtra("drawableRes", Integer.parseInt(minitags[2]));
        intent.putExtra("name", minitags[3]);
        View view = mGroupAdapter.getCardView(Integer.parseInt(minitags[0])).findViewById(Integer.parseInt(minitags[4]));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainControllerActivity.this,
                Pair.create(view.findViewById(Integer.parseInt(minitags[5])), getString(R.string.transition_name_group)));

        //noinspection RestrictedApi
        startActivityForResult(intent, RoomEditControllerActivity.EDIT_REQUEST_CODE, options.toBundle());
    }
}