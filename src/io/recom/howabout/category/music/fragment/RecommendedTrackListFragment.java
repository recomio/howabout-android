package io.recom.howabout.category.music.fragment;

import io.recom.howabout.category.music.activity.TrackListActivity;
import io.recom.howabout.category.music.adapter.RandomTrackListAdapter;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.RecommendedTracksRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class RecommendedTrackListFragment extends TrackListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		String trackId = bundle.getString("trackId");
		Log.d("trackId", trackId);

		this.tracksRequest = new RecommendedTracksRequest(trackId);
		performRequest(new RecommendedTracksRequestListener());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private class RecommendedTracksRequestListener implements
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

			RecommendedTrackListFragment.this.trackList = trackList;

			TrackListActivity trackListActivity = (TrackListActivity) getActivity();
			trackListAdapter = new RandomTrackListAdapter(trackListActivity,
					trackListActivity.getGroovesharkWebView(), trackList);
			imagesGridView.setAdapter(trackListAdapter);
			trackListAdapter.notifyDataSetChanged();

			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	protected void performRequest(RequestListener<TrackList> requestListener) {
		((TrackListActivity) getActivity()).getContentManager().execute(
				tracksRequest, requestListener);
	}

}
