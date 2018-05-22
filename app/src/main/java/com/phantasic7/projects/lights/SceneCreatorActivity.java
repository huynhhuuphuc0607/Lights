package com.phantasic7.projects.lights;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightConfiguration;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import java.util.ArrayList;
import java.util.List;

public class SceneCreatorActivity extends AppCompatActivity {

    private TextView sceneNameTextView;
    private RecyclerView sceneSceneRecyclerView;
    private SceneAdapter mSceneAdapter;

    //Todo: For demonstration purposes only, we will use 2 checkboxes instead of a recycler view to save time
    private CheckBox checkboxGroup1;
    private CheckBox checkboxGroup2;
    private int[] colors;

    private List<Group> groupsList;

    private String nameScene;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_creator);

        sceneNameTextView = findViewById(R.id.sceneNameTextView);
        sceneSceneRecyclerView = findViewById(R.id.sceneSceneRecyclerView);
        checkboxGroup1 = findViewById(R.id.checkboxGroup1);
        checkboxGroup2 = findViewById(R.id.checkboxGroup2);

        Utils.changeStatusBarIconColor(this, true);

        groupsList = new ArrayList<>();
        groupsList.add((Group)getIntent().getParcelableExtra("group 1"));
        groupsList.add((Group)getIntent().getParcelableExtra("group 2"));
        colors = new int[]{groupsList.get(0).getColor(), groupsList.get(1).getColor()};

        initiateCheckBoxes();
        showBuiltInScenes();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LightConfiguration mLightConfiguration;
        LightState mLightState = new LightState();
        int size = groupsList.size();

        for(int i = 0; i < size; i++) {
            List<LightPoint> lights =
                    LibraryLoader.getLightPoints(LibraryLoader.mBridge.getBridgeState().getGroup("" + groupsList.get(i).getGroupID()).getLightIds());
            for (LightPoint lightPoint : lights) {
                int color = colors[i];

                mLightConfiguration = lightPoint.getLightConfiguration();
                mLightState.setXYWithColor(new HueColor(new HueColor.RGB(Color.red(color), Color.green(color), Color.blue(color)),
                        mLightConfiguration.getModelIdentifier(),
                        mLightConfiguration.getSwVersion()));
                lightPoint.updateState(mLightState);
            }
        }
    }

    private void showBuiltInScenes() {
        sceneSceneRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mSceneAdapter = new SceneAdapter(this, new RecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Palette palette = mSceneAdapter.getPaletteFromNewScene(position);
                LightConfiguration mLightConfiguration;
                LightState mLightState = new LightState();

                for(Group group : groupsList)
                {
                    List<LightPoint> lights =
                            LibraryLoader.getLightPoints(LibraryLoader.mBridge.getBridgeState().getGroup("" + group.getGroupID()).getLightIds());
                    for(LightPoint lightPoint : lights)
                    {
                        int color = palette.getVibrantColor(0x000000);

                        mLightConfiguration = lightPoint.getLightConfiguration();
                        mLightState.setXYWithColor(new HueColor(new HueColor.RGB(Color.red(color), Color.green(color), Color.blue(color)),
                                mLightConfiguration.getModelIdentifier(),
                                mLightConfiguration.getSwVersion()));
                        lightPoint.updateState(mLightState);
                    }
                }
            }
        });
        sceneSceneRecyclerView.setAdapter(mSceneAdapter);
    }

    private void initiateCheckBoxes() {
        //Todo: For demonstration purposes only, we will use 2 checkboxes instead of a recycler view to save time
        checkboxGroup1.setText(groupsList.get(0).getName());
        checkboxGroup2.setText(groupsList.get(1).getName());
    }

    public void rename(View v)
    {
        final EditText renameEditText = new EditText(SceneCreatorActivity.this);
        renameEditText.setHint("Ex: Cool blue");

        //noinspection RestrictedApi
        new AlertDialog.Builder(SceneCreatorActivity.this).setTitle("Rename").setView(renameEditText, 16, 16, 16, 16)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nameScene = renameEditText.getText().toString();
                        sceneNameTextView.setText(nameScene);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void applyScene(View v)
    {
        new AlertDialog.Builder(SceneCreatorActivity.this).setTitle("Coming soon...")
                .setMessage("Sorry! The feature is not available right now")
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }
}
