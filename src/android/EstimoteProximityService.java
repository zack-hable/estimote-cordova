package com.zackhable.estimote;

import android.app.Service;
import android.os.IBinder;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.NotificationManagerCompat;
// Kotlin Libs
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
// Estimote Libs
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;

public class EstimoteProximityService extends Service {
	
	private static final String APP_ID = "<YOUR-APP-ID>";
	private static final String APP_TOKEN = "<YOUR-APP-TOKEN>";
	
	private static final String SERVICE_TAG = "EstimoteProximityService";
	private ProximityObserver.Handler proximityObservationHandler = null;
	private boolean restaurant = false;
	private boolean casino = false;
	private boolean slots = false;
	
	public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
	
	public EstimoteProximityService() {
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
        Log.d(SERVICE_TAG, "Service created!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();
            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {
        Log.d(SERVICE_TAG, "Start foreground service.");
		
		EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
		
		// build observer object
		ProximityObserver obs = new ProximityObserverBuilder(this, cloudCredentials)
		.withAnalyticsReportingDisabled()
		.withEstimoteSecureMonitoringDisabled()
		.withTelemetryReportingDisabled()
		.build();
		
		// handle zone logic

		NotificationCreator notificationCreator = new NotificationCreator("", "", "", "", "");
        // Start foreground service.
        startForeground(1, notificationCreator.createTriggerNotification(this));
    }
	
	private void checkIfDone() {
		if (!slots && !casino && !restaurant) {
			stopForegroundService();
		}
	}

    private void stopForegroundService() {
        Log.d(SERVICE_TAG, "Stop foreground service.");
		
		if (proximityObservationHandler != null) {
			proximityObservationHandler.stop();
			proximityObservationHandler = null;
		}

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }
	
}