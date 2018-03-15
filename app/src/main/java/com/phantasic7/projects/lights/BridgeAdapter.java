package com.phantasic7.projects.lights;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;

import java.util.List;

/**
 * Created by HuynhHuu on 09-Mar-18.
 */

public class BridgeAdapter extends ArrayAdapter {

    private Context mContext;
    private int resID;
    private List<BridgeDiscoveryResult> mBridgeDiscoveryResults;

    public BridgeAdapter(@NonNull Context context, int resource, @NonNull List<BridgeDiscoveryResult> bridgeDiscoveryResults) {
        super(context, resource, bridgeDiscoveryResults);
        mContext = context;
        resID = resource;
        mBridgeDiscoveryResults = bridgeDiscoveryResults;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        BridgeDiscoveryResult result = mBridgeDiscoveryResults.get(position);

        if(convertView == null)
            convertView = inflater.inflate(resID,null);

        TextView bridgeNameTextView = convertView.findViewById(R.id.bridgeNameTextView);
        bridgeNameTextView.setText(result.getIP());

        return convertView;
    }
}
