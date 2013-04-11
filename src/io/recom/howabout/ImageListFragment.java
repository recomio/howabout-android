package io.recom.howabout;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import io.recom.howabout.model.ImageList;
import io.recom.howabout.model.ImageListAdapter;
import io.recom.howabout.net.RandomImagesRequest;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
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
	protected ProgressBar progressBar;

	private SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
	private RandomImagesRequest randomImagesRequest = new RandomImagesRequest();
	
	private ImageListAdapter imageListAdapter;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        performRequest();
    }
	
	@Override
	public void onStart() {
        contentManager.start(getActivity());
        super.onStart();
        
         
        imageList.setOnItemClickListener(this);
        progressBar.setVisibility(View.VISIBLE);
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
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Toast.makeText(getActivity(), Integer.toString(position), Toast.LENGTH_SHORT).show();
	}

	private void performRequest() {
		contentManager.execute(randomImagesRequest, new ListImagesRequestListener());
	}
	
	private class ListImagesRequestListener implements RequestListener<ImageList> {

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

			imageListAdapter = new ImageListAdapter(imageList);
			ImageListFragment.this.imageList.setAdapter(imageListAdapter);
			imageListAdapter.notifyDataSetChanged();

			getActivity().setProgressBarIndeterminateVisibility(false);
			
			progressBar.setVisibility(View.GONE);
		}

	}
}
