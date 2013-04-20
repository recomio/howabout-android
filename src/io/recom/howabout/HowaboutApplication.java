package io.recom.howabout;

import io.recom.howabout.category.music.player.MusicPlayer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HowaboutApplication extends Application {

	protected MusicPlayer musicPlayer;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
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

	public void setMusicPlayer(MusicPlayer musicPlayer) {
		this.musicPlayer = musicPlayer;
	}

	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}

}
