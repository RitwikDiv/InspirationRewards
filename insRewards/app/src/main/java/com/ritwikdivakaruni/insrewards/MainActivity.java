package com.ritwikdivakaruni.insrewards;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String sId = "A20386609";
    private static final int B_REQUEST_CODE = 1;
    private static final int UP_REQUEST_CODE = 2;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private RewardsPreferences preferences;
    private EditText unameView;
    private EditText passView;
    private TextView newAccView;
    private CheckBox credChkBox;
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private Location currentLocation;
    private Criteria criteria;
    private JSONObject jsonObj;
    private static int MY_LOCATION_REQUEST_CODE = 329;
    private static int MY_EXT_STORAGE_REQUEST_CODE = 330;
    MainActivity mainActivity = this;
    private ArrayList<RewardRecords> rewardsArrList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        //Setting ActionBar icon and title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);
        setTitle("  Rewards");

        //Setting file and location permissions
        if (isOnline())
            setFileLocationPermissions();
        else
            errorDialog("errorDialog: No Internet Connectivity!!", "No Internet Connection", "Cannot start application");
        //Getting Login Ids
        unameView = (EditText) findViewById(R.id.usernameView);
        passView = (EditText) findViewById(R.id.passowrdView);
        credChkBox = (CheckBox) findViewById(R.id.remCredChk);
        progressBar=findViewById(R.id.progressBar);
        //Storing the Login Credentials to SharedPreferences
        preferences = new RewardsPreferences(this);

        Log.d(TAG, "onCreate: " + preferences.getValue(getString(R.string.userPreferences)));
        unameView.setText(preferences.getValue(getString(R.string.userPreferences)));
        passView.setText(preferences.getValue(getString(R.string.passwordPreferences)));
        credChkBox.setChecked(preferences.getBoolValue(getString(R.string.checkPreferences)));
    }

    public void setFileLocationPermissions() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        int permLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permLoc != PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permExt != PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void errorDialog(String logStmt, String title, String message) {
        int d = Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onLoginBtnClick(View v) {

        if (credChkBox.isChecked()) {
            Log.d(TAG, ":onLoginBtnClick ");
            preferences.save(getString(R.string.userPreferences), unameView.getText().toString());
            preferences.save(getString(R.string.passwordPreferences), passView.getText().toString());
            preferences.saveBool(getString(R.string.checkPreferences), credChkBox.isChecked());
        }
        String uName = unameView.getText().toString();
        String pswd = passView.getText().toString();

        if (uName.equals("") || pswd.equals("")) {
            errorDialog("errorDialog: incomplete input fields!!", "Incomplete Input Fields", "Please Enter Valid UserName/Password");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new LoginAPIAsyncTask(mainActivity).execute(sId, uName, pswd);

        }
    }

    public void onNewAccCreateClick(View v) {
        Log.d(TAG, "onNewAccCreateClick: Main");
        if (isOnline()) {
            makeCustomToast(this, "Creating new Profile", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, CreateActivity.class);
            startActivityForResult(intent, B_REQUEST_CODE);
        } else
            makeCustomToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, " " + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    public void getLoginAPIResp(CreateProfileBean respBean, List<RewardRecords> rewardsList, String connectionResult) {
        Log.d(TAG, "getLoginAPIResp: " + respBean);
        if (respBean == null) {
            makeCustomToast(this, connectionResult, Toast.LENGTH_SHORT);
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            Log.d(TAG, "getLoginAPIResp: " + respBean.getUsername() + respBean.getFirstName() + respBean.getLastName() + respBean.getLocation() + respBean.getDepartment() + respBean.getPassword() + respBean.getPosition() + respBean.getStory() + respBean.getPointsToAward());

            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("USERPROFILE", respBean);
            intent.putExtra("USERPROFILE_LIST", (Serializable) rewardsList);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);

        }
    }
}
