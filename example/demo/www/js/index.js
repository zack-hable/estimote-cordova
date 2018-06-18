/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
	pids: [],
	zids: [],
	
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
		document.addEventListener('pause', this.onAppMinimize.bind(this), false); // this won't work as expected on iOS
    },
	
	systemPermissionsFailed: function(info) {
		console.log("The request for system permissions was denied or had an error!");
		console.log(info);
	},
	buildScanner: function(resp) {
		console.log("Bluetooth service enabled: "+resp);
		// build proximity scanner
		window.plugins.EstimoteProximity.proximityObserverBuilder()
		.withScannerInForegroundService([null, null, null, "Cordova Proximity Demo", null])
		.withLowLatencyPowerMode()
		.withOnSuccessAction(app.buildZones)
		.withOnErrorAction(app.buildScannerFailed)
		.build();
	},
	buildScannerFailed: function(info) {
		console.log("The request for building a scanner failed!");
		console.log(info);
	},
	buildZones: function(resp) {
		console.log("Proximity Scanner ID: "+resp);
		app.pids.push(resp);
		window.plugins.EstimoteProximity.proximityZoneBuilder()
		.forAttachmentKeyAndValue("zone", "desk")
		.inNearRange()
		.withOnEnterAction(app.onEnterZone)
		.withOnExitAction(app.onExitZone)
		.withOnSuccessAction(app.startScanner)
		.withOnErrorAction(app.buildZonesFailed)
		.forProximityObserver(app.pids[0])
		.create();
	},
	buildZonesFailed: function(info) {
		console.log("The request for building a beacon zone failed!");
		console.log(info);
	},
	startScanner: function(resp) {
		console.log("Proximity Zone ID: "+resp);
		app.zids.push(resp);
		window.plugins.EstimoteProximity.startProximityObserver(app.pids[0], app.zids, app.scannerStarted, app.scannerFailed);
	},
	onEnterZone: function(info) {
		console.log("User entered the zone!");
		console.log(info);
		document.getElementById("status").textContent = "entered";
	},
	onExitZone: function(info) {
		console.log("User exited the zone!");
		console.log(info);
		document.getElementById("status").textContent = "exited";
	},
	scannerStarted: function(info) {
		console.log("Proximity scanner started successfully!");
	},
	scannerFailed: function(info) {
		console.log("Proximity scanner failed to start!");
	},
	
	onAppMinimize: function() {
		console.log("attempting to stop all Proximity Observers...");
		for(i=0; i<app.pids.length; i++) {
			window.plugins.EstimoteProximity.stopProximityObserver(app.pids[i], app.scannersStopped, app.scannersStoppedFailed);
		}
		console.log("finished stopping all Proximity Observers!");
	},

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
		
		// store cloud credentials
		window.plugins.EstimoteProximity.setCloudCredentials(/*APP ID, APP TOKEN*/, app.displaySuccess, app.displayError);
		// request System permissions
		window.plugins.EstimoteProximity.getSystemPermissions(app.buildScanner, app.systemPermissionsFailed);
    }
	
};

app.initialize();