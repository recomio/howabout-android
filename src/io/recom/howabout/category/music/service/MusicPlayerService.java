package io.recom.howabout.category.music.service;

import io.recom.howabout.HowaboutApplication;
import io.recom.howabout.R;
import io.recom.howabout.category.music.activity.MusicPlaylistActivity;
import io.recom.howabout.category.music.adapter.MusicPlaylistAdapter;
import io.recom.howabout.category.music.model.PlayInfo;
import io.recom.howabout.category.music.net.GroovesharkStreamUrlGetter;
import io.recom.howabout.category.music.net.GroovesharkStreamUrlGetter.OnGetGroovesharkStreamKey;
import io.recom.howabout.category.music.net.PlayInfoRequest;
import io.recom.howabout.category.music.net.YoutubeMp4StreamUrlRequest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MusicPlayerService extends Service {
	protected static final String APP_NAME = "이건어때?";
	protected static final int NOTIFICATION_ID = 1337;
	protected final String FLURRY_API_KEY = "PXPN496ZGVC95C52Y2YM";

	protected static MediaPlayer mediaPlayer = new MediaPlayer();

	protected static SpiceManager contentManager = new SpiceManager(
			JacksonSpringAndroidSpiceService.class);

	protected static MusicPlaylistAdapter playlistAdapter;

	protected static boolean isLoading = false;
	protected static boolean isLoaded = false;

	public class LocalBinder extends Binder {
		MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		contentManager.start(this);

		FlurryAgent.onStartSession(this, FLURRY_API_KEY);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				isLoaded = false;
				playlistAdapter.playNext();

				playlistAdapter.notifyDataSetChanged();
			}
		});

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				mediaPlayer.start();
				isLoading = false;
				isLoaded = true;

				playlistAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		String type = bundle.getString("type");
		String trackTitle = bundle.getString("trackTitle");
		String artistName = bundle.getString("artistName");
		String thumbnailUrl = bundle.getString("thumbnailUrl");

		if (type.equals("play")) {
			play(trackTitle, artistName, thumbnailUrl);
		} else if (type.equals("pause")) {
			mediaPlayer.pause();
		} else if (type.equals("stop")) {
			isLoaded = false;
			mediaPlayer.stop();
			mediaPlayer.reset();
		} else if (type.equals("playPauseToggle")) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				if (!(isLoading() || isLoaded())) {
					play(trackTitle, artistName, thumbnailUrl);
				} else {
					try {
						mediaPlayer.start();
					} catch (Exception e) {
						play(trackTitle, artistName, thumbnailUrl);
					}
				}
			}
		}

		playlistAdapter.notifyDataSetChanged();

		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		stop();
		FlurryAgent.onEndSession(this);

		contentManager.shouldStop();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	private void play(final String trackTitle, final String artistName,
			String thumbnailUrl) {
		Notification notification = new Notification(R.drawable.ic_launcher,
				trackTitle + " - " + artistName, System.currentTimeMillis());
		Intent intent = new Intent(this, MusicPlaylistActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		notification.setLatestEventInfo(this, trackTitle, artistName,
				pendingIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		startForeground(NOTIFICATION_ID, notification);

		playlistAdapter.setCurrentLyrics("");
		isLoaded = false;
		isLoading = true;

		mediaPlayer.stop();
		mediaPlayer.reset();

		PlayInfoRequest playInfoRequest = new PlayInfoRequest(trackTitle,
				artistName);

		contentManager.execute(playInfoRequest,
				new RequestListener<PlayInfo>() {
					@Override
					public void onRequestFailure(SpiceException e) {
						isLoading = false;
						playlistAdapter.playNext();
						playlistAdapter.notifyDataSetChanged();

						Toast.makeText(
								MusicPlayerService.this,
								trackTitle + "(" + artistName + ")"
										+ "\n 무료 스트리밍 음원을 찾지 못했습니다.",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onRequestSuccess(final PlayInfo playInfo) {
						playlistAdapter.setCurrentLyrics(playInfo.getLyrics());

						Log.i("grooveshark", playInfo.getTrackTitle());
						final String groovesharkSongId = playInfo
								.getTinysongId();
						if (groovesharkSongId != null) {
							HowaboutApplication application = (HowaboutApplication) getApplicationContext();
							GroovesharkStreamUrlGetter groovesharkStreamUrlGetter;
							try {
								groovesharkStreamUrlGetter = new GroovesharkStreamUrlGetter(
										application, groovesharkSongId,
										new OnGetGroovesharkStreamKey() {
											@Override
											public void sucess(String streamUrl) {
												playInfo.setGroovesharkSongID(groovesharkSongId);
												playInfo.setGroovesharkStreamUrl(streamUrl);
												playStream(playInfo);
											}

											@Override
											public void error(Exception e) {
												playStream(playInfo);
											}
										});

								groovesharkStreamUrlGetter
										.getGroovesharkStreamUrlAsync();
							} catch (Exception e) {
								playStream(playInfo);
							}
						} else {
							playStream(playInfo);
						}
					}

				});
	}

	private void stop() {
		mediaPlayer.stop();
		stopForeground(true);
	}

	protected void playStream(final PlayInfo playInfo) {
		final String trackTitle = playInfo.getTrackTitle();
		final String artistName = playInfo.getArtistName();

		if (playInfo.isGrooveshark()) {
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(playInfo.getGroovesharkStreamUrl());
				mediaPlayer.prepareAsync();
			} catch (Exception e) {

			} finally {
				playlistAdapter.notifyDataSetChanged();
			}
		} else {
			YoutubeMp4StreamUrlRequest youtubeMp4StreamUrlRequest = new YoutubeMp4StreamUrlRequest(
					playInfo.getYoutubeMovieId());

			contentManager.execute(youtubeMp4StreamUrlRequest,
					new RequestListener<String>() {
						@Override
						public void onRequestFailure(SpiceException e) {
							isLoading = false;
							playlistAdapter.playNext();
							playlistAdapter.notifyDataSetChanged();

							Toast.makeText(
									MusicPlayerService.this,
									trackTitle + "(" + artistName + ")"
											+ "\n 무료 스트리밍 음원을 찾지 못했습니다.",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onRequestSuccess(String youtubeMp4StreamUrl) {
							playInfo.setYoutubeMp4StreamUrl(youtubeMp4StreamUrl);

							try {
								mediaPlayer.reset();
								mediaPlayer.setDataSource(youtubeMp4StreamUrl);
								mediaPlayer.prepareAsync();
							} catch (Exception e) {

							} finally {
								playlistAdapter.notifyDataSetChanged();
							}
						}

					});
		}
	}

	public static void setPlaylistAdapter(MusicPlaylistAdapter playlistAdapter) {
		MusicPlayerService.playlistAdapter = playlistAdapter;
	}

	public static boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public static boolean isLoading() {
		return isLoading;
	}

	public static boolean isLoaded() {
		return isLoaded;
	}

	public static int getDuration() {
		return mediaPlayer.getDuration();
	}

	public static int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}

}
