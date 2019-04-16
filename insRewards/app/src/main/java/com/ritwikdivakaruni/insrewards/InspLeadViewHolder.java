package com.ritwikdivakaruni.insrewards;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InspLeadViewHolder extends RecyclerView.ViewHolder {
    public TextView inspLeadName;
    public TextView inspLeadPoints;
    public TextView inspLeadPosDept;
    public ImageView inspLeadImge;

    public InspLeadViewHolder(@NonNull View itemView) {
        super(itemView);
        inspLeadName = itemView.findViewById(R.id.inspLeadName);
        inspLeadPosDept = itemView.findViewById(R.id.inspLeadPosDept);
        inspLeadPoints = itemView.findViewById(R.id.inspLeadPoints);
        inspLeadImge = itemView.findViewById(R.id.inspLeadImge);
    }
}
