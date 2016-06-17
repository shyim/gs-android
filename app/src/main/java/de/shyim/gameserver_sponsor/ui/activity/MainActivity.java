package de.shyim.gameserver_sponsor.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.shyim.gameserver_sponsor.cache.ImageCache;
import de.shyim.gameserver_sponsor.connector.ApiClientActivity;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.ui.fragments.BlogList;
import de.shyim.gameserver_sponsor.ui.fragments.GPFragment;
import de.shyim.gameserver_sponsor.ui.fragments.ServerFragment;
import de.shyim.gameserver_sponsor.task.DownloadImagesTask;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ServerFragment.OnFragmentInteractionListener, BlogList.OnFragmentInteractionListener {

    private ImageView mImageView;
    private Menu mMenu;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private Integer currentGS = null;
    private DrawerLayout drawer = null;
    private MenuItem prevMenuItem = null;
    private Integer menuServerId = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabs_layout);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        mMenu = navigationView.getMenu();

        TextView mUsername = (TextView) header.findViewById(R.id.navHeaderUsername);
        TextView mEmail = (TextView) header.findViewById(R.id.navHeaderEmail);
        mImageView = (ImageView) header.findViewById(R.id.navHeaderAvatar);

        SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);
        mUsername.setText(sharedPreferences.getString("username", ""));
        mEmail.setText(sharedPreferences.getString("email", ""));

        setTitle("Blog");

        BlogList newFragment = new BlogList();
        Bundle args = new Bundle();
        newFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentMain, newFragment);
        transaction.commit();
        setMenuItem(mMenu.getItem(0));

        ImageCache.keys = new ArrayList<>();
        ImageCache.images = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ApiClientActivity(this, "/index/avatar", new JSONObject(), "avatar").execute();
        new ApiClientActivity(this, "/server", new JSONObject(), "server").execute();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_blog:
                BlogList newFragment = new BlogList();
                Bundle args = new Bundle();
                newFragment.setArguments(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentMain, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_gp:
                GPFragment gpFragment = new GPFragment();
                FragmentTransaction transactionGP = getSupportFragmentManager().beginTransaction();
                transactionGP.replace(R.id.contentMain, gpFragment);
                transactionGP.addToBackStack(null);
                transactionGP.commit();
                break;
            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                editor.commit();

                Intent mStartActivity = new Intent(this, LoginActivity.class);
                PendingIntent mPendingIntent = PendingIntent.getActivity(this, 123456, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
                break;
            case R.id.nav_share_us:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.ShareText));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.ChooseIntent)));
                return true;
            case R.id.nav_bug_report:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"android@gameserver-sponsor.de"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bug in App");
                intent.putExtra(Intent.EXTRA_TEXT, "Message");

                startActivity(Intent.createChooser(intent, getString(R.string.ChooseIntent)));

                return true;
        }

        if (!item.getTitle().toString().contains(":")) {
            this.currentGS = null;
            tabLayout.setVisibility(View.GONE);
        } else {
            setMenuItem(item);
        }

        setTitle(item.getTitle());

        return true;
    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        switch (action) {
            case "server":
                try {
                    if (object.getBoolean("success")) {
                        final ArrayList<JSONObject> servers = new ArrayList<JSONObject>();
                        JSONArray server = object.getJSONArray("server");
                        for (int i = 0; i < server.length(); i++) {
                            servers.add(server.getJSONObject(i));
                        }

                        final MainActivity activity = this;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * Remove all Server Items, before adding new
                                 */
                                mMenu.getItem(activity.menuServerId).getSubMenu().clear();
                                for (int i = 0; i < servers.size(); i++) {
                                    try {
                                        final MenuItem item = mMenu.getItem(activity.menuServerId).getSubMenu().add(servers.get(i).getString("IP") + ":" + servers.get(i).getString("Port"));
                                        final Integer gsID = Integer.valueOf(servers.get(i).getString("id"));
                                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                setMenuItem(item);
                                                drawer.closeDrawer(GravityCompat.START);
                                                setTitle(item.getTitle());
                                                /**
                                                 * Change Fragment to Server
                                                 */
                                                ServerFragment newFragment = new ServerFragment();
                                                Bundle args = new Bundle();
                                                args.putInt("gsID", gsID);
                                                currentGS = gsID;
                                                newFragment.setArguments(args);
                                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.contentMain, newFragment);
                                                transaction.commit();

                                                return true;
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } else {
                        /**
                         * Not loggedin
                         */
                        SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent myIntent = new Intent(this, LoginActivity.class);
                        startActivity(myIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "avatar":
                String avatarUrl;
                try {
                    avatarUrl = object.getString("avatar");
                } catch (JSONException e) {
                    avatarUrl = null;
                }
                File f = new File(getFilesDir() + "/avatar.png");
                if (f.exists()) {
                    Bitmap bm = null;
                    try {
                        FileInputStream inputStream = new FileInputStream(f);
                        bm = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final Bitmap bm2 = bm;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bm2);
                        }
                    });
                } else {
                    new DownloadImagesTask(mImageView, getFilesDir() + "/avatar.png").execute(avatarUrl);
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    private void setMenuItem(MenuItem item)
    {
        if (prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        }
        item.setChecked(true);
        prevMenuItem = item;
    }
}