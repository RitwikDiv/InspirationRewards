package com.ritwikdivakaruni.insrewards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardActivity extends AppCompatActivity {
    private static final String TAG = "RewardActivity";


    private TextView pointsAwarded;
    private TextView dept;
    private TextView position;
    private TextView storyText;
    private ImageView userPhoto;
    private TextView name;
    private String imgString = "";
    private EditText rewardPintsToEdit;
    private EditText commentsEdit;
    private TextView commentLenView;
    InspLeaderBoardBean bean = null;

    //To send over API
    private String studentIdSource = "";
    private String usernameSource = "";
    private String passwordSource = "";
    private String studentIdTarget = "";
    private String usernameTarget = "";
    private String nameTarget = "";
    private String dateTarget = "";
    private String notesTarget = "";
    private int valueTarget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        //Setting ActionBar icon and title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);

        name = findViewById(R.id.awardProfileName);
        pointsAwarded = findViewById(R.id.awardProfilePtAwarded);
        dept = findViewById(R.id.awardProfileDept);
        position = findViewById(R.id.awardProfilePos);
        storyText = findViewById(R.id.awardProfileStoryText);
        userPhoto = findViewById(R.id.awardProfileImage);
        commentsEdit = findViewById(R.id.awardProfileComment);
        rewardPintsToEdit = findViewById(R.id.awardProfilePtSend);
        commentLenView = findViewById(R.id.awardProfileCmtLen);
        commentsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                String countText = "( " + len + " of 80 )";
                commentLenView.setText(countText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("AWARDPROFILE")) {
            bean = (InspLeaderBoardBean) intent.getSerializableExtra("AWARDPROFILE");
            Log.d(TAG, "getAwardProfileAct: " + bean.getImageBytes().length() + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                setTitle(" " + bean.getFirstName() + " " + bean.getLastName());
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                //Update
                pointsAwarded.setText("  " + bean.rewardPtAward);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: RewardActivity");
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveChangesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveChangesDialog() {
        Log.d(TAG, "saveChangesDialog: ");

        if (!rewardPintsToEdit.getText().toString().isEmpty() &&!commentsEdit.getText().toString().isEmpty()) {
            saveAlertOnCreateActivity();
        } else {
            warningDialog("Warning dialog on Incomplete data fields");
        }
    }

    public void warningDialog(String logStmt) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incomplete Profile Data!");
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        builder.setMessage("Please fill all the fields...");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveAlertOnCreateActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Rewards Points?");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Add rewards for "+bean.getFirstName()+ " "+bean.getLastName()+"?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: OK button Clicked");
                callAsyncAPI();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
    }

    public void callAsyncAPI() {
        Intent loginIntentFromLeaderBoard = getIntent();
        studentIdSource = loginIntentFromLeaderBoard.getStringExtra("STUDENTIDLOGIN");
        passwordSource = loginIntentFromLeaderBoard.getStringExtra("PASSLOGIN");
        usernameSource = loginIntentFromLeaderBoard.getStringExtra("UNAMELOGIN");
        nameTarget = loginIntentFromLeaderBoard.getStringExtra("NAMELOGIN");

        usernameTarget = bean.getUsername();
        studentIdTarget = bean.getStudentId();
        notesTarget = commentsEdit.getText().toString().trim();
        valueTarget = Integer.parseInt(rewardPintsToEdit.getText().toString());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/YYYY");
        dateTarget = format.format(new Date());
        Log.d(TAG, "callAsyncAPI: value" + valueTarget);

        Log.d(TAG, "callAsyncAPI: " + usernameSource + passwordSource + studentIdSource + studentIdTarget + usernameTarget + nameTarget + dateTarget + notesTarget + valueTarget);
        new RewardsAPIAsyncTask(this, new RewardsBean(studentIdSource, usernameSource, passwordSource, studentIdTarget, usernameTarget, nameTarget, dateTarget, notesTarget, valueTarget)).execute();
    }

    public void getRewardsAPIResp(String resp) {
        Log.d(TAG, "getRewardsAPIResp: from API " + resp);
        if (resp.contains("Reward Added")) {
            makeCustomToast(RewardActivity.this, resp, Toast.LENGTH_SHORT);
            Intent intent = new Intent(RewardActivity.this, InspLeaderboardActivity.class);
            intent.putExtra("STIDLOGIN", studentIdSource);
            intent.putExtra("PSSLOGIN", passwordSource);
            intent.putExtra("USERLOGIN", usernameSource);
            setResult(RESULT_OK,intent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error!");
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setMessage(resp);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
