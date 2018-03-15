package com.phantasic7.projects.lights;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    CircularProgressBar brightnessProgressBar;
    TextView groupNameTextView;
    TextView sceneNameTextView;
    TextView brightnessTextView;
    AppBarLayout groupAppBarLayout;
    RecyclerView groupRecyclerView;
    RecyclerView.Adapter mRecyclerViewAdapter;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;

    long groupId;
    String color;
    long sceneId;

    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        brightnessProgressBar = findViewById(R.id.brightnessProgressBar);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        sceneNameTextView = findViewById(R.id.sceneNameTextView);
        brightnessTextView = findViewById(R.id.brightnessTextView);
        groupAppBarLayout = findViewById(R.id.groupAppBarLayout);
        groupRecyclerView = findViewById(R.id.groupRecyclerView);

        mDBHelper = new DBHelper(this);

        Intent fromMainController = getIntent();
        groupId = fromMainController.getLongExtra("GroupID",0);
        sceneId = fromMainController.getLongExtra("SceneID",0);
        color = fromMainController.getStringExtra("Color");

        groupNameTextView.setText(mDBHelper.getGroup(groupId).getName());
        sceneNameTextView.setText(mDBHelper.getScene(sceneId).getName());
        groupAppBarLayout.setBackgroundColor(Color.parseColor(color));

        Utility.changeStatusBarColor(this, color);
        animateBrightnessProgressBar();
        createLightCardViews();
    }

    private void createLightCardViews()
    {
        List<Light> lights = mDBHelper.getAllLights(groupId);
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        groupRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerViewAdapter = new LightAdapter(this,R.layout.one_light_row, lights, color);
        groupRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void animateBrightnessProgressBar()
    {
//        ProgressBarAnimation anim = new ProgressBarAnimation(brightnessProgressBar, 0, 100);
//        anim.setDuration(1000);
//        brightnessProgressBar.startAnimation(anim);
        brightnessProgressBar.setColor(Color.WHITE);
        brightnessProgressBar.animateProgress(brightnessTextView, brightnessProgressBar.getProgress(), 75);
    }


}
