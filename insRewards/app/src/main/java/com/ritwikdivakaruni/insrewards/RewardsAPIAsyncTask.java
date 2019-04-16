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

public class RewardsAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "RewardsAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/rewards";
    private RewardsBean bean;
    @SuppressLint("StaticFieldLeak")
    private RewardActivity rewardActivity;
    private int ourcode = -1;

    public RewardsAPIAsyncTask(RewardActivity rewardActivity, RewardsBean bean) {

        this.rewardActivity = rewardActivity;
        this.bean = bean;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String studentIdSource = bean.studentIdSource;
        String usernameSource = bean.usernameSource;
        String passwordSource = bean.passwordSource;
        String studentIdTarget = bean.studentIdTarget;
        String usernameTarget = bean.usernameTarget;
        String nameTarget = bean.nameTarget;
        String dateTarget = bean.dateTarget;
        String notesTarget = bean.notesTarget;
        int valueTarget = bean.valueTarget;
        JSONObject sourceJson = new JSONObject();
        JSONObject targetJson = new JSONObject();
        JSONObject jsonToSend = new JSONObject();
        Log.d(TAG, "doInBackground: Rewards" + usernameSource + passwordSource + studentIdSource + studentIdTarget + usernameTarget + nameTarget + dateTarget + notesTarget + valueTarget);
        try {
            sourceJson.put("studentId", studentIdSource);
            sourceJson.put("username", usernameSource);
            sourceJson.put("password", passwordSource);
            targetJson.put("studentId", studentIdTarget);
            targetJson.put("username", usernameTarget);
            targetJson.put("name", nameTarget);
            targetJson.put("date", dateTarget);
            targetJson.put("notes", notesTarget);
            targetJson.put("value", valueTarget);

            jsonToSend.put("target", targetJson);
            jsonToSend.put("source", sourceJson);
            Log.d(TAG, "input JSON" + jsonToSend);
            return doAPICall(jsonToSend);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + loginEndPoint;  // Build the full URL

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
            Log.d(TAG, "doAPICall: rewards" + responseCode);
            // If successful (HTTP_OK)

            if (responseCode == HTTP_OK) {
                ourcode = responseCode;
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
                ourcode = responseCode;
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

    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        if (ourcode == HTTP_OK)
            rewardActivity.getRewardsAPIResp(connectionResult);
        else if (ourcode == -1)
            rewardActivity.getRewardsAPIResp("Some other error");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                rewardActivity.getRewardsAPIResp(errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}

