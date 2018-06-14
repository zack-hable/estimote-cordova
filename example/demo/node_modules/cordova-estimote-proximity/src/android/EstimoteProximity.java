package com.zackhable.estimote;

// Java Libs
import java.util.List;
import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class EstimoteProximity extends CordovaPlugin {
	
	private static final String PLUGIN_NAME = "EstimoteProximity";
	
	private static Context appContext = null;
	private static Activity appActivity = null;
	
	private static boolean hasBluetoothAccess = false;
	private static EstimoteCloudCredentials cloudCredentials = null;
	private static List<ProximityObserver> proximityObservers = null;
	private static List<ProximityObserver.Handler> proximityHandlers = null;
	private static List<CallbackContext> proximityCallbacks = null;

	
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
		proximityHandlers = new ArrayList();
		proximityCallbacks = new ArrayList();
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
		if ("getSystemPermissions".equals(action)) {
			getBluetoothPermissions();
		}
		else if ("hasSystemPermissions".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, hasBluetoothAccess()));
		}
		else if ("setCloudCredentials".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, setCloudCredentials(args)));
		}
		else if ("hasCloudCredentials".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, hasCloudCredentials()));
		}
		else if ("buildProximityObserver".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, buildProximityObserver()));
		}
		else if ("startProximityObserver".equals(action)) {
			PluginResult start = new PluginResult(Status.OK, startProximityObserver(args, callbackContext));
			start.setKeepCallback(true);
			callbackContext.sendPluginResult(start);
		}
		else if ("stopProximityObserver".equals(action)) {
			callbackContext.sendPluginResult(new PluginResult(Status.OK, stopProximityObserverHandler(args)));
		}
		else { // endpoint doesn't exist
			return false;
		}
		// otherwise tell them we finished successfully
		return true;
    }
	
	/*** PLUGIN FUNCTIONS ***/
	
	private int buildProximityObserver() {
		if (!hasBluetoothAccess()) {
			getBluetoothPermissions();
		}
		if (hasCloudCredentials()) {
			try {			
				ProximityObserver proximityObserver = new ProximityObserverBuilder(appContext, cloudCredentials)
                .withBalancedPowerMode()
                .build();

				proximityObservers.add(proximityObserver);
				proximityHandlers.add(null);
				proximityCallbacks.add(null);
				return proximityObservers.size()-1;
			}
			catch (Exception e) {
				Log.e(PLUGIN_NAME, e.getStackTrace().toString());
			}
		}
		return -1;
	}
	
	private boolean startProximityObserver(JSONArray args, CallbackContext callbackContext) {
		try {
			int pid = args.getInt(0);
			if (isValidPid(pid)) {
				if (proximityObservers.get(pid) != null) {
					String attachKey = args.getString(1);
					String attachValue = args.getString(2);
					proximityCallbacks.set(pid, callbackContext);
					
					ProximityZone beaconZone = proximityObservers.get(pid).zoneBuilder()
					.forAttachmentKeyAndValue(attachKey, attachValue)
					.inFarRange()
					.withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
						@Override 
						public Unit invoke(ProximityAttachment proximityAttachment) {
							Log.d(PLUGIN_NAME, "entered zone!");
							PluginResult update = new PluginResult(Status.OK, "enter");
							update.setKeepCallback(true);
							proximityCallbacks.get(pid).sendPluginResult(update);
							return null;
						}
					})
					.withOnExitAction(new Function1<ProximityAttachment, Unit>() {
						@Override 
						public Unit invoke(ProximityAttachment proximityAttachment) {
							Log.d(PLUGIN_NAME, "exited zone!");
							PluginResult update = new PluginResult(Status.OK, "exit");
							update.setKeepCallback(true);
							proximityCallbacks.get(pid).sendPluginResult(update);
							return null;
						}
					})
					.create();
					
					proximityObservers.get(pid).addProximityZones(beaconZone);
				
					proximityHandlers.set(pid, proximityObservers.get(pid).start());
				}
			}
			else {
				return false;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	
	private boolean stopProximityObserverHandler(JSONArray args) {
		try {
			int pid = args.getInt(0);
			if (isValidPid(pid)) {
				proximityHandlers.get(pid).stop();
				proximityHandlers.set(pid, null);
				proximityObservers.set(pid, null);
				PluginResult finish = new PluginResult(Status.NO_RESULT);
                finish.setKeepCallback(false);
                proximityCallbacks.get(pid).sendPluginResult(finish);
			}
			else {
				return false;
			}
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	private boolean isValidPid(int pid) {
		if (pid >= proximityObservers.size()) {
			return false;
		}
		return true;
	}
	
	private boolean hasCloudCredentials() {
		if (cloudCredentials == null) {
			Log.w(PLUGIN_NAME, "No Cloud Credentials have been initialized yet!");
			return false;
		}
		return true;
	}
	
	private boolean setCloudCredentials(JSONArray args) {
		try {
			String appId = args.getString(0);
			String appToken = args.getString(1);
			cloudCredentials = new EstimoteCloudCredentials(appId, appToken);
		}
		catch (JSONException e) {
			Log.e(PLUGIN_NAME, e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	private boolean hasBluetoothAccess() {
		return hasBluetoothAccess;
	}
	
	/**
	 * Ensures that the app has access to the necessary system resources
	 */
	private void getBluetoothPermissions() {
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
	}
	
}