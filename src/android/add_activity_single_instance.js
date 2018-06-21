#!/usr/bin/env node

module.exports = function(context) {

  var ACTIVTY_ATTRIBUTE = 'android:launchMode="singleInstance"';

  var fs = context.requireCordovaModule('fs'),
      path = context.requireCordovaModule('path');

  var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
  var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');

  if (fs.existsSync(manifestFile)) {
    fs.readFile(manifestFile, 'utf8', function (err, data) {
      if (err) {
        throw new Error('Unable to find AndroidManifest.xml: ' + err);
      }

      if (data.indexOf(ACTIVTY_ATTRIBUTE) == -1) {
        var result = data.replace(/<activity/g, '<activity ' + ACTIVTY_ATTRIBUTE + '"');
        fs.writeFile(manifestFile, result, 'utf8', function (err) {
          if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
        })
      }
    });
  }
};