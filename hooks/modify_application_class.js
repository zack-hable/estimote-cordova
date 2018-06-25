#!/usr/bin/env node

module.exports = function(context) {
    var fs = require('fs');
    var path = require('path');
    var config_xml = path.join(context.opts.projectRoot, 'config.xml');
    var et = context.requireCordovaModule('elementtree');

    var data = fs.readFileSync(config_xml).toString();
    var etree = et.parse(data);
	
	var appClass = path.join(context.opts.projectRoot, 'platforms/android/app/src/main/java/com/zackhable/estimote/EstimoteProximityApplication.java');

	if (fs.existsSync(appClass)) {
		fs.readFile(appClass, 'utf8', function (err, data) {
			if (err) {
				throw new Error('Unable to find EstimoteProximityApplication.java: ' + err);
			}
			var APPLICATION_CLASS = etree.getroot().attrib.id;

			var result = data.replace(/MAIN_ACTIVITY/g, APPLICATION_CLASS);

			fs.writeFile(appClass, result, 'utf8', function (err) {
				if (err) {
					throw new Error('Unable to write into EstimoteProximityApplication.java: ' + err);
				}
				else {
					console.log("EstimoteProximityApplication updated successfully!");
				}
			})
		});
	}
	else {
		throw new Error("EstimoteProximityApplication.java does not exist!");
	}
};