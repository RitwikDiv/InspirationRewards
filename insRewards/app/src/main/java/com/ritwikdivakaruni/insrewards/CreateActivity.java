package com.ritwikdivakaruni.insrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import android.location.Address;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    private static int MY_EXT_STORAGE_REQUEST_CODE = 329;
    private static final int U_REQUEST_CODE = 1;
    private static int MY_LOCATION_REQUEST_CODE = 330;
    private LocationManager locationManager;
    private Location currentLocation;
    private Criteria criteria;
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText firstEdit;
    private TextView lastEdit;
    private EditText deptEdit;
    private TextView posEdit;
    private EditText storyEdit;
    private String locationFromLatLong = "";
    private String studentId = "A20386609";
    private String username = "";
    private String password = "";
    private String firstName = "";
    private String lastName = "";
    private int pointsToAward =1000;
    private String department = "";
    private String story = "";
    private String position = "";
    private boolean admin = false;
    private String rewards = "";
    private ImageView addUser;
    private TextView getCharsView;
    private File currentImageFile;
    private int REQUEST_IMAGE_GALLERY = 1;
    private int REQUEST_IMAGE_CAPTURE = 2;
    private String imgString = "";
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_with_logo);
        setTitle("Create Profile");
        userNameEdit = findViewById(R.id.createUname);
        passwordEdit = findViewById(R.id.createPass);
        firstEdit = findViewById(R.id.createFirstName);
        lastEdit = findViewById(R.id.createLastName);
        deptEdit = findViewById(R.id.createDept);
        posEdit = findViewById(R.id.createPos);
        storyEdit = findViewById(R.id.createStory);
        getCharsView = findViewById(R.id.availChars);
        addUser = findViewById(R.id.createUserPhoto);
        setLocationPrereq();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        storyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                String countText = "( " + len + " of 360 )";
                getCharsView.setText(countText);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void captureImage(View v) {
        Log.d(TAG, "setFilePermissions: ");
        int permExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permExt != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_EXT_STORAGE_REQUEST_CODE);
        } else {
            openCameraGalleryDialog();
            makeCustomToast(this, "Permission already granted", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + permissions);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_EXT_STORAGE_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                openCameraGalleryDialog();
                return;
            }
        } else if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        } else {
            makeCustomToast(this, "Permission denied", Toast.LENGTH_LONG);
        }
    }

    public void openCameraGalleryDialog() {
        Log.d(TAG, "openCameraGalleryDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Take picture from: ");
        builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doGallery();
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                doCamera();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
    }

    public void doCamera() {
        Log.d(TAG, "doCamera: " + REQUEST_IMAGE_CAPTURE);
        currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: createActivity");
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveChangesDialog();
                return true;
            case android.R.id.home:
                makeCustomToast(this, "New Profile Not Created", Toast.LENGTH_SHORT);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();
        makeCustomToast(this, "New Profile not created", Toast.LENGTH_SHORT);
    }
    private void ifNewImageIsNotSelected() {
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 0, bitmapAsByteArrayStream);
        imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);
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

    public void saveChangesDialog() {
        Log.d(TAG, "saveChangesDialog: " + locationFromLatLong + imgString);
        //Setting the Location to Chicago, Illinois if location is not found by Location Service
        if (locationFromLatLong.isEmpty())
            locationFromLatLong = "Chicago, Illinois";
        if (imgString == "")
            ifNewImageIsNotSelected();
        if (!userNameEdit.getText().toString().isEmpty() && !passwordEdit.getText().toString().isEmpty() && !firstEdit.getText().toString().isEmpty()
                && !lastEdit.getText().toString().isEmpty() && !deptEdit.getText().toString().isEmpty() && !posEdit.getText().toString().isEmpty()
                && !storyEdit.getText().toString().isEmpty() && locationFromLatLong != "" && imgString != "") {
            saveAlertOnCreateActivity();
        } else {
            warningDialog("Warning dialog on Incomplete data fields");
        }
    }

    public void saveAlertOnCreateActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setIcon(R.drawable.logo);
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
        username = userNameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        firstName = firstEdit.getText().toString();
        lastName = lastEdit.getText().toString();
        department = deptEdit.getText().toString();
        story = storyEdit.getText().toString().trim();
        position = posEdit.getText().toString();
        new CreateProfileAPIAsyncTask(this, new CreateProfileBean(studentId, username, password, firstName, lastName, pointsToAward, department, story, position, admin, locationFromLatLong, imgString)).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + REQUEST_IMAGE_CAPTURE + " " + resultCode + " " + requestCode + " " + data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                Log.d(TAG, "onActivityResult: Gallery camera");
                processGallery(data);
            } catch (Exception e) {
                makeCustomToast(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Log.d(TAG, "onActivityResult: inside camera");
                processCamera();
            } catch (Exception e) {
                makeCustomToast(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }

    private void processCamera() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        addUser.setImageURI(selectedImage);
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitmapAsByteArrayStream);
        imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);
        makeCustomToast(this, "Photo Size: " + String.format(Locale.getDefault(), "%,d", bm.getByteCount()), Toast.LENGTH_LONG);
        //currentImageFile.delete();
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        addUser.setImageBitmap(selectedImage);
        makeCustomToast(this, String.format(Locale.getDefault(), "%,d", selectedImage.getByteCount()), Toast.LENGTH_LONG);
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitmapAsByteArrayStream);
        imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        makeCustomToast(this, "Image size over Network: " + bm.getByteCount(), Toast.LENGTH_LONG);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);

    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(50, 50, 50, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    public void getCreateProfileAPIResp(CreateProfileBean respBean,String connectionResult) {

        if(respBean==null)
        {
            makeCustomToast(this, connectionResult, Toast.LENGTH_SHORT);
            return;
        }
        else
        {
            Intent intent = new Intent(CreateActivity.this, UserProfileActivity.class);
            intent.putExtra("USERPROFILE", respBean);
            startActivity(intent);
            makeCustomToast(this, "New User Created", Toast.LENGTH_SHORT);
            finish();
        }

    }

    // Geolocation services
    public void setLocationPrereq() {
        Log.d(TAG, "setLocationPrereq: ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE);
        } else {
            setLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        Log.d(TAG, "setLocation: ");
        String bestProvider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(bestProvider);
        List<Address> addresses;
        if (currentLocation != null) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                latitude = (double) currentLocation.getLatitude();
                longitude = (double) currentLocation.getLongitude();
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                displayAddresses(addresses);
                Log.d(TAG, "setLocation: " + addresses.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            makeCustomToast(this, "Your current location is unavailable", Toast.LENGTH_SHORT);
        }
    }

    public void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        if (addresses.size() == 0) {
            locationFromLatLong = "No Location Found";
            return;
        }
        for (Address ad : addresses) {

            String a = String.format("%s, %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea())
            );

            if (!a.trim().isEmpty())
                sb.append(" ").append(a.trim());
            sb.append("\n");
        }
        locationFromLatLong = sb.toString().trim();
        Log.d(TAG, "displayAddresses: " + locationFromLatLong);
    }

    public void recheckLocation(View v) {
        makeCustomToast(this, "Checking for location again", Toast.LENGTH_SHORT);
        setLocation();
    }

}
