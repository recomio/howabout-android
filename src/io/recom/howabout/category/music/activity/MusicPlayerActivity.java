package io.recom.howabout.category.music.activity;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import io.recom.howabout.category.music.net.YoutubeMp4StreamUrlRequest;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

@ContentView(R.layout.activity_music_player)
public class MusicPlayerActivity extends RoboSherlockSpiceFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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
	public void onStart() {
		super.onStart();
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
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class PlayInfoRequestListener implements RequestListener<PlayInfo> {
		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(MusicPlayerActivity.this,
					"이 노래의 무료 스트리밍을 찾지 못했습니다.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(final PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) MusicPlayerActivity.this
					.getApplication();

			if (playInfo.isGrooveshark()) {
				application.getMusicPlayer().play(playInfo);
				Toast.makeText(MusicPlayerActivity.this, "from Grooveshark",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// if have to use youtube, should get youtube mp4 stream url.
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			getContentManager().execute(youtubeMp4StreamUrlRequest,
					new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							Toast.makeText(MusicPlayerActivity.this,
									"Youtube 스트리밍 주소를 가져오지 못했습니다.",
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							Log.d("youtubeMp4StreamUrl", youtubeMp4StreamUrl);

							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							HowaboutApplication application = (HowaboutApplication) MusicPlayerActivity.this
									.getApplication();

							application.getMusicPlayer().play(playInfo);

							Toast.makeText(MusicPlayerActivity.this,
									"from Youtube", Toast.LENGTH_SHORT).show();
						}

					});
		}
	}

}
