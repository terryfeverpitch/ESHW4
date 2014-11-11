package com.ESHW4.VideoPlayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	Scroller scroller;  
	int mDistance;  
	int mDuration;  
	float mVelocity; 
	   
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    setSingleLine();  
	    scroller = new Scroller(context, new LinearInterpolator());  
	    setScroller(scroller); 
	    setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
	}
	
	@Override
	public void computeScroll(){
		super.computeScroll();  
	    if(scroller.computeScrollOffset()){  
	       scrollTo(scroller.getCurrX(), scroller.getCurrY());  
	    } 
	    else {  
	       if(scroller.isFinished()){  
	    	   scroller.startScroll(-getWidth(), 0, calculateMoveDistance(false, mVelocity), 0, mDuration);  
	       }  
	    }
	}
	
	private int calculateMoveDistance(boolean isFirstRun, float velocity){  
	     Rect rect = new Rect();  
	     String textString = (String) getText();  
	     getPaint().getTextBounds(textString, 0, textString.length(), rect);  
	     int moveDistance = rect.width();  
	     rect = null;  
	     this.mDistance = isFirstRun ? moveDistance : moveDistance + getWidth();  
	     this.mDuration = (int) (velocity * mDistance);  
	     return this.mDistance;  
	} 
	
	public void scrollText(float velocity) {  
	     this.mVelocity = velocity;  
	     scroller.startScroll(0, 0, calculateMoveDistance(true, velocity), 0, mDuration);  
	 }
}
