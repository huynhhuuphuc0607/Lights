package com.phantasic7.projects.lights;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RoomEditControllerActivity extends AppCompatActivity {

    private TextView roomTypeNameTextView;
    private RecyclerView roomtypeRecyclerView;

    private int drawableRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_controller);

        roomTypeNameTextView = findViewById(R.id.roomTypeNameTextView);
        roomtypeRecyclerView = findViewById(R.id.roomtypeRecyclerView);

        Intent intent = getIntent();
        drawableRes = intent.getIntExtra("drawableRes",0);
        roomTypeNameTextView.setText(intent.getStringExtra("name"));

        roomtypeRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        roomtypeRecyclerView.setAdapter(new RoomTypeAdapter(RoomEditControllerActivity.this,drawableRes));
    }
}
