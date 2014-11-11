package com.ESHW4;

import java.util.ArrayList;
import com.ESHW4.FIleExplorer.FileExplorerItem;
import com.ESHW4.FIleExplorer.FileExplorerListAdapter;
import com.ESHW4.FIleExplorer.FileExplorerManager;
import com.ESHW4.FIleExplorer.SerializableArrayList;
import com.ESHW4.VideoPlayer.CustomSeekBar;
import com.ESHW4.VideoPlayer.MarqueeTextView;
import com.ESHW4.VideoPlayer.VideoPlayer;
import com.ESHW4.VideoPlayer.VideoSurfaceView;
import com.ESHW4.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventManager {
	protected static final int SerializableArrayList = 0;
	public static FileExplorerActivity fileExplorerActivity = null;
	public static VideoPlayerActivity  videoPlayerActivity  = null;
	public static VideoPlayer 	       videoPlayer          = null;
	public static VideoSurfaceView 	   videoSurfaceView     = null;
	public static ImageButton 		   ibtn_play            = null;
	public static ImageButton 		   ibtn_unlock          = null;
	public static MarqueeTextView      tv_title 		    = null;
	public static CustomSeekBar        seekbar              = null;
	public static LinearLayout         player_linearlayout_top    = null;
	public static LinearLayout         player_linearlayout_bottom = null;
	public static View 			       view_navigator             = null;
	
	public static AudioManager audioManager = null; 
	
	private static boolean isLock = false;
	
	public static Handler seekbar_handler = new Handler();
	public static Runnable seekbar_runnable = new Runnable() {
		@Override
		public void run() {
			if(seekbar != null) {
				seekbar.invalidate();
				seekbar_handler.postDelayed(this, 500);
			}
		}	
	};
	
	private static Handler lock_handler = new Handler();
	private static Runnable lock_runnable = new Runnable() {
		@Override
		public void run() {
			ibtn_unlock.setVisibility(View.GONE);
		}	
	};
	
	public static void setComponent(Object obj) {
		if(obj.getClass() == FileExplorerActivity.class)
			fileExplorerActivity = (FileExplorerActivity) obj;
		else if(obj.getClass() == VideoPlayerActivity.class) {
			videoPlayerActivity = (VideoPlayerActivity) obj;
			ibtn_play = (ImageButton) videoPlayerActivity.findViewById(R.id.player_ibtn_play);
			ibtn_unlock = (ImageButton) videoPlayerActivity.findViewById(R.id.player_ibtn_unlock);
			tv_title = (MarqueeTextView) videoPlayerActivity.findViewById(R.id.player_tv_title);
			seekbar = (CustomSeekBar) videoPlayerActivity.findViewById(R.id.player_seekbar);
			player_linearlayout_top = (LinearLayout) videoPlayerActivity.findViewById(R.id.player_linearlayout_top);
			player_linearlayout_bottom = (LinearLayout) videoPlayerActivity.findViewById(R.id.player_linearlayout_bottom);
			view_navigator = (View) videoPlayerActivity.findViewById(R.id.player_view_navigator);
			
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view_navigator.getLayoutParams();
			lp.weight = 5;
			view_navigator.setLayoutParams(lp);
			
			LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams)seekbar.getLayoutParams();
			lp2.weight = 0.5f;
			seekbar.setLayoutParams(lp2);
			
			audioManager = (AudioManager) videoPlayerActivity.getSystemService(Context.AUDIO_SERVICE);
		}
		else if(obj.getClass() == VideoPlayer.class)
			videoPlayer = (VideoPlayer) obj;
		else if(obj.getClass() == VideoSurfaceView.class)
			videoSurfaceView = (VideoSurfaceView) obj;
	}

	// FileExplorer events
	public static OnItemClickListener genFileExplorerListOnItemClickListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileExplorerListAdapter adapter = (FileExplorerListAdapter) parent.getAdapter();
				
				if(adapter.getItemViewType(position) == FileExplorerListAdapter.TYPE_SEPARATOR) {
					FileExplorerManager fileManager = new FileExplorerManager();
					FileExplorerListAdapter newAdapter = new FileExplorerListAdapter(fileExplorerActivity);
					ListView listview = (ListView) fileExplorerActivity.findViewById(R.id.explorer_lv_list);
					ArrayList<FileExplorerItem> list;
					
					fileManager.setRootPath(adapter.getItem(position).path);			
					list = fileManager.getPlayList();
					
					for(int i = 0; i < list.size(); i++) {
						if(list.get(i).type == FileExplorerItem.FILE) {
							newAdapter.addItem(list.get(i));
						}
						else if(list.get(i).type == FileExplorerItem.DIR){
							newAdapter.addSectionHeaderItem(list.get(i));
						}
					}
					fileExplorerActivity.setTitle(adapter.getItem(position).path);
					listview.setAdapter(newAdapter);
				}
				else {
					for(int i = 0; i < adapter.getCount(); i++) {
						if(adapter.getItem(i).type == FileExplorerItem.FILE) {
							break;
						}
						else if(adapter.getItem(i).type == FileExplorerItem.DIR){
							position -= 1;
						}
					}
					SerializableArrayList se = new SerializableArrayList(fileExplorerActivity.getList());
					
					Intent intent = new Intent();
					intent.setClass(fileExplorerActivity, VideoPlayerActivity.class);
					
					Bundle bundle = new Bundle();
					bundle.putInt("currentIndex", position);
					bundle.putSerializable("list", se);
					intent.putExtras(bundle);
					
					fileExplorerActivity.startActivity(intent);
					fileExplorerActivity.finish();
				}
			}
		};
	}
	
	public static void doOnBackPressed() {
		ListView listview = (ListView) fileExplorerActivity.findViewById(R.id.explorer_lv_list);
		listview.performItemClick(listview.getAdapter().getView(0, null, null), 0, listview.getAdapter().getItemId(0));	
	}
	
	// VideoPlayer events
	public static void doVideoPlayerOnBackPressed() {
		seekbar_handler.removeCallbacks(seekbar_runnable);
		videoPlayer.state = VideoPlayer.STATE_STOP;
		videoPlayer.release();
		Intent intent = new Intent();
		intent.setClass(videoPlayerActivity, FileExplorerActivity.class);
		videoPlayerActivity.startActivity(intent);
		videoPlayerActivity.finish();
	}
	
	public static OnTouchListener genVideoSurfaceViewTouchListener() {
		return new OnTouchListener() {
			private int x1, y1, x2, y2;
			private int distance = 0, origin = 0;
			private float r = 1;
			private int videoWidth, videoHeight;
			private RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
					 RelativeLayout.LayoutParams.WRAP_CONTENT);
			private final static int IDLE = -1;
			private final static int SEEKTO = 0;
			private final static int UI_TOOGLE = 1;
			private final static int ZOOM = 2;
			private int state = UI_TOOGLE;
			private float start_x = 0, start_y = 0;
			
			int audioVolume, brightness;
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isLock) {
					ibtn_unlock.setVisibility(View.VISIBLE);
					lock_handler.postDelayed(lock_runnable, 2000);
					return false;
				}
			    int pointerCount = event.getPointerCount();
			    int maskedAction = event.getActionMasked();

			    switch (maskedAction) {
				    case MotionEvent.ACTION_DOWN:
				    	start_x = event.getX();
				    	start_y = event.getY();
				    	state = UI_TOOGLE;

				    	audioVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				    	try {
				    		brightness = android.provider.Settings.System.getInt(videoPlayerActivity.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
				    	} catch (SettingNotFoundException e) {
				    		e.printStackTrace();
				    	}
				    	break;
				    case MotionEvent.ACTION_POINTER_DOWN: {
				    	if(pointerCount == 2) {
				    		state = ZOOM;
				    		videoWidth = videoSurfaceView.getWidth();
					    	videoHeight = videoSurfaceView.getHeight();
					    	lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
							lp.addRule(RelativeLayout.CENTER_VERTICAL);
							x1 = (int) event.getX(0);	y1 = (int) event.getY(0);  
							x2 = (int) event.getX(1); 	y2 = (int) event.getY(1);
							origin = (int) (Math.sqrt( Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
				    	}
				    	break;
				    }
				    case MotionEvent.ACTION_MOVE: {
				    	if(pointerCount == 1) {
				    		float w = (start_x - event.getX()) * 0.5f;
				    		float h = (start_y - event.getY());
				    		if(w > 30 || w < -30) {
				    			state = SEEKTO;
				    			seekbar.setProcessShift((int) w);
				    		}
				    		else if((h > 10 || h < - 10) && state != SEEKTO) {	
				    			if(event.getX() >= v.getWidth() / 2) {
				    				int vol = (int)(audioVolume + h / 30);
				    				if(vol >= 15) vol = 15;
				    				else if(vol <= 0) vol = 0;
				    				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
				    				CustomToast.showToast(videoPlayerActivity, "volume :" + vol, 100);
				    			}
				    			else {
				    				int bri = (int)(brightness + h / 5);
				    				if(bri >= 255) bri = 255;
				    				else if(bri <= 0) bri = 0;
				    				android.provider.Settings.System.putInt(videoPlayerActivity.getContentResolver(),
				    						android.provider.Settings.System.SCREEN_BRIGHTNESS, bri);
				    				String str = (bri / (255 / 15)) + "";
				    				CustomToast.showToast(videoPlayerActivity, "brightness :" + str, 100);
				    			}
				    		}
				    	}
				    	else if(pointerCount == 2 && state == ZOOM) {
				    		x1 = (int) event.getX(0);	y1 = (int) event.getY(0);  
							x2 = (int) event.getX(1); 	y2 = (int) event.getY(1);
				    		distance = (int) (Math.sqrt( Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));		    		
				    		r = (float) distance / (float) origin;    		
				    		if(r >= 3)
								r = 3;
							if(r <= 0.5)
								r = (float) 0.5;
							
							lp.width = (int)((float)videoWidth * r);
							lp.height = (int)((float)videoHeight * r);
							videoSurfaceView.setLayoutParams(lp);
				    	}
				    	break;
				    }
				    case MotionEvent.ACTION_UP:
				    	if(pointerCount == 1 && state != ZOOM) {
				    		if(state == SEEKTO) {
				    			seekbar.setAuto();
				    			if(VideoSurfaceView.isFullscreen) {
				    				seekbar.setVisibility(View.GONE);
				    			}
				    		}
				    		else 
				    			system_UI_toggle();
				    		state = IDLE;
				    	}
				    	break;
				    case MotionEvent.ACTION_POINTER_UP:
				    case MotionEvent.ACTION_CANCEL: {
				    	break;
				    }
			    }
			    return true;
			}
		};
	}

	public static OnClickListener genPlayBtnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(videoPlayer.state == VideoPlayer.STATE_READY) {
					videoPlayer.play();
					videoPlayer.state = VideoPlayer.STATE_PLAYING;
					ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
					
					seekbar_handler.removeCallbacks(seekbar_runnable);
					seekbar_handler.post(seekbar_runnable);
				}
				else if(videoPlayer.state == VideoPlayer.STATE_PLAYING) {
					videoPlayer.pause();
					videoPlayer.state = VideoPlayer.STATE_PAUSE;
					ibtn_play.setImageResource(android.R.drawable.ic_media_play);
					
					seekbar_handler.removeCallbacks(seekbar_runnable);
				}
				else if(videoPlayer.state == VideoPlayer.STATE_PAUSE) {
					videoPlayer.play();
					videoPlayer.state = VideoPlayer.STATE_PLAYING;
					ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
					
					seekbar_handler.removeCallbacks(seekbar_runnable);
					seekbar_handler.post(seekbar_runnable);
				}
			}
		};
	}
	
	public static OnClickListener genNextBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(videoPlayer.state == VideoPlayer.STATE_STOP)
					return;
				videoPlayer.state  = VideoPlayer.STATE_CHANGING;
				videoPlayer.next();
				tv_title.setText(videoPlayer.getVideoTitle());
				
				ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
				
				seekbar_handler.removeCallbacks(seekbar_runnable);
				seekbar_handler.post(seekbar_runnable);
			}
		};
	}
	
	public static OnClickListener genPreviousBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				videoPlayer.state  = VideoPlayer.STATE_CHANGING;
				videoPlayer.previous();
				tv_title.setText(videoPlayer.getVideoTitle());
				
				ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
				
				seekbar_handler.removeCallbacks(seekbar_runnable);
				seekbar_handler.post(seekbar_runnable);
			}
		};
	}
	
	public static OnClickListener genExtendBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				videoPlayer.scale = (videoPlayer.scale + 1) % 2; 
	
				@SuppressWarnings("deprecation")
				int screenWidth = videoPlayerActivity.getWindowManager().getDefaultDisplay().getWidth();
//	            @SuppressWarnings("deprecation")
//				int screenHeight = videoPlayerActivity.getWindowManager().getDefaultDisplay().getHeight();
	            // float screenProportion = (float) screenWidth / (float) screenHeight;

				int videoWidth = videoPlayer.getVideoWidth();
		        int videoHeight = videoPlayer.getVideoHeight();
		        float videoProportion = (float) videoWidth / (float) videoHeight;
		        
		        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
		        																 RelativeLayout.LayoutParams.WRAP_CONTENT);
		        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
	            
				switch(videoPlayer.scale) {
					case VideoPlayer.SCALE_DEFAULT:
						lp.width = screenWidth;
						lp.height = (int) ((float) screenWidth / videoProportion);
						videoSurfaceView.setLayoutParams(lp);
						break;
					case VideoPlayer.SCALE_ORIGIN:	
						lp.width = videoWidth;
						lp.height = videoHeight;
						videoSurfaceView.setLayoutParams(lp);
						break;
				}
			}
		};
	}
	
	public static OnClickListener genFolderBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				doVideoPlayerOnBackPressed();
				/*seekbar_handler.removeCallbacks(seekbar_runnable);
				videoPlayer.stop();
				videoPlayer.release();
				//videoPlayer.release();
				Intent intent = new Intent();
				intent.setClass(videoPlayerActivity, FileExplorerActivity.class);
				videoPlayerActivity.startActivity(intent);
				videoPlayerActivity.finish();*/
			}
		};
	}
	
	public static OnClickListener genLockBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLock = true;
				ibtn_unlock.setVisibility(View.VISIBLE);
				system_UI_toggle();
				lock_handler.postDelayed(lock_runnable, 2000);
			}
		};
	}
	
	public static OnClickListener genUnlockBtnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				isLock = false;
				ibtn_unlock.setVisibility(View.GONE);
				system_UI_toggle();
			}
		};
	}
	
	public static OnCompletionListener genPlayerCompletionListener() {
		return new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(videoPlayer.state == VideoPlayer.STATE_STOP)
					return;
				videoPlayer.state = VideoPlayer.STATE_CHANGING;
				videoPlayer.next();
				tv_title.setText(videoPlayer.getVideoTitle());
				
				ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
				
				seekbar_handler.removeCallbacks(seekbar_runnable);
				seekbar_handler.post(seekbar_runnable);
			}
		};
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void system_UI_toggle() {
		VideoSurfaceView.isFullscreen = !VideoSurfaceView.isFullscreen;
		if(VideoSurfaceView.isFullscreen) {
			videoPlayerActivity.getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
		            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
		            | View.SYSTEM_UI_FLAG_IMMERSIVE);
			
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view_navigator.getLayoutParams();
			lp.weight = 15;
			view_navigator.setLayoutParams(lp);
			
			LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams)seekbar.getLayoutParams();
			lp2.weight = 1f;
			seekbar.setLayoutParams(lp2);
			
			seekbar.setVisibility(View.GONE);
			player_linearlayout_top.setVisibility(View.GONE);
        	player_linearlayout_bottom.setVisibility(View.GONE);
		}
		else {
			videoPlayerActivity.getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)view_navigator.getLayoutParams();
			lp.weight = 5;
			view_navigator.setLayoutParams(lp);
			
			LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams)seekbar.getLayoutParams();
			lp2.weight = 0.5f;
			seekbar.setLayoutParams(lp2);
			
			seekbar.setVisibility(View.VISIBLE);
			player_linearlayout_top.setVisibility(View.VISIBLE);
			player_linearlayout_bottom.setVisibility(View.VISIBLE);
		}
	}
	
	private static class CustomToast {
		private static Toast toast;
		private static Handler toastHandler = new Handler();
		private static Runnable toastRunnable = new Runnable() {
			public void run() {
				toast.cancel();
			}
		};
		
		public static void showToast(Context ctx, String text, int duration) {
			toastHandler.removeCallbacks(toastRunnable);
			if (toast != null)
				toast.setText(text);
			else
				toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
			toastHandler.postDelayed(toastRunnable, duration + 100);
			toast.show();
		}
	}
}
