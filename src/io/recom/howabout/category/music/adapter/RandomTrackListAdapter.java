package io.recom.howabout.category.music.adapter;

import io.recom.howabout.MainActivity;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.RandomTracksRequest;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RandomTrackListAdapter extends TrackListAdapter {

	protected RandomTracksRequest tracksRequest = new RandomTracksRequest();
	protected boolean isLoading = false;

	public RandomTrackListAdapter(Activity activity, TrackList trackList) {
		super(activity, trackList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View trackListItemView = super.getView(position, convertView,
				parent);

		// load more random images if needed.
		if (position == getCount() - 1 && !isLoading) {
			Log.i("ImageListAdapter", "need to load more images.");
			isLoading = true;
			((MainActivity) activity).getContentManager().execute(
					tracksRequest, new RandomTracksRequestListener());
		}

		return trackListItemView;
	}

	private class RandomTracksRequestListener implements
			RequestListener<TrackList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Log.i("ImageListAdapter", "onRequestFailure()");

			Toast.makeText(activity, "Error during request: " + e.getMessage(),
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

			RandomTrackListAdapter.this.trackList.addAll(trackList);
			RandomTrackListAdapter.this.notifyDataSetChanged();

			isLoading = false;
		}
	}

}
