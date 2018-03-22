package com.phantasic7.projects.lights;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.slider.LightnessSlider;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightConfiguration;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import java.util.List;

public class ColorControllerActivity extends AppCompatActivity {
    public static int GROUP_REQUEST_CODE = 1111;
    public static int LIGHT_REQUEST_CODE = 2222;
    private ColorPickerView colorPickerView;
    private int groupPosition;
    private int groupId;
    private com.philips.lighting.hue.sdk.wrapper.domain.resource.Group mGroup;
    private LightState mLightState;
    private LightConfiguration mLightConfiguration;
    private int color;
    private BridgeState mBridgeState;
    private int size;
    private List<LightPoint> lights;
    private Intent intent;
    private boolean forGroup;

    private int r, g, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_controller);
        Utils.changeStatusBarIconColor(this, true);
        colorPickerView = findViewById(R.id.colorPickerView);
        intent = getIntent();
        forGroup = intent.getBooleanExtra("group?",true);
        color = intent.getIntExtra("color",0);

        colorPickerView.setInitialColor(color,false);

        if(forGroup) {
            groupPosition = intent.getIntExtra("position",0);
            groupId = intent.getIntExtra("groupid", 0);
            mBridgeState = LibraryLoader.mBridge.getBridgeState();
            mGroup = mBridgeState.getGroup("" + groupId);

            lights = LibraryLoader.getLights(mGroup.getLightIds());
            size = lights.size();
            mLightState = new LightState();
        }

        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                for (int j = 0; j < size; j++) {
                    color = i;
                    r = Color.red(i);
                    g = Color.green(i);
                    b = Color.blue(i);

                    LightPoint light = lights.get(j);
                    mLightConfiguration = light.getLightConfiguration();
                    mLightState.setXYWithColor(new HueColor(new HueColor.RGB(r, g, b),
                            mLightConfiguration.getModelIdentifier(),
                            mLightConfiguration.getSwVersion()));
                    light.updateState(mLightState);
                }
            }
        });
    }

    public void applyColor(View v)
    {
        Intent backIntent;
        if(forGroup)
        {
            backIntent = new Intent(this,MainControllerActivity.class);
            backIntent.putExtra("groupid", groupId);
            backIntent.putExtra("color",color);
            backIntent.putExtra("position", groupPosition);

            setResult(RESULT_OK,backIntent);

        }
        finish();
    }
}
