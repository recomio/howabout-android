package io.recom.howabout.category.music.activity;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.PlayInfoRequest;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

@ContentView(R.layout.activity_music_player)
public class MusicPlayerActivity extends RoboSherlockSpiceFragmentActivity {

	@InjectView(R.id.webView)
	protected WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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

	@Override
	public void onStart() {
		super.onStart();

		Bundle bundle = getIntent().getExtras();
		String method = bundle.getString("method");

		if (method.equals("listen")) {
			String trackTitle = bundle.getString("trackTitle");
			String artistName = bundle.getString("artistName");

			PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
					artistName);

			getContentManager().execute(playInfoRequest,
					new PlayInfoRequestListener());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// http://www.androidpub.com/34650
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.music_player, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			moveTaskToBack(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void play(int position) {
		HowaboutApplication application = (HowaboutApplication) getApplication();
		List<PlayInfo> playlist = application.getMusicPlayList();

		if (position > playlist.size() - 1) {
			return;
		}

		PlayInfo playInfo = playlist.get(position);

		evalJS("GS.models.queue.reset();");
		evalJS("GS.models.queue.addNext( new GS.models.Song({'SongID': "
				+ playInfo.getGroovesharkSongID() + ", 'SongName': '"
				+ playInfo.getGroovesharkSongName() + "', 'ArtistID': "
				+ playInfo.getGroovesharkArtistID() + ", 'ArtistName': '"
				+ playInfo.getGroovesharkArtistName() + "', 'AlbumID': "
				+ playInfo.getGroovesharkAlbumID() + ", 'AlbumName': '"
				+ playInfo.getGroovesharkAlbumName() + "'}) );");
		evalJS("GS.audio.playNext();");

		application.setMusicPlaylistPosition(position);
	}

	protected void evalJS(String script) {
		webView.loadUrl("javascript:" + script);
	}

	private class PlayInfoRequestListener implements RequestListener<PlayInfo> {
		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(MusicPlayerActivity.this,
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) MusicPlayerActivity.this
					.getApplication();
			int position = application.getMusicPlaylistPosition();
			application.getMusicPlayList().add(position, playInfo);

			play(position);
		}
	}

}
