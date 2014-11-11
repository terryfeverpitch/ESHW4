package com.ESHW4.VideoPlayer;

import com.ESHW4.EventManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ClickableViewAccessibility")
public class CustomSeekBar extends View implements View.OnTouchListener {
	private VideoPlayer player = null;
	
	private Paint paint;
	private int width;
	private int fontSize = 45;
	private int view_width, view_height; /*55FF0000*/
	private int space = 10;
	
	private final int USER = 0;
	private final int AUTO = 1;
	private final int VIEW = 2;
	
	private int fromWho = AUTO;
	private int VIEW_START;
	
	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setTextSize(fontSize);
		paint.setTextAlign(Align.LEFT);
		setOnTouchListener(this);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		view_width  = w;
		view_height = h;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		paint.setColor(Color.parseColor("#FFFF00"));

		if(!isEnabled() || player == null) {
			canvas.drawText("00:00", space, view_height - 10, paint);
			return;
		}
		else {
			paint.setTextAlign(Align.RIGHT);
			canvas.drawText(player.getDurationString(), view_width - space, view_height - 10, paint);
			
			paint.setTextAlign(Align.LEFT);
			if(player.state == VideoPlayer.STATE_READY)
				canvas.drawText("00:00", space, view_height - 10, paint);
			else 
				canvas.drawText(player.getCurrentPositionString(), space, view_height - 10, paint);
		}
		
		if(fromWho == USER) {
			VIEW_START = width;
			player.seekTo((int)(((float)width / (float)view_width) * (float)player.getDuration()));
		}
		else if (fromWho == AUTO){
			VIEW_START = width = (int)(((float)player.getCurrentPosition() / (float)player.getDuration()) * (float)view_width);
		}
		else {
			player.seekTo((int)(((float)width / (float)view_width) * (float)player.getDuration()));
		}
		
		canvas.drawLine(width, 0, width, view_height, paint);
		canvas.drawLine(0, 0, width, 0, paint);
		canvas.drawLine(0, view_height, width, view_height, paint);
		paint.setColor(Color.parseColor("#55FF0000"));
		canvas.drawRect(0, 0, width, view_height, paint);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			player.state = VideoPlayer.STATE_PAUSE;
			player.pause();
			EventManager.ibtn_play.setImageResource(android.R.drawable.ic_media_play);
			EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
			width = clamp((int) event.getX());
			fromWho = USER;
			invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
			width = clamp((int) event.getX());
			fromWho = USER;
			invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
			//player.state = VideoPlayer.STATE_PLAYING;
			player.play();
			EventManager.ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
			fromWho = AUTO;
			EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
			EventManager.seekbar_handler.postDelayed(EventManager.seekbar_runnable, 500);
		}
		
		return true;
	}
	
	public void setPlayer(VideoPlayer player) {
		this.player = player;
	}

	public void setProcessShift(int p) {
		if(fromWho == AUTO) {
			VIEW_START = width;
			player.state = VideoPlayer.STATE_PAUSE;
			player.pause();
			EventManager.ibtn_play.setImageResource(android.R.drawable.ic_media_play);
			EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
			fromWho = VIEW; 
			setVisibility(View.VISIBLE);
		}
		
		width = clamp(VIEW_START - p);
		invalidate();
	}
	
	public void setAuto() {
		player.play();
		EventManager.ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
		fromWho = AUTO;
		EventManager.seekbar_handler.removeCallbacks(EventManager.seekbar_runnable);
		EventManager.seekbar_handler.postDelayed(EventManager.seekbar_runnable, 500);
	}
		
	private int clamp(int n) {
		if(n >= view_width)
			n = view_width;
		else if(n <= 0) 
			n = 0;
		
		return n;
	}
}
