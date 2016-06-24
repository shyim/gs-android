package de.shyim.gameserver_sponsor.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClient;

public class BlogDetail extends Fragment {
    private OnFragmentInteractionListener mListener;
    private WebView webView;
    public static BlogDetail newInstance(String param1, String param2) {
        BlogDetail fragment = new BlogDetail();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_detail, container, false);
    }

    void setBlogId(Integer blogId) {
        ApiClient.get("blog/single/" + blogId.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String title = "";
                String content = "";

                try {
                    title = response.getString("title");
                    content = response.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String finalTitle = title;
                final String finalContent = content;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().setTitle(finalTitle);
                        webView.loadData(finalContent, "text/html; charset=utf-8", "utf-8");
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) view.findViewById(R.id.blog_detail);

        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}
