package de.shyim.gameserver_sponsor.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.adapter.ViewPagerAdapter;
import de.shyim.gameserver_sponsor.connector.ApiClientFragment;
import de.shyim.gameserver_sponsor.ui.fragments.gp.GPList;

public class GPFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new ApiClientFragment(this, "/gp/count", new JSONObject(), "gp").execute();

        return inflater.inflate(R.layout.fragment_gp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager_gp);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        adapter.addFragment(new GPList(), "Alle");
        adapter.addFragment(new GPList(), "Erhalten");
        adapter.addFragment(new GPList(), "Ausgegeben");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        Integer gp = 0;

        try {
            gp = object.getInt("gp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Integer gp2 = gp;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setTitle(getActivity().getTitle() + " (" + gp2.toString() + " GP)");
            }
        });
    }
}
