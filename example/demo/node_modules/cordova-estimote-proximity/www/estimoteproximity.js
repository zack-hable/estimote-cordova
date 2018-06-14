var exec = require('cordova/exec');

var PLUGIN_NAME = 'EstimoteProximity';

function EstimoteProximity() {
	this.getSystemPermissions = function(onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'getSystemPermissions', []);
	};
	this.hasSystemPermissions = function(onSuccess, onError) {
		exec(onSuccess, onError, PLUGIN_NAME, 'hasSystemPermissions', []);
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