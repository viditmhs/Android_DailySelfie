package com.example.DailySelfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by IntelliJ IDE
 * User : vidit
 * Date : 11/29/14.
 * Time : 11:01 AM
 * Contact id; vidit.maheshwari@gmail.com
 * To modify this template follow File->Settings->File and Code Templates->Includes
 */
public class DailySelfieAlaramNotificationReceiver extends BroadcastReceiver {

    // Notification ID to allow for future updates
	private static final int MY_NOTIFICATION_ID = 1;

	// Notification Text Elements
	private final CharSequence tickerText = "Time to take a selfie";
	private final CharSequence contentTitle = "Selfie time";
	private final CharSequence contentText = "Selfie Time!!";

	// Notification Action Elements
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

    private String info = "INFO::";

	// Notification Sound and Vibration on Arrival
	/*private final Uri soundURI = Uri
			.parse("android.resource://course.examples.Alarms.AlarmCreate/"
					+ R.raw.alarm_rooster);*/
	private final long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public void onReceive(Context context, Intent intent) {

        Log.i(info,"DailySelfieAlaramNotificationReceiver,onReceive.");

		// The Intent to be used when the user clicks on the Notification View
		mNotificationIntent = new Intent(context, DailySelfieActivity.class);

		// The PendingIntent that wraps the underlying Intent
		mContentIntent = PendingIntent.getActivity(context, 0,
				mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// Build the Notification
		Notification.Builder notificationBuilder = new Notification.Builder(
				context).setTicker(tickerText)
				.setSmallIcon(android.R.drawable.ic_menu_camera)
				.setAutoCancel(true).setContentTitle(contentTitle)
				.setContentText(contentText).setContentIntent(mContentIntent)
				.setVibrate(mVibratePattern);

		// Get the NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(MY_NOTIFICATION_ID,
				notificationBuilder.build());

		// Log occurence of notify() call
		Log.i(info, "Sending notification");

	}
}
