package com.ESHW4;

import java.util.ArrayList;

import com.ESHW4.R;
import com.ESHW4.FIleExplorer.FileExplorerItem;
import com.ESHW4.FIleExplorer.SerializableArrayList;
import com.ESHW4.VideoPlayer.CustomSeekBar;
import com.ESHW4.VideoPlayer.MarqueeTextView;
import com.ESHW4.VideoPlayer.VideoPlayer;
import com.ESHW4.VideoPlayer.VideoSurfaceView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class VideoPlayerActivity extends Activity {
	private ImageButton player_ibtn_play, player_ibtn_next, player_ibtn_previous,player_ibtn_extend, 
					    player_ibtn_folder, player_ibtn_lock, player_ibtn_unlock;
	private LinearLayout player_linearlayout_top, player_linearlayout_bottom;
	private CustomSeekBar player_seekbar;
	private VideoPlayer videoPlayer = new VideoPlayer();
	private VideoSurfaceView videoSurfaceView;
	private View player_view_navigator;
	private MarqueeTextView player_tv_title;
	private SurfaceHolder surfaceHolder;
	private ArrayList<FileExplorerItem> list;
	private int index = 0;
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE);
		
		setContentView(R.layout.video_player_main);

		player_linearlayout_top    = (LinearLayout) findViewById(R.id.player_linearlayout_top);
		player_linearlayout_bottom = (LinearLayout) findViewById(R.id.player_linearlayout_bottom);
		player_ibtn_play      = (ImageButton) findViewById(R.id.player_ibtn_play);
		player_ibtn_next	  = (ImageButton) findViewById(R.id.player_ibtn_next);
		player_ibtn_previous  = (ImageButton) findViewById(R.id.player_ibtn_previous);
		player_ibtn_extend    = (ImageButton) findViewById(R.id.player_ibtn_extend);	
		player_ibtn_folder    = (ImageButton) findViewById(R.id.player_ibtn_folder);	
		player_ibtn_lock	  = (ImageButton) findViewById(R.id.player_ibtn_lock);
		player_ibtn_unlock    = (ImageButton) findViewById(R.id.player_ibtn_unlock);
		videoSurfaceView      = (VideoSurfaceView) findViewById(R.id.videoSurfaceView);
		player_seekbar		  = (CustomSeekBar) findViewById(R.id.player_seekbar);
		player_view_navigator = (View) findViewById(R.id.player_view_navigator);
		player_tv_title = (MarqueeTextView) findViewById(R.id.player_tv_title);
		
		EventManager.setComponent(this);
		EventManager.setComponent(videoPlayer);
		EventManager.setComponent(videoSurfaceView);
		VideoSurfaceView.isFullscreen = true;
		
		surfaceHolder = videoSurfaceView.getHolder();		
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                videoPlayer.setDisplay(surfaceHolder);
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
            	EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
//                if(videoPlayer != null) 
//                	videoPlayer.release();
            }
        });
		
		player_linearlayout_top.setVisibility(View.GONE);
		player_linearlayout_bottom.setVisibility(View.GONE);
		player_seekbar.setVisibility(View.GONE);
		
		player_view_navigator.setOnTouchListener(EventManager.genVideoSurfaceViewTouchListener());
		player_ibtn_play.setOnClickListener(EventManager.genPlayBtnClickListener());
		player_ibtn_next.setOnClickListener(EventManager.genNextBtnClickListener());
		player_ibtn_previous.setOnClickListener(EventManager.genPreviousBtnClickListener());
		player_ibtn_extend.setOnClickListener(EventManager.genExtendBtnClickListener());
		player_ibtn_folder.setOnClickListener(EventManager.genFolderBtnClickListener());
		player_ibtn_lock.setOnClickListener(EventManager.genLockBtnClickListener());
		player_ibtn_unlock.setOnClickListener(EventManager.genUnlockBtnClickListener());
			
		Bundle bundle = getIntent().getExtras();
		index = bundle.getInt("currentIndex");
		SerializableArrayList s = (SerializableArrayList) bundle.getSerializable("list");
		list = s.getSerialzableArrayList();
		
		videoPlayer.setPlayList(list);
		videoPlayer.setCurrentIndex(index);
		videoPlayer.play(list.get(index).path);
		
		player_tv_title.scrollText(20);
		player_tv_title.setText(list.get(index).name);
		player_ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
		player_seekbar.setPlayer(videoPlayer);
		EventManager.seekbar_handler.post(EventManager.seekbar_runnable);
	}
	
	@Override
	public void onBackPressed() {
		EventManager.doVideoPlayerOnBackPressed();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    // TODO Auto-generated method stub
		Log.i("configureation", "changed");
	    super.onConfigurationChanged(newConfig);
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.i("landscape", "orientation is landscape");
	    }
	    else {
	    	Log.i("portail", "orientation is portail");
	    }
	}
    
    @Override
    public void onStart() {
        super.onStart();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.i("onResume", "on resume");
    	videoPlayer.state = VideoPlayer.STATE_PAUSE;
    	videoPlayer.play();
		videoPlayer.state = VideoPlayer.STATE_PLAYING;
		player_ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
		
		EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
		EventManager.seekbar_handler.post(EventManager.seekbar_runnable);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if(videoPlayer != null) {
        	try {
        		Log.i("onPause", "on pause try");
        		if(videoPlayer.isPlaying()) {
        			Log.i("onPause", "on pause video pause");
        			videoPlayer.pause();
					videoPlayer.state = VideoPlayer.STATE_PAUSE;
					player_ibtn_play.setImageResource(android.R.drawable.ic_media_play);
					
					EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
        		}
        	} catch(IllegalStateException e) {
        		Log.i("onPause", "on pause catch");
        		videoPlayer.release();
        	}
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.i("onStop", "on stop");
    }    
    
    @Override
    public void onRestart() {
        super.onRestart();
        Log.i("onRestart", "on restart");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
