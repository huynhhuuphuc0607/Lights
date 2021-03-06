package com.phantasic7.projects.lights;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.Alert;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.domain.resource.*;

import java.util.List;

public class LightControllerActivity extends AppCompatActivity {

    CircularProgressBar brightnessProgressBar;
    TextView groupNameTextView;
    TextView noOfLightsTextView;
    TextView brightnessTextView;
    AppBarLayout groupAppBarLayout;
    RecyclerView groupRecyclerView;
    RecyclerView.Adapter mRecyclerViewAdapter;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;


    private int groupPosition;
    private int groupId;
    private com.philips.lighting.hue.sdk.wrapper.domain.resource.Group mGroup;
    private LightState mLightState;
    private int color;
    private BridgeState mBridgeState;
    private int size;
    private List<LightPoint> lights;
    private int groupBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightcontroller);

        brightnessProgressBar = findViewById(R.id.brightnessProgressBar);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        noOfLightsTextView = findViewById(R.id.noOfLightsTextView);
        brightnessTextView = findViewById(R.id.brightnessTextView);
        groupAppBarLayout = findViewById(R.id.groupAppBarLayout);
        groupRecyclerView = findViewById(R.id.groupRecyclerView);

        Intent intent = getIntent();
        color = intent.getIntExtra("color", 0);

        groupPosition = intent.getIntExtra("position", 0);
        groupId = intent.getIntExtra("groupid", 0);
        mBridgeState = LibraryLoader.mBridge.getBridgeState();
        mGroup = mBridgeState.getGroup("" + groupId);
      //  mGroup = LibraryLoader.getGroup(groupId);
        groupBrightness = intent.getIntExtra("brightness", 0);

        lights = LibraryLoader.getLightPoints(mGroup.getLightIds());
        size = lights.size();
        mLightState = new LightState();

        groupNameTextView.setText(mGroup.getName());
      //  sceneNameTextView.setText(mDBHelper.getScene(sceneId).getName());
        groupAppBarLayout.setBackgroundColor(color);

        Utils.changeStatusBarColor(this, color);
        animateBrightnessProgressBar();
        createLightCardViews();
    }

    private void createLightCardViews()
    {
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        groupRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerViewAdapter = new LightAdapter(this,R.layout.one_light_row, lights, color);
        groupRecyclerView.setAdapter(mRecyclerViewAdapter);
        int count = lights.size();
        noOfLightsTextView.setText(getResources().getQuantityString(R.plurals.numberOfLightsAvailable,count,count));
    }

    private void animateBrightnessProgressBar()
    {
//        ProgressBarAnimation anim = new ProgressBarAnimation(brightnessProgressBar, 0, 100);
//        anim.setDuration(1000);
//        brightnessProgressBar.startAnimation(anim);
        brightnessProgressBar.setColor(Color.WHITE);
        brightnessProgressBar.animateProgress(brightnessTextView, brightnessProgressBar.getProgress(), groupBrightness);
    }
}
