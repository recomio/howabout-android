package io.recom.howabout.category.music.adapter;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.category.music.model.Track;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.service.MusicPlayerService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MusicPlaylistAdapter extends BaseAdapter {

	protected HowaboutApplication application;

	protected ImageLoader imageLoader = ImageLoader.getInstance();;

	protected TrackList trackList = new TrackList();
	protected int currentPosition = -1;

	protected String currentLyrics;

	public MusicPlaylistAdapter(HowaboutApplication application) {
		super();

		this.application = application;
	}

	public MusicPlaylistAdapter(HowaboutApplication application,
			TrackList trackList) {
		this(application);

		setTrackList(trackList);
	}

	public void setTrackList(TrackList trackList) {
		this.trackList = trackList;

		if (trackList.size() > 0) {
			setCurrentPosition(0);
		}

		saveTrackListToPrefs();
	}

	public void add(Track track) {
		trackList.add(track);

		saveTrackListToPrefs();
	}

	public void add(int position, Track track) {
		trackList.add(position, track);

		saveTrackListToPrefs();
	}

	public void remove(int position) {
		if (position == getCurrentPosition()) {
			stop();
		}

		if (position <= getCurrentPosition()) {
			setCurrentPosition(getCurrentPosition() - 1);
		}

		trackList.remove(position);

		notifyDataSetChanged();

		saveTrackListToPrefs();
	}

	public void clear() {
		stop();
		setCurrentPosition(-1);

		trackList.clear();

		notifyDataSetChanged();

		saveTrackListToPrefs();
	}

	protected void saveTrackListToPrefs() {
		Gson gson = new Gson();
		String trackListJson = gson.toJson(trackList);

		String prefsName = application.getString(R.string.prefs_name);
		SharedPreferences prefs = application
				.getSharedPreferences(prefsName, 0);
		Editor editor = prefs.edit();
		editor.putString("trackListJson", trackListJson);
		editor.commit();
	}

	public void play(Track track) {
		setCurrentPosition(getCurrentPosition() + 1);
		add(getCurrentPosition(), track);

		play();
	}

	public void play() {
		Track track = trackList.get(getCurrentPosition());

		Intent intent = new Intent(application, MusicPlayerService.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", "play");
		bundle.putString("trackTitle", track.getTrackTitle());
		bundle.putString("artistName", track.getArtistName());
		bundle.putString("thumbnailUrl", track.getThumbnailUrl());
		intent.putExtras(bundle);
		application.startService(intent);
	}

	public void play(int position) {
		if (position < 0 || position >= trackList.size()) {
			return;
		}

		setCurrentPosition(position);
		play();
	}

	public void playNext() {
		if (getCurrentPosition() >= trackList.size()) {
			return;
		}

		play(getCurrentPosition() + 1);
	}

	public void playPrev() {
		if (getCurrentPosition() <= 0) {
			return;
		}

		play(getCurrentPosition() - 1);
	}

	public void pause() {
		Intent intent = new Intent(application, MusicPlayerService.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", "pause");
		intent.putExtras(bundle);
		application.startService(intent);
	}

	public void stop() {
		Intent intent = new Intent(application, MusicPlayerService.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", "stop");
		intent.putExtras(bundle);
		application.startService(intent);
	}

	public void playPauseToggle() {
		if (currentLyrics == null) {
			play();
		} else {
			Intent intent = new Intent(application, MusicPlayerService.class);
			Bundle bundle = new Bundle();
			bundle.putString("type", "playPauseToggle");
			intent.putExtras(bundle);
			application.startService(intent);
		}
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int position) {
		currentPosition = position;

		notifyDataSetChanged();
	}

	public void setCurrentLyrics(String lyrics) {
		currentLyrics = lyrics;

		notifyDataSetChanged();
	}

	public String getCurrentLyrics() {
		return currentLyrics;
	}

	public Track getCurrentItem() {
		if (currentPosition >= 0 && trackList.size() > currentPosition) {
			return trackList.get(currentPosition);
		} else {
			return null;
		}
	}

	@Override
	public int getCount() {
		return trackList.size();
	}

	@Override
	public Track getItem(int position) {
		return trackList.get(position);
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
		final ImageView isCurrentItem = (ImageView) playlistItemView
				.findViewById(R.id.isCurrentItem);

		if (position == currentPosition) {
			isCurrentItem.setVisibility(View.VISIBLE);
		} else {
			isCurrentItem.setVisibility(View.GONE);
		}

		final Track track = trackList.get(position);

		trackTitle.setText(track.getTrackTitle());
		artistName.setText(track.getArtistName());

		String thumbnailUrl = track.getThumbnailUrl();

		imageLoader.displayImage(thumbnailUrl, imageView,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// imageView.setVisibility(View.INVISIBLE);
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
