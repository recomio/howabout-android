package io.recom.howabout.category.music.adapter;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.activity.MusicPlayerActivity;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class TrackListAdapter extends BaseAdapter {

	protected final Activity activity;
	protected final TrackList trackList;
	protected ImageLoader imageLoader;

	public TrackListAdapter(Activity activity, TrackList trackList) {
		this.activity = activity;
		this.trackList = trackList;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		if (trackList != null && trackList.size() != 0) {
			return trackList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return trackList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View trackListItemView;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			trackListItemView = inflater.inflate(R.layout.track_list_item,
					parent, false);
		} else {
			trackListItemView = convertView;
		}

		final ProgressBar progressBar = (ProgressBar) trackListItemView
				.findViewById(R.id.load);
		final ImageView imageView = (ImageView) trackListItemView
				.findViewById(R.id.image);
		final TextView trackTitle = (TextView) trackListItemView
				.findViewById(R.id.trackTitle);
		final TextView artistName = (TextView) trackListItemView
				.findViewById(R.id.artistName);

		final Track track = trackList.get(position);
		trackTitle.setText(track.getTrackTitle());
		artistName.setText(track.getArtistName());

		String imageUrl = track.getThumbmailUrl();

		imageLoader.displayImage(imageUrl, imageView,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						progressBar.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						imageView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						progressBar.setVisibility(View.GONE);
					}
				});

		final Button listenTrackButton = (Button) trackListItemView
				.findViewById(R.id.listenTrackButton);
		final Button addTrackButton = (Button) trackListItemView
				.findViewById(R.id.addTrackButton);

		// when click a 'listen' button.
		listenTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TrackListAdapter.this.activity,
						MusicPlayerActivity.class);

				Bundle bundle = new Bundle();
				bundle.putString("method", "listen");
				bundle.putString("trackId", track.getId());
				bundle.putString("trackTitle", track.getTrackTitle());
				bundle.putString("artistName", track.getArtistName());
				intent.putExtras(bundle);

				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

				activity.startActivity(intent);
			}
		});

		// when click an 'add' button.
		addTrackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestPlayInfo(track.getTrackTitle(), track.getArtistName());
			}
		});

		return trackListItemView;
	}

	public void requestPlayInfo(String trackTitle, String artistName) {
		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		((RoboSherlockSpiceFragmentActivity) activity).getContentManager()
				.execute(playInfoRequest, new PlayInfoRequestListener());
	}

	private class PlayInfoRequestListener implements RequestListener<PlayInfo> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(activity, "Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) activity
					.getApplication();
			application.getMusicPlayer().add(playInfo);

			Toast.makeText(activity, "추가되었습니다.", Toast.LENGTH_LONG).show();
		}
	}

}
