package io.recom.howabout.category.adult.adapter;

import io.recom.howabout.MainActivity;
import io.recom.howabout.R;
import io.recom.howabout.category.adult.model.ImageList;
import io.recom.howabout.category.adult.net.RandomImagesRequest;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImageListAdapter extends BaseAdapter {
	
	private final Activity activity;
	private final ImageList imageList;
	private ImageLoader imageLoader;
	
	private RandomImagesRequest randomImagesRequest = new RandomImagesRequest();
	private boolean isLoading = false;

	
	public ImageListAdapter(Activity activity, ImageList imageList) {
		this.activity = activity;
		this.imageList = imageList;
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		if (imageList != null && imageList.size() != 0) {
			return imageList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return imageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View imageListItemView;
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			imageListItemView = inflater.inflate(R.layout.photo_list_item, parent, false);
		} else {
			imageListItemView = convertView;
		}
		
		
		final ImageView imageView = (ImageView) imageListItemView.findViewById(R.id.image);
		final ProgressBar progressBar = (ProgressBar) imageListItemView.findViewById(R.id.load);
		
		String imageUrl = imageList.get(position).getThumbmailUrl();
		
		imageLoader.displayImage(imageUrl, imageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		
		// load more random images if needed.
		if (position == getCount() - 1 && !isLoading) {
			Log.i("ImageListAdapter", "need to load more images.");
			isLoading = true;
			((MainActivity)activity).getContentManager().execute(randomImagesRequest, new RandomImagesRequestListener());
		}
		
		return imageListItemView;
	}
	
	
	private class RandomImagesRequestListener implements RequestListener<ImageList> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Log.i("ImageListAdapter", "onRequestFailure()");
			
			Toast.makeText( activity,
					"Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			
			isLoading = false;
		}

		@Override
		public void onRequestSuccess(ImageList imageList) {
			Log.i("ImageListAdapter", "onRequestSuccess()");
			
			if (imageList == null) {
				isLoading = false;
				return;
			}
			
			ImageListAdapter.this.imageList.addAll(imageList);
			ImageListAdapter.this.notifyDataSetChanged();
			
			isLoading = false;
		}
	}
}
