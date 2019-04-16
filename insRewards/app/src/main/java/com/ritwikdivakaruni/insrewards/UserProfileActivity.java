package com.ritwikdivakaruni.insrewards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "UserProfileActivity";
    private TextView uname;
    private TextView location;
    private TextView pointsAwarded;
    private TextView dept;
    private TextView position;
    private TextView pointsAvailable;
    private TextView storyText;
    private ImageView userPhoto;
    private TextView name;
    private TextView rewardsCommentsCount;
    private String imgString = "";

    private static final int PROFILE_EDIT_REQUEST_CODE = 1;
    private static final int PROFILE_DASH_REQUEST_CODE = 2;

    private RecyclerView recyclerView; // Layout's recyclerview
    private UserProfileRewardHistoryAdapter userProfileRewardHistoryAdapter;
    private List<RewardRecords> rewardArrayList = new ArrayList<>();
    CreateProfileBean bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Setting ActionBar icon and title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);
        setTitle("  Your Profile");

        //Adapter for Rewards History
        recyclerView = findViewById(R.id.userProfileRecycler);
        userProfileRewardHistoryAdapter = new UserProfileRewardHistoryAdapter(this, rewardArrayList);
        recyclerView.setAdapter(userProfileRewardHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        name = findViewById(R.id.userProfileName);
        uname = findViewById(R.id.userProfileUname);
        location = findViewById(R.id.userProfileLocation);
        dept = findViewById(R.id.userProfileDept);
        position = findViewById(R.id.userProfilePos);
        pointsAvailable = findViewById(R.id.userProfilePtAvail);
        pointsAwarded = findViewById(R.id.userProfilePtAwarded);
        storyText = findViewById(R.id.userProfileStoryText);
        storyText.getBackground().setAlpha(80);
        userPhoto = findViewById(R.id.userProfileImage);
        rewardsCommentsCount = findViewById(R.id.userProfileCommentsCnt);
        Intent intent = getIntent();
        if (intent.hasExtra("USERPROFILE")) {
            Log.d(TAG, "onCreate: ");
            bean = (CreateProfileBean) intent.getSerializableExtra("USERPROFILE");
            Log.d(TAG, "getUserProfileAct: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                uname.setText("(" + bean.getUsername() + ")");
                location.setText(bean.getLocation());
                //Update
                pointsAvailable.setText("  " + bean.getPointsToAward());
                dept.setText("  " + bean.getDepartment());
                position.setText("  " + bean.getPosition());
                storyText.setText(bean.getStory());

                //Image Transformation
                imgString = bean.getImageBytes();
                byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                userPhoto.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent arrListIntent = getIntent();
        if (intent.hasExtra("USERPROFILE_LIST")) {
            List<RewardRecords> list = new ArrayList<>();
            rewardArrayList.clear();
            list = (List<RewardRecords>) intent.getSerializableExtra("USERPROFILE_LIST");
            if (list != null) {
                int totalRewards = 0;
                for (int i = 0; i < list.size(); i++) {
                    RewardRecords rewardRec = list.get(i);
                    totalRewards += rewardRec.getValue();
                }
                pointsAwarded.setText("  " + totalRewards);
                rewardsCommentsCount.setText("(" + list.size() + "):");
            } else {
                pointsAwarded.setText("0");
                rewardsCommentsCount.setText("(" + list.size() + "):");
            }

            rewardArrayList.addAll(list);
            userProfileRewardHistoryAdapter.notifyDataSetChanged();
        }
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "Image Size: " + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.editMenu:
                Log.d(TAG, "onOptionsItemSelected: EditClick" + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
                Intent editIntent = new Intent(this, EditActivity.class);
                editIntent.putExtra("EDITPROFILE", bean);
                editIntent.putExtra("EDITPROFILE_LIST", (Serializable) rewardArrayList);
                startActivityForResult(editIntent, PROFILE_EDIT_REQUEST_CODE);
                return true;
            case R.id.dashboardMenu:
                Log.d(TAG, "onOptionsItemSelected: DashboardClick");
                Intent dashboardIntent = new Intent(this, InspLeaderboardActivity.class);
                dashboardIntent.putExtra("INSPLEADPROFILE", bean);
                startActivityForResult(dashboardIntent, PROFILE_DASH_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: Profile: " + requestCode + " " + resultCode);
        if (requestCode == PROFILE_EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getIntentFromEditToProfile(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else if (requestCode == PROFILE_DASH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getIntentFromInspToProfile(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    public void getIntentFromInspToProfile(Intent data) {
        if (data.hasExtra("POINTSTOAWARD")) {
            Log.d(TAG, "getIntentFromInspToProfile: "+data.getStringExtra("POINTSTOAWARD"));
            int points=0;
            points=Integer.parseInt(data.getStringExtra("POINTSTOAWARD"));
            pointsAvailable.setText("  " + data.getStringExtra("POINTSTOAWARD"));
            bean.setPointsToAward(points);
        }
    }

    public void getIntentFromEditToProfile(Intent data) {
        if (data.hasExtra("USERPROFILE")) {
            Log.d(TAG, "getIntentFromEditToProfile: ");
            bean = (CreateProfileBean) data.getSerializableExtra("USERPROFILE");
            Log.d(TAG, "getUserProfileAct: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                uname.setText("(" + bean.getUsername() + ")");
                location.setText(bean.getLocation());
                //Update
                pointsAvailable.setText("  " + bean.getPointsToAward());
                dept.setText("  " + bean.getDepartment());
                position.setText("  " + bean.getPosition());
                storyText.setText(bean.getStory());
                //Image Transformation
                imgString = bean.getImageBytes();
                byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                userPhoto.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data.hasExtra("USERPROFILE_LIST")) {
            List<RewardRecords> list = new ArrayList<>();
            rewardArrayList.clear();
            list = (List<RewardRecords>) data.getSerializableExtra("USERPROFILE_LIST");
            if (list != null) {
                int totalRewards = 0;
                for (int i = 0; i < list.size(); i++) {
                    RewardRecords rewardRec = list.get(i);
                    totalRewards += rewardRec.getValue();
                }
                pointsAwarded.setText("  " + totalRewards);
                rewardsCommentsCount.setText("(" + list.size() + "):");
            } else {
                pointsAwarded.setText("0");
                rewardsCommentsCount.setText("(" + list.size() + "):");
            }

            rewardArrayList.addAll(list);
            userProfileRewardHistoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userprofile_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
