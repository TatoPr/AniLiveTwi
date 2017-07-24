package tatopr.twlive;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.*;
import android.view.View;
import android.widget.Toast;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OAuthActivity extends android.app.Activity {
    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        mCallbackURL=getString(R.string.twitter_callback_url);
        mTwitter=TwitterUtils.getTwitterInstance(this);

        findViewById(R.id.action_start_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorize();
            }
        });
    }

    //start OAuth authorize
    private void startAuthorize() {
        AsyncTask<Void, Void, String> task=new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken=mTwitter.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String url) {
                if(url!=null) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {

                }
            }
        };
        task.execute();
    }

    public void onNewIntent(Intent intent) {
        if(intent==null
                ||intent.getData()==null
                ||!intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier=intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task=new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(AccessToken accessToken) {
                if(accessToken!=null){
                    showToast("Successfully authorized!");
                    successOAuth(accessToken);
                } else {
                    showToast("Failed...");
                }
            }
        };
        task.execute(verifier);
    }

    //after authorization, save access token and back main activity
    private void successOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(this, accessToken);
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //make text box
    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
