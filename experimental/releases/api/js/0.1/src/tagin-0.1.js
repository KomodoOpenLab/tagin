/**
 * tagin!, a WiFi-based point-of-interest (POI) service
 * for tagging outdoor and indoor locations
 * Copyright (c) 2009, University of Toronto
 * scyp@atrc.utoronto.ca
 *
 * Dual licensed under the MIT and GPL licenses.
 * http://scyp.atrc.utoronto.ca/projects/tagin/license
 * 
 * @author Susahosh Rahman
 * @email susahosh.rahman@utoronto.ca
 **/


var tagin = tagin || {};
/*global tagin, DOMParser, ActiveXObject*/ 
 
(function (tagin) {

	//tagin! hookup interfaces.
	
	var taginClientInterface = {
		responseHandler: function (response) {},
		errorHandler: function (error) {},
		updateStatus: function (message) {}
	};
	
	//tagin! internal functions
	
	//functions for initialization checks
	
	var initialized = false;
	var checkInitiaization = function () {
		if (!initialized) {
			throw "tagin! API has not been initialized. Did you forget to call the loadTagin() function?";
		}
		return true;
	};
	var checkInterface = function (theObject, theInterface) {
		//based on Glen Ford's article: Programming to the Interface In JavaScript
	    for (var member in theInterface) {
	        if ((typeof theObject[member] !== typeof theInterface[member])) {
	            //alert("object failed to implement interface member " + member);
	            return false;
	        }
	    }
	    return true;
	};
	
	//functions for xml processing
	
	var DTD = "";//NOTE: this is not ready at the moment..so keep it empty for now
	
	var createElement = function (elementName, value) {
		if (!elementName || !value) {
			return "";
		}
		
		return "<" + elementName + ">" + value + "</" + elementName + ">";
	};
	
	var concat = function (body, param) {
		return param ? body + param : body;
	};
	
	var generateRequestXML = function (requestType, xmlBodyParameter, wifiDataXML) {
		if (!(xmlBodyParameter || wifiDataXML)) {
			tagin.clientInterface.updateStatus("Warning: sending empty request to server!");
		}
		return concat(concat('<?xml version="1.0"?>', DTD), createElement("client_message", concat(concat(createElement('message_type', requestType), wifiDataXML), xmlBodyParameter)));
	};
	
	var generateTagsElement = function (tagvals) {
		if (!tagvals) {
			return null;
		}
		try {
			var tags = tagvals.split(/,/);
			var tagList = '';
			for (var i = 0; i < tags.length; i++) {
				tagList += createElement('tag', tags[i].trim());
			}
			return createElement('tags', tagList);
		} catch (e) {
			return null;
		}
	};
	
	var generateLocationElement = function (loc) {
		var ele = "";
		ele += createElement("unit", loc.room);
		ele += createElement("floor", loc.floor);
		ele += createElement("building_name", loc.building);
		ele += createElement("street", loc.street);
		ele += createElement("city", loc.city);
		ele += createElement("province", loc.province);
		ele += createElement("country", loc.country);
		ele += createElement("postal", loc.postal);
		if (!ele) {
			return ele;
		}
		return createElement("location", ele);
		
	};
	
	var generateFingerPrintXML = function (apList) {
		if (!apList || apList.length === 0) {
			return null;
		}
		var scanList = '';
	    for (var i = 0; i < apList.length; i++) {
			var ap = apList[i];
			scanList += createElement("ap",
							createElement("mac", ap.mac) + 
							createElement("strength", ap.strength) +
							createElement("channel", ap.channel) +
							createElement("mode", ap.mode)
						);
	    }
	    return createElement("scans", createElement("scan", scanList));
	};
	
	var parseXMLFromString = function (text) {
		var xmlDoc = null;
		try {
			//Firefox, Mozilla, Opera, etc.
			var parser = new DOMParser();
			xmlDoc = parser.parseFromString(text, "text/xml");
		} catch (exp) {
			try {
				//Internet Explorer
				xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
				xmlDoc.async = "false";
				xmlDoc.loadXML(text);
			} catch (e) { 
				tagin.clientInterface.errorHandler(e);
				return null;
			}
		}
		return xmlDoc;
	};
	
	var getTagElementInfo = function (tag, element) {
	    try {
			return tag.getElementsByTagName(element)[0].childNodes[0].nodeValue;
		} catch (e) {
            return null;
		}
	};

	var getTags = function (xml) {
	    if (!xml) {
			return null;
	    }
	    var tags = [];
		//check if there is 'tag' element present in the xml
	    var tagElements = xml.getElementsByTagName("tag");
	    if (tagElements.length === 0) {
			//if there is no 'tag' elements, check to see
			//if empty tags element is present
			var tagsElement = xml.getElementsByTagName("tags");
			if (tagsElement.length === 1) {
				//if there is...return empty array
				return tags;
			}
			//else there is something wrong return null
            return null;
	    }
	
	    for (var i = 0; i < tagElements.length; i++) {
			var tag = tagElements[i];
			var value = getTagElementInfo(tag, "value");
			var distance = getTagElementInfo(tag, "distance");
			var frequency = getTagElementInfo(tag, "frequency");
			if (!(value && distance && frequency)) {
				return null;
			}
			tags[i] = new tagin.dataManager.Tag(value, distance, frequency);
	    }
	    return tags;
	};
	
	var getElementValue = function (xml, elementName) {
		var value = null;
		try {
	        var element = xml.getElementsByTagName(elementName);
	        value = element[0].childNodes[0].nodeValue;
		}
		catch (e) {
			return null;
		}
		return value;
	};

	var getLocationInfoFor = function (xml, spec) {
        if (!spec) {
			return '';
        }
        var value = getElementValue(xml, spec);
        return value ? value : '';
	};
	
	var getCurrentLocation = function (xml) {
		if (!xml) {
			return null;
		}
		var room = getLocationInfoFor(xml, 'room');
		var floor = getLocationInfoFor(xml, 'floor');
		var building = getLocationInfoFor(xml, 'building');
		var street = getLocationInfoFor(xml, 'street');
		var city = getLocationInfoFor(xml, 'city');
		var province = getLocationInfoFor(xml, 'province');
		var country = getLocationInfoFor(xml, 'country');
		var postal = getLocationInfoFor(xml, 'postal');
		if (!room && !floor && !building && !street && !city && !province && 
			!country && !postal) {
			return undefined;
		}	
		return new tagin.dataManager.Location(room, floor, building, street, city, province, country, postal);
	};
	
	
	//functions for communicating to tagin! server
	
	var req;
	var handleResponse = function (mType, val, errorMessage) {
		//var val = callback(args);
		//alert(val);
		if (val === null) {
			tagin.clientInterface.errorHandler(errorMessage);
		}
		else {
			tagin.clientInterface.responseHandler(
			    new tagin.serverResponse.Response(mType, 
				    mType === "error" ? "Server error: " + val : val));
		}
	};
	
	var processServerResponse = function (responsexml) {
		var messageType = getElementValue(responsexml, "response_type");
		switch (messageType) {
		case null:
			tagin.clientInterface.errorHandler("Received invalid response");
			break;
		case 'all_visible_tags':
			handleResponse(messageType, getTags(responsexml), "Error retriving tags from response.");
			break;
		case 'current_location':
		    handleResponse(messageType, getCurrentLocation(responsexml), "Error retriving current location from response.");
			break;
		case 'message':
		    handleResponse(messageType, getElementValue(responsexml, "message"), "Error retriving message from response.");
			break;
		case 'error':
		    handleResponse(messageType, getElementValue(responsexml, "error"), "Error retriving information from response.");
			break;
		default:
		    handleResponse("message", "Invalid response from server.", "");
			break;
		}
	};
	var serverResponseHandler = function () {
//		alert("recevied response");
        if (req.readyState === 4) {
			if (req.status === 200) {
				//alert(req.responseText);
				
				var xml = parseXMLFromString(req.responseText);
				if (!xml) {
					tagin.clientInterface.errorHandler("Error parsing response.");
					return;
				} else {
					processServerResponse(xml);
				}//end else if xml == null
			} else {
				//alert("error" + req.status);
				tagin.clientInterface.errorHandler("Error contacting server.");
			}
        }
	};
	
	var sendRequest = function (requestXMLBody) {
		req = new XMLHttpRequest();
		var scypServer = "http://noodle.atrc.utoronto.ca";
		var port = "8000";
	    var server = scypServer;
	    req.open('POST', server + ":" + port + "/", true);
	    req.onreadystatechange = serverResponseHandler;
	    req.overrideMimeType('text/plain; charset=x-user-defined');
		tagin.clientInterface.updateStatus("Contacting server...");
		//alert(requestXMLBody);
	    req.send(requestXMLBody);
	};
	
	var sendRequestWithWiFiData = function (requestType, xmlBodyParameter) {
		var fingerprintToSend = tagin.getFingerprint();
		if (!fingerprintToSend) {
			tagin.clientInterface.errorHandler("Failed to send request. No Fingerprint given! Did you forget to call tagin.setFingerprint() function?");
			return;
		}
		var wifiData = fingerprintToSend.wifiInfo;
		if (!wifiData || wifiData.length === 0) {
			tagin.clientInterface.errorHandler("No WiFi data available.");
			return;
		}
		sendRequest(generateRequestXML(requestType, xmlBodyParameter, generateFingerPrintXML(wifiData)));
	};
	

	/**tagin! public API functions*/
	var fingerprint = null;
	tagin.dataManager = {};
	tagin.dataManager.WiFi = function (mac, strength, channel, mode) {
		this.mac = mac;
		this.strength = strength;
		this.channel = channel;
		this.mode = mode;
	};
	tagin.dataManager.GPS = function (lat, lon) {
		this.lat = lat;
		this.lon = lon;
	};
	tagin.dataManager.Fingerprint = function (wifiInfo, gpsInfo) {
		//gps is optional..at least for now
		if (gpsInfo) {
			if (gpsInfo instanceof tagin.dataManager.GPS) {
				throw "Invalid GPS data given. GPS data must a GPS object";
			}
		}
		if (!(wifiInfo instanceof Array)) {
			throw "Invalid scan data given. Scan data must be an array of WiFi objects";
		}
		for (var i = 0; i < wifiInfo.length; i++) {
			if (!wifiInfo[i] instanceof tagin.dataManager.WiFi) {
				throw "Invalid WiFi data given. Scan data must be an array of WiFi objects";
			}
		}
		this.wifiInfo = wifiInfo;
		this.gpsInfo = gpsInfo;
	};

	tagin.dataManager.Tag = function (value, distance, frequency) {
		this.value = value;
		this.distance = distance;
		this.frequency = frequency;
	};
	
	tagin.dataManager.Location = function (room, floor, building, street, city, province, country, postal) {
		this.room = room;
		this.floor = floor;
		this.building = building;
		this.street = street;
		this.city = city;
		this.province = province;
		this.country = country;
		this.postal = postal;
	};
	
	tagin.serverResponse = {};
	tagin.serverResponse.Response = function (responseType, responseData) {
		this.responseType = responseType;
		this.responseData = responseData;
		this.getResponseType = function () {
			return this.responseType; 
		};
		this.getResponseData = function () {
			return this.responseData; 
		};
	};
	
	tagin.loadTagin = function (clientInterface) {
		if (!clientInterface) {
			throw "No GUI Interface given.";
		}
		if (!checkInterface(clientInterface, taginClientInterface)) {
			throw "Invalid GUI Interface given.";
		}
		if (initialized) {
			return;
		}
		tagin.clientInterface = clientInterface;
		initialized = true;
	};
	
	tagin.unloadTagin = function () {
		tagin.clientInterface = null;
		initialized = false;
	};
	
	tagin.setFingerprint = function (fp) {
		if (!(fp instanceof tagin.dataManager.Fingerprint)) {
			throw "Invalid Fingerprint given";
		}
		fingerprint = fp;
	};
	
	tagin.getFingerprint = function () {
		return fingerprint;
	};
	
	tagin.requestAllVisibleTags = function () {
		checkInitiaization();
		sendRequestWithWiFiData("request_all_visible_tags", null);
	};
	tagin.requestCurrentLocation = function () {
		checkInitiaization();
		sendRequestWithWiFiData("request_current_location", null);
	};
	tagin.requestRefreshedData = function () {
		checkInitiaization();
		tagin.requestAllVisibleTags();
		setTimeout(tagin.requestCurrentLocation, 2000);	
	};
	tagin.addTags = function (tagsString) {
		checkInitiaization();
		var tagsElement = generateTagsElement(tagsString);
		if (!tagsElement) {
			tagin.clientInterface.errorHandler("Error occured while parsing tags.");
			return;
		}
		sendRequestWithWiFiData("add_tags", tagsElement);
	};
	tagin.addLocation = function (newLocation) {
		checkInitiaization();
		if (!(newLocation instanceof tagin.dataManager.Location)) {
			throw "Invalid location given. Did you give an instance of tagin.dataManager.Location class?";
		}
		var locationElement = generateLocationElement(newLocation);
		if (!locationElement) {
			tagin.clientInterface.updateStatus("Error generating location info.");
			return;
		}
		sendRequestWithWiFiData("add_location", locationElement);
	};
	
}(tagin));
