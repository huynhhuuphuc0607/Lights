package com.phantasic7.projects.lights;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static java.lang.Math.pow;

/**
 * Created by HuynhHuu on 22-Feb-18.
 */

public class Utility {
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public static int getContrastColor(int color)
    {
        return Color.rgb(255-Color.red(color),
                255-Color.green(color),
                255-Color.blue(color));
    }

    public static int getAccentColor(Context context, int color)
    {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (color, value, true);
        return value.data;
    }

    public static void changeStatusBarIconColor(Activity myActivity, boolean changeToBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = myActivity.getWindow().getDecorView();
            if (changeToBlack) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // We want to change tint color to white again.
                // You can also record the flags in advance so that you can turn UI back completely if
                // you have set other flags before, such as translucent or full screen.
                decor.setSystemUiVisibility(0);
            }
        }
    }

    public static void changeStatusBarColor(Activity mActivity, String color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public static int XYToRGBColor(float xCor, float yCor, int brightness)
    {
        float x = xCor; // the given x value
        float y = yCor; // the given y value
        float z = 1.0f - x - y;
        float Y = brightness; // The given brightness value
        float X = (Y / y) * x;
        float Z = (Y / y) * z;

        float r =  X * 1.656492f - Y * 0.354851f - Z * 0.255038f;
        float g = -X * 0.707196f + Y * 1.655397f + Z * 0.036152f;
        float b =  X * 0.051713f - Y * 0.121364f + Z * 1.011530f;

        r = r <= 0.0031308f ? 12.92f * r : (1.0f + 0.055f) * (float)pow(r, (1.0f / 2.4f)) - 0.055f;
        g = g <= 0.0031308f ? 12.92f * g : (1.0f + 0.055f) * (float)pow(g, (1.0f / 2.4f)) - 0.055f;
        b = b <= 0.0031308f ? 12.92f * b : (1.0f + 0.055f) * (float)pow(b, (1.0f / 2.4f)) - 0.055f;

        int R = Math.round(255 * r);
        int G = Math.round(255 * g);
        int B = Math.round(255 * b);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
