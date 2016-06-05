package de.shyim.gameserver_sponsor.ui.fragments.server;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClientFragment;
import de.shyim.gameserver_sponsor.connector.object.DefaultServerArgument;
import de.shyim.gameserver_sponsor.ui.fragments.BaseFragment;

public class ConsoleFragment extends BaseFragment {
    private OnFragmentInteractionListener mListener;
    private Integer gsID;
    public TextView serverLog;
    private SwipeRefreshLayout swipeContainer = null;

    public static ConsoleFragment newInstance(String param1, String param2) {
        ConsoleFragment fragment = new ConsoleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_console, container, false);
    }

    public void setGsID (Integer gsID) {
        this.gsID = gsID;

        new ApiClientFragment(this, "/server/log", new DefaultServerArgument(gsID), "log").execute();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ConsoleFragment myConsoleFragment = this;
        serverLog = (TextView) view.findViewById(R.id.server_log);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerServerLog);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ApiClientFragment(myConsoleFragment, "/server/log", new DefaultServerArgument(myConsoleFragment.gsID), "log").execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        String log = null;
        try {
            log = object.getString("log");
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
