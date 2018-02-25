package com.phantasic7.projects.lights;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HuynhHuu on 20-Feb-18.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder> {
    Context mContext;
    int resID;
    List<Light> mLights;
    String color;

    public LightAdapter(Context context, int resID, List<Light> lightList, String color) {
        mContext = context;
        this.resID = resID;
        mLights = lightList;
        this.color = color;
    }

    @Override
    public LightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resID,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LightAdapter.ViewHolder holder, int position) {
        Light light = mLights.get(position);

        TextView lightTextView = holder.lightTextView;
        lightTextView.setText(light.getName());
        lightTextView.setTextColor(Color.parseColor(color));

        ImageView lightImageView = holder.lightImageView;
        lightImageView.setImageResource(R.drawable.light);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        holder.itemView.startAnimation(fadeInAnimation);
    }

    @Override
    public int getItemCount() {
        return mLights.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lightTextView;
        public ImageView lightImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            lightTextView = itemView.findViewById(R.id.lightTextView);
            lightImageView = itemView.findViewById(R.id.lightImageView);
        }
    }
}