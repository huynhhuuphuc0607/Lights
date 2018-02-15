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

public class MainControllerActivity extends AppCompatActivity {

    LinearLayout groupsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincontroller);

        groupsLinearLayout = findViewById(R.id.groupsLinearLayout);
        createGroupCardViews();
    }

    private void createGroupCardViews() {
        int numCardViews = 3;
        String[] colors = new String[]{"#abcdef", "#123345", "#ab2301"};
        String[] scenes = new String[]{"Cozy night", "Savannah warm", "Cool blue"};

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);

        for (int i = 0; i < numCardViews; i++) {
            CardView cv = new CardView(this);
            cv.setLayoutParams(layoutParams);
            // Set CardView corner radius
            cv.setRadius(9);
            // Set cardView content padding
            cv.setContentPadding(15, 15, 15, 15);
            // Set a background color for CardView
            cv.setCardBackgroundColor(Color.parseColor(colors[i]));
            // Set the CardView maximum elevation
            cv.setMaxCardElevation(15);
            // Set CardView elevation
            cv.setCardElevation(9);
            final TextView groupName = new TextView(this);
            groupName.setLayoutParams(layoutParams);
            groupName.setText("Bedroom");

            final TextView sceneName = new TextView(this);
            sceneName.setLayoutParams(layoutParams);
            sceneName.setText(scenes[i] + " Status: On");

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
            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> pair1 = Pair.create((View)groupName, getString(R.string.transition_name_group));
                        Pair<View, String> pair2 = Pair.create((View)sceneName, getString(R.string.transition_name_scene));
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(MainControllerActivity.this, pair1, pair2);
                        startActivity(new Intent(MainControllerActivity.this, GroupsActivity.class),options.toBundle());

                    } else
                        startActivity(new Intent(MainControllerActivity.this, GroupsActivity.class));
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
