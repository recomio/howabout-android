package io.recom.howabout.model;

import io.recom.howabout.R;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageListAdapter extends BaseAdapter {
	
	private final ImageList imageList;
	private ImageLoader imageLoader;

	
	public ImageListAdapter(ImageList imageList) {
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
		
		String imageUrl = imageList.get(position).getUrl();
		
		imageLoader.displayImage(imageUrl, imageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		
		return imageListItemView;
	}
}
