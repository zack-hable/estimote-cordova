package com.zackhable.estimote;

// Java Libs
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// Cordova Libs
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
// Android Libs
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.app.Notification;
// Kotlin Libs
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
// Custom Libs (3rd Party)
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.trigger.ProximityTrigger;
import com.estimote.proximity_sdk.trigger.ProximityTriggerBuilder;

public class EstimoteProximity extends CordovaPlugin {
	private static final String PLUGIN_NAME = "EstimoteProximity";
	
	private static Context appContext = null;
	private static Activity appActivity = null;
	
	private static boolean hasBluetoothAccess = false;
	private static EstimoteCloudCredentials cloudCredentials = null;
	private static List<ProximityObserver> proximityObservers = null;
	private static List<ProximityObserver.Handler> proximityObserverHandlers = null;
	private static List<CallbackContext> proximityZoneCallbacks = null;
	private static List<ProximityZone> proximityZones = null;
	private static Map<Integer, List<Integer>> proximityZonesObserver = null;
	private static List<ProximityTrigger> proximityTriggers = null;
	private static List<ProximityTrigger.Handler> proximityTriggerHandlers = null;
	
	/**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.d(PLUGIN_NAME, "Initializing Estimote Proximity class...");
		appActivity = cordova.getActivity();
		appContext = appActivity.getApplicationContext();
		proximityObservers = new ArrayList();
		proximityObserverHandlers = new ArrayList();
		proximityZoneCallbacks = new ArrayList();
		proximityZones = new ArrayList();
		proximityZonesObserver = new HashMap();
		proximityTriggers = new ArrayList();
		proximityTriggerHandlers = new ArrayList();
	}
	
	/**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
		// Get System Permissions access
		if ("getSystemPermissions".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, getBluetoothPermissions()));
				}
			});
		}
		// Check System Permissions access
		else if ("hasSystemPermissions".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, hasBluetoothAccess()));
		}
		// Set Cloud Credentials
		else if ("setCloudCredentials".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, setCloudCredentials(args)));
				}
			});
		}
		// Check Cloud Credentials initialization
		else if ("hasCloudCredentials".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, hasCloudCredentials()));
		}
		// Build Proximity Observer
		else if ("buildProximityObserver".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, buildProximityObserver(args)));
				}
			});
		}
		// Start Proximity Observer
		else if ("startProximityObserver".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, startProximityObserver(args)));
				}
			});
		}
		// Stop Proximity Observer Handler
		else if ("stopProximityObserver".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, stopProximityObserverHandler(args)));
				}
			});
		}
		// Build Proximity Zone
		else if ("buildProximityZone".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					PluginResult start = new PluginResult(Status.OK, buildProximityZone(args, callbackContext));
					start.setKeepCallback(true);
					callbackContext.sendPluginResult(start);
				}
			});
		}
		// Delete Proximity Zone
		else if ("deleteProximityZone".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, deleteProximityZone(args)));
				}
			});
		}
		// Build Proximity Trigger
		else if ("buildProximityTrigger".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, buildProximityTrigger(args)));
				}
			});
		}
		// Start Proximity Trigger
		else if ("startProximityTrigger".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, startProximityTrigger(args)));
				}
			});
		}
		// Stop Proximity Trigger Handler
		else if ("stopProximityTrigger".equals(action)) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					callbackContext.sendPluginResult(new PluginResult(Status.OK, stopProximityTriggerHandler(args)));
				}
			});
		}
		// endpoint doesn't exist
		else {
			return false;
		}
		// otherwise tell them we finished successfully
		return true;
    }
	
	/*** PLUGIN FUNCTIONS ***/
	
	/**
	 * Builds a Proximity Trigger Handler
	 *
	 * @param args				JSONArray of values to use as arguments when building the Proximity Zone
	 * @return					Boolean value indicating successful building of a Proximity Trigger
	 */
	private int buildProximityTrigger(JSONArray args) {
		try {
			JSONArray notificationArgs = args.getJSONArray(0);
			
			String chanId = notificationArgs.getString(0);
			String chanName = notificationArgs.getString(1);
			String chanDesc = notificationArgs.getString(2);
			String notTitle = notificationArgs.getString(3);
			String notText = notificationArgs.getString(4);
			NotificationCreator notificationCreator = new NotificationCreator(chanId, chanName, chanDesc, notTitle, notText);
			ProximityTrigger trigger = new ProximityTriggerBuilder(appContext)
			.displayNotificationWhenInProximity(notificationCreator.createTriggerNotification(appContext))
			.build();
			
			proximityTriggers.add(trigger);
			proximityTriggerHandlers.add(null);
			return proximityTriggers.size()-1;
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
		}
		return -1;
	}
	
	/**
	 * Starts a Proximity Trigger Handler
	 *
	 * @param args				JSONArray of values to use as arguments when starting the Proximity Zone
	 * @return					Boolean value indicating successful starting of a Proximity Trigger
	 */
	private boolean startProximityTrigger(JSONArray args) {
		try {
			int triggerId = args.getInt(0);
			
			if (isValidTid(triggerId)) {
				proximityTriggerHandlers.set(triggerId, proximityTriggers.get(triggerId).start());
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Stops a Proximity Trigger Handler
	 *
	 * @param args				JSONArray of values to use as arguments when stopping the Proximity Zone
	 * @return					Boolean value indicating successful stopping of a Proximity Trigger
	 */
	private boolean stopProximityTriggerHandler(JSONArray args) {
		try {
			int triggerId = args.getInt(0);
			
			if (isValidTid(triggerId)) {
				proximityTriggerHandlers.get(triggerId).stop();
				proximityTriggerHandlers.set(triggerId, null);
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes a Proximity Zone and stops any Proximity Observers that may be monitoring that zone
	 *
	 * @param args				JSONArray of values to use as arguments when deleting the Proximity Zone
	 * @return					Boolean value indicating successful deletion of Proximity Zone
	 */
	private boolean deleteProximityZone(JSONArray args) {
		try {
			int zoneId = args.getInt(0);
			if (isValidZid(zoneId)) {
				proximityZoneCallbacks.get(zoneId).sendPluginResult(new PluginResult(Status.OK, true));
				List<Integer> zoneObserver = proximityZonesObserver.get(zoneId);
				
				for (Integer pid: zoneObserver) {
					stopProximityObserverHandler(new JSONArray(pid));
				}
				
				proximityZones.set(zoneId, null);
				proximityZoneCallbacks.set(zoneId, null);
				proximityZonesObserver.remove(zoneId);
			}
			else {
				return false;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Builds a Proximity Zone based off of input params
	 *
	 * @param args				JSONArray of values to use as arguments when building the Proximity Zone
	 * @return					Boolean value indicating successful creation of Proximity Zone
	 */
	private int buildProximityZone(JSONArray args, CallbackContext callbackContext) {
		try {
			int pid = args.getInt(0);
			String attachmentKey = args.getString(1);
			String attachmentValue = args.getString(2);
			double rangeMode = args.getDouble(3);
			
			if (isValidPid(pid)) {
				ProximityObserver.ProximityZoneAttachmentBuilder zoneAttachBuilder = proximityObservers.get(pid).zoneBuilder();
				ProximityObserver.ProximityZoneRangeBuilder zoneRangeBuilder = null;
				ProximityObserver.ProximityZoneBuilder zoneBuilder = null;
				
				zoneRangeBuilder = zoneAttachBuilder.forAttachmentKey(attachmentKey);
				if (attachmentValue != null && attachmentValue != "null" && attachmentValue != "") {
					zoneRangeBuilder = zoneAttachBuilder.forAttachmentKeyAndValue(attachmentKey, attachmentValue);
				}

				switch ((int)rangeMode) {
					case -2: // near range
						zoneBuilder = zoneRangeBuilder.inNearRange();
						break;
					case -1: // far range
						zoneBuilder = zoneRangeBuilder.inFarRange();
						break;
					default: // custom range
						zoneBuilder = zoneRangeBuilder.inCustomRange(rangeMode);
				}
				
				zoneBuilder = zoneBuilder
				.withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
					@Override 
					public Unit invoke(ProximityAttachment proximityAttachment) {
						Log.d(PLUGIN_NAME, "entered zone!");
						Map<String, String> data = proximityAttachment.getPayload();
						data.put("device_id", proximityAttachment.getDeviceId());
						data.put("event", "enter");
						PluginResult update = new PluginResult(Status.OK, mapToJSON(data).toString());
						update.setKeepCallback(true);
						callbackContext.sendPluginResult(update);
						return null;
					}
				})
				.withOnExitAction(new Function1<ProximityAttachment, Unit>() {
					@Override 
					public Unit invoke(ProximityAttachment proximityAttachment) {
						Log.d(PLUGIN_NAME, "exited zone!");
						Map<String, String> data = proximityAttachment.getPayload();
						data.put("device_id", proximityAttachment.getDeviceId());
						data.put("event", "exit");
						PluginResult update = new PluginResult(Status.OK, mapToJSON(data).toString());
						update.setKeepCallback(true);
						callbackContext.sendPluginResult(update);
						return null;
					}
				});
				proximityZones.add(zoneBuilder.create());
				proximityZoneCallbacks.add(callbackContext);
				int zoneId = proximityZones.size()-1;
				proximityZonesObserver.put(zoneId, new ArrayList<Integer>());
				
				return zoneId;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
		}
		return -1;
	}
	
	/**
	 * Builds a ProximityObserver based off of input params
	 *
	 * @param args				JSONArray of values to use as arguments when building the ProximityObserver
	 * @return					Boolean value indicating successful creation of ProximityObserver
	 */
	private int buildProximityObserver(JSONArray args) {
		if (!hasBluetoothAccess()) {
			getBluetoothPermissions();
		}
		if (hasCloudCredentials()) {
			try {
				boolean analyticsReportingDisabled = args.getBoolean(0);
				boolean estimoteSecureMonitoringDisabled = args.getBoolean(1);
				int powerMode = args.getInt(2);
				boolean telemetryReportingDisabled = args.getBoolean(3);
				boolean scannerInForegroundService = args.getBoolean(4);
				
				ProximityObserverBuilder proximityObserverBuilder = new ProximityObserverBuilder(appContext, cloudCredentials);
				if (analyticsReportingDisabled) {
					proximityObserverBuilder = proximityObserverBuilder.withAnalyticsReportingDisabled();
				}
				if (estimoteSecureMonitoringDisabled) {
					proximityObserverBuilder = proximityObserverBuilder.withEstimoteSecureMonitoringDisabled();
				}
				if (scannerInForegroundService)  {
					JSONArray notificationArgs = args.getJSONArray(5);
					String chanId = notificationArgs.getString(0);
					String chanName = notificationArgs.getString(1);
					String chanDesc = notificationArgs.getString(2);
					String notTitle = notificationArgs.getString(3);
					String notText = notificationArgs.getString(4);
					NotificationCreator notificationCreator = new NotificationCreator(chanId, chanName, chanDesc, notTitle, notText);
					proximityObserverBuilder = proximityObserverBuilder.withScannerInForegroundService(notificationCreator.createNotification(appContext));
				}
				if (telemetryReportingDisabled) {
					proximityObserverBuilder = proximityObserverBuilder.withTelemetryReportingDisabled();
				}
				switch (powerMode) {
					case 0: // low power mode
						proximityObserverBuilder = proximityObserverBuilder.withLowPowerMode();
						break;
					case 1: // low latency mode
						proximityObserverBuilder = proximityObserverBuilder.withLowLatencyPowerMode();
						break;
					default: // balanced mode
						proximityObserverBuilder = proximityObserverBuilder.withBalancedPowerMode();
				}
				
				ProximityObserver proximityObserver = proximityObserverBuilder.build();
				proximityObservers.add(proximityObserver);
				proximityObserverHandlers.add(null);
				return proximityObservers.size()-1;
			}
			catch (JSONException e) {
				Log.e(PLUGIN_NAME, "", e);
			}
		}
		return -1;
	}
	
	/**
	 * Starts a ProximityObserver object
	 *
	 * @param args					JSONArray of arguments to use when adding BeaconZones
	 * @return						Boolean value indicating successful creation of BeaconZone and starting ProximityObserver
	 */
	private boolean startProximityObserver(JSONArray args) {
		try {
			int pid = args.getInt(0);
			if (isValidPid(pid)) {
				JSONArray zoneIds = args.getJSONArray(1);
				List<ProximityZone> zones = new ArrayList();
				for (int i = 0; i < zoneIds.length(); i++) {
					int zoneId = zoneIds.getInt(i);
					if (isValidZid(zoneId)) {
						Log.i(PLUGIN_NAME, "Adding zone:"+zoneId+" to observer:"+Integer.toString(pid));
						zones.add(proximityZones.get(zoneId));
						List<Integer> zoneObserver = proximityZonesObserver.get(zoneId);
						if (!zoneObserver.contains(pid)) {
							zoneObserver.add(pid);
						}
					}
				}
				if (zones.size() == 0) {
					return false;
				}
				proximityObservers.get(pid).addProximityZones(zones);
				proximityObserverHandlers.set(pid, proximityObservers.get(pid).start());
			}
			else {
				return false;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Stops a ProximityObserver object
	 *
	 * @param args					JSONArray of arguments to use when stopping ProximityObserver
	 * @return						Boolean value indicating a successful stop of a ProximityObserver
	 */
	private boolean stopProximityObserverHandler(JSONArray args) {
		try {
			int pid = args.getInt(0);
			if (isValidPid(pid)) {
				proximityObserverHandlers.get(pid).stop();
				proximityObserverHandlers.set(pid, null);
			}
			else {
				return false;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Verifies if a Proximity ID is valid
	 *
	 * @param pid					Proximity ID to verify
	 * @return						Boolean value indicating valid pid of a ProximityObserver
	 */
	private boolean isValidPid(int pid) {
		if (pid >= proximityObservers.size() || proximityObservers.get(pid) == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Verifies if a Proximity Zone ID is valid
	 *
	 * @param zid					Proximity Zone ID to verify
	 * @return						Boolean value indicating valid Zone ID of a Proximity Zone
	 */
	private boolean isValidZid(int zid) {
		if (zid >= proximityZones.size() || proximityZones.get(zid) == null) {
			return false;
		}
		return true;
	}
	
	private boolean isValidTid(int tid) {
		if (tid >= proximityTriggers.size() || proximityTriggers.get(tid) == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if CloudCredentials have been initialized
	 *
	 * @return						Boolean value indicating successful initialization of CloudCredentials
	 */
	private boolean hasCloudCredentials() {
		if (cloudCredentials == null) {
			Log.w(PLUGIN_NAME, "No Cloud Credentials have been initialized yet!");
			return false;
		}
		return true;
	}
	
	/**
	 * Sets CloudCredentials
	 *
	 * @param args					JSONArray of arguments to use when initializing CloudCredentials
	 * @return						Boolean value indicating successful creation of CloudCredentials
	 */
	private boolean setCloudCredentials(JSONArray args) {
		try {
			String appId = args.getString(0);
			String appToken = args.getString(1);
			cloudCredentials = new EstimoteCloudCredentials(appId, appToken);
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if app has access to necessary system resources
	 *
	 * @return						Boolean value indicating access to necessary system resources
	 */
	private boolean hasBluetoothAccess() {
		return hasBluetoothAccess;
	}
	
	/**
	 * Gains permission to use necessary system resources
	 *
	 * @return						Boolean value indicating access to necessary system resources
	 */
	private boolean getBluetoothPermissions() {
		Log.d(PLUGIN_NAME, "Requesting access to system resources...");
		RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
			appActivity, 
				new Function0<Unit>() {
				@Override
				public Unit invoke() {
					/* start scanner */
					hasBluetoothAccess = true;
					return null;
				}
			},
			new Function1<List<? extends Requirement>, Unit>() {
				@Override
				public Unit invoke(List<? extends Requirement> requirements) {
					/* scanning won't work, handle this case in your app */
					return null;
				}
			},
			new Function1<Throwable, Unit>() {
				@Override
				public Unit invoke(Throwable throwable) {
					/* Oops, some error occurred, handle it here! */
					return null;
				}
			}
		);
		return hasBluetoothAccess;
	}
	
	/*** HELPER FUNCTIONS ***/
	
	/**
	 * Converts a Map<String, String> to a JSONObject
	 *
	 * @param data					Map<String, String> to be converted
	 * @return						Converted JSONObject
	 */
	private JSONObject mapToJSON(Map<String, String> data) {
		JSONObject result = new JSONObject();
		try {
			for (Map.Entry<String, String> entry: data.entrySet()) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, "", e);
			return null;
		}
		return result;
	}
	
}