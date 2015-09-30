package io.macu.demoappy;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by macu on 15-09-28.
 */
public class DemoApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			// Install a debug logger.
			// Calls to Timber.* will go through the installed logger.
			// If no logger is installed, calls to Timber.* do nothing.
			Timber.plant(new Timber.DebugTree());
		}
	}

}
