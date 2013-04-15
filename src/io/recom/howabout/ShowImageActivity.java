package io.recom.howabout;

import io.recom.howabout.category.adult.adapter.ShowImageListAdapter;
import io.recom.howabout.category.adult.model.Image;
import io.recom.howabout.category.adult.model.ImageList;
import io.recom.howabout.category.adult.net.ClusteredImagesRequest;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

@ContentView(R.layout.activity_show_image)
public class ShowImageActivity extends RoboSherlockFragmentActivity {

	@InjectView(R.id.load)
	private ProgressBar progressBar;

	@InjectView(R.id.linearView)
	private LinearLayout linearView;

	private String pHash;
	private int limit;
	private ClusteredImagesRequest clusteredImagesRequest;
	private ShowImageListAdapter showImageListAdapter;

	private SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		pHash = bundle.getString("pHash");
		limit = bundle.getInt("limit");

		clusteredImagesRequest = new ClusteredImagesRequest(pHash, limit);
	}

	@Override
	public void onStart() {
		super.onStart();

		contentManager.start(this);
		performRequest();
	}

	@Override
	public void onStop() {
		contentManager.shouldStop();

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.show_image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void performRequest() {
		contentManager.execute(clusteredImagesRequest,
				new ListImagesRequestListener());
	}

	private class ListImagesRequestListener implements
			RequestListener<ImageList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(ShowImageActivity.this,
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();

			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onRequestSuccess(ImageList imageList) {
			if (imageList == null) {
				return;
			}

			imageList.add(0, new Image(clusteredImagesRequest.getpHash()));
			showImageListAdapter = new ShowImageListAdapter(imageList);
			for (int i = 0; i < showImageListAdapter.getCount(); i++) {
				View showImageItemView = showImageListAdapter.getView(i, null,
						ShowImageActivity.this.linearView);
				linearView.addView(showImageItemView);
			}

			setProgressBarIndeterminateVisibility(false);
			progressBar.setVisibility(View.GONE);
		}
	}

}
