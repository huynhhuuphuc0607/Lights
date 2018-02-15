package com.phantasic7.projects.lights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GroupsActivity extends AppCompatActivity {

    TextView groupNameTextView;
    TextView sceneNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        sceneNameTextView = findViewById(R.id.sceneNameTextView);
    }
}
