package com.belfoapps.qarib.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.pojo.Hype;

import java.util.ArrayList;

public class OnBoardingPagerAdapter extends RecyclerView.Adapter<OnBoardingPagerAdapter.ViewHolder> {
    private static final String TAG = "MainPagerAdapter";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private Context context;
    private ArrayList<Hype> hypes;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    public OnBoardingPagerAdapter(Context context, ArrayList<Hype> hypes) {
        this.context = context;
        this.hypes = hypes;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.hype_layout, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(hypes.get(position).getTitle());
        holder.description.setText(hypes.get(position).getDescription());
        holder.image.setImageResource(hypes.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        if (hypes == null) return 0;
        else return hypes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);

        }
    }
}
