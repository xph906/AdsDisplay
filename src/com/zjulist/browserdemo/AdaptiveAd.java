package com.zjulist.browserdemo;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.R.bool;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdaptiveAd {
	
	public AdaptiveAd(Activity activity)
	{
		this.activity=activity;
		this.time=5000;
		this.adListener=new TimerListener();
		this.catchedAdListener = new TimerListener();
		adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		/*Change this to the device for test*/
		//adRequestBuilder.addTestDevice("014E0F500100D00B");

		interstitialAd = new InterstitialAd(activity.getApplicationContext());
	    interstitialAd.setAdUnitId("ca-app-pub-6441379973241331/6628577808");
	    interstitialAd.setAdListener(adListener);
	    interstitialAd.loadAd(adRequestBuilder.build());
	    iInLoading = true;
	    
	    catchedInterstitialAd = new InterstitialAd(activity.getApplicationContext());
	    catchedInterstitialAd.setAdUnitId("ca-app-pub-6441379973241331/6628577808");
	    catchedInterstitialAd.setAdListener(catchedAdListener);
	    catchedInterstitialAd.loadAd(adRequestBuilder.build());
	    cInLoading = true;
	    
		toastTimer=new ToastTimer(activity.getApplicationContext());
		calc = Calendar.getInstance();
		showFlag = false;
		Log.i("ADS","AdaptiveAd is created");
	}
	
	public boolean isLoaded(){
		if((interstitialAd!=null) &&interstitialAd.isLoaded())
			return true;
		else if((catchedInterstitialAd!=null) && catchedInterstitialAd.isLoaded())
			return true;
		return false;
	}
	
	public void loadAd()
	{
		if(!interstitialAd.isLoaded() && !iInLoading){
			Log.i("ADS", "start load interstitialAd");
			interstitialAd.loadAd(adRequestBuilder.build());
			iInLoading = true;
		}
	}
	public void loadCatchedAd()
	{
		if(!catchedInterstitialAd.isLoaded() && !cInLoading){
			Log.i("ADS", "start load interstitialAd");
			catchedInterstitialAd.loadAd(adRequestBuilder.build());
			cInLoading = true;
		}
	}
	
	public void endAd()
	{
		Log.i("ADS","start endAd");
		
		updateProgress(100);
		(new CloseAdThread()).start();
		loadAd();
		loadCatchedAd();
	}
	
	public void updateProgress(int newProgress){
		if(!showFlag)
			return ;
		Log.i("ADS","UpdateProgress "+newProgress);
		toastTimer.updateProgress(newProgress);
	}
	
	public boolean showAd(){
		try{	
			Log.i("ADS","start showAd");
			if(showFlag==false)
			{
				if((interstitialAd!=null) && interstitialAd.isLoaded())
				{
					interstitialAd.show();
					showFlag = true;
					this.adsStartingTime = calc.get(Calendar.SECOND);
					Log.i("ADS","showAds show interstitialAd ADS");
					toastTimer.makeToast(activity.getApplicationContext(), time);
					toastTimer.updateProgress(0);
					toastTimer.show();
					return true;
				}
				else if((catchedInterstitialAd!=null) && catchedInterstitialAd.isLoaded())
				{
					catchedInterstitialAd.show();
					showFlag = true;
					this.adsStartingTime = calc.get(Calendar.SECOND);
					Log.i("ADS","showAds show catchedInterstitialAd ADS");
					toastTimer.makeToast(activity.getApplicationContext(), time);
					toastTimer.updateProgress(0);
					toastTimer.show();
					return true;
				}
				else{
					Log.i("ADS","fail showAds cause neither is loaded");
					loadAd();
					loadCatchedAd();
				}
			}
			else{
				Log.i("ADS","fail showAds cause ad is already showing");
			}
		}
		catch(Exception e)
		{
			Log.e("ADS","showAds exception: "+e);
		}
		return false;
	}


	class TimerListener extends AdListener
	{

		@Override
		public void onAdClosed() {
			Log.i("ADS","onAdClosed");
			if(showFlag)
				showFlag=false;
			loadAd();
			toastTimer.disappear();
			super.onAdClosed();
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			Log.e("ADS","onAdFailedToLoad errorCode"+errorCode);
			iInLoading = false;
			loadAd();
			
			super.onAdFailedToLoad(errorCode);
		}

		@Override
		public void onAdLeftApplication() {
			Log.i("ADS","onAdLeftApplication");
			super.onAdLeftApplication();
		}

		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			iInLoading = false;
			Log.i("ADS","onAdLoaded interstitialAd");
		}

		@Override
		public void onAdOpened() {
			Log.i("ADS","onAdOpened");
			if(!showFlag)
				showFlag=true;
			super.onAdOpened();
		}
		
	}
	
	class CatchedTimerListener extends AdListener
	{

		@Override
		public void onAdClosed() {
			Log.i("ADS","onAdClosed catchedAd");
			if(showFlag == true)
				showFlag=false;
			loadCatchedAd();
			toastTimer.disappear();
			super.onAdClosed();
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			Log.e("ADS","onAdFailedToLoad catchedAd errorCode"+errorCode);
			cInLoading = false;
			loadCatchedAd();
			super.onAdFailedToLoad(errorCode);
		}

		@Override
		public void onAdLeftApplication() {
			Log.i("ADS","onAdLeftApplication catchedAd" );
			super.onAdLeftApplication();
		}

		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			cInLoading = false;
			Log.i("ADS","onAdLoaded catchedAd");

		}

		@Override
		public void onAdOpened() {
			Log.i("ADS","onAdOpened");
			if(!showFlag)
				showFlag=true;
			super.onAdOpened();
		}
		
	}
	

	class CloseAdThread extends Thread {
	    public CloseAdThread(){
	    }

	    public void run() {
	    	try{
				Log.i("ADS","start closing ads");
				if(!showFlag){
					Log.i("ADS","already been closed");
					return ;
				}
				int currentTime = calc.get(Calendar.SECOND);
				if(currentTime - adsStartingTime < 2){
					Log.i("ADS","ads too short, play at least two seconds, then close it");
					Thread.sleep(2000);
				}
				Instrumentation inst = new Instrumentation();  
		        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),   
		              MotionEvent.ACTION_DOWN, 24, 54, 0));  
		        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),   
		              MotionEvent.ACTION_UP, 24, 54, 0));
			    }
		    catch(Exception e)
		    {
		    	Log.e("ADS","Faile close ads "+e);
		    }
	    }

	}
	
	private InterstitialAd interstitialAd,catchedInterstitialAd;
	private long time;
	//private Timer tm;
	//private Timer adTimer;
	private Calendar calc;
	private int adsStartingTime;
	private Activity activity;
    private TimerListener adListener, catchedAdListener;
    private ToastTimer toastTimer;
    private AdRequest.Builder adRequestBuilder;
	private boolean showFlag;
	private boolean iInLoading, cInLoading;

}
