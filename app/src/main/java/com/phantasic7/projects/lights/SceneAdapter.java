package com.phantasic7.projects.lights;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HuynhHuu on 22-May-18.
 */

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {
    private static String[] names = new String[]{"Cherry Blossom", "Glacier", "Sunset", "Forest", "Deep Ocean"};
    private static int[] drawableResources = new int[]{R.drawable.scene_sakura, R.drawable.scene_glacier,
            R.drawable.scene_sunset, R.drawable.scene_forest, R.drawable.scene_deep_ocean};

    private Context context;
    private RecyclerViewItemClickListener listener;

    public SceneAdapter(Context context, RecyclerViewItemClickListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SceneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.one_scene_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickListener(view, viewHolder.getLayoutPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SceneAdapter.ViewHolder holder, int position) {
        CircleImageView sceneCircularImageView = holder.sceneCircularImageView;
        TextView sceneNameTextView = holder.sceneNameTextView;

        sceneCircularImageView.setImageResource(drawableResources[position]);
        sceneNameTextView.setText(names[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView sceneCircularImageView;
        public TextView sceneNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            sceneCircularImageView = itemView.findViewById(R.id.sceneCircularImageView);
            sceneNameTextView = itemView.findViewById(R.id.sceneNameTextView);
        }
    }

    public Palette getPaletteFromNewScene(int newPosition) {
        Palette palette = null;
        Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResources[newPosition]);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            palette = Palette.from(myBitmap).generate();
        }
        return palette;
    }
}
