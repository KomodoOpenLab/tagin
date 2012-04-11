/***
tagin!, a WiFi-based point-of-interest (POI) service for tagging 
outdoor and indoor locations
Copyright (c) 2008 - 2009, University of Toronto
scyp@atrc.utoronto.ca
http://scyp.atrc.utoronto.ca/wips/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program, see enclosed file gpl-3.txt or browse to
<http://www.gnu.org/licenses/gpl.txt>

@author: Susahosh Rahman
***/

var latestAccessPoints;
var currentAddress = new LocationSpec(undefined, undefined, undefined, undefined, undefined, undefined, undefined);
var     DTD = "";


function LocationSpec(room, floor, building, street, city, province, country, postal) {
	this.room = room;
	this.floor = floor;
	this.building = building;
	this.street = street;
	this.city = city;
	this.province = province;
	this.country = country;
	this.postal = postal;
}

function Tag(value, distance, frequency) {
	this.value = value;
	this.distance = distance;
	this.frequency = frequency;
}

function AccessPoint(mac, strength, channel, mode) {
	this.mac = mac;
	this.strength = strength;
	this.channel = channel;
	this.mode = mode;
}

/*function updateLatestAPList(accessPoints, isDebugMode) {
    if (isDebugMode) {
    	var taginFP = document.getElementById("taginFP");
		taginFP.innerHTML = "";
		var taginCounter = document.getElementById("taginCounter");
		taginCounter.innerHTML = "<p>Refreshed " + wiFiChanges++ + " time(s) </p>";
    }
	//alert(accessPoints == null);
	if (accessPoints == null) {
		latestAccessPoints = null;
	}
	else {
		latestAccessPoints = new Array();
		//alert(accessPoints);
	}
    for (var i=0; i<accessPoints.length; i++) {
        var ap = accessPoints[i];
        mac = ap.mac;
        mac = mac.replace(/-/ig, ":")
		latestAccessPoints[i] = new AccessPoint(mac, ap.signal, "-1", "unknown");
		if (isDebugMode) {
	        taginFP.innerHTML = taginFP.innerHTML + "<p>" + mac + " " + " " + ap.signal + "</p>";
		}
    }
	//alert(latestAccessPoints);
}*/


function generateRequestXML(requestType, xmlBodyParameter, shouldIncludeWifiData) {
		xmlRequest =  '<?xml version="1.0"?>'+ DTD + '<client_message><message_type>'+requestType+'</message_type>';
		if (shouldIncludeWifiData) {
			wifiData = generateFingerPrintXML(latestAccessPoints);
			if (wifiData == null) {
				document.getElementById("status").innerHTML = "";
				document.getElementById("status").innerHTML = "No WiFi data available!";
				return null;
			}
			xmlRequest += wifiData;
		}
		if (xmlBodyParameter != null) {
			xmlRequest += xmlBodyParameter;
		}
		if (xmlBodyParameter == null && shouldIncludeWifiData == false) {
				document.getElementById("status").innerHTML = "";
				document.getElementById("status").innerHTML = "Warning: sending empty request to server!";
		}
	    xmlRequest += '</client_message>';
		return xmlRequest;
}

function generateFingerPrintXML(apList) {
    xml = "<scans><scan>"
	if (apList == null) {
		return null;
	} else if (apList.length == 0) {
		return null;
	}
    for (var i=0; i<apList.length; i++) {
        var ap = apList[i];
	xml += "<ap>"
	xml += "<mac>" + ap.mac + "</mac>";
	xml += "<strength>" + ap.strength + "</strength>";
	xml += "<channel>" + ap.channel + "</channel>";
	xml += "<mode>" + ap.mode + "</mode>";
	xml += "</ap>"
    }
    xml += "</scan></scans>"
    return xml
}


function parseXMLFromString(text) {
	var xmlDoc = null;
	try {
	  //Internet Explorer
	  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
	  xmlDoc.async="false";
	  xmlDoc.loadXML(text);
	  } catch(e) {
		  try {
		  //Firefox, Mozilla, Opera, etc.
		  parser=new DOMParser();
		  xmlDoc=parser.parseFromString(text,"text/xml");
		  } catch(e) { 
			alert(e.message);
		  	return null;
		  }
	}
	return xmlDoc
}

function getTagElementInfo(tag, element) {
	v = null;
	try {
	 v = tag.getElementsByTagName(element)[0].childNodes[0].nodeValue;
	} catch (e) {
		return null;
	}
	return v;
}

function getElementValue(xml, elementName) {
	value = null;
	try {
		element = xml.getElementsByTagName(elementName);
		value = element[0].childNodes[0].nodeValue;
	} catch (e) {
		return null;
	}
	return value;
}


function getLocationInfoFor(xml,spec) {
	if (spec == null) {
		return '';
	}
	value = getElementValue(xml, spec);
	if (value == null) {
		return '';
	}
	return value;
}

function getCurrentLocation(xml) {
	if (xml == null) {
		return null;
	}
	room = getLocationInfoFor(xml,'room');
	floor = getLocationInfoFor(xml, 'floor');
	building= getLocationInfoFor(xml, 'building');
	street = getLocationInfoFor(xml, 'street');
	city = getLocationInfoFor(xml, 'city');
	province = getLocationInfoFor(xml, 'province');
	country = getLocationInfoFor(xml, 'country');
	postal = getLocationInfoFor(xml, 'postal');
	//alert(currentAddress.city);
	if (city == '' && (currentAddress.city != undefined && currentAddress.city != '')) {
		city = currentAddress.city;
	}
	if (province == '' && (currentAddress.province != undefined && currentAddress.province != '')) {
		province = currentAddress.province;
	}
	if (country == '' && (currentAddress.country != undefined && currentAddress.country != '')) {
		country = currentAddress.country;
	}
	if (room == '' && floor == '' && building == '' && 
		street == '' && city == '' && province == '' && 
		country == '' && postal == '') {
		return [];
	}
	loc = new LocationSpec(room, floor, building, street, city, province, country, postal);
	return loc;
}

function getTags(xml) {
	if (xml == null) {
		return null;
	}
	tags = new Array();
	tagElements = xml.getElementsByTagName("tag");
	if (tagElements.length == 0) {
		tagsElement = xml.getElementsByTagName("tags");
		if (tagsElement.length == 1) {
			return tags;
		}
		return null;
	}
	for (i=0;i < tagElements.length; i++) {
		tag = tagElements[i];
		value = getTagElementInfo(tag, "value");
		distance = getTagElementInfo(tag, "distance");
		frequency = getTagElementInfo(tag, "frequency");
		if (value == null || distance == null || frequency == null) {
			return null;
		}
		tags[i] = new Tag(value, distance, frequency);
	}
	return tags;
}
