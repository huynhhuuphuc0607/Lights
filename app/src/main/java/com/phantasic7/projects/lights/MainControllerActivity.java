package com.phantasic7.projects.lights;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainControllerActivity extends AppCompatActivity {

    LinearLayout groupsLinearLayout;
    List<Group> groupsList;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincontroller);

        groupsLinearLayout = findViewById(R.id.groupsLinearLayout);

        dbHelper = new DBHelper(this);
        groupsList = new ArrayList<>();
        createGroups();
        createGroupCardViews();
    }

    private void createGroups() {
        dbHelper.addLight(new Light("AAAAAA", "Bed","1|2"));
        dbHelper.addLight(new Light("BBBBBB", "Desk","1|2"));
        dbHelper.addLight(new Light("CCCCCC", "Dinner table","2|3"));
        dbHelper.addLight(new Light("DDDDDD", "Sink","4"));

        dbHelper.addGroup(new Group(1, "Bedroom","#FFFF00",1, DBHelper.createStringListfromString("AAAAAA|BBBBBB")));
        dbHelper.addGroup(new Group(2, "Party", "#00FFFF", 2, DBHelper.createStringListfromString("AAAAAA|BBBBBB|CCCCCC")));
        dbHelper.addGroup(new Group(3, "Kitchen","#00FFFF", 2, DBHelper.createStringListfromString("CCCCCC|DDDDDD")));

        dbHelper.addScene(new Scene(1, "Cozy", "#FFFF00", 75));
        dbHelper.addScene(new Scene(2, "Artic Cool", "#00FFFF", 75));

        groupsList = dbHelper.getAllGroups();
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
            groupName.setLayoutParams(layoutParams);
            groupName.setText(group.getName());

            final TextView sceneName = new TextView(this);
            sceneName.setLayoutParams(layoutParams);
            sceneName.setText("Scene: " + dbHelper.getScene(group.getSceneID()).getName() + " Status: On");

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

}
