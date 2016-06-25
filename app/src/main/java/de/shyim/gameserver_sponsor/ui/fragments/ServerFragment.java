package de.shyim.gameserver_sponsor.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.adapter.ViewPagerAdapter;
import de.shyim.gameserver_sponsor.ui.fragments.server.ConsoleFragment;
import de.shyim.gameserver_sponsor.ui.fragments.server.InfoFragment;

public class ServerFragment extends Fragment {
    private ViewPagerAdapter adapter;
    private Integer gsID;

    private OnFragmentInteractionListener mListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.gsID = getArguments().getInt("gsID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_server);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        InfoFragment infoFragment = new InfoFragment();
        infoFragment.setGsID(gsID);

        ConsoleFragment consoleFragment = new ConsoleFragment();
        consoleFragment.setGsID(gsID);

        adapter.addFragment(infoFragment, "Aktionen");
        adapter.addFragment(consoleFragment, "Konsole");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
