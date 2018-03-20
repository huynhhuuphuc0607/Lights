package com.phantasic7.projects.lights;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by HuynhHuu on 19-Mar-18.
 */

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {

    private String[] names;
    private int[] drawableResources;
    private int size;
    private int chosenRes;
    private Context context;

    public RoomTypeAdapter(Context context, int chosenRes)
    {
        size = 19;
        this.chosenRes = chosenRes;
        this.context = context;

        names = new String[]{"Living room","Kitchen","Dining","Bedroom","Kid's bedroom","Bathroom",
                "Nursery", "Recreation room","Office","Gym","Hallway","Toilet","Front door","Garage",
                "Terrace","Garden","Driveway","Carport","Other"};

        drawableResources = new int[]{R.drawable.ic_living,R.drawable.ic_kitchen,R.drawable.ic_dining,
                R.drawable.ic_bedroom,R.drawable.ic_kids_bedroom, R.drawable.ic_bathroom,
                R.drawable.ic_nursery, R.drawable.ic_recreation, R.drawable.ic_office,
                R.drawable.ic_gym, R.drawable.ic_hallway, R.drawable.ic_toilet, R.drawable.ic_frontdoor,
                R.drawable.ic_garage, R.drawable.ic_terrace, R.drawable.ic_garden, R.drawable.ic_driveway,
                R.drawable.ic_carport, R.drawable.ic_other};
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.one_room_type_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView roomTypeTextView = holder.roomTypeTextView;
        ImageView roomTypeImageView = holder.roomTypeImageView;
        LinearLayout roomTypeLinearLayout = holder.roomTypeLinearLayout;

        roomTypeTextView.setText(names[position]);
        roomTypeImageView.setImageResource(drawableResources[position]);

        if(position%2 == 1)
            roomTypeLinearLayout.setBackgroundColor(Color.parseColor("#dddddd"));
        if(chosenRes == drawableResources[position])
            roomTypeLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView roomTypeTextView;
        public ImageView roomTypeImageView;
        public LinearLayout roomTypeLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            roomTypeImageView = itemView.findViewById(R.id.roomTypeImageView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            roomTypeLinearLayout = itemView.findViewById(R.id.roomTypeLinearLayout);
        }
    }

    private void highlightChosenItem(int chosenRes)
    {}
}
