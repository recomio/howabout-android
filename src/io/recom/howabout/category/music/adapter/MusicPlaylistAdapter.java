package io.recom.howabout.category.music.adapter;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.category.music.model.PlayInfo;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MusicPlaylistAdapter extends BaseAdapter {

	protected final Activity activity;
	protected final List<PlayInfo> playInfoList;
	protected ImageLoader imageLoader;

	public MusicPlaylistAdapter(Activity activity) {
		this.activity = activity;

		HowaboutApplication application = (HowaboutApplication) activity
				.getApplication();
		this.playInfoList = application.getMusicPlayList();
		this.imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		if (playInfoList != null && playInfoList.size() != 0) {
			return playInfoList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return playInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View playlistItemView;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			playlistItemView = inflater.inflate(R.layout.music_playlist_item,
					parent, false);
		} else {
			playlistItemView = convertView;
		}

		final ImageView imageView = (ImageView) playlistItemView
				.findViewById(R.id.image);
		final TextView trackTitle = (TextView) playlistItemView
				.findViewById(R.id.trackTitle);
		final TextView artistName = (TextView) playlistItemView
				.findViewById(R.id.artistName);

		final PlayInfo playInfo = playInfoList.get(position);

		trackTitle.setText(playInfo.getTrackTitle());
		artistName.setText(playInfo.getArtistName());

		String imageUrl = playInfo.getThumbmailUrl();

		imageLoader.displayImage(imageUrl, imageView,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						imageView.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						imageView.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
					}
				});

		return playlistItemView;
	}

}
