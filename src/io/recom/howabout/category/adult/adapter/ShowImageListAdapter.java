package io.recom.howabout.category.adult.adapter;

import io.recom.howabout.R;
import io.recom.howabout.category.adult.model.ImageList;

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

public class ShowImageListAdapter extends BaseAdapter {

	private final ImageList imageList;
	private ImageLoader imageLoader;

	public ShowImageListAdapter(ImageList imageList) {
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
		final View showImageListItemView;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			showImageListItemView = inflater.inflate(R.layout.show_image_item,
					parent, false);
		} else {
			showImageListItemView = convertView;
		}

		final ImageView imageView = (ImageView) showImageListItemView
				.findViewById(R.id.image);
		final ProgressBar progressBar = (ProgressBar) showImageListItemView
				.findViewById(R.id.load);

		String imageUrl = imageList.get(position).getBasicUrl();

		imageLoader.displayImage(imageUrl, imageView,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						progressBar.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {

						// 스크린 width에 꽉 차게 비트맵 리사이징.
						if (showImageListItemView.getWidth() > 0
								&& loadedImage.getWidth() > 0) {
							float ratio = (float) showImageListItemView
									.getWidth()
									/ (float) loadedImage.getWidth();
							imageView.setImageBitmap(Bitmap.createScaledBitmap(
									loadedImage,
									showImageListItemView.getWidth(),
									(int) ((float) loadedImage.getHeight() * ratio),
									false));
						}

						imageView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						progressBar.setVisibility(View.GONE);
					}
				});

		return showImageListItemView;
	}
}
