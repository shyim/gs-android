package de.shyim.gameserver_sponsor.ui.fragments.server;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClient;

public class ActionFragment extends Fragment {
    private Integer gsID;

    public void setGsID(Integer gsID)
    {
        this.gsID = gsID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_action, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View myView = view;
        final ActionFragment myFragment = this;

        Button startServerBtn = (Button) view.findViewById(R.id.serverStart);
        startServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("gsID", myFragment.gsID);
                ApiClient.get("server/start", params, new JsonHttpResponseHandler());
                Toast.makeText(myView.getContext(), "Server startet in kürze", Toast.LENGTH_LONG).show();
            }
        });

        Button stopServerButton = (Button) view.findViewById(R.id.serverStop);
        stopServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("gsID", myFragment.gsID);
                ApiClient.get("server/stop", params, new JsonHttpResponseHandler());
                Toast.makeText(myView.getContext(), "Server stoppt in kürze", Toast.LENGTH_LONG).show();
            }
        });

        Button restartServerButton = (Button) view.findViewById(R.id.serverRestart);
        restartServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("gsID", myFragment.gsID);
                ApiClient.get("server/restart", params, new JsonHttpResponseHandler());
                Toast.makeText(myView.getContext(), "Server startet in kürze neu", Toast.LENGTH_LONG).show();
            }
        });
    }
}
