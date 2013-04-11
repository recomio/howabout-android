package io.recom.howabout;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.recom.howabout.model.Image;
import io.recom.howabout.model.ImageList;
import io.recom.howabout.net.RandomImagesRequest;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
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
public class ImageListFragment extends RoboFragment implements OnItemClickListener {

	@InjectView(R.id.photoGrid)
	protected GridView imageList;

	@InjectView(R.id.load)
	protected ProgressBar bar;

	private SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
	private RandomImagesRequest randomImagesRequest = new RandomImagesRequest();

	
	public ImageListFragment() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMoreImages();
    }
	
	@Override
	public void onStart() {
        contentManager.start(getActivity());
        super.onStart();
    }

    @Override
	public void onStop() {
        contentManager.shouldStop();
        super.onStop();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.photo_list, container, false);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	private void performRequest() {
		contentManager.execute(randomImagesRequest, new ListImagesRequestListener());
	}

	public void loadMoreImages() {
//		 bar.setVisibility(View.VISIBLE);

		performRequest();

//		 bar.setVisibility(View.GONE);
	}

	private class ListImagesRequestListener implements RequestListener<ImageList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(getActivity(),
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(ImageList images) {
			// listTweets could be null just if contentManager.getFromCache(...)
			// doesn't return anything.
			if (images == null) {
				Log.i("ListImagesRequestListener", null);
				return;
			}

			// tweetsAdapter.clear();

			for (Image image : images) {
				Log.i("image", image.getpHash() + ", " + image.getWidth() + ", " + image.getHeight());
				// tweetsAdapter.add( tweet.getText() );
			}

			// tweetsAdapter.notifyDataSetChanged();

			getActivity().setProgressBarIndeterminateVisibility(false);
		}

	}
}
