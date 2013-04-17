package io.recom.howabout.category.music.fragment;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.recom.howabout.category.music.activity.TrackListActivity;
import io.recom.howabout.category.music.adapter.TrackListAdapter;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.SearchedTracksRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SearchedTrackListFragment extends TrackListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		String searchKeyword = bundle.getString("searchKeyword");

		this.tracksRequest = new SearchedTracksRequest(searchKeyword);
		performRequest(new SearchedTracksRequestListener());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private class SearchedTracksRequestListener implements
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
			
			SearchedTrackListFragment.this.trackList = trackList;

			trackListAdapter = new TrackListAdapter(getActivity(), trackList);
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
