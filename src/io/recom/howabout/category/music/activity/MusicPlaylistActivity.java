package io.recom.howabout.category.music.activity;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockFlurryAdlibSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.service.MusicPlayerService;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

@ContentView(R.layout.activity_music_playlist)
public class MusicPlaylistActivity extends
		RoboSherlockFlurryAdlibSpiceFragmentActivity {

	protected ImageLoader imageLoader;

	protected MusicPlaylistAdapter playlistAdapter;

	@InjectView(R.id.lyricsScrollView)
	protected ScrollView lyricsScrollView;

	@InjectView(R.id.songImageView)
	protected ImageView songImageView;

	@InjectView(R.id.lyricsTextView)
	protected TextView lyricsTextView;

	@InjectView(R.id.currentPositionTextView)
	protected TextView currentPositionTextView;

	@InjectView(R.id.durationTextView)
	protected TextView durationTextView;

	@InjectView(R.id.currentPositionProgressBar)
	protected ProgressBar currentPositionProgressBar;

	@InjectView(R.id.listView)
	protected ListView listView;

	@InjectView(R.id.loadingProgressBar)
	protected ProgressBar loadingProgressBar;

	@InjectView(R.id.playPauseToggleButton)
	protected ImageButton playPauseToggleButton;

	@InjectView(R.id.prevButton)
	protected ImageButton prevButton;

	@InjectView(R.id.nextButton)
	protected ImageButton nextButton;

	@InjectResource(android.R.drawable.ic_media_play)
	protected Drawable playIcon;

	@InjectResource(android.R.drawable.ic_media_pause)
	protected Drawable pauseIcon;

	protected Timer updateCurrentPositionTimer = new Timer();

	class UpdateCurrentPositionTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!MusicPlayerService.isLoaded()) {
						durationTextView.setText("--:--");
						currentPositionTextView.setText("--:--");
						currentPositionProgressBar.setMax(0);
						currentPositionProgressBar.setProgress(0);

						return;
					}

					int duration = MusicPlayerService.getDuration();
					int currentPosition = MusicPlayerService
							.getCurrentPosition();

					String durationString = String.format(Locale.KOREA,
							"%02d:%02d", duration / 1000 / 60,
							duration / 1000 % 60);
					String currentPositionString = String.format(Locale.KOREA,
							"%02d:%02d", currentPosition / 1000 / 60,
							currentPosition / 1000 % 60);
					durationTextView.setText(durationString);
					currentPositionTextView.setText(currentPositionString);

					currentPositionProgressBar.setMax(duration);
					currentPositionProgressBar.setProgress(currentPosition);
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageLoader = ImageLoader.getInstance();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setAdsContainer(R.id.adView);

		HowaboutApplication application = (HowaboutApplication) getApplication();
		playlistAdapter = application.getPlaylistAdapter();

		if (MusicPlayerService.isLoading()) {
			loadingProgressBar.setVisibility(View.VISIBLE);
			playPauseToggleButton.setVisibility(View.GONE);
		} else {
			if (MusicPlayerService.isPlaying()) {
				playPauseToggleButton.setImageDrawable(pauseIcon);
			} else {
				playPauseToggleButton.setImageDrawable(playIcon);
			}

			loadingProgressBar.setVisibility(View.GONE);
			playPauseToggleButton.setVisibility(View.VISIBLE);
		}

		listView.setAdapter(playlistAdapter);
		playlistAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				try {
					Track track = playlistAdapter.getCurrentItem();

					actionBar.setTitle(track.getTrackTitle());
					actionBar.setSubtitle(track.getArtistName());

					if (MusicPlayerService.isLoading()) {
						loadingProgressBar.setVisibility(View.VISIBLE);
						playPauseToggleButton.setVisibility(View.GONE);
					} else {
						if (MusicPlayerService.isPlaying()) {
							playPauseToggleButton.setImageDrawable(pauseIcon);
						} else {
							playPauseToggleButton.setImageDrawable(playIcon);
						}

						loadingProgressBar.setVisibility(View.GONE);
						playPauseToggleButton.setVisibility(View.VISIBLE);
					}

					imageLoader.displayImage(track.getThumbnailUrl(),
							songImageView, new ImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
								}

								@Override
								public void onLoadingCancelled(String imageUri,
										View view) {
								}
							});

					String lyrics = playlistAdapter.getCurrentLyrics();
					if (lyrics == null) {
						lyricsScrollView.setVisibility(View.GONE);
						lyricsTextView.setText("");
					} else {
						lyricsScrollView.setVisibility(View.VISIBLE);
						lyricsTextView.setText(lyrics);
					}
					listView.invalidate();
				} catch (Exception e) {

				}
			}

			@Override
			public void onInvalidated() {

			}
		});
		playlistAdapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				playlistAdapter.play(position);
				playlistAdapter.notifyDataSetChanged();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				playlistAdapter.remove(position);
				return true;
			}
		});

		playPauseToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playlistAdapter.playPauseToggle();

				if (MusicPlayerService.isPlaying()) {
					playPauseToggleButton.setImageDrawable(pauseIcon);
				} else {
					playPauseToggleButton.setImageDrawable(playIcon);
				}
			}
		});

		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playlistAdapter.playPrev();
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playlistAdapter.playNext();
			}
		});

		UpdateCurrentPositionTask updateCurrentPositionTask = new UpdateCurrentPositionTask();
		updateCurrentPositionTimer.scheduleAtFixedRate(
				updateCurrentPositionTask, 0, 1000);
	}

	@Override
	public void onStart() {
		super.onStart();

		HowaboutApplication application = (HowaboutApplication) getApplication();
		final int currentPosition = application.getPlaylistAdapter()
				.getCurrentPosition();

		if (currentPosition >= 0) {
			listView.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(currentPosition);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.music_playlist, menu);

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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
