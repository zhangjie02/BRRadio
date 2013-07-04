package com.example.brradio;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

public class MainActivity extends SherlockFragmentActivity implements TabListener, PlayerCallback {

	// http://code.google.com/p/aacdecoder-android/
	// http://stackoverflow.com/questions/13746124/android-facebook-sdk-3-how-to-login-programatically-without-loginbutton
	
	

	public MultiPlayer multiPlayer;
	public boolean playerStarted;

	private Handler uiHandler;
	private String url;

	private ComponentName mRemoteControlResponder;
	
	private static final String TAG = "MainFragment";
	
	
   
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		
		
		
		playerStarted = false;

		uiHandler = new Handler();

		// url = "http://midiaserver.tvcultura.com.br:8001"; // Radio Cultura
		// url = "http://fr3.ah.fm:9000/"; // AH FM
		url = "http://67.228.135.41:8124"; // Metropolitana FM

		// Set up the action bar to show tabs.
		//final ActionBar actionBar = getSupportActionBar();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
	
		// For each of the sections in the app, add a tab to the action bar.
		getSupportActionBar().addTab(getSupportActionBar().newTab().setCustomView(R.layout.tab).setTabListener(this));
		getSupportActionBar().addTab(getSupportActionBar().newTab().setCustomView(R.layout.tab).setTabListener(this));
		getSupportActionBar().addTab(getSupportActionBar().newTab().setCustomView(R.layout.tab).setTabListener(this));
		getSupportActionBar().addTab(getSupportActionBar().newTab().setCustomView(R.layout.tab).setTabListener(this));
		getSupportActionBar().addTab(getSupportActionBar().newTab().setCustomView(R.layout.tab).setTabListener(this));
	}
	


	@Override
	protected void onPause() {
		super.onPause();
		//uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((AudioManager) getSystemService(Context.AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(mRemoteControlResponder);
		stop();
		//uiHelper.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).registerMediaButtonEventReceiver(mRemoteControlResponder);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data); 
	} 
	
	
	

	public void start() {
		stop();

		multiPlayer = new MultiPlayer(this);
		multiPlayer.playAsync(url);
	}

	public void stop() {
		if (multiPlayer != null) {
			multiPlayer.stop();
			multiPlayer = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.myLayout);
		// enlever commentaire si on veux utiliser un autre layout
	}

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
		
		//uiHelper.onSaveInstanceState(outState);
	}
	

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
*/
	// -------------------------------------------------------------------------------------
	// TabBar
	// -------------------------------------------------------------------------------------

	@Override
	public void onTabSelected(Tab tab,FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
		case 0:getSupportFragmentManager().beginTransaction().replace(R.id.container, new RadioFrag()).commit();break;
		case 1:getSupportFragmentManager().beginTransaction().replace(R.id.container, new SiteFrag()).commit();break;
		case 2:getSupportFragmentManager().beginTransaction().replace(R.id.container, new NewsFrag()).commit();break;
		case 3:getSupportFragmentManager().beginTransaction().replace(R.id.container, new FacebookFrag()).commit();break;
		case 4:getSupportFragmentManager().beginTransaction().replace(R.id.container, new TwitterFrag()).commit();break;
		default:
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(Tab tab,FragmentTransaction fragmentTransaction) {
	}

	// -------------------------------------------------------------------------------------
	// PlayerCallback
	// -------------------------------------------------------------------------------------

	@Override
	public void playerException(final Throwable arg0) {
		// System.out.print("Player error : " + arg0.getLocalizedMessage());
		Log.d(TAG, "Player error : " + arg0.getLocalizedMessage());
	}

	@Override
	public void playerMetadata(String arg0, String arg1) {
		System.out.print("Key : " + arg0 + " with value : " + arg1);
		// Toast.makeText(this, "Key : " + arg0 + " with value : " + arg1,
		// Toast.LENGTH_LONG).show();
		Log.d(TAG, "Key : " + arg0 + " with value : " + arg1);
	}

	@Override
	public void playerPCMFeedBuffer(boolean arg0, int arg1, int arg2) {
	}

	@Override
	public void playerStarted() {
		playerStarted = true;
		uiHandler.post(new Runnable() {
			public void run() {
				if (getSupportActionBar().getSelectedNavigationIndex() == 0)
					((RadioFrag) getSupportFragmentManager().findFragmentById(
							R.id.container)).refreshPlayerUI();
			}
		});
	}
	
	
	@Override
    protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            Log.i(TAG, "New Intent Arrived");
            //dealWithTwitterResponse(intent);
            if (getSupportActionBar().getSelectedNavigationIndex() == 4)
				((TwitterFrag) getSupportFragmentManager().findFragmentById(R.id.container)).onNewIntent(intent);
    }
	
	
	

	@Override
	public void playerStopped(int arg0) {
		playerStarted = false;
		uiHandler.post(new Runnable() {
			public void run() {
				if (getSupportActionBar().getSelectedNavigationIndex() == 0)
					((RadioFrag) getSupportFragmentManager().findFragmentById(
							R.id.container)).refreshPlayerUI();
			}
		});
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode()
	 * == KeyEvent.KEYCODE_VOLUME_DOWN) { return false;
	 * 
	 * } return super.onKeyDown(keyCode, event); }
	 * 
	 * 
	 * @Override public boolean onKeyUp (int keyCode, KeyEvent event) { if
	 * (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode()
	 * == KeyEvent.KEYCODE_VOLUME_DOWN) { if
	 * (getActionBar().getSelectedNavigationIndex() == 0)
	 * ((RadioFrag)getSupportFragmentManager
	 * ().findFragmentById(R.id.container)).refreshPlayerUI();
	 * 
	 * return false;
	 * 
	 * } return super.onKeyDown(keyCode, event); }
	 */
	/*
	 * @Override public boolean dispatchKeyEvent(KeyEvent event) { int action =
	 * event.getAction(); int keyCode = event.getKeyCode(); switch (keyCode) {
	 * case KeyEvent.KEYCODE_VOLUME_UP: if (action == KeyEvent.ACTION_UP) { if
	 * (getActionBar().getSelectedNavigationIndex() == 0)
	 * ((RadioFrag)getSupportFragmentManager
	 * ().findFragmentById(R.id.container)).refreshPlayerUI(); } return false;
	 * case KeyEvent.KEYCODE_VOLUME_DOWN: if (action == KeyEvent.ACTION_DOWN) {
	 * if (getActionBar().getSelectedNavigationIndex() == 0)
	 * ((RadioFrag)getSupportFragmentManager
	 * ().findFragmentById(R.id.container)).refreshPlayerUI(); } return false;
	 * default: return super.dispatchKeyEvent(event); } }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, 0);
			if (getSupportActionBar().getSelectedNavigationIndex() == 0)
				((RadioFrag) getSupportFragmentManager().findFragmentById(
						R.id.container)).refreshPlayerUI();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, 0);
			if (getSupportActionBar().getSelectedNavigationIndex() == 0)
				((RadioFrag) getSupportFragmentManager().findFragmentById(
						R.id.container)).refreshPlayerUI();
			return true;
		default:
			return false;
		}
	}



}
