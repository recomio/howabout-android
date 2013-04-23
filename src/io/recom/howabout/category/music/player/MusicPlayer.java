package io.recom.howabout.category.music.player;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import io.recom.howabout.category.music.net.YoutubeMp4StreamUrlRequest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MusicPlayer {

	protected RoboSherlockSpiceFragmentActivity mainActivity;
	protected RoboSherlockSpiceFragmentActivity lastActivity;

	protected boolean javascriptInterfaceBroken = false;

	protected WebView webView;
	protected JavaScriptInterface javascriptInterface = new JavaScriptInterface();

	protected MediaPlayer mediaPlayer = new MediaPlayer();

	protected List<PlayInfo> playInfoList = new ArrayList<PlayInfo>();
	protected int currentPosition = -1;

	protected MusicPlaylistAdapter musicPlaylistAdapter;

	@SuppressLint("SetJavaScriptEnabled")
	public MusicPlayer(RoboSherlockSpiceFragmentActivity activity,
			final WebView webView) {
		this.mainActivity = activity;
		this.webView = webView;

		if (Build.VERSION.RELEASE.startsWith("2.3")) {
			javascriptInterfaceBroken = true;
		}

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient() {
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

		webView.addJavascriptInterface(javascriptInterface, "AndroidFunction");
		webView.loadUrl("http://grooveshark.com");
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

	public void add(RoboSherlockSpiceFragmentActivity activity,
			String trackTitle, String artistName) {
		lastActivity = activity;

		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		activity.getContentManager().execute(playInfoRequest,
				new AddPlayInfoRequestListener());
	}

	public void add(PlayInfo playInfo) {
		playInfoList.add(playInfo);
	}

	public void add(int position, PlayInfo playInfo) {
		playInfoList.add(position, playInfo);
	}

	public int getCount() {
		return playInfoList.size();
	}

	public PlayInfo getItem(int position) {
		return playInfoList.get(position);
	}

	public void play(RoboSherlockSpiceFragmentActivity activity,
			String trackTitle, String artistName) {
		lastActivity = activity;

		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		activity.getContentManager().execute(playInfoRequest,
				new ListenPlayInfoRequestListener());
	}

	public void play(PlayInfo playInfo) {
		setCurrentPosition(getCurrentPosition() + 1);
		add(getCurrentPosition(), playInfo);

		play();
	}

	public void play(int position) {
		if (playInfoList.size() > position) {
			setCurrentPosition(position);
			play();
		}
	}

	public void play(String src) {
		Log.i("play", src);

		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(src);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			Log.d("MediaPlayer", e.toString());
		}
	}

	public void play() {
		if (getCurrentPosition() > playInfoList.size() - 1) {
			return;
		}

		PlayInfo playInfo = playInfoList.get(getCurrentPosition());

		evalJS("GS.models.queue.reset();");
		evalJS("GS.audio.audio.pause();");

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}

		if (playInfo.isGrooveshark()) {
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

		} else {

			// evalJS("GS.audio.audio.src = \""
			// + playInfo.getYoutubeMp4StreamUrl() + "\";");
			// evalJS("GS.audio.audio.play();");
			play(playInfo.getYoutubeMp4StreamUrl());
		}

		musicPlaylistAdapter.notifyDataSetChanged();
	}

	protected void evalJS(String script) {
		webView.loadUrl("javascript:" + script);
	}

	public void pause() {
		evalJS("GS.audio.audio.pause();");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int position) {
		currentPosition = position;

		musicPlaylistAdapter.notifyDataSetChanged();
	}

	public boolean next() {
		if (getCurrentPosition() + 1 == playInfoList.size()) {
			return false;
		}

		setCurrentPosition(getCurrentPosition() + 1);
		return true;
	}

	public void playNext() {
		if (!next()) {
			return;
		}

		play();
	}

	public void remove(int position) {
		if (position == getCurrentPosition()) {
			pause();
		}

		if (position < getCurrentPosition()) {
			setCurrentPosition(getCurrentPosition() - 1);
		}

		playInfoList.remove(position);
	}

	public List<PlayInfo> getPlayInfoList() {
		return playInfoList;
	}

	public void setMusicPlaylistAdapter(
			MusicPlaylistAdapter musicPlaylistAdapter) {
		this.musicPlaylistAdapter = musicPlaylistAdapter;
	}

	public MusicPlaylistAdapter getMusicPlaylistAdapter() {
		return musicPlaylistAdapter;
	}

	public RoboSherlockSpiceFragmentActivity getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(RoboSherlockSpiceFragmentActivity lastActivity) {
		this.lastActivity = lastActivity;
	}

	private class ListenPlayInfoRequestListener implements
			RequestListener<PlayInfo> {
		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(lastActivity, "이 노래의 무료 스트리밍을 찾지 못했습니다.",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(final PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) lastActivity
					.getApplication();

			if (playInfo.isGrooveshark()) {
				application.getMusicPlayer().play(playInfo);
				Toast.makeText(lastActivity, "from Grooveshark",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Toast.makeText(lastActivity, "from Youtube", Toast.LENGTH_SHORT)
					.show();

			// if have to use youtube, should get youtube mp4 stream url.
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			lastActivity.getContentManager().execute(
					youtubeMp4StreamUrlRequest, new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							Toast.makeText(lastActivity,
									"이 노래의 무료 스트리밍을 찾지 못했습니다.",
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							Log.d("youtubeMp4StreamUrl", youtubeMp4StreamUrl);

							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							HowaboutApplication application = (HowaboutApplication) lastActivity
									.getApplication();

							application.getMusicPlayer().play(playInfo);
						}

					});
		}
	}

	private class AddPlayInfoRequestListener implements
			RequestListener<PlayInfo> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(lastActivity,
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(final PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) lastActivity
					.getApplication();

			if (playInfo.isGrooveshark()) {
				application.getMusicPlayer().add(playInfo);
				Toast.makeText(lastActivity, "from Grooveshark",
						Toast.LENGTH_SHORT).show();
				Toast.makeText(lastActivity, "추가되었습니다.", Toast.LENGTH_LONG)
						.show();
				return;
			}

			Toast.makeText(lastActivity, "from Youtube", Toast.LENGTH_SHORT)
					.show();

			// if have to use youtube, should get youtube mp4 stream url.
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			lastActivity.getContentManager().execute(
					youtubeMp4StreamUrlRequest, new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							Toast.makeText(lastActivity,
									"이 노래의 무료 스트리밍을 찾지 못했습니다.",
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							Log.d("youtubeMp4StreamUrl", youtubeMp4StreamUrl);

							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							HowaboutApplication application = (HowaboutApplication) lastActivity
									.getApplication();

							application.getMusicPlayer().add(playInfo);
							Toast.makeText(lastActivity, "추가되었습니다.",
									Toast.LENGTH_LONG).show();
						}

					});
		}
	}

	public class JavaScriptInterface implements JavascriptCallback {
		public void onLoadStart(String src) {
			Log.i("JavaScriptInterface", "onPlay(): " + src);

			pause();
			play(src);
		}

		public void onPlay() {
			Log.i("JavaScriptInterface", "onPlay()");
		}

		public void onEnded() {
			Log.i("JavaScriptInterface", "onEnded()");

			playNext();
		}
	}

}
