package io.recom.howabout;

import io.recom.howabout.category.music.adapter.PlaylistAdapter;
import io.recom.howabout.category.music.service.MusicPlayerService;
import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HowaboutApplication extends Application {

	protected PlaylistAdapter playlistAdapter;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialze universal image loader.
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(
				defaultOptions).build();
		ImageLoader.getInstance().init(config);

		// initialize playlistAdapter to use globally.
		playlistAdapter = new PlaylistAdapter(this);
		MusicPlayerService.setPlaylistAdapter(playlistAdapter);
	}

	public PlaylistAdapter getPlaylistAdapter() {
		return playlistAdapter;
	}

}
