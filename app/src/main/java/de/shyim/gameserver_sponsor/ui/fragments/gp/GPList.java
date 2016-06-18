package de.shyim.gameserver_sponsor.ui.fragments.gp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.adapter.GPListAdapter;
import de.shyim.gameserver_sponsor.connector.ApiClientFragment;
import de.shyim.gameserver_sponsor.struct.GPItem;
import de.shyim.gameserver_sponsor.ui.fragments.BaseFragment;

public class GPList extends BaseFragment {
    private ListView listView;
    private String mode = "";
    private Boolean listLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gplist, container, false);
    }

    public GPList setMode(String mode) {
        this.mode = mode;

        return this;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.gp_list);

        new ApiClientFragment(this, "/gp/index/" + mode, new JSONObject(), "").execute();
    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        final ArrayList<GPItem> listBlockItems = new ArrayList<>();
        try {
            JSONArray blogItems = object.getJSONArray("data");
            for (int i = 0; i < blogItems.length(); i++) {
                JSONObject rawData = blogItems.getJSONObject(i);
                GPItem gpItem = new GPItem();
                gpItem.setId(rawData.getInt("id"));
                gpItem.setUserID(rawData.getInt("userID"));
                gpItem.setName(rawData.getString("name"));
                gpItem.setValue(rawData.getInt("value"));
                gpItem.setStatus(rawData.getString("status"));
                gpItem.setTimestamp(rawData.getString("timestamp"));
                listBlockItems.add(gpItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListView myListView = listView;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myListView.setAdapter(new GPListAdapter(getContext(), listBlockItems));
            }
        });
    }
}
