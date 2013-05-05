package io.recom.howabout.category.music.activity;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockFlurryAdlibSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.PlaylistAdapter;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.service.MusicPlayerService;
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

	protected PlaylistAdapter playlistAdapter;

	@InjectView(R.id.lyricsScrollView)
	protected ScrollView lyricsScrollView;

	@InjectView(R.id.songImageView)
	protected ImageView songImageView;

	@InjectView(R.id.lyricsTextView)
	protected TextView lyricsTextView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageLoader = ImageLoader.getInstance();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setAdsContainer(R.id.adView);

		HowaboutApplication application = (HowaboutApplication) getApplication();
		playlistAdapter = application.getPlaylistAdapter();

		if (MusicPlayerService.isPlaying()) {
			playPauseToggleButton.setImageDrawable(pauseIcon);
		} else {
			playPauseToggleButton.setImageDrawable(playIcon);
		}

		listView.setAdapter(playlistAdapter);
		playlistAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				try {
					Track track = playlistAdapter.getCurrentItem();

					actionBar.setTitle(track.getTrackTitle());
					actionBar.setSubtitle(track.getArtistName());

					if (MusicPlayerService.isPlaying()) {
						playPauseToggleButton.setImageDrawable(pauseIcon);
					} else {
						playPauseToggleButton.setImageDrawable(playIcon);
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
				// musicPlayer.playPauseToggle();

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
