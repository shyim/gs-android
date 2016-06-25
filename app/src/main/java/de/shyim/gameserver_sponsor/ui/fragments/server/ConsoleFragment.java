package de.shyim.gameserver_sponsor.ui.fragments.server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClient;

public class ConsoleFragment extends Fragment {
    private Integer gsID;
    private TextView serverLog;
    private SwipeRefreshLayout swipeContainer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_console, container, false);
    }

    public void setGsID (Integer gsID) {
        this.gsID = gsID;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serverLog = (TextView) view.findViewById(R.id.server_log);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerServerLog);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLogs();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshLogs();
    }

    private void refreshLogs()
    {
        RequestParams params = new RequestParams();
        params.put("gsID", this.gsID);

        ApiClient.get("server/log", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String log = null;
                try {
                    log = response.getString("log");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String logText = log;
                final SwipeRefreshLayout swipeRefreshLayout = swipeContainer;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        serverLog.setText(logText);
                    }
                });
            }
        });
    }
}
