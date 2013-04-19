package io.recom.howabout.category.music;

import io.recom.howabout.category.music.model.PlayInfo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MusicPlayer {

	protected List<PlayInfo> playInfoList = new ArrayList<PlayInfo>();
	protected int currentPosition = -1;
	protected WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	public MusicPlayer(WebView webView) {
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
			evalJS("GS.audio.audio.load();");
			evalJS("GS.audio.audio.play();");
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

}
