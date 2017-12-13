package com.mokto.audiocontroller;

import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.app.NotificationManager;
import android.content.Intent;

public class AudioControllerNotificationKiller extends Service {

	private static int NOTIFICATION_ID;
	private NotificationManager mNM;
	private final IBinder mBinder = new KillBinder(this);

	@Override
	public IBinder onBind(Intent intent) {
		this.NOTIFICATION_ID=intent.getIntExtra("notificationID",1);
		return mBinder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {

		String PACKAGE_NAME = getApplicationContext().getPackageName();

		ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(PACKAGE_NAME);

		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(this.NOTIFICATION_ID);
	}
}
