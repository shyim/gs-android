package de.shyim.gameserver_sponsor.ui.fragments.server;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class InfoFragment extends Fragment {
    private Integer gsID;
    private TextView serverStatus = null;
    private Integer canceled = 0;
    private ProgressDialog progressDialog = null;

    public void setGsID (final Integer gsID) {
        this.gsID = gsID;

        final InfoFragment myServerFragment = this;
        final Handler serverStatusHandler = new Handler();
        Runnable serverStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if (canceled == 1) {
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("gsID", myServerFragment.gsID);

                ApiClient.get("server/online", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                final Boolean onlineStatus = response.getBoolean("online");

                                if (getActivity() == null) {
                                    return;
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onlineStatus) {
                                            myServerFragment.serverStatus.setText("Server ist online");
                                            myServerFragment.serverStatus.setTextColor(Color.parseColor("#27a4b0"));
                                            myServerFragment.serverStatus.setTypeface(null, Typeface.BOLD);
                                        } else {
                                            myServerFragment.serverStatus.setText("Server ist offline");
                                            myServerFragment.serverStatus.setTextColor(Color.parseColor("#BD362F"));
                                            myServerFragment.serverStatus.setTypeface(null, Typeface.BOLD);
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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

        serverStatus = (TextView) view.findViewById(R.id.serverOnlineStatus);
        final TextView gameTitle = (TextView) view.findViewById(R.id.serverGameTitle);
        final TextView gameSlots = (TextView) view.findViewById(R.id.serverSlots);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Laden");
        progressDialog.setMessage("Lade Serverinformationen");
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.put("gsID", gsID);
        ApiClient.get("server/getSingle", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.hide();
                try {
                    gameTitle.setText(gameTitle.getText() + " " + response.getString("name"));
                    gameSlots.setText(gameSlots.getText() + " " + response.getString("slots"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
