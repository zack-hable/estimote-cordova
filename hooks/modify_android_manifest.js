#!/usr/bin/env node

module.exports = function(context) {
  console.log("Attempting to update AndroidManifest...");
  var APPLICATION_CLASS = "com.zackhable.estimote.EstimoteProximityApplication";
  var ACTIVITY_ATTRIBUTE = 'android:launchMode="singleInstance"';

  var fs = context.requireCordovaModule('fs'),
      path = context.requireCordovaModule('path');

  var platformRoot = path.join(context.opts.projectRoot, 'platforms/android/app/src/main');
  var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');

  if (fs.existsSync(manifestFile)) {
    fs.readFile(manifestFile, 'utf8', function (err, data) {
      if (err) {
        throw new Error('Unable to find AndroidManifest.xml: ' + err);
      }
	  
	  var needsWrite = false;
	  var result = data;
	  
	  // add application 
	  if (result.indexOf(APPLICATION_CLASS) == -1) {
		result = result.replace(/<application/g, '<application android:name="' + APPLICATION_CLASS + '"');
		needsWrite = true;
	  }
	  // add activity launch mode
      if (result.indexOf(ACTIVITY_ATTRIBUTE) == -1) {
		// check if old activity launch mode and remove it
	    if (result.indexOf('android:launchMode="singleTop"') != -1) {
		  result = result.replace(/android:launchMode="singleTop"/g, '');
	    }
        result = result.replace(/<activity/g, '<activity ' + ACTIVITY_ATTRIBUTE);
		needsWrite = true;
      }
	  
	  if (needsWrite) {
		fs.writeFile(manifestFile, result, 'utf8', function (err) {
		  if (err) {
			  throw new Error('Unable to write into AndroidManifest.xml: ' + err);
		  }
		  else {
			  console.log("AndroidManifest updated successfully!");
		  }
		})
	  }
    });
  }
  else {
	  throw new Error("AndroidManifest.xml does not exist!");
  }
};