package com.zackhable.estimote;

import android.app.Application;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import android.content.Intent;
import android.util.Log;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.Identifier;

public class EstimoteProximityApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "EstimoteProximity";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Background monitor started!");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser()
		.setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		beaconManager.setBackgroundBetweenScanPeriod(45000);
		beaconManager.setRegionStatePeristenceEnabled(false); // only use when debugging or possible for user to get multiple launches in location
		Log.i(TAG, "background between scans: "+beaconManager.getBackgroundBetweenScanPeriod());
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.zackhable.estimote.beaconRegion", Identifier.parse("E1F54E02-1E23-44E0-9C3D-512EB56ADEC9"), null, null); // indentifier for Estimote Beacons
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Waking up the app!");
		/*
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.  
        //regionBootstrap.disable();
        Intent intent = new Intent(this, com.pngaming.estimote.proximitydemo.MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);*/
		Intent intent = new Intent(this, EstimoteProximityService.class);
		intent.setAction(EstimoteProximityService.ACTION_START_FOREGROUND_SERVICE);
		startService(intent);
    }

    @Override
    public void didExitRegion(Region arg0) {
        // Don't care
    }        
}