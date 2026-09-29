package com.godot.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/** Receives the notification action outside the game View hierarchy. */
public final class InputRecoveryReceiver extends BroadcastReceiver {
	private static final String TAG = "Sts2InputRecovery";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null || !InputRecoveryNotification.getResetAction().equals(intent.getAction())) {
			return;
		}
		Log.i(TAG, "Notification requested mobile control reset.");
		GodotApp.resetMobileControlSystem();
	}
}
