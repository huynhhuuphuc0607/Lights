package com.phantasic7.projects.lights;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.HueHTTPResponse;
import com.philips.lighting.hue.sdk.wrapper.connection.RequestCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;

import java.util.List;

import static com.phantasic7.projects.lights.LibraryLoader.mBridge;

;

/**
 * Created by HuynhHuu on 09-Mar-18.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private int resID;
    private List<Group> mGroups;
    private long lastTime = 0;
    private int lastBri;

    public GroupAdapter(Context context, int resource, List<Group> groups) {
        mContext = context;
        resID = resource;
        mGroups = groups;
        //  mGroups.remove(0);
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

        ImageView groupImageView = holder.groupImageView;
        TextView groupNameTextView = holder.groupNameTextView;
        final SeekBar groupBrightnessSeekBar = holder.groupBrightnessSeekBar;
        final Switch groupSwitch = holder.groupSwitch;

        //imageView
        groupImageView.setImageResource(R.drawable.ic_bedroom);
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
            }
        });

        //seekbar
        final int brightness = group.getBrightness();
        groupBrightnessSeekBar.getProgressDrawable()
                .setColorFilter(group.getColor(), PorterDuff.Mode.SRC_IN);
        groupBrightnessSeekBar.getThumb().setColorFilter(group.getColor(), PorterDuff.Mode.SRC_IN);
        groupBrightnessSeekBar.setProgress(brightness);
        final com.philips.lighting.hue.sdk.wrapper.domain.resource.Group hueGroup =
                mBridge.getBridgeState().getGroups().get((int) group.getGroupID());
        final LightState lightState = new LightState();
        if(!group.isOn())
            groupBrightnessSeekBar.setEnabled(false);

        groupBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int i, boolean b) {
                if(System.currentTimeMillis() - lastTime > 99) {
                    lightState.setBrightness(i * 2);

                    hueGroup.apply(lightState, new BridgeResponseCallback() {
                        @Override
                        public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> list1) {

                        }
                    });
                    lastTime = System.currentTimeMillis();
                }
                else
                    lastBri = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                lightState.setBrightness(lastBri * 2);

                hueGroup.apply(lightState, new BridgeResponseCallback() {
                    @Override
                    public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> list1) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView groupImageView;
        public TextView groupNameTextView;
        public Switch groupSwitch;
        public SeekBar groupBrightnessSeekBar;

        public ViewHolder(View itemView) {
            super(itemView);
            groupImageView = itemView.findViewById(R.id.groupImageView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            groupSwitch = itemView.findViewById(R.id.groupSwitch);
            groupBrightnessSeekBar = itemView.findViewById(R.id.groupBrightnessSeekBar);
        }
    }
}
