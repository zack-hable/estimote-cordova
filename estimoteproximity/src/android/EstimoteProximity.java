package com.zackhable.estimote;

// Java Libs
import java.util.List;
// Cordova Libs
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
// Android Libs
import android.util.Log;
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
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;

public class EstimoteProximity extends CordovaPlugin {
	
	public static final String PLUGIN_NAME = "EstimoteProximity";
	private static boolean hasBluetoothAccess = false;
	
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
			callbackContext.sendPluginResult(new PluginResult(Status.OK, hasBluetoothAccess));
		}
		else { // endpoint doesn't exist
			return false;
		}
		// otherwise tell them we finished successfully
		return true;
    }
	
	/*** PLUGIN FUNCTIONS ***/
	
	/**
	 * Ensures that the app has access to the necessary system resources
	 */
	private void getBluetoothPermissions() {
		Log.d(PLUGIN_NAME, "Requesting access to system resources...");
		RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
			cordova.getActivity(), 
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