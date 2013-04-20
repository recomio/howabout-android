package io.recom.howabout.category.music.player;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import io.recom.howabout.category.music.net.YoutubeMp4StreamUrlRequest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
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

	protected WebView webView;
	protected MediaPlayer mediaPlayer;

	protected List<PlayInfo> playInfoList = new ArrayList<PlayInfo>();
	protected int currentPosition = -1;

	@SuppressLint("SetJavaScriptEnabled")
	public MusicPlayer(RoboSherlockSpiceFragmentActivity activity,
			WebView webView) {
		this.mainActivity = activity;
		this.webView = webView;

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		webView.loadUrl("http://grooveshark.com");
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
		currentPosition++;
		add(currentPosition, playInfo);

		play();
	}

	public void play(int position) {
		if (playInfoList.size() > position) {
			currentPosition = position;
			play();
		}
	}

	public void play() {
		if (currentPosition > playInfoList.size() - 1) {
			return;
		}

		PlayInfo playInfo = playInfoList.get(currentPosition);

		evalJS("GS.models.queue.reset();");
		evalJS("GS.audio.audio.pause();");
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
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

			evalJS("GS.audio.audio.src = \""
					+ playInfo.getYoutubeMp4StreamUrl() + "\";");
			evalJS("GS.audio.audio.play();");

			// try {
			// mediaPlayer = new MediaPlayer();
			// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// mediaPlayer.setDataSource(playInfo.getYoutubeMp4StreamUrl());
			// mediaPlayer.prepare(); // might take long! (for buffering, etc)
			// mediaPlayer.start();
			// } catch (Exception e) {
			//
			// }
		}
	}

	protected void evalJS(String script) {
		webView.loadUrl("javascript:" + script);
	}

	public void pause() {
		evalJS("GS.audio.audio.pause();");
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPostion(int position) {
		currentPosition = position;
	}

	public List<PlayInfo> getPlayInfoList() {
		return playInfoList;
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
		public void onRequestSuccess(PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) lastActivity
					.getApplication();
			application.getMusicPlayer().add(playInfo);

			Toast.makeText(lastActivity, "추가되었습니다.", Toast.LENGTH_LONG).show();
		}
	}

}
