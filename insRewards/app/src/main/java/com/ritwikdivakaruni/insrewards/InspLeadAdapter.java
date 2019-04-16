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
import java.util.List;

public class InspLeadAdapter extends RecyclerView.Adapter<InspLeadViewHolder> {
    private static final String TAG = "InspirationRewardsLeaderboardAdapter";
    private List<InspLeaderBoardBean> inspLeaderArrayList;
    private InspLeaderboardActivity inspLeaderboardActivity;
    public InspLeadAdapter(InspLeaderboardActivity inspLeaderboardActivity, List<InspLeaderBoardBean> inspLeaderArrayList) {
        this.inspLeaderArrayList = inspLeaderArrayList;
        this.inspLeaderboardActivity = inspLeaderboardActivity;
    }

    @NonNull
    @Override
    public InspLeadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.leaderboard_entry, viewGroup, false);
        itemView.setOnClickListener(inspLeaderboardActivity);
        itemView.setOnLongClickListener(inspLeaderboardActivity);
        return new InspLeadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InspLeadViewHolder inspLeadViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        InspLeaderBoardBean inspLeaderBean = inspLeaderArrayList.get(position);
        inspLeadViewHolder.inspLeadName.setText(inspLeaderBean.getLastName() + ", " + inspLeaderBean.getFirstName());
        inspLeadViewHolder.inspLeadPosDept.setText(inspLeaderBean.getPosition() + ", " + inspLeaderBean.getDepartment());
        inspLeadViewHolder.inspLeadPoints.setText(""+inspLeaderBean.rewardPtAward);
        String imgString = inspLeaderBean.getImageBytes();
        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        inspLeadViewHolder.inspLeadImge.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {

        return inspLeaderArrayList.size();
    }
}
