package com.example.brradio;

import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class TwitterFrag extends Fragment {
	
	// http://blog.blundell-apps.com/sending-a-tweet/
	// http://www.androidhive.info/2012/09/android-twitter-oauth-connect-tutorial/

	
    private static final String PREF_ACCESS_TOKEN = "accessToken";
    private static final String PREF_ACCESS_TOKEN_SECRET = "accessTokenSecret";
    private static final String CONSUMER_KEY = "unXOKeLGei0L29s9vylQ";
    private static final String CONSUMER_SECRET = "TgBKzjZlXhh6fs7fRqP7xUOrecD8wnWTHNhcN9iek"; 
    private static final String CALLBACK_URL = "t4jsample:///";
    private SharedPreferences mPrefs;
    private Twitter mTwitter;
    private RequestToken mReqToken;
    
    private WebView mWebViewLogin;
    
    
    private static final String TAG = "MainFragment";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		/*
		// open Twitter for typing message
		String tweetUrl = "https://twitter.com/intent/tweet?text=PUT TEXT HERE &url=" + "https://www.google.com";
		Uri uri = Uri.parse(tweetUrl);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
		 */
		
		View view = inflater.inflate(R.layout.twitter, container,false);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		
		Button tweetButton = (Button)view.findViewById(R.id.tweetButton);
		tweetButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	
		        mPrefs = getActivity().getSharedPreferences("twitterPrefs", 0);
		        mTwitter = new TwitterFactory().getInstance();
		        mTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
				
		        if (mPrefs.contains(PREF_ACCESS_TOKEN)) {
		            loginAuthorisedUser();
		        } else {
		            loginNewUser();
		        }
		    }
		});  
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();	
		
		//Intent intent1 = getActivity().getIntent();
		//Intent intent2 = getActivity().getParentActivityIntent();
		
		
	    dealWithTwitterResponse(getActivity().getIntent());
	}
	
	private void loginNewUser() {
        try {
                mReqToken = mTwitter.getOAuthRequestToken(CALLBACK_URL);

                // Add Login webview to current fragment's view
                /*
                if (mWebViewLogin == null)
                	mWebViewLogin = new WebView(getActivity());
                //mWebViewLogin.setWebViewClient(new WebViewClient());
                mWebViewLogin.requestFocus(View.FOCUS_DOWN);
                mWebViewLogin.loadUrl(mReqToken.getAuthenticationURL());
                ((ViewGroup)getView().findViewById(R.id.viewGroup)).addView(mWebViewLogin);
                */
                
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReqToken.getAuthenticationURL())));

        } catch (TwitterException e) {
                Log.i(TAG, "TwitterException : " + e.getLocalizedMessage());
        }
    }
    
    private void loginAuthorisedUser() {
        String token = mPrefs.getString(PREF_ACCESS_TOKEN, null);
        String secret = mPrefs.getString(PREF_ACCESS_TOKEN_SECRET, null);

        // Create the twitter access token from the credentials we got previously
        mTwitter.setOAuthAccessToken(new AccessToken(token, secret));
        
        // Try to send tweet
        sendTweet();
    }
    
    private void sendTweet()
    {
    	try {
       	 	Date date = new Date();
       	 	mTwitter.updateStatus(date.toString());
       	 	Log.i(TAG,"Tweet Successful!" ); 
       	 	Toast.makeText(getActivity(),"Tweet Successful!",Toast.LENGTH_SHORT).show();
       }
       catch (TwitterException e)
       {
       		Log.i(TAG, "TwitterException : " + e.getLocalizedMessage());
       }
    }
  
    public void onNewIntent(Intent intent) {
            Log.i(TAG, "New Intent Arrived");
            dealWithTwitterResponse(intent);
    }
    
    private void saveAccessToken(AccessToken at) {
        String token = at.getToken();
        String secret = at.getTokenSecret();
        Editor editor = mPrefs.edit();
        editor.putString(PREF_ACCESS_TOKEN, token);
        editor.putString(PREF_ACCESS_TOKEN_SECRET, secret);
        editor.commit();
    }
    
    private void dealWithTwitterResponse(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) { // If the user has just logged in
                String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                authoriseNewUser(oauthVerifier);
        }
    }
    
    private void authoriseNewUser(String oauthVerifier) {
    	
    	// Set the content view back after we changed to a webview
        ((ViewGroup)mWebViewLogin.getParent()).removeView(mWebViewLogin);
        
        if (oauthVerifier == null)
        {
        	Log.i(TAG, "Login cancelled !");
        	return;
        }
    	
        try {
                AccessToken at = mTwitter.getOAuthAccessToken(mReqToken, oauthVerifier);
                mTwitter.setOAuthAccessToken(at);
                saveAccessToken(at);

                

                // Try to send tweet
                sendTweet();
                
        } catch (TwitterException e) {
        	Log.i(TAG, "TwitterException : " + e.getLocalizedMessage());
        }
    }
	
}
