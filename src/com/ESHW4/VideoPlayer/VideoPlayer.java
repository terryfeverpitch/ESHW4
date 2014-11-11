package com.ESHW4.VideoPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.ESHW4.EventManager;
import com.ESHW4.FIleExplorer.FileExplorerItem;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.util.Log;

public class VideoPlayer extends MediaPlayer {
	public final static int STATE_READY    = 0;
	public final static int STATE_PLAYING  = 1;
	public final static int STATE_PAUSE    = 2;
	public final static int STATE_CHANGING = 3;
	public final static int STATE_STOP     = 4;
	
	public final static int SCALE_DEFAULT = 0;
	public final static int SCALE_ORIGIN  = 1;
	
	public int state = STATE_READY;
	public int scale = SCALE_DEFAULT;
	
	private ArrayList<FileExplorerItem> list;
	private int currentIndex = 0;
	
	final int HOUR   = 60 * 60 * 1000;
    final int MINUTE = 60 * 1000;
    final int SECOND = 1000;
	
	public VideoPlayer() {
		super();
		setOnCompletionListener(EventManager.genPlayerCompletionListener());
	}
	
	public void play() {
		try {
			if(state == STATE_PLAYING || state == STATE_CHANGING) {
				String currentPath = list.get(currentIndex).path;
	    		reset();
	    		FileInputStream fis = new FileInputStream(new File(currentPath));
				setDataSource(fis.getFD());
				fis.close();
	    		prepare();
			}	
    		start();
    		state = STATE_PLAYING;
		} catch (IllegalStateException | IllegalArgumentException | SecurityException | IOException e) {
			//stop();
        }
	}
	
	public void play(int index) {
		currentIndex = index;
		FileExplorerItem item = list.get(index);
		try {
			Log.i("video player", item.name);
			Log.i("video player", index + "");
			reset();
			setDataSource(item.path);
			prepare();
			start();
			state = STATE_PLAYING;
		} 
		catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
		}
	}
	
	public void play(String path) {
		try {
			reset();
			setDataSource(path);
			prepare();
			start();
			state = STATE_PLAYING;
		} 
		catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
		}
	}
	
	public void next() {
		setCurrentIndex(currentIndex + 1);
		play();
	}
	
	public void previous() {
		setCurrentIndex(currentIndex - 1);
		play();
	}

	public void setPlayList(ArrayList<FileExplorerItem> list) {
		this.list = list;
	}
	
	public void setCurrentIndex(int i) {
		if(i < 0) {
			currentIndex = list.size() - 1;
		}
		else {
			currentIndex = i % list.size();
		}
	}
	
	public String getVideoTitle() {
		return list.get(currentIndex).name;
	}
	
	public String getDurationString() {
		return milliToString(getDuration());
	}
	
	public String getCurrentPositionString() {
		return milliToString(getCurrentPosition());
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	@SuppressLint("DefaultLocale")
	private String milliToString(int millisecond) {
		int ms = millisecond;
		int h, m, s;
		
		if(ms == -1) 
			return "00:00";
		
		h = ms / HOUR;   ms = (h > 0) ? (ms % (h * HOUR)) : (ms);
		m = ms / MINUTE; ms = (m > 0) ? (ms % (m * MINUTE)) : (ms);
		s = ms / SECOND;
		
		if(h > 0) {
			return String.format("%02d:%02d:%02d", h, m, s);
		}
		else {
			return String.format("%02d:%02d", m, s);
		}
	}
}
