package de.shyim.gameserver_sponsor.connector;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class ApiClient {
    private static final String BASE_URL = "https://gameserver-sponsor.de/api/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void setToken(String token)
    {
        client.addHeader("X-GS3", token);
        Log.d("ApiClient", "Adding Token " + token);
    }

    public static void setLanguage(String language)
    {
        client.addHeader("ACCEPT_LANGUAGE", language);
    }

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}