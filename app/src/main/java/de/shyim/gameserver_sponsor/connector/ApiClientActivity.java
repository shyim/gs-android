package de.shyim.gameserver_sponsor.connector;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.shyim.gameserver_sponsor.ui.activity.BaseActivity;

public class ApiClientActivity extends AsyncTask<Void, Void, Boolean> {
    private String sURI = "/";
    public static String sToken = "";
    private String sAction;
    private BaseActivity activityApiActivity;
    private JSONObject jsonRequest;

    public ApiClientActivity(BaseActivity activity, String uri, JSONObject request, String action) {
        sURI = uri;
        sAction = action;
        activityApiActivity = activity;
        jsonRequest = request;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        StringBuilder responseOutput = new StringBuilder();

        try {
            String sHost = "https://gameserver-sponsor.de/api";
            URL url = new URL(sHost + sURI);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            String reqParams = jsonRequest.toString();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-GS3", sToken);
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(reqParams);
            dataOutputStream.flush();
            dataOutputStream.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content;

            while ((content = bufferedReader.readLine()) != null) {
                responseOutput.append(content);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.d("Response", responseOutput.toString());
            activityApiActivity.onApiResponse(new JSONObject(responseOutput.toString()), sAction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
