package io.recom.howabout.category.music.fragment;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.category.music.activity.MusicPlaylistActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.service.MusicPlayerService;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MusicBottomBarFragment extends RoboFragment {

	@InjectResource(android.R.drawable.ic_media_play)
	protected Drawable playIcon;

	@InjectResource(android.R.drawable.ic_media_pause)
	protected Drawable pauseIcon;

	protected View rootView;
	protected ImageView imageView;
	protected TextView trackTitle;
	protected TextView artistName;
	protected ProgressBar isLoading;
	protected ImageView isPlaying;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	protected DataSetObserver playlistObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_music_bottom_bar,
				container, false);

		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						MusicPlaylistActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

		imageView = (ImageView) rootView.findViewById(R.id.image);
		trackTitle = (TextView) rootView.findViewById(R.id.trackTitle);
		artistName = (TextView) rootView.findViewById(R.id.artistName);
		isLoading = (ProgressBar) rootView.findViewById(R.id.isLoading);
		isPlaying = (ImageView) rootView.findViewById(R.id.isPlaying);
		isPlaying.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HowaboutApplication application = (HowaboutApplication) getActivity()
						.getApplication();
				MusicPlaylistAdapter playlistAdapter = application
						.getPlaylistAdapter();
				playlistAdapter.playPauseToggle();
			}
		});

		playlistObserver = new DataSetObserver() {
			@Override
			public void onChanged() {
				updateTrack();
			}

			@Override
			public void onInvalidated() {
				updateTrack();
			}
		};

		HowaboutApplication application = (HowaboutApplication) getActivity()
				.getApplication();
		MusicPlaylistAdapter playlistAdapter = application.getPlaylistAdapter();

		playlistAdapter.registerDataSetObserver(playlistObserver);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		updateTrack();
	}

	@Override
	public void onDestroy() {
		HowaboutApplication application = (HowaboutApplication) getActivity()
				.getApplication();
		MusicPlaylistAdapter playlistAdapter = application.getPlaylistAdapter();

		playlistAdapter.unregisterDataSetObserver(playlistObserver);

		super.onDestroy();
	}

	protected void updateTrack() {
		HowaboutApplication application = (HowaboutApplication) getActivity()
				.getApplication();
		Track track = application.getPlaylistAdapter().getCurrentItem();

		if (track != null) {
			trackTitle.setText(track.getTrackTitle());
			artistName.setText(track.getArtistName());
			imageLoader.displayImage(track.getThumbnailUrl(), imageView,
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
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

			rootView.setVisibility(View.VISIBLE);
		} else {
			rootView.setVisibility(View.GONE);
		}

		if (MusicPlayerService.isLoading()) {
			isLoading.setVisibility(View.VISIBLE);
			isPlaying.setVisibility(View.GONE);
		} else {
			if (MusicPlayerService.isPlaying()) {
				isPlaying.setImageDrawable(pauseIcon);
			} else {
				isPlaying.setImageDrawable(playIcon);
			}

			isLoading.setVisibility(View.GONE);
			isPlaying.setVisibility(View.VISIBLE);
		}
	}
}
