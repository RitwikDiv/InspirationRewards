package com.ritwikdivakaruni.insrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class CreateProfileAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "CreateProfileAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/profiles";
    private CreateProfileBean bean;
    private int ourcode = -1;
    @SuppressLint("StaticFieldLeak")
    private CreateActivity createActivity;
    public CreateProfileAPIAsyncTask(CreateActivity createActivity, CreateProfileBean bean) {
        this.createActivity = createActivity;
        this.bean = bean;
    }

    @Override
    protected String doInBackground(Void... voids) {
        JSONObject jsonObject = new JSONObject();
        String studentId = bean.studentId;
        String username = bean.username;
        String password = bean.password;
        String firstName = bean.firstName;
        String lastName = bean.lastName;
        int pointsToAward = bean.pointsToAward;
        String story = bean.story;
        String department = bean.department;
        String position = bean.position;
        boolean admin = bean.admin;
        String location = bean.location;
        String imageBytes = bean.imageBytes;
        Log.d(TAG, "doInBackground: CreateProfile" + studentId + username + password + firstName + lastName + pointsToAward + department + position + admin + location);
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("pointsToAward", pointsToAward);
            jsonObject.put("department", department);
            jsonObject.put("story", story);
            jsonObject.put("position", position);
            jsonObject.put("admin", admin);
            jsonObject.put("location", location);
            jsonObject.put("imageBytes", imageBytes);
            jsonObject.put("rewardRecords", "[]");

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
            String urlString = baseUrl + loginEndPoint;
            Uri uri = Uri.parse(urlString);
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();
            int responseCode = connection.getResponseCode();
            StringBuilder result = new StringBuilder();
            Log.d(TAG, "doAPICall: CreateProfile" + responseCode);
            if (responseCode == HTTP_OK) {
                ourcode = responseCode;
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                return result.toString();

            } else {
                ourcode = responseCode;
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                return result.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());
        } finally {
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
        return "Some error has occurred";
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        CreateProfileBean bean = null;
        if (ourcode == HTTP_OK) {
            try {
                JSONObject jsonObject = new JSONObject(connectionResult);
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
                bean = new CreateProfileBean("A20386609", username, password, firstName, lastName, pointsToAward, department, story, position, admin, location, imageBytes);
                createActivity.getCreateProfileAPIResp(bean, "Profile Created");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (ourcode == -1) {
            createActivity.getCreateProfileAPIResp(null, "Error!");
        } else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                createActivity.getCreateProfileAPIResp(null, errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

}
