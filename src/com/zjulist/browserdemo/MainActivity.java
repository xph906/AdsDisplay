package com.zjulist.browserdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	ActionBar actionBar;
	EditText webUrlText;
	Button goButton;
	WebView webView;
	ProgressBar progressBar;
	AdaptiveAd adaptiveAd;
	String host;
	MyWebViewClient webViewClient = new MyWebViewClient();
	MywebChromeClient webChromeClient = new MywebChromeClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		host = "http://www.wably.com";
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("");
		actionBar.setCustomView(R.layout.actionbar_view);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.hide();
		
		webUrlText = (EditText) actionBar.getCustomView().findViewById(
				R.id.WebUrlText);
		if(webUrlText.getText().toString().isEmpty())
			webUrlText.setText(host);
		
		goButton = (Button) actionBar.getCustomView().findViewById(
				R.id.GoButton);
		
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(webChromeClient);
		
		adaptiveAd=new AdaptiveAd(MainActivity.this);

		goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = webUrlText.getText().toString().toLowerCase();
				if (!url.startsWith("http")){
					url = "http://"+url;
				}
				webView.loadUrl(url);
			}
		});
		
		webUrlText.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_ENTER){
						String url = webUrlText.getText().toString().toLowerCase();
						if (!url.startsWith("http")){
							url = "http://"+url;
						}
						webView.loadUrl(url);
						return true;
				}
				
				return false;
			}
			
		});
		
		webView.loadUrl(webUrlText.getText().toString());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("ADS:browser","shouldOverrideUrlLoading forces webview stays in "+host);
			//super.shouldOverrideUrlLoading(view, url);
			if((url!=null) && (url.toLowerCase().startsWith(host))){
				webView.loadUrl(url);
				webUrlText.setText(url);
			}
			
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i("ADS:browser","onPageStarted");
			webUrlText.setText(webView.getUrl());
			 
            //adaptiveAd.loadAd();
            boolean isAdsShown = adaptiveAd.showAd();  
			super.onPageStarted(view, url, favicon);
			if(isAdsShown){
				Log.i("ADS:browser","showProgressBar");
				
				progressBar.setProgress(0);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i("ADS:browser","onPageFinished");
			progressBar.setVisibility(View.GONE);
			adaptiveAd.endAd();
			webUrlText.setText(url);
			super.onPageFinished(view, url);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			
			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public void onTooManyRedirects(WebView view, Message cancelMsg,
				Message continueMsg) {
			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			super.onFormResubmission(view, dontResend, resend);
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			super.doUpdateVisitedHistory(view, url, isReload);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			super.onReceivedSslError(view, handler, error);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			return super.shouldOverrideKeyEvent(view, event);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			super.onUnhandledKeyEvent(view, event);
		}

		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			super.onScaleChanged(view, oldScale, newScale);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			// TODO Auto-generated method stub
			super.onReceivedLoginRequest(view, realm, account, args);
		}

	}

	class MywebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			progressBar.setProgress(newProgress);
			adaptiveAd.updateProgress(newProgress);
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			// TODO Auto-generated method stub
			super.onReceivedIcon(view, icon);
		}

	}
}
