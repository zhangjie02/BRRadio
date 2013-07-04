package com.example.brradio;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;



public class FacebookFrag extends Fragment {
	

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	private static final String TAG = "MainFragment";
	
	//private Session.StatusCallback callback;
	

	
	
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	
	private UiLifecycleHelper uiHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.facebook, container,false);
		
		/*
		Session session = Session.getActiveSession();

		if (session != null)
		{
			Log.i(TAG,"Close session in onCreateView");
			session.closeAndClearTokenInformation();
		}
		else
		{
			Log.i(TAG,"No cession in onCreateView");
		}
		
		callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
		*/
		
		final FacebookFrag frag = this;
		
		
		Button shareButton = (Button)view.findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	
		    	if (Session.getActiveSession() == null || !Session.getActiveSession().isOpened())
		    	{
		    		Session session = Session.openActiveSession(getActivity(), frag, true, callback);
				    Session.setActiveSession(session);
		    	}
		    	else
		    	{
		    		//publishStory();
		    		Session session = Session.openActiveSession(getActivity(), frag, true, callback);
		    		onSessionStateChange(session, session.getState(), null);
		    	}
		    	
		    	
		    }
		});
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
		
		
	    if (savedInstanceState != null) {
	        pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
	    }
	    
	    
	   

		return view;
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		
	    Session session = Session.getActiveSession();
	    if (session != null && (session.isOpened() || session.isClosed()) ) {
	         onSessionStateChange(session, session.getState(), null);
	    }
	    
		uiHelper.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.i(TAG,"onSessionStateChange : " + state);
		
	    if (state.isOpened())
	    {
	        Log.i(TAG, "Logged in...");
	        if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            pendingPublishReauthorization = false;
	            Log.i(TAG, "Publish call 1");
	            publishStory();
	        }
	        else
	        {
	        	Log.i(TAG, "Publish call 2");
	        	publishStory();
	        }
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }

	}
	
	

	
	private void publishStory() {
	    Session session = Session.getActiveSession();
	    Log.i(TAG,"publishStory()");
	    if (session != null){

	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	        	Log.i(TAG,"NewPermissionsRequest");
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
	            session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }

	        Bundle postParams = new Bundle();
	        postParams.putString("name", "Facebook SDK for Android");
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        postParams.putString("link", "https://developers.facebook.com/android");
	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	            	Log.i(TAG, "Response onCompleted : " + response);
	            	if (response.getGraphObject() == null) {
	            		Log.i(TAG, "GraphObject() is null !");
	            		FacebookRequestError error = response.getError();
	            		Log.i(TAG, "Error : " + error.getErrorMessage());
	            		return;
	            	}
	            	

	            	
	                JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG, "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Log.i(TAG, "Error : " + error.getErrorMessage());
	                    } else {
	                    Log.i(TAG, "Published with postid : " + postId);     
	                }
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams,HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }

	}
	
	

	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		//outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
}
