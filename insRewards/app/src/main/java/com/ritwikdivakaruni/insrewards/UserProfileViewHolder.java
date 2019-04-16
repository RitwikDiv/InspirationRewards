package com.ritwikdivakaruni.insrewards;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView userPrDate;
    public TextView userPrName;
    public TextView userPrPts;
    public TextView userPrText;
    public ImageView userPrImge;

    public UserProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        userPrDate = itemView.findViewById(R.id.rewardHistoyDate_view);
        userPrName = itemView.findViewById(R.id.rewardHistoyName_view);
        userPrPts = itemView.findViewById(R.id.rewardHistoyPts_view);
        userPrText = itemView.findViewById(R.id.rewardHistoyTxt_view);
        userPrImge = itemView.findViewById(R.id.rewardHistoyImg_view);
    }
}