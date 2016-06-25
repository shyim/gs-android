package de.shyim.gameserver_sponsor.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import de.shyim.gameserver_sponsor.R;
import de.shyim.gameserver_sponsor.connector.ApiClient;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);
        if (!sharedPreferences.getString("token", "").equals("")) {
            ApiClient.setToken(sharedPreferences.getString("token", ""));
            ApiClient.setLanguage(Locale.getDefault().getLanguage());

            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        }
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            RequestParams params = new RequestParams();
            params.put("email", mEmailView.getText().toString());
            params.put("password", mPasswordView.getText().toString());

            final LoginActivity activity = this;

            ApiClient.get("", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if(response.getBoolean("success")) {
                            SharedPreferences sharedPreferences = getSharedPreferences("gs3", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", response.getString("token"));
                            editor.putString("username", response.getString("username"));
                            editor.putString("email", response.getString("email"));
                            editor.apply();
                            final String toastMessage = response.getString("message");

                            ApiClient.setToken(response.getString("token"));
                            ApiClient.setLanguage(Locale.getDefault().getLanguage());

                            runOnUiThread(new Runnable() {
                                public void run()
                                {
                                    Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
                                    activity.showProgress(false);
                                    Intent myIntent = new Intent(activity, MainActivity.class);
                                    startActivity(myIntent);
                                }
                            });
                        } else {
                            final String toastMessage = getString(R.string.error_incorrect_password);

                            runOnUiThread(new Runnable() {
                                public void run()
                                {
                                    Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
                                    activity.showProgress(false);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.showProgress(false);
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    activity.showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    activity.showProgress(false);
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void openRegister(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gameserver-sponsor.de/register"));
        startActivity(browserIntent);
    }
}

