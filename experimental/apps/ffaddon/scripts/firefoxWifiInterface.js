/**
 * tagin!, a WiFi-based point-of-interest (POI) service
 * for tagging outdoor and indoor locations
 * Copyright (c) 2009, University of Toronto
 * scyp@atrc.utoronto.ca
 *
 * Dual licensed under the MIT and GPL licenses.
 * http://scyp.atrc.utoronto.ca/projects/tagin/license
 *
 * @author: Doug Turner, Jorge Silva, Susahosh Rahman
 **/


/*global tagin, firefoxwifiInterface, netscape, Components*/
var firefoxWifiInterface = firefoxWifiInterface  || {};
var tagin = tagin || {};

(function (firefoxwifiInterface, tagin) {
	
	var wiFiChanges = 0;
	var WifiListener = function () {};
	var wifi_listener = null;

	var updateFingerprint = function (accessPoints) {
		wiFiChanges++;
		var latestAccessPoints = [];
		for (var i = 0; i < accessPoints.length; i++) {
			var ap = accessPoints[i];
			latestAccessPoints[i] = new tagin.dataManager.WiFi(ap.mac.replace(/-/ig, ":"), ap.signal, "-1", "unknown");
		}
		tagin.setFingerprint(new tagin.dataManager.Fingerprint(latestAccessPoints, null));
	};

	WifiListener.prototype = {
		onChange: function (accessPoints) {
		    netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
			updateFingerprint(accessPoints);
			setTimeout(tagin.requestCurrentLocation, 250);	
			setTimeout(tagin.requestAllVisibleTags, 3250);	
		},
	
		onError: function (value) {
		    //alert("error: " + value);
			tagin.clientInterface.errorHandler(value);
		},
	
		QueryInterface: function (iid) {
		    netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		    if (iid.equals(Components.interfaces.nsIWifiListener) ||
			iid.equals(Components.interfaces.nsISupports)) {
				return this;
			}
		    throw Components.results.NS_ERROR_NO_INTERFACE;
		}
	};
		
	/** firefox WiFi Interface*/		
	firefoxWifiInterface.startWiFi = function () {
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		wifi_listener = new WifiListener();
		var wifi_service = Components.classes["@mozilla.org/wifi/monitor;1"].getService(Components.interfaces.nsIWifiMonitor);
		wifi_service.startWatching(wifi_listener);
	};
	
	firefoxWifiInterface.stopWiFi = function () {
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		var wifi_service = Components.classes["@mozilla.org/wifi/monitor;1"].getService(Components.interfaces.nsIWifiMonitor);
		wifi_service.stopWatching(wifi_listener);        
	};
	
	
	
}(firefoxWifiInterface, tagin));

