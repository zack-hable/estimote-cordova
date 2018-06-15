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
	
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
		document.addEventListener('pause', this.onAppMinimize.bind(this), false); // this won't work as expected on iOS
    },
	
	buildScanner: function(resp) {
		console.log("Bluetooth service enabled: "+resp);
		// build proximity scanner
		window.plugins.EstimoteProximity.proximityObserverBuilder()
		.withScannerInForegroundService([null, null, null, "Cordova Proximity Demo", null])
		.onSuccess(app.startScanner)
		.onError(app.displayError)
		.build();
	},
	startScanner: function(resp) {
		console.log("Proximity Scanner PID: "+resp);
		app.pids.push(resp);
		window.plugins.EstimoteProximity.startProximityObserver(resp, "zone", "desk", app.receiveResults, app.displayError);
	},
	receiveResults: function(resp) {
		console.log("Results received from scanner:"+resp);
		document.getElementById("status").innerText = resp;
	},
	
	displaySuccess: function(resp) {
		console.log("Success response received: "+resp);
	},
	displayError: function(resp) {
		console.log("Error response received: "+resp);
	},
	
	onAppMinimize: function() {
		console.log("attempting to stop all Proximity Observers...");
		for(i=0; i<app.pids.length; i++) {
			window.plugins.EstimoteProximity.stopProximityObserver(app.pids[i], app.displaySuccess, app.displayError);
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
		window.plugins.EstimoteProximity.getSystemPermissions(app.buildScanner, app.displayError);
    }
	
};

app.initialize();