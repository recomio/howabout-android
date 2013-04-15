package io.recom.howabout.category.adult.fragment;

import io.recom.howabout.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.fragment_test)
public class TestFragment extends RoboFragment {
	private String category;
	private String tab;
	
	@InjectView(R.id.textTest)
	TextView textTest;
	
	@InjectView(R.id.webView)
	WebView webView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		category = bundle.getString("category");
		tab = bundle.getString("tab");
		
		return inflater.inflate(R.layout.fragment_test, container, false);
	}
	
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		textTest.setText( category + ", " + tab );
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		
		webView.loadUrl("http://grooveshark.com");
	}
	
	private class MyWebViewClient extends WebViewClient{
		
		// to prevent new browser. 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	view.loadUrl(url);
            return (false);
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
}
