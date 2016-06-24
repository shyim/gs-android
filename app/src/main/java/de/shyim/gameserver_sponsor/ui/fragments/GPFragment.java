package de.shyim.gameserver_sponsor.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.adapter.ViewPagerAdapter;
import de.shyim.gameserver_sponsor.connector.ApiClient;
import de.shyim.gameserver_sponsor.ui.fragments.gp.GPList;

public class GPFragment extends Fragment {
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ApiClient.get("gp/count", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Integer gp = 0;

                try {
                    gp = response.getInt("gp");
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
        });

        return inflater.inflate(R.layout.fragment_gp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_gp);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        adapter.addFragment(new GPList().setMode(""), getString(R.string.all));
        adapter.addFragment(new GPList().setMode("in"), getString(R.string.incoming));
        adapter.addFragment(new GPList().setMode("out"), getString(R.string.spent));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        List<Fragment> fragmentList = adapter.getFragments();

        for (int i = 0; i < fragmentList.size(); i++) {
            fragmentTransaction.remove(fragmentList.get(i));
        }
        fragmentTransaction.commit();
    }
}
