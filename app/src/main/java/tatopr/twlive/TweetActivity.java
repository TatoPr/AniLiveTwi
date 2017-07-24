package tatopr.twlive;

import android.*;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.*;

public class TweetActivity extends Activity {

    private EditText mInputText;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        mTwitter=TwitterUtils.getTwitterInstance(this);

        mInputText=(EditText)findViewById(R.id.input_text);

        findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });
    }

    private void tweet() {
        AsyncTask<String, Void, Boolean> task=new AsyncTask<String, Void, Boolean>() {
            @Override //send tweet
            protected Boolean doInBackground(String... params) {
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    showToast("Successfully tweeted!");
                    finish();
                } else {
                    showToast("Failed to tweet...");
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
