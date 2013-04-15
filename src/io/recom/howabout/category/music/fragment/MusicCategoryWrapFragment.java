package io.recom.howabout.category.music.fragment;

import io.recom.howabout.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;

@ContentView(R.layout.fragment_category_wrap)
public class MusicCategoryWrapFragment extends RoboFragment {

	// Avoid non-default constructors in fragments: use a default constructor
	// plus Fragment#setArguments(Bundle) instead.
	protected String category;
	
	protected RandomTrackListFragment randomTrackListFragment;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		randomTrackListFragment = new RandomTrackListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("category", "music");
		bundle.putString("method", "random");
		randomTrackListFragment.setArguments(bundle);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i("MusicCagegoryWrapFragment", "onActivityCreated()");

		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.contentView, randomTrackListFragment, "music")
				.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("MusicCagegoryWrapFragment", "onStart()");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("MusicCagegoryWrapFragment", "onStop()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.category = bundle.getString("category");

		return inflater.inflate(R.layout.fragment_category_wrap, container,
				false);
	}

}
