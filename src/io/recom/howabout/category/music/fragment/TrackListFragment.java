package io.recom.howabout.category.music.fragment;

import io.recom.howabout.MainActivity;
import io.recom.howabout.R;
import io.recom.howabout.category.music.activity.RecommendedTrackListActivity;
import io.recom.howabout.category.music.adapter.TrackListAdapter;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.net.TracksRequest;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.octo.android.robospice.request.listener.RequestListener;

@ContentView(R.layout.track_list)
public abstract class TrackListFragment extends RoboFragment implements
		OnItemClickListener {

	protected TrackList trackList;

	@InjectView(R.id.photoGrid)
	protected GridView imagesGridView;

	@InjectView(R.id.load)
	protected ProgressBar progressBar;

	protected String category;
	protected String tab;

	protected TracksRequest tracksRequest;
	protected TrackListAdapter trackListAdapter;

	public TrackListFragment() {
		super();

		Log.d("TrackListFragment", "Constructor()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		this.category = bundle.getString("category");
		this.tab = bundle.getString("tab");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.photo_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		imagesGridView.setOnItemClickListener(this);
		progressBar.setVisibility(View.VISIBLE);

		// this code prevents to re-load trackList from internet.
		if (trackList != null) {
			// trackListAdapter = new TrackListAdapter(getActivity(),
			// trackList);
			imagesGridView.setAdapter(trackListAdapter);
			// trackListAdapter.notifyDataSetChanged();

			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

		Track track = (Track) trackListAdapter.getItem(position);

		Intent intent = new Intent(getActivity(),
				RecommendedTrackListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Bundle bundle = new Bundle();
		bundle.putString("trackId", track.getId());
		bundle.putString("trackTitle", track.getTrackTitle());
		bundle.putString("artistName", track.getArtistName());
		intent.putExtras(bundle);

		startActivity(intent);
	}

	protected void performRequest(RequestListener<TrackList> requestListener) {
		((MainActivity) getActivity()).getContentManager().execute(
				tracksRequest, requestListener);
	}

}
