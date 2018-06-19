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
		this.withOnSuccessAction = function(success) {
			this.success = success;
			return this;
		};
		this.withOnErrorAction = function(error) {
			this.error = error;
			return this;
		};
		this.build = function() {
			window.plugins.EstimoteProximity.buildProximityObserver(this.analyticsReportingDisabled, this.estimoteSecureMonitoringDisabled, this.scannerInForegroundService, this.telemetryReportingDisabled, this.powerMode, this.notification, this.success, this.error);
		};
	};
	function EstimoteProximityZoneBuilder() {
		var self = this;
		this.pid = -1;
		this.attachKey = "";
		this.attachValue = "";
		this.rangeMode = -2;
		this.onEnter = null;
		this.onExit = null;
		this.onSuccess = null;
		this.onError = null;
		
		this.forAttachmentKey = function(key) {
			this.attachKey = key;
			return this;
		};
		this.forAttachmentKeyAndValue = function(key, value) {
			this.attachKey = key;
			this.attachValue = value;
			return this;
		};
		this.inNearRange = function() {
			this.rangeMode = -2;
			return this;
		};
		this.inFarRange = function() {
			this.rangeMode = -1;
			return this;
		};
		this.inCustomRange = function(rangeDist) {
			this.rangeMode = rangeDist;
			return this;
		};
		this.withOnEnterAction = function(onEnter) {
			this.onEnter = onEnter;
			return this;
		};
		this.withOnExitAction = function(onExit) {
			this.onExit = onExit;
			return this;
		};
		this.forProximityObserver = function(pid) {
			this.pid = pid;
			return this;
		};
		this.withOnSuccessAction = function(onSuccess) {
			this.onSuccess = onSuccess;
			return this;
		};
		this.withOnErrorAction = function(onError) {
			this.onError = onError;
			return this;
		};
		this.onEvent = function(resp) {
			data = JSON.parse(resp);
			if (data["event"] == "enter") {
				self.onEnter(resp);
			}
			else if (data["event"] == "exit") {
				self.onExit(resp);
			}
			else {
				self.onSuccess(resp);
			}
		};
		this.create = function() {
			window.plugins.EstimoteProximity.buildProximityZone(this.pid, this.attachKey, this.attachValue, this.rangeMode, this.onEvent, this.onError);
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
	this.startProximityObserver = function(pid, proximityZones, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'startProximityObserver', [pid, proximityZones]);
	};
	this.stopProximityObserver = function(pid, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'stopProximityObserver', [pid]);
	};
	this.buildProximityZone = function(pid = -1, attachmentKey = "", attachmentValue = "", rangeMode = -2, onEvent, onError) {
		exec(onEvent, onError, PLUGIN_NAME, 'buildProximityZone', [pid, attachmentKey, attachmentValue, rangeMode]);
	};
	this.proximityZoneBuilder = function() {
		return new EstimoteProximityZoneBuilder();
	};
	this.deleteProximityZone = function(zoneId, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'deleteProximityZone', [zoneId]);
	};
	this.buildProximityTrigger = function(notification = [], onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'buildProximityTrigger', [notification]);
	};
	this.startProximityTrigger = function(tid, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'startProximityTrigger', [tid]);
	};
	this.stopProximityTrigger = function(tid, onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'stopProximityTrigger', [tid]);
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