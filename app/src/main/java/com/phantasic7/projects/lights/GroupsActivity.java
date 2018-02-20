package com.phantasic7.projects.lights;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    TextView groupNameTextView;
    TextView sceneNameTextView;
    AppBarLayout groupAppBarLayout;
    ListView lightsListView;
    ImageView lightImageView;
    TextView lightTextView;

    long groupId;
    String color;
    long sceneId;

    DBHelper mDBHelper;
    LightAdapter mLightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        sceneNameTextView = findViewById(R.id.sceneNameTextView);
        groupAppBarLayout = findViewById(R.id.groupAppBarLayout);
        lightsListView = findViewById(R.id.lightsListView);

        lightTextView = findViewById(R.id.lightTextView);

        mDBHelper = new DBHelper(this);

        Intent fromMainController = getIntent();
        groupId = fromMainController.getLongExtra("GroupID",0);
        sceneId = fromMainController.getLongExtra("SceneID",0);
        color = fromMainController.getStringExtra("Color");

        groupNameTextView.setText(mDBHelper.getGroup(groupId).getName());
        sceneNameTextView.setText(mDBHelper.getScene(sceneId).getName());
        groupAppBarLayout.setBackgroundColor(Color.parseColor(color));

        createLightCardViews();
    }

    private void createLightCardViews()
    {
        List<Light> lights = mDBHelper.getAllLights(groupId);
        mLightAdapter = new LightAdapter(this, R.layout.light_one_row, lights);
        lightsListView.setAdapter(mLightAdapter);
    }
}
