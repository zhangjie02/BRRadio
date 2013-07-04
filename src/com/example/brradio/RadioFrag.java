package com.example.brradio;


import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class RadioFrag extends Fragment implements OnSeekBarChangeListener, OnClickListener, AnimationListener {
	
	private Button btPlay;
	private Button btPause;
	private TextView textView;
	private SeekBar seekBar;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View view = inflater.inflate(R.layout.radio, container,false);

		btPlay =  (Button) view.findViewById(R.id.btPlay);
	    btPause = (Button) view.findViewById(R.id.btPause);
	    textView = (TextView) view.findViewById(R.id.textView);
	    
		AudioManager audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
		
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int val = 100 * currentVolume / maxVolume;
		
		seekBar = (SeekBar) view.findViewById(R.id.seekBar); 
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setProgress(val);
		
		btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        
        refreshPlayerUI();
        
		return view;
	}
	
	
	
	
	@Override
	public void onClick(View view)
	{
		switch (view.getId()) {
			case R.id.btPlay: clickPlay(); break;
			case R.id.btPause: clickStop(); break;
		}
		
		// tests animations
		/*
		// important : do not initialize with setAlpha, uase an animation
		AlphaAnimation alpha = new AlphaAnimation(0.0F, 0.0F);
	    alpha.setDuration(0); // Make animation instant
	    alpha.setFillAfter(true); // Tell it to persist after the animation ends
	    btPlay.startAnimation(alpha);
	    
		Animation show = AnimationUtils.loadAnimation(getActivity(), R.anim.show);
		show.setAnimationListener(this);
	    btPlay.startAnimation(show);
	    //btPlay.clearAnimation();
	    */
	}
	

	public void refreshPlayerUI()
	{
		MainActivity mainActivity = (MainActivity)getActivity();
		
		textView.setText(mainActivity.playerStarted?"Playing":"Stopped");
        btPlay.setEnabled(!mainActivity.playerStarted);
        btPause.setEnabled(mainActivity.playerStarted);  
        
        AudioManager audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
		
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int val = 100 * currentVolume / maxVolume;
		
		seekBar.setProgress(val);
		
		Log.d("AAA", "Valeur : " + val);
	}
	
	
	private void clickPlay()
	{
		MainActivity mainActivity = (MainActivity)getActivity();
		mainActivity.start();
		textView.setText("Starting...");
		//refreshPlayerUI();
	}
	
	private void clickStop()
	{
		MainActivity mainActivity = (MainActivity)getActivity();
		mainActivity.stop();
		textView.setText("Stopping...");
		//refreshPlayerUI();
	}
	
	
	//-------------------------------------------------------------------------------------
	//	ProgressBar
	//-------------------------------------------------------------------------------------
	
	@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{		
		AudioManager audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
		//int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//int val = 100 * currentVolume / maxVolume;
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress * maxVolume / 100, 0);
    }
	

	@Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
	}
	
	
	//-------------------------------------------------------------------------------------
	//	AnimationListener
	//-------------------------------------------------------------------------------------

	@Override
	public void onAnimationEnd(Animation animation) {}

	@Override
	public void onAnimationRepeat(Animation animation) {}

	@Override
	public void onAnimationStart(Animation animation) {}
	
	
	
	

}
