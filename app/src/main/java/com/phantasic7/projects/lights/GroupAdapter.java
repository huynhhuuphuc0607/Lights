package com.phantasic7.projects.lights;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightConfiguration;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;

import java.util.List;

;

/**
 * Created by HuynhHuu on 09-Mar-18.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private int resID;
    private List<Group> mGroups;
    private LightState mLightState;
    private CardView[] cardViews;
    private LightConfiguration mLightConfiguration;

    public GroupAdapter(Context context, int resource, List<Group> groups) {
        mContext = context;
        resID = resource;
        mGroups = groups;

        mLightState = new LightState();
        Log.i("Phantastic Lights", "" + groups.size());
        cardViews = new CardView[groups.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(resID, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Group group = mGroups.get(position);

        CardView groupCardView = holder.groupCardView;
        ImageView groupImageView = holder.groupImageView;
        TextView groupNameTextView = holder.groupNameTextView;
        final SeekBar groupBrightnessSeekBar = holder.groupBrightnessSeekBar;
        final Switch groupSwitch = holder.groupSwitch;
        final TextView groupBrightnessTextView = holder.groupBrightnessTextView;
        LinearLayout featureLinearLayout = holder.featureLinearLayout;
        final LinearLayout colorLinearLayout = holder.colorLinearLayout;
        final LinearLayout manageLinearLayout = holder.manageLinearLayout;
        LinearLayout roomEditLinearLayout = holder.roomEditLinearLayout;

        //imageView
        Log.i("Phantastic Lights", "Group class: " + group.getType());
        int drawableResource = RoomTypeAdapter.getDrawableResouceFromType(group.getType());
        groupImageView.setImageResource(drawableResource);
        groupImageView.setTag(drawableResource);

        //textView
        groupNameTextView.setText(group.getName());

        //switch
        groupSwitch.getThumbDrawable().setColorFilter(group.getColor(), PorterDuff.Mode.SRC_IN);
        groupSwitch.getTrackDrawable().setColorFilter(ColorUtils.setAlphaComponent(group.getColor(), 210), PorterDuff.Mode.SRC_IN);
        groupSwitch.setChecked(group.isOn());
        groupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LibraryLoader.toggleGroup((int) group.getGroupID(), b);
                groupBrightnessSeekBar.setEnabled(b);
                mGroups.get(position).setOn(b);
                colorLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor());
                manageLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor() + "|" + group.getBrightness());
            }
        });

        //seekbar
        final int brightness = group.getBrightness();
        groupBrightnessTextView.setText(brightness * 100 / 254 + "%");
        groupBrightnessSeekBar.getProgressDrawable()
                .setColorFilter(group.getColor(), PorterDuff.Mode.SRC_IN);
        groupBrightnessSeekBar.getThumb().setColorFilter(group.getColor(), PorterDuff.Mode.SRC_IN);
        groupBrightnessSeekBar.setProgress(brightness);

        //  final LightState lightState = new LightState();
        if (!group.isOn())
            groupBrightnessSeekBar.setEnabled(false);

        final List<LightPoint> lights = LibraryLoader.getLights(group.getLightIDs());
        final int size = lights.size();
        groupBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            LightPoint light;

            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
//                if(System.currentTimeMillis() - lastTime > 99) {
//                    lightState.setBrightness(i);
//                    groupBrightnessTextView.setText(i*100/254 + "%");
//                    hueGroup.apply(lightState, new BridgeResponseCallback() {
//                        @Override
//                        public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> list1) {
//
//                        }
//                    });
//                    lastTime = System.currentTimeMillis();
//                }
//                else
//                    lastBri = i;
                for (int j = 0; j < size; j++) {
                    light = lights.get(j);
                    mLightConfiguration = light.getLightConfiguration();
                    mLightState.setBrightness(i);
                    light.updateState(mLightState);
                }
                groupBrightnessTextView.setText(i * 100 / 254 + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                lightState.setBrightness(lastBri);
//
//                groupBrightnessTextView.setText(lastBri*100/254 + "%");
//                hueGroup.apply(lightState, new BridgeResponseCallback() {
//                    @Override
//                    public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> list1) {
//
//                    }
//                });
                mGroups.get(position).setBrightness(seekBar.getProgress());
                colorLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor());
                manageLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor() + "|" + group.getBrightness());
            }
        });
        colorLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor());
        manageLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + group.isOn() + "|" + group.getColor() + "|" + group.getBrightness());
        roomEditLinearLayout.setTag(position + "|" + group.getGroupID() + "|" + groupImageView.getTag() +
                "|" + group.getName() + "|" + groupCardView.getId() + "|" + groupNameTextView.getId());

        try {
            cardViews[position] = groupCardView;
        }catch (Exception e)
        {}
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView groupCardView;
        public ImageView groupImageView;
        public TextView groupNameTextView;
        public Switch groupSwitch;
        public SeekBar groupBrightnessSeekBar;
        public TextView groupBrightnessTextView;

        public LinearLayout featureLinearLayout;
        public LinearLayout colorLinearLayout;
        public LinearLayout manageLinearLayout;
        public LinearLayout roomEditLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            groupCardView = itemView.findViewById(R.id.groupCardView);
            groupImageView = itemView.findViewById(R.id.groupImageView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            groupSwitch = itemView.findViewById(R.id.groupSwitch);
            groupBrightnessSeekBar = itemView.findViewById(R.id.groupBrightnessSeekBar);
            groupBrightnessTextView = itemView.findViewById(R.id.groupBrightnessTextView);

            featureLinearLayout = itemView.findViewById(R.id.featureLinearLayout);
            colorLinearLayout = itemView.findViewById(R.id.colorLinearLayout);
            manageLinearLayout = itemView.findViewById(R.id.manageLinearLayout);
            roomEditLinearLayout = itemView.findViewById(R.id.roomEditLinearLayout);
        }
    }

    public void changeDrawableResource(int position, int drawableResource) {
        ImageView imageView = cardViews[position].findViewById(R.id.groupImageView);
        imageView.setImageResource(drawableResource);
    }

    public CardView getCardView(int position)
    {
        return cardViews[position];
    }
}
