package com.phantasic7.projects.lights;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;

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
}
