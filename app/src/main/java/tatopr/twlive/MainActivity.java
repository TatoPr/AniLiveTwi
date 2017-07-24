package tatopr.twlive;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import twitter4j.*;
import twitter4j.Twitter;

import com.loopj.android.image.SmartImageView;

public class MainActivity extends ListActivity {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TwitterUtils.hasAccessToken(this)) {
            Intent intent=new Intent(this, OAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            mAdapter=new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter=TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when menu selected judge which item was selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                reloadTimeLine();
                return true;
            case R.id.menu_tweet:
                Intent intent=new Intent(this, TweetActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {

        private LayoutInflater mInflater;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater =(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        //apply to view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= mInflater.inflate(R.layout.list_item_tweet,null);
            }
            Status item=getItem(position);
            TextView name=(TextView)convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView userid=(TextView)convertView.findViewById(R.id.userid);
            userid.setText("@"+item.getUser().getScreenName());
            TextView text=(TextView)convertView.findViewById(R.id.text);
            text.setText(item.getText());
            SmartImageView icon=(SmartImageView)convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return convertView;
        }
    }

    //get timeline and output listview
    private void reloadTimeLine() {
        AsyncTask<Void,Void, List<twitter4j.Status>> task=new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try{
                   return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override //after get timeline list in background
            protected void onPostExecute(List<twitter4j.Status> result) {
                if(result!=null) {
                    mAdapter.clear();
                    for(twitter4j.Status status:result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("failed to get TimeLine...");
                }
            }
        };
        task.execute();
    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}

