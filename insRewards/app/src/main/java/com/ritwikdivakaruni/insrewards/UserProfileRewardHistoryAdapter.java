package com.ritwikdivakaruni.insrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class UserProfileRewardHistoryAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {
    private static final String TAG = "UserProfileRewardHistoryAdapter";
    List<RewardRecords> rewardsArrayList = new ArrayList<>();
    private UserProfileActivity userProfileActivity;

    public UserProfileRewardHistoryAdapter(UserProfileActivity userProfileActivity, List<RewardRecords> rewardsArrayList) {
        this.userProfileActivity = userProfileActivity;
        this.rewardsArrayList = rewardsArrayList;
    }

    @NonNull
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rewardhistory_entry, viewGroup, false);
        itemView.setOnClickListener(userProfileActivity);
        itemView.setOnLongClickListener(userProfileActivity);
        return new UserProfileViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull UserProfileViewHolder userProfileViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        RewardRecords rewardRecords = rewardsArrayList.get(position);
        userProfileViewHolder.userPrDate.setText(rewardRecords.getDate());
        userProfileViewHolder.userPrName.setText(rewardRecords.getName());
        userProfileViewHolder.userPrText.setText(rewardRecords.getNotes());
        userProfileViewHolder.userPrPts.setText("" + rewardRecords.getValue());
    }

    public int getItemCount() {
        return rewardsArrayList.size();
    }
}
