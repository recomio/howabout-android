/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package io.recom.howabout.category.music.service;

import io.recom.howabout.R;
import io.recom.howabout.category.music.activity.MusicPlaylistActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayerService extends Service {
	public static final String APP_NAME = "이건어때?";
	private static final int NOTIFICATION_ID = 1337;

	public class LocalBinder extends Binder {
		MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		String trackTitle = bundle.getString("trackTitle");
		String artistName = bundle.getString("artistName");

		play(trackTitle, artistName);

		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	private void play(String trackTitle, String artistName) {
		Log.i(getClass().getName(), "Got to play()!");

		Notification notification = new Notification(R.drawable.ic_launcher,
				trackTitle + " - " + artistName, System.currentTimeMillis());
		Intent intent = new Intent(this, MusicPlaylistActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		notification.setLatestEventInfo(this, trackTitle, artistName,
				pendingIntent);
		// notification.flags |= Notification.FLAG_NO_CLEAR;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;

		startForeground(NOTIFICATION_ID, notification);
	}

	private void stop() {
		Log.i(getClass().getName(), "Got to stop()!");
		stopForeground(true);
	}
}
