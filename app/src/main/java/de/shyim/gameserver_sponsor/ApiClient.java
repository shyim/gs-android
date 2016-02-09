package de.shyim.gameserver_sponsor;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ApiClient extends AsyncTask<Void, Void, Boolean> {
    private String sHost = "https://devv6.gameserver-sponsor.de/api";
    private String sURI = "/";
    private String sToken;
    private ApiActivity activityApiActivity;
    private JSONObject jsonRequest;

    ApiClient(ApiActivity activity, String uri, String token, JSONObject request) {
        sURI = uri;
        sToken = token;
        activityApiActivity = activity;
        jsonRequest = request;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        StringBuilder responseOutput = new StringBuilder();

        try {
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
            String content = "";

            while((content = bufferedReader.readLine()) != null) {
                responseOutput.append(content);
            }
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            activityApiActivity.onApiResponse(new JSONObject(responseOutput.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
