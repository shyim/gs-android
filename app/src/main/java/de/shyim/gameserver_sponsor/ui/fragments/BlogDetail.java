package de.shyim.gameserver_sponsor.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClientFragment;

public class BlogDetail extends BaseFragment {
    private OnFragmentInteractionListener mListener;
    private Integer blogId;
    public WebView webView;
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

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;

        new ApiClientFragment(this, "/blog/single/" + blogId.toString(), new JSONObject(), "").execute();
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

    @Override
    public void onApiResponse(JSONObject object, String action) {
        String title = "";
        String content = "";

        try {
            title = object.getString("title");
            content = object.getString("content");
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}
