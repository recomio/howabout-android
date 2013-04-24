package io.recom.howabout.category.music.player;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.RoboSherlockSpiceFragmentActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import io.recom.howabout.category.music.net.YoutubeMp4StreamUrlRequest;
import io.recom.howabout.category.music.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MusicPlayer {

	protected RoboSherlockSpiceFragmentActivity activity;
	protected GroovesharkWebView groovesharkWebView;

	protected MediaPlayer mediaPlayer = new MediaPlayer();

	protected List<PlayInfo> playInfoList = new ArrayList<PlayInfo>();
	protected int currentPosition = -1;

	protected MusicPlaylistAdapter musicPlaylistAdapter;

	@SuppressLint("SetJavaScriptEnabled")
	public MusicPlayer(RoboSherlockSpiceFragmentActivity activity,
			GroovesharkWebView groovesharkWebView) {
		setActivity(activity);
		setGroovesharkWebView(groovesharkWebView);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				playNext();
			}
		});
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				mediaPlayer.start();
			}
		});
	}

	public void add(RoboSherlockSpiceFragmentActivity activity,
			String trackTitle, String artistName) {
		setActivity(activity);

		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		activity.getContentManager().execute(playInfoRequest,
				new AddPlayInfoRequestListener());
	}

	public void add(PlayInfo playInfo) {
		playInfoList.add(playInfo);
	}

	public void add(int position, PlayInfo playInfo) {
		playInfoList.add(position, playInfo);
	}

	public int getCount() {
		return playInfoList.size();
	}

	public PlayInfo getItem(int position) {
		return playInfoList.get(position);
	}

	public void play(RoboSherlockSpiceFragmentActivity activity,
			GroovesharkWebView groovesharkWebView, String trackTitle,
			String artistName) {
		setActivity(activity);
		setGroovesharkWebView(groovesharkWebView);

		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		activity.getContentManager().execute(playInfoRequest,
				new ListenPlayInfoRequestListener());
	}

	public void play(PlayInfo playInfo) {
		setCurrentPosition(getCurrentPosition() + 1);
		add(getCurrentPosition(), playInfo);

		play();
	}

	public void play(int position) {
		if (playInfoList.size() > position) {
			setCurrentPosition(position);
			play();
		}
	}

	public void play(String src) {
		if (src.equals("http://grooveshark.com/")) {
			return;
		}

		Log.i("play", src);

		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(src);
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			Log.d("MediaPlayer", e.toString());
		}
	}

	public void play() {
		if (getCurrentPosition() > playInfoList.size() - 1) {
			return;
		}

		PlayInfo playInfo = playInfoList.get(getCurrentPosition());

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}

		groovesharkWebView.pause();

		if (playInfo.isGrooveshark()) {
			groovesharkWebView.play(playInfo);
		} else {
			play(playInfo.getYoutubeMp4StreamUrl());
		}

		musicPlaylistAdapter.notifyDataSetChanged();

		Intent intent = new Intent(activity, MusicPlayerService.class);
		Bundle bundle = new Bundle();
		bundle.putString("trackTitle", playInfo.getTrackTitle());
		bundle.putString("artistName", playInfo.getArtistName());
		intent.putExtras(bundle);
		activity.startService(intent);
	}

	public void pause() {
		groovesharkWebView.pause();

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int position) {
		currentPosition = position;

		musicPlaylistAdapter.notifyDataSetChanged();
	}

	public boolean next() {
		if (getCurrentPosition() + 1 == playInfoList.size()) {
			return false;
		}

		setCurrentPosition(getCurrentPosition() + 1);
		return true;
	}

	public void playNext() {
		if (!next()) {
			return;
		}

		play();
	}

	public void remove(int position) {
		if (position == getCurrentPosition()) {
			pause();
		}

		if (position < getCurrentPosition()) {
			setCurrentPosition(getCurrentPosition() - 1);
		}

		playInfoList.remove(position);
	}

	public List<PlayInfo> getPlayInfoList() {
		return playInfoList;
	}

	public void setMusicPlaylistAdapter(
			MusicPlaylistAdapter musicPlaylistAdapter) {
		this.musicPlaylistAdapter = musicPlaylistAdapter;
	}

	public MusicPlaylistAdapter getMusicPlaylistAdapter() {
		return musicPlaylistAdapter;
	}

	public RoboSherlockSpiceFragmentActivity getActivity() {
		return activity;
	}

	public void setActivity(RoboSherlockSpiceFragmentActivity activity) {
		this.activity = activity;
	}

	public void setGroovesharkWebView(GroovesharkWebView groovesharkWebView) {
		this.groovesharkWebView = groovesharkWebView;
		this.groovesharkWebView
				.setOnGroovesharkListener(new OnGroovesharkListener() {
					@Override
					public void onLoadStart(String src) {
						pause();
						play(src);
					}

					@Override
					public void onPlay() {
					}

					@Override
					public void onEnded() {
					}
				});
	}

	private class ListenPlayInfoRequestListener implements
			RequestListener<PlayInfo> {
		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(activity, "이 노래의 무료 스트리밍을 찾지 못했습니다.",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(final PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) activity
					.getApplication();

			if (playInfo.isGrooveshark()) {
				application.getMusicPlayer().play(playInfo);
				Toast.makeText(activity, "from Grooveshark", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Toast.makeText(activity, "from Youtube", Toast.LENGTH_SHORT).show();

			// if have to use youtube, should get youtube mp4 stream url.
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			activity.getContentManager().execute(youtubeMp4StreamUrlRequest,
					new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							Toast.makeText(activity,
									"이 노래의 무료 스트리밍을 찾지 못했습니다.",
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							Log.d("youtubeMp4StreamUrl", youtubeMp4StreamUrl);

							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							HowaboutApplication application = (HowaboutApplication) activity
									.getApplication();

							application.getMusicPlayer().play(playInfo);
						}

					});
		}
	}

	private class AddPlayInfoRequestListener implements
			RequestListener<PlayInfo> {

		@Override
		public void onRequestFailure(SpiceException e) {
			Toast.makeText(activity, "Error during request: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(final PlayInfo playInfo) {
			if (playInfo == null) {
				return;
			}

			HowaboutApplication application = (HowaboutApplication) activity
					.getApplication();

			if (playInfo.isGrooveshark()) {
				application.getMusicPlayer().add(playInfo);
				Toast.makeText(activity, "from Grooveshark", Toast.LENGTH_SHORT)
						.show();
				Toast.makeText(activity, "추가되었습니다.", Toast.LENGTH_LONG).show();
				return;
			}

			Toast.makeText(activity, "from Youtube", Toast.LENGTH_SHORT).show();

			// if have to use youtube, should get youtube mp4 stream url.
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			activity.getContentManager().execute(youtubeMp4StreamUrlRequest,
					new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							Toast.makeText(activity,
									"이 노래의 무료 스트리밍을 찾지 못했습니다.",
									Toast.LENGTH_LONG).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							Log.d("youtubeMp4StreamUrl", youtubeMp4StreamUrl);

							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							HowaboutApplication application = (HowaboutApplication) activity
									.getApplication();

							application.getMusicPlayer().add(playInfo);
							Toast.makeText(activity, "추가되었습니다.",
									Toast.LENGTH_LONG).show();
						}

					});
		}
	}

}
