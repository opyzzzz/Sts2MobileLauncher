package com.godot.game;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * System notification used as an input-recovery escape hatch.
 * It deliberately lives outside the Godot/View hierarchy.
 */
final class InputRecoveryNotification {
	private static final String TAG = "Sts2InputRecovery";
	private static final String CHANNEL_ID = "sts2_input_recovery";
	private static final int NOTIFICATION_ID = 22017;
	private static final String ACTION_RESET = BuildConfig.APPLICATION_ID + ".RESET_CONTROLS";

	private InputRecoveryNotification() {}

	static void show(Context context) {
		Context app = context.getApplicationContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager manager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
			if (manager != null) {
				NotificationChannel channel = new NotificationChannel(
					CHANNEL_ID,
					"Slay the Spire 2 操控恢复",
					NotificationManager.IMPORTANCE_LOW);
				channel.setDescription("游戏触摸或鼠标失去响应时，用于恢复操控状态");
				channel.setShowBadge(false);
				manager.createNotificationChannel(channel);
			}
		}
		Intent resetIntent = new Intent(app, InputRecoveryReceiver.class);
		resetIntent.setAction(ACTION_RESET);
		PendingIntent resetPendingIntent = PendingIntent.getBroadcast(
			app, 22018, resetIntent,
			PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		Intent gameIntent = GodotApp.createLaunchIntent(app, false);
		PendingIntent gamePendingIntent = PendingIntent.getActivity(
			app, 22019, gameIntent,
			PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(app, CHANNEL_ID)
			.setSmallIcon(R.mipmap.icon)
			.setContentTitle("Slay the Spire 2")
			.setContentText("操控恢复按钮已就绪")
			.setSubText("触摸 / 鼠标失效时可从通知栏恢复")
			.setContentIntent(gamePendingIntent)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setSilent(true)
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.addAction(0, "↻ 重置操控", resetPendingIntent)
			.setCategory(NotificationCompat.CATEGORY_SERVICE);

		try {
			NotificationManagerCompat.from(app).notify(NOTIFICATION_ID, builder.build());
		} catch (SecurityException exception) {
			Log.w(TAG, "Notification permission is unavailable; input recovery notification was not shown.", exception);
		}
	}

	static void cancel(Context context) {
		try {
			NotificationManagerCompat.from(context.getApplicationContext()).cancel(NOTIFICATION_ID);
		} catch (Exception exception) {
			Log.w(TAG, "Unable to cancel input recovery notification.", exception);
		}
	}

	static String getResetAction() {
		return ACTION_RESET;
	}
}
