package com.phantasic7.projects.lights;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuynhHuu on 20-Feb-18.
 */

public class LightAdapter extends ArrayAdapter {
    Context mContext;
    int resID;
    List<Light> mLights;

    public LightAdapter(@NonNull Context context, int resource, @NonNull List<Light> lightList) {
        super(context, resource, lightList);
        mContext = context;
        resID = resource;
        mLights = lightList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  v = inflater.inflate(resID,null);

        Light light = mLights.get(position);
        TextView lightTextView = v.findViewById(R.id.lightTextView);
        lightTextView.setText(light.getName());

        return v;
    }
}
