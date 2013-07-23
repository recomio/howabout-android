package io.recom.howabout;

import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.TrackList;
import io.recom.howabout.category.music.service.MusicPlayerService;
import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HowaboutApplication extends Application {

	protected MusicPlaylistAdapter playlistAdapter;

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

		// saved playlist.
		String prefsName = getString(R.string.prefs_name);
		SharedPreferences prefs = getSharedPreferences(prefsName, 0);
		String trackListJson = prefs.getString("trackListJson", null);

		// initialize playlistAdapter to use globally.
		if (trackListJson == null) {
			playlistAdapter = new MusicPlaylistAdapter(this);
		} else {
			Gson gson = new Gson();
			TrackList trackList = gson.fromJson(trackListJson, TrackList.class);

			playlistAdapter = new MusicPlaylistAdapter(this, trackList);
		}
		MusicPlayerService.setPlaylistAdapter(playlistAdapter);
	}

	public MusicPlaylistAdapter getPlaylistAdapter() {
		return playlistAdapter;
	}

}
