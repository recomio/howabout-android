package io.recom.howabout.category.music.service;

import io.recom.howabout.MainActivity;
import io.recom.howabout.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Notif {

	public static int notifId = 654654;

	@SuppressWarnings("deprecation")
	public static Notification getNotification(Context context) {

		Notification n = new Notification(R.drawable.ic_launcher,
				"Ticker Text", System.currentTimeMillis());

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), 0);
		n.setLatestEventInfo(context, "Content Title", "Content Text",
				pendingIntent);

		return n;
	}

	public static void cancel(Context context) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		nm.cancel(notifId);
	}

}
