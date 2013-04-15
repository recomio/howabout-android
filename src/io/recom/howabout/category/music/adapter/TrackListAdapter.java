package io.recom.howabout.category.music.adapter;

import io.recom.howabout.MainActivity;
import io.recom.howabout.R;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.RandomTracksRequest;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TrackListAdapter extends BaseAdapter {
	
	private final Activity activity;
	private final TrackList trackList;
	private ImageLoader imageLoader;
	
	private RandomTracksRequest tracksRequest = new RandomTracksRequest();
	private boolean isLoading = false;

	
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
			trackListItemView = inflater.inflate(R.layout.track_list_item, parent, false);
		} else {
			trackListItemView = convertView;
		}
		
		
		final RelativeLayout rootView = (RelativeLayout) trackListItemView.findViewById(R.id.RootView);
		final ProgressBar progressBar = (ProgressBar) trackListItemView.findViewById(R.id.load);
		final LinearLayout itemLayout = (LinearLayout) trackListItemView.findViewById(R.id.itemLayout);
		final ImageView imageView = (ImageView) trackListItemView.findViewById(R.id.image);
		final TextView trackTitle = (TextView) trackListItemView.findViewById(R.id.trackTitle);
		final TextView artistName = (TextView) trackListItemView.findViewById(R.id.artistName);
		
		Track track = trackList.get(position);
		trackTitle.setText(track.getTrackTitle());
		artistName.setText(track.getArtistName());
		
		String imageUrl = track.getThumbmailUrl();
		
		imageLoader.displayImage(imageUrl, imageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
				itemLayout.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				itemLayout.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				
				// rootView의 width를 wrap_content로.
				rootView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		
		// load more random images if needed.
		if (position == getCount() - 1 && !isLoading) {
			Log.i("ImageListAdapter", "need to load more images.");
			isLoading = true;
			((MainActivity)activity).getContentManager().execute(tracksRequest, new TracksRequestListener());
		}
		
		return trackListItemView;
	}
	
	
	private class TracksRequestListener implements RequestListener<TrackList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Log.i("ImageListAdapter", "onRequestFailure()");
			
			Toast.makeText( activity,
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			
			isLoading = false;
		}

		@Override
		public void onRequestSuccess(TrackList trackList) {
			Log.i("ImageListAdapter", "onRequestSuccess()");
			
			if (trackList == null) {
				isLoading = false;
				return;
			}
			
			TrackListAdapter.this.trackList.addAll(trackList);
			TrackListAdapter.this.notifyDataSetChanged();
			
			isLoading = false;
		}
	}
}
