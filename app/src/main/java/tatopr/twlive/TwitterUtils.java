package tatopr.twlive;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import android.*;
import android.content.Context;
import android.content.SharedPreferences;

public class TwitterUtils {

    private static final String TOKEN="token";
    private static final String TOKEN_SECRET="token_secret";
    private static final String PREF_NAME="twitter_access_token";

    //get Twitter Instance and set access token if it is saved
    public static Twitter getTwitterInstance(Context context) {
        String consumerKey=context.getString(R.string.twitter_consumer_key);
        String consumerSecret=context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory=new TwitterFactory();
        Twitter twitter=factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if(hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }
        return twitter;
    }

    //save access token to preferences
    public static void storeAccessToken(Context context, AccessToken accesstoken) {
        SharedPreferences preferences=context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(TOKEN, accesstoken.getToken());
        editor.putString(TOKEN_SECRET, accesstoken.getTokenSecret());
        editor.commit();
    }

    //load access token from preferences
    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences=context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String token=preferences.getString(TOKEN, null);
        String tokenSecret=preferences.getString(TOKEN_SECRET, null);
        if(token!=null && tokenSecret!=null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    //judge whether access token is saved or not
    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context)!=null;
    }
}
