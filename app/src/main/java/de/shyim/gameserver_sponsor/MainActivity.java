package de.shyim.gameserver_sponsor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.shyim.gameserver_sponsor.Model.Gameserver;

public class MainActivity extends ApiActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mUsername;
    private TextView mEmail;
    private ImageView mImageView;
    private Menu mMenu;
    private NavigationView navigationView;
    private ArrayList<Gameserver> gameservers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        mMenu = navigationView.getMenu();

        mUsername = (TextView) header.findViewById(R.id.navHeaderUsername);
        mEmail = (TextView) header.findViewById(R.id.navHeaderEmail);
        mImageView = (ImageView) header.findViewById(R.id.navHeaderAvatar);

        SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);

        mUsername.setText(sharedPreferences.getString("username", ""));
        mEmail.setText(sharedPreferences.getString("email", ""));

        setTitle("Dashboard");

        new ApiClient(this, "/index/avatar", sharedPreferences.getString("token", ""), new JSONObject(), "avatar").execute();
        new ApiClient(this, "/server", sharedPreferences.getString("token", ""), new JSONObject(), "server").execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        setTitle(item.getTitle());
        return true;
    }

    @Override
    public void onApiResponse(JSONObject object, String action) {
        if (action.equals("server")) {
            try {
                if (object.getBoolean("success")) {
                    final ArrayList<JSONObject> servers = new ArrayList<JSONObject>();
                    JSONArray server = object.getJSONArray("server");
                    for (int i = 0; i < server.length(); i++) {
                        servers.add(server.getJSONObject(i));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < servers.size(); i++) {
                                try {
                                    MenuItem item = mMenu.getItem(1).getSubMenu().add(servers.get(i).getString("IP") + ":" + servers.get(i).getString("Port"));
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
            System.out.println(object.toString());
        } else {
            String md5;
            String avatarUrl;
            try {
                md5 = object.getString("avatarMd5");
                avatarUrl = object.getString("avatar");
            } catch (JSONException e) {
                md5 = null;
                avatarUrl = null;
            }
            File f = new File(getFilesDir() + "/avatar.png");
            if (f.exists()) {
                Bitmap bm = null;
                try {
                    FileInputStream inputStream = new FileInputStream(f);
                    bm = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
        }
    }
}