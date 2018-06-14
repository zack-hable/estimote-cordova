cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "cordova-estimote-proximity.estimoteProximity",
    "file": "plugins/cordova-estimote-proximity/www/estimoteproximity.js",
    "pluginId": "cordova-estimote-proximity",
    "clobbers": [
      "window.plugins.estimoteProximity"
    ]
  }
];
module.exports.metadata = 
// TOP OF METADATA
{
  "cordova-plugin-whitelist": "1.3.3",
  "cordova-estimote-proximity": "0.1.0"
};
// BOTTOM OF METADATA
});