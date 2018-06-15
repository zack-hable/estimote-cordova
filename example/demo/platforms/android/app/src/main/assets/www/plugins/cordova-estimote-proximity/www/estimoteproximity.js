cordova.define("cordova-estimote-proximity.estimoteProximity", function(require, exports, module) {
var exec = require('cordova/exec');

var PLUGIN_NAME = 'EstimoteProximity';

function EstimoteProximity() {
	this.getSystemPermissions = function(onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'getSystemPermissions', []);
	};
	this.hasSystemPermissions = function(onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'hasSystemPermissions', []);
	};
	this.setCloudCredentials = function(appId, appToken, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'setCloudCredentials', [appId, appToken]);
	};
	this.hasCloudCredentials = function(onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'hasCloudCredentials', []);
	};
	this.buildProximityObserver = function(analyticsReportingDisabled = false, estimoteSecureMonitoringDisabled = false, scannerInForegroundService = false, telemetryReportingDisabled = false, powerMode = 2, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'buildProximityObserver', [analyticsReportingDisabled, estimoteSecureMonitoringDisabled, scannerInForegroundService, telemetryReportingDisabled, powerMode]);
	};
	this.startProximityObserver = function(pid, attachKey, attachVal, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'startProximityObserver', [pid, attachKey, attachVal]);
	};
	this.stopProximityObserver = function(pid, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'stopProximityObserver', [pid]);
	};
}

EstimoteProximity.install = function() {
	if (!window.plugins) {
		window.plugins = {};
	}
	window.plugins.EstimoteProximity = new EstimoteProximity();
	return window.plugins.EstimoteProximity;
};

// Installation constructor that binds plugin to window
cordova.addConstructor(EstimoteProximity.install);
});
