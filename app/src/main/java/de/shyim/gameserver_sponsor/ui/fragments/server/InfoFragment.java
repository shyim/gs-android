package de.shyim.gameserver_sponsor.ui.fragments.server;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClientFragment;
import de.shyim.gameserver_sponsor.ui.fragments.BaseFragment;

public class InfoFragment extends BaseFragment {
    private Integer gsID;
    private TextView serverStatus = null;
    private Integer canceled = 0;

    public void setGsID (Integer gsID) {
        this.gsID = gsID;

        final InfoFragment myServerFragment = this;
        final Handler serverStatusHandler = new Handler();
        Runnable serverStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if (canceled == 1) {
                    return;
                }
                JSONObject req = new JSONObject();
                try {
                    req.put("gsID", myServerFragment.gsID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ApiClientFragment(myServerFragment, "/server/getSingle", req, "serverStatus").execute();

                serverStatusHandler.postDelayed(this, 2500);
            }
        };
        serverStatusHandler.postDelayed(serverStatusRunnable, 500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final InfoFragment myFragment = this;
        final View myView = view;

        serverStatus = (TextView) view.findViewById(R.id.serverOnlineStatus);

        Button startServerBtn = (Button) view.findViewById(R.id.serverStart);
        startServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject req = new JSONObject();
                try {
                    req.put("gsID", myFragment.gsID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ApiClientFragment(myFragment, "/server/start", req, "serverStart").execute();
                Toast.makeText(myView.getContext(), "Server startet in kürze", Toast.LENGTH_LONG).show();
            }
        });

        Button stopServerButton = (Button) view.findViewById(R.id.serverStop);
        stopServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject req = new JSONObject();
                try {
                    req.put("gsID", myFragment.gsID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ApiClientFragment(myFragment, "/server/stop", req, "serverStart").execute();
                Toast.makeText(myView.getContext(), "Server stoppt in kürze", Toast.LENGTH_LONG).show();
            }
        });

        Button restartServerButton = (Button) view.findViewById(R.id.serverRestart);
        restartServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject req = new JSONObject();
                try {
                    req.put("gsID", myFragment.gsID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ApiClientFragment(myFragment, "/server/restart", req, "serverStart").execute();
                Toast.makeText(myView.getContext(), "Server startet in kürze neu", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        final InfoFragment myFragment = this;
        switch (action) {
            case "serverStatus":
                try {
                    if (object.getBoolean("success")) {
                        final Boolean onlineStatus = object.getBoolean("online");

                        if (getActivity() == null) {
                            return;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onlineStatus) {
                                    myFragment.serverStatus.setText("Server ist online");
                                    myFragment.serverStatus.setTextColor(Color.parseColor("#27a4b0"));
                                    myFragment.serverStatus.setTypeface(null, Typeface.BOLD);
                                } else {
                                    myFragment.serverStatus.setText("Server ist offline");
                                    myFragment.serverStatus.setTextColor(Color.parseColor("#BD362F"));
                                    myFragment.serverStatus.setTypeface(null, Typeface.BOLD);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        canceled = 1;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
