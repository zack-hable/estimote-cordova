var exec = require('cordova/exec');

var PLUGIN_NAME = 'EstimoteProximity';

function EstimoteProximity() {
	function EstimoteProximityBuilder() {
		this.analyticsReportingDisabled = false;
		this.estimoteSecureMonitoringDisabled = false;
		this.scannerInForegroundService = false;
		this.telemetryReportingDisabled = false;
		this.powerMode = 2;
		this.notification = [];
		this.success = null;
		this.error = null;
		
		this.withAnalyticsReportingDisabled = function() {
			this.analyticsReportingDisabled = true;
			return this;
		};
		this.withEstimoteSecureMonitoringDisabled = function() {
			this.estimoteSecureMonitoringDisabled = true;
			return this;
		};
		this.withScannerInForegroundService = function(notification) {
			this.scannerInForegroundService = true;
			this.notification = notification;
			return this;
		};
		this.withTelemetryReportingDisabled = function() {
			this.telemetryReportingDisabled = true;
			return this;
		};
		this.withBalancedPowerMode = function() {
			this.powerMode = 2;
			return this;
		};
		this.withLowLatencyPowerMode = function() {
			this.powerMode = 1;
			return this;
		};
		this.withLowPowerMode = function() {
			this.powerMode = 0;
			return this;
		};
		this.onSuccess = function(success) {
			this.success = success;
			return this;
		};
		this.onError = function(error) {
			this.error = error;
			return this;
		};
		this.build = function() {
			window.plugins.EstimoteProximity.buildProximityObserver(this.analyticsReportingDisabled, this.estimoteSecureMonitoringDisabled, this.scannerInForegroundService, this.telemetryReportingDisabled, this.powerMode, this.notification, this.success, this.error);
		};
	};
	
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
	this.buildProximityObserver = function(analyticsReportingDisabled = false, estimoteSecureMonitoringDisabled = false, scannerInForegroundService = false, telemetryReportingDisabled = false, powerMode = 2, notification = [], onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'buildProximityObserver', [analyticsReportingDisabled, estimoteSecureMonitoringDisabled, powerMode, telemetryReportingDisabled, scannerInForegroundService, notification]);
	};
	this.proximityObserverBuilder = function() {
		return new EstimoteProximityBuilder();
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