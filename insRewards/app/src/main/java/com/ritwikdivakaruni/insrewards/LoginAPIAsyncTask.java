package com.ritwikdivakaruni.insrewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAPIAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "LoginProfileAsyncTask";
    private MainActivity mainActivity;
    private final String baseurlAdress = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private final String loginURL = "/login";
    private int ourcode=-1;
    public LoginAPIAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        CreateProfileBean bean = null;
        if (ourcode == HTTP_OK)
        {
            try {
                JSONObject jsonObject = new JSONObject(connectionResult);
                String studentId = jsonObject.getString("studentId");
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                String firstName = jsonObject.getString("firstName");
                String lastName = jsonObject.getString("lastName");
                int pointsToAward = jsonObject.getInt("pointsToAward");
                String department = jsonObject.getString("department");
                String story = jsonObject.getString("story");
                String position = jsonObject.getString("position");
                boolean admin = jsonObject.getBoolean("admin");
                String location = jsonObject.getString("location");
                String imageBytes = jsonObject.getString("imageBytes");

                List<RewardRecords> rewardsList=new ArrayList<>();
                if(jsonObject.getJSONArray("rewards")!=null)
                {
                    JSONArray rewards = jsonObject.getJSONArray("rewards");
                    for(int i=0;i<rewards.length();i++)
                    {
                        RewardRecords rewardRecordsBean=new RewardRecords();
                        JSONObject reward=rewards.getJSONObject(i);
                        String studentIdR = reward.getString("studentId");
                        String usernameR = reward.getString("username");
                        String nameR = reward.getString("name");
                        String notesR = reward.getString("notes");
                        String dateR = reward.getString("date");
                        int valueR = reward.getInt("value");
                        rewardRecordsBean.setStudentId(studentIdR);
                        rewardRecordsBean.setUsernameRecord(usernameR);
                        rewardRecordsBean.setName(nameR);
                        rewardRecordsBean.setDate(dateR);
                        rewardRecordsBean.setNotes(notesR);
                        rewardRecordsBean.setValue(valueR);
                        rewardsList.add(rewardRecordsBean);
                    }
                }

                bean = new CreateProfileBean(studentId, username, password, firstName, lastName, pointsToAward, department, story, position, admin, location, imageBytes);
                Log.d(TAG, "onPostExecute: " + bean);
                mainActivity.getLoginAPIResp(bean,rewardsList,"SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (ourcode == -1)
            mainActivity.getLoginAPIResp(null,null,"Some Other Error Occured");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                mainActivity.getLoginAPIResp(null,null,errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String stuId = strings[0];
        String uName = strings[1];
        String pswd = strings[2];
        Log.d(TAG, "doInBackground: Inputs+"+stuId+uName+pswd);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", stuId);
            jsonObject.put("username", uName);
            jsonObject.put("password", pswd);

            String ab = doAPICall(jsonObject);
            Log.d(TAG, "doInBackground: " + ab);
            return doAPICall(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseurlAdress + loginURL;  // Build the full URL

            Uri uri = Uri.parse(urlString);    // Convert String url to URI
            URL url = new URL(uri.toString()); // Convert URI to URL

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET

            // Set the Content-Type and Accept properties to use JSON data
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            // Write the JSON (as String) to the open connection
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            Log.d(TAG, "doAPICall: response" + responseCode);
            // If successful (HTTP_OK)
            if (responseCode == HTTP_OK) {
                ourcode=responseCode;
                // Read the results - use connection's getInputStream
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                ourcode=responseCode;
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();
            }

        } catch (Exception e) {
            // Some exception occurred! Log it.
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally { // Close everything!
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "Some error has occurred"; // Return an error message if Exception occurred
    }
}