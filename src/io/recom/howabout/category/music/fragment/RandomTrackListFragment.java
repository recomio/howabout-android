package io.recom.howabout.category.music.fragment;

import io.recom.howabout.RoboSherlockFlurryAdlibSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.RandomTrackListAdapter;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.RandomTracksRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RandomTrackListFragment extends TrackListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.tracksRequest = new RandomTracksRequest();
		performRequest(new RandomTracksRequestListener());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private class RandomTracksRequestListener implements
			RequestListener<TrackList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(getActivity(),
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();

			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onRequestSuccess(TrackList trackList) {
			if (trackList == null) {
				return;
			}

			RandomTrackListFragment.this.trackList = trackList;

			RoboSherlockFlurryAdlibSpiceFragmentActivity activity = (RoboSherlockFlurryAdlibSpiceFragmentActivity) getActivity();
			trackListAdapter = new RandomTrackListAdapter(activity, trackList);

			imagesGridView.setAdapter(trackListAdapter);
			trackListAdapter.notifyDataSetChanged();

			progressBar.setVisibility(View.GONE);
		}
	}

}
