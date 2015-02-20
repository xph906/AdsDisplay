package com.zjulist.browserdemo;


import java.util.Timer;
import java.util.TimerTask;

import android.R.anim;
import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ToastTimer {
	
	public static final int LENGTH_SHORT = 2000;  
    public static final int LENGTH_LONG = 3500;  
    
	private final Handler mHandler = new Handler();      
    private long mDuration=LENGTH_SHORT;  
    private int mGravity = Gravity.CENTER;  
    private int mX, mY;  
    private float mHorizontalMargin;  
    private float mVerticalMargin;  
    private View mView;  
    private View mNextView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int progress;
    private Timer tm;
    
    private WindowManager mWM;  
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();  
	
	public ToastTimer(Context context)
	{
		init(context);
	}
	private void init(Context context)  
    {     
         final WindowManager.LayoutParams params = mParams;  
         params.height = WindowManager.LayoutParams.WRAP_CONTENT;  
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;  
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;  
         params.format = PixelFormat.TRANSLUCENT;  
         params.windowAnimations = android.R.style.Animation_Toast;  
         params.type = WindowManager.LayoutParams.TYPE_TOAST;  
           
         mWM = (WindowManager) context.getApplicationContext()  
                 .getSystemService(Context.WINDOW_SERVICE); 
         
    }  
	
	public void makeToast(Context context,long duration)   
    {  
        LinearLayout mLayout=new LinearLayout(context);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        mProgressBar=new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);  
        
        mTextView = new TextView(context);
        mTextView.setText(createProgressText(0));
        
        mLayout.addView(mProgressBar,params);
        mLayout.addView(mTextView);
       
        mNextView = mLayout;  
        mDuration = duration;  
    }  
	
	public void show() {  
		handleShow();
     /*   mHandler.post(mShow);  
        tm=new Timer();
        progress=0;
        tm.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(progress<100)
				{
				   progress+=20;
				   if(progress<100)
					   mProgressBar.setProgress(progress);
				   else {
			           mProgressBar.setProgress(100);
			      	}
				}
			}
		}, 0,mDuration/5);
        
        if(mDuration>0)  
        {  
             mHandler.postDelayed(mHide, mDuration);  
        }  
        */
    }
	public void hide() {  
		    tm.cancel();
	        mHandler.post(mHide);  
	      
	
	} 
	
	private final Runnable mShow = new Runnable() {  
        public void run() {  
            handleShow();  
        }  
    };  
  
    private final Runnable mHide = new Runnable() {  
        public void run() {  
            handleHide();  
        }  
    };
    private String createProgressText(int progress){
    	StringBuilder sb = new StringBuilder();
    	sb.append("Web page is loading (");
    	sb.append(String.valueOf(progress)+"%) ");
    	sb.append("...");
    	return sb.toString();
    }
    
    private void handleShow() {  
    	  
        if (mView != mNextView) {  
            // remove the old view if necessary  
            handleHide();  
            mView = mNextView;  
            final int gravity = mGravity;  
            mParams.gravity = gravity;  
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL)   
            {  
                mParams.horizontalWeight = 1.0f;  
            }  
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL)   
            {  
                mParams.verticalWeight = 1.0f;  
            }  
            mParams.x = 0;  
            mParams.y = 1200;  
            //mParams.verticalMargin = mVerticalMargin;  
            //mParams.horizontalMargin = mHorizontalMargin;  
            if (mView.getParent() != null)   
            {  
                mWM.removeView(mView);  
            }  
            mWM.addView(mView, mParams);  
        }  
    }  
    public void disappear(){
    	handleHide();
    }
    public void updateProgress(int newProgress){
    	if(mProgressBar!=null){
    		Log.d("ADS:ToastTimer","updateProgress:"+newProgress);
    		if(newProgress == mProgressBar.getProgress())
    			return;
    		mProgressBar.setProgress(newProgress);
    		mTextView.setText(createProgressText(newProgress));
    	}
    }
  
    private void handleHide()   
    {  
        if (mView != null)   
        {  
            if (mView.getParent() != null)   
            {  
                mWM.removeView(mView);  
            }  
            mView = null;  
        }  
    }  

}