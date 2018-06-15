package com.zackhable.estimote;

// Original Estimote imports
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
// Modified imports
import android.app.NotificationManager;
import android.R;
import android.R.drawable;
import android.support.v4.app.NotificationCompat.Builder;

/**
 * This class will create proper Notification for given OS version.
 * Notice, that Android Oreo needs to have additional NotificationChannel created.
 * Feel free to use this class as you want.
 *
 * Converted to Java by Zack Hable 
 *
 * @author Estimote Inc. (contact@estimote.com)
 * @author Zack Hable
 */
public class NotificationCreator {
	
	private String CHANNEL_ID = "PROXIMITY_SCAN";
    private String CHANNEL_NAME = "Proximity bluetooth scan notifications";
    private String CHANNEL_DESCRIPTION = "";
	private String NOTIFICATION_TITLE = "Proximity Scanner";
	private String NOTIFICATION_TEXT = "Proximity Scanner is running...";
	
	public NotificationCreator(String chanId, String chanName, String chanDesc, String notTitle, String notText) {
		CHANNEL_ID = (chanId == null || chanId == "null" || chanId == "") ? CHANNEL_ID : chanId;
		CHANNEL_NAME = (chanName == null || chanName == "null" || chanName == "") ? CHANNEL_NAME : chanName;
		CHANNEL_DESCRIPTION = (chanDesc == null || chanDesc == "null" || chanDesc == "") ? CHANNEL_DESCRIPTION : chanDesc;
		NOTIFICATION_TITLE = (notTitle == null || notTitle == "null" || notTitle == "") ? NOTIFICATION_TITLE : notTitle;
		NOTIFICATION_TEXT = (notText == null || notText == "null" || notText == "") ? NOTIFICATION_TEXT : notText;
	}

    public Notification createNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(context);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.arrow_up_float/*beacon_gray*/)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        String id = CHANNEL_ID;
        String name = CHANNEL_NAME;
        String description = CHANNEL_DESCRIPTION;
        int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        mChannel.setDescription(description);
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);// as android.app.NotificationManager
        mNotificationManager.createNotificationChannel(mChannel);
    }
	
}