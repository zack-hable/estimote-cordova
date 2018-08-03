# Cordova Estimote Integration 

### Currently Implemented
* System permissions dialog
* Proximity SDK (v0.6.2)
	* Creation of Proximity Zones
	* Creation of Proximity Observers
	* Creation of Triggers (experimental)
	* Switch from Attachments to Tags
### Todos
* Proximity SDK
    * Triggers Verification
* Location SDK
* iOS Support
### Examples

## Running Proximity Demo
`git clone https://github.com/zack-hable/Estimote-Cordova-Examples.git`\
`cd proximitydemo`\
`cordova platform add android`\
`cordova plugin add cordova-estimote`\
`cordova run android`

## Adding to Your Project
`cordova plugin add https://github.com/zack-hable/estimote-cordova.git`
or from NPM
`npm i cordova-estimote-proximity --save`

If your app uses a foregroudn service (runs when your app is closed) then add your logic (and credentials) in [EstimoteProximityService.java](src/android/EstimoteProximityService.java)\
otherwise it will not be enabled