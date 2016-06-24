package de.shyim.gameserver_sponsor.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.adapter.BlogListAdapter;
import de.shyim.gameserver_sponsor.connector.ApiClient;
import de.shyim.gameserver_sponsor.struct.BlogItem;

public class BlogList extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ListView listView;

    public static BlogList newInstance(String param1, String param2) {
        BlogList fragment = new BlogList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.blog_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                BlogItem blogItem = (BlogItem) listView.getItemAtPosition(position);

                BlogDetail blogDetail = new BlogDetail();
                blogDetail.setBlogId(blogItem.getId());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentMain, blogDetail);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ApiClient.get("blog", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                final ArrayList<BlogItem> listBlockItems = new ArrayList<>();
                try {
                    JSONArray blogItems = response.getJSONArray("data");
                    for (int i = 0; i < blogItems.length(); i++) {
                        JSONObject rawData = blogItems.getJSONObject(i);
                        BlogItem blogItem = new BlogItem();
                        blogItem.setId(rawData.getInt("id"));
                        blogItem.setDate(rawData.getString("date"));
                        blogItem.setTitle(rawData.getString("title"));
                        blogItem.setImage(rawData.getString("image"));
                        blogItem.setAuthor(rawData.getString("author"));
                        listBlockItems.add(blogItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ListView myListView = listView;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myListView.setAdapter(new BlogListAdapter(getContext(), listBlockItems));
                    }
                });
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
