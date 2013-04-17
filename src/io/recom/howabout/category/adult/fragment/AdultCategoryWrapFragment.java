package io.recom.howabout.category.adult.fragment;

import io.recom.howabout.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;

@ContentView(R.layout.fragment_category_wrap)
public class AdultCategoryWrapFragment extends RoboFragment {

	// Avoid non-default constructors in fragments: use a default constructor
	// plus Fragment#setArguments(Bundle) instead.
	protected String category;

	public AdultCategoryWrapFragment() {
		super();

		Log.i("AdultCategoryWrapFragment", "Constructor()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i("AdultCagegoryWrapFragment", "onActivityCreated()");

		Fragment imageListFragment = new AdultImageListFragment();

		Bundle bundle = new Bundle();
		bundle.putString("category", "adult");
		bundle.putString("method", "random");
		imageListFragment.setArguments(bundle);

		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.contentView, imageListFragment, "adult").commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("AdultCagegoryWrapFragment", "onStart()");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("AdultCagegoryWrapFragment", "onStop()");
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
