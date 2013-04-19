package io.recom.howabout.category.adult.fragment;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.recom.howabout.MainActivity;
import io.recom.howabout.R;
import io.recom.howabout.category.adult.activity.ShowImageActivity;
import io.recom.howabout.category.adult.adapter.ImageListAdapter;
import io.recom.howabout.category.adult.model.Image;
import io.recom.howabout.category.adult.model.ImageList;
import io.recom.howabout.category.adult.net.RandomImagesRequest;
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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


@ContentView(R.layout.photo_list)
public class AdultImageListFragment extends RoboFragment implements
		OnItemClickListener {

	@InjectView(R.id.photoGrid)
	protected GridView imagesGridView;

	@InjectView(R.id.load)
	protected ProgressBar progressBar;

	protected String category;
	protected String tab;

	private RandomImagesRequest randomImagesRequest = new RandomImagesRequest();
	private ImageListAdapter imageListAdapter;

	public AdultImageListFragment() {
		super();
		Log.d("ImageListFragment", "Constructor()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		performRequest();

		imagesGridView.setOnItemClickListener(this);
		progressBar.setVisibility(View.VISIBLE);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.category = bundle.getString("category");
		this.tab = bundle.getString("tab");

		return inflater.inflate(R.layout.photo_list, container, false);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		Image image = (Image) imageListAdapter.getItem(position);

		Intent intent = new Intent(getActivity(), ShowImageActivity.class);

		Bundle bundle = new Bundle();
		bundle.putString("pHash", image.getpHash());
		bundle.putInt("limit", 7);
		intent.putExtras(bundle);

		startActivity(intent);
	}

	private void performRequest() {
		((MainActivity) getActivity()).getContentManager().execute(
				randomImagesRequest, new RandomImagesRequestListener());
	}

	private class RandomImagesRequestListener implements
			RequestListener<ImageList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(getActivity(),
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();

			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onRequestSuccess(ImageList imageList) {
			if (imageList == null) {
				return;
			}

			imageListAdapter = new ImageListAdapter(getActivity(), imageList);
			AdultImageListFragment.this.imagesGridView.setAdapter(imageListAdapter);
			imageListAdapter.notifyDataSetChanged();

			progressBar.setVisibility(View.GONE);
		}
	}

}
