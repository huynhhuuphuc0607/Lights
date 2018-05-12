package com.phantasic7.projects.lights;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.domain.clip.Alert;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;

import java.util.List;

/**
 * Created by HuynhHuu on 20-Feb-18.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder> {
    Context mContext;
    int resID;
    List<LightPoint> mLights;
    int color;

    public LightAdapter(Context context, int resID, List<LightPoint> lightList, int color) {
        mContext = context;
        this.resID = resID;
        mLights = lightList;
        this.color = color;
    }

    @Override
    public LightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resID, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LightAdapter.ViewHolder holder, int position) {
        final LightPoint light = mLights.get(position);

        LinearLayout lightLinearLayout = holder.lightLinearLayout;
        lightLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean isOn = light.getLightState().isOn();
                light.updateState(light.getLightState().setAlert(Alert.SELECT));
                Log.i(LibraryLoader.TAG, "blink blink " + light.getName() + "Before blinking: "+ isOn);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        light.getLightState().setAlert(Alert.NONE);
                    }
                }, 1000);
            }
        });

        TextView lightTextView = holder.lightTextView;
        lightTextView.setText(light.getName());
        lightTextView.setTextColor(color);

        ImageView lightImageView = holder.lightImageView;
        lightImageView.setImageResource(R.drawable.light);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        holder.itemView.startAnimation(fadeInAnimation);
    }

    @Override
    public int getItemCount() {
        return mLights.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lightLinearLayout;
        public TextView lightTextView;
        public ImageView lightImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            lightLinearLayout = itemView.findViewById(R.id.lightLinearLayout);
            lightTextView = itemView.findViewById(R.id.lightTextView);
            lightImageView = itemView.findViewById(R.id.lightImageView);
        }
    }
}
