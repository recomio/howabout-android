package io.recom.howabout;

import io.recom.howabout.category.music.model.PlayInfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HowaboutApplication extends Application {

	private List<PlayInfo> musicPlayList = new ArrayList<PlayInfo>();
	private int musicPlaylistPosition = 0;

	public List<PlayInfo> getMusicPlayList() {
		return musicPlayList;
	}

	public void setMusicPlayList(List<PlayInfo> musicPlayList) {
		this.musicPlayList = musicPlayList;
	}

	public int getMusicPlaylistPosition() {
		return musicPlaylistPosition;
	}

	public void setMusicPlaylistPosition(int musicPlaylistPosition) {
		this.musicPlaylistPosition = musicPlaylistPosition;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(
				defaultOptions).build();

		ImageLoader.getInstance().init(config);
	}
}
