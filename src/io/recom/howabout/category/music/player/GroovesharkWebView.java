package io.recom.howabout.category.music.player;

import io.recom.howabout.category.music.model.PlayInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GroovesharkWebView extends WebView {

	protected OnGroovesharkListener groovesharkListener;

	protected JavaScriptInterface javascriptInterface = new JavaScriptInterface();
	protected boolean javascriptInterfaceBroken = false;

	public GroovesharkWebView(Context context) {
		super(context);
	}

	public GroovesharkWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GroovesharkWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void init() {
		if (Build.VERSION.RELEASE.startsWith("2.3")) {
			javascriptInterfaceBroken = true;
		}

		getSettings().setJavaScriptEnabled(true);
		getSettings().setDomStorageEnabled(true);
		setWebChromeClient(new WebChromeClient());
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				// cause of the android 2.3.x webview javascript interface bug.
				if (javascriptInterfaceBroken) {
					if (url.indexOf("AndroidFunctionCall:") >= 0) {
						String stringArray[] = url.split(":");
						String event = stringArray[1];

						if (event.equals("onLoadStart")) {
							String src = "";
							for (int i = 2; i < stringArray.length; i++) {
								if (i > 2) {
									src += ":";
								}
								src += stringArray[i];
							}
							if (!src.equals("http://grooveshark.com/")) {
								javascriptInterface.onLoadStart(src);
							}
						} else if (event.equals("onPlay")) {
							javascriptInterface.onPlay();
						} else if (event.equals("onEnded")) {
							javascriptInterface.onEnded();
						}

						return true;
					}
				}

				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				addMusicEndedEventListener();

				Log.i("onPageFinished", "onPageFinished()");
			}
		});

		addJavascriptInterface(javascriptInterface, "AndroidFunction");
		loadUrl("http://grooveshark.com");
	}

	private void addMusicEndedEventListener() {
		if (javascriptInterfaceBroken) {
			evalJS("GS.audio.audio.addEventListener('loadstart', function() { location.href='AndroidFunctionCall:onLoadStart:' + GS.audio.audio.src; }, false);");
			evalJS("GS.audio.audio.addEventListener('play', function() { location.href='AndroidFunctionCall:onPlay'; }, false);");
			evalJS("GS.audio.audio.addEventListener('ended', function() { location.href='AndroidFunctionCall:onEnded'; }, false);");
		} else {
			evalJS("GS.audio.audio.addEventListener('loadstart', function() { AndroidFunction.onLoadStart( GS.audio.audio.src ); }, false);");
			evalJS("GS.audio.audio.addEventListener('play', function() { AndroidFunction.onPlay(); }, false);");
			evalJS("GS.audio.audio.addEventListener('ended', function() { AndroidFunction.onEnded(); }, false);");
		}
	}

	public void play(PlayInfo playInfo) {
		if (playInfo.isGrooveshark()) {
			pause();
			evalJS("GS.models.queue.reset();");

			StringBuilder scriptStringBuilder = new StringBuilder();
			scriptStringBuilder
					.append("GS.models.queue.addNext( new GS.models.Song({\"SongID\": ");
			scriptStringBuilder.append(playInfo.getGroovesharkSongID());
			scriptStringBuilder.append(", \"SongName\": \"");
			scriptStringBuilder.append(playInfo.getGroovesharkSongName());
			scriptStringBuilder.append("\", \"ArtistID\": ");
			scriptStringBuilder.append(playInfo.getGroovesharkArtistID());
			scriptStringBuilder.append(", \"ArtistName\": \"");
			scriptStringBuilder.append(playInfo.getGroovesharkArtistName());
			scriptStringBuilder.append("\", \"AlbumID\": ");
			scriptStringBuilder.append(playInfo.getGroovesharkAlbumID());
			scriptStringBuilder.append(", \"AlbumName\": \"");
			scriptStringBuilder.append(playInfo.getGroovesharkAlbumName());
			scriptStringBuilder.append("\"}) );");
			evalJS(scriptStringBuilder.toString());
			evalJS("GS.audio.playNext();");
		}
	}

	public void pause() {
		evalJS("GS.audio.audio.pause();");
	}

	protected void evalJS(String script) {
		loadUrl("javascript:" + script);
	}

	public void setOnGroovesharkListener(
			OnGroovesharkListener groovesharkListener) {
		this.groovesharkListener = groovesharkListener;
	}

	public class JavaScriptInterface implements JavascriptCallback {
		public void onLoadStart(String src) {
			Log.d("GroovesharkListener", "onLoadStart(): " + src);

			groovesharkListener.onLoadStart(src);
		}

		public void onPlay() {
			Log.d("GroovesharkListener", "onPlay()");

			groovesharkListener.onPlay();
		}

		public void onEnded() {
			Log.d("GroovesharkListener", "onEnded()");

			groovesharkListener.onEnded();
		}
	}

}
