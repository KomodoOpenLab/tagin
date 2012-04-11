/**
 * tagin!, a WiFi-based point-of-interest (POI) service for tagging 
 * outdoor and indoor locations
 * Copyright (c) 2010, Inclusive Design Research Centre
 * jsilva@ocad.ca
 *
 * You may use tagin! under the terms of either the MIT License or the
 * GNU General Public License (GPL).
 * 
 * The MIT License is recommended for most projects. It is simple and easy to
 * understand, and it places almost no restrictions on what you can do with
 * tagin! 
 * 
 * If the GPL suits your project better, you are also free to use tagin! under
 * that license.
 * 
 * You don't have to do anything special to choose one license or the other,
 * and you don't have to notify anyone which license you are using.
 * 
 * MIT License: http://scyp.idrc.ocad.ca/pub/licenses/MITL.txt
 * GPL License: http://scyp.idrc.ocad.ca/pub/licenses/GPL.txt
 * 
 * @author Susahosh Rahman
 * @email susahosh@gmail.com
 * @author Jorge Silva
 * @email jsilva@ocad.ca
 **/

package ca.idi.tagin.xmlutil;

import java.util.List;

import android.net.wifi.ScanResult;
import ca.idi.tagin.communication.Request;
import ca.idi.tagin.wifi.WifiScanner;

public class XMLProcesser {
	// private final String DTD =
	// "<!DOCTYPE client_message [<!ELEMENT client_message (message_type, authentication?,location?,tag, scans)> <!ELEMENT message_type (#PCDATA)> <!ELEMENT authentication (user, key)> <!ELEMENT user (#PCDATA)><!ELEMENT key (#PCDATA)><!ELEMENT location (unit, floor, building_name, street, city, province, country)><!ELEMENT unit (#PCDATA)><!ELEMENT floor (#PCDATA)><!ELEMENT building_name (#PCDATA)><!ELEMENT street (#PCDATA)><!ELEMENT city (#PCDATA)><!ELEMENT province (#PCDATA)><!ELEMENT country (#PCDATA)><!ELEMENT tag (#PCDATA)><!ELEMENT scans (scan+)><!ELEMENT scan (ap+)><!ELEMENT ap (mac,strength,channel,mode)><!ELEMENT mac (#PCDATA)><!ELEMENT strength (#PCDATA)><!ELEMENT channel (#PCDATA)><!ELEMENT mode (#PCDATA)>]>";
	private final String DTD = "";

	public String generateRequestXML(Request req) throws NoWifiDataException {
		String xmlRequest = "<?xml version=\"1.0\"?>" + DTD
				+ "<client_message><message_type>" + req.getType()
				+ "</message_type>";
		if (req.shouldIncludeWifiData()) {
			String wifiData = generateFingerPrintXML(req.getFingerPrint());
			if (wifiData == null) {
				throw new NoWifiDataException();
			}
			xmlRequest += wifiData;
		}
		String xmlBodyParameter = req.getXmlBodyParameter();
		if (xmlBodyParameter != null) {
			xmlRequest += xmlBodyParameter;
		}
		/*
		 * if (xmlBodyParameter == null && shouldIncludeWifiData == false) {
		 * document.getElementById("status").innerHTML = "";
		 * document.getElementById("status").innerHTML =
		 * "Warning: sending empty request to server!"; }
		 */
		xmlRequest += "</client_message>";
		return xmlRequest;
	}

	public String generateFingerPrintXML(List<ScanResult> apList) {
		String xml = "<scans><scan>";
		if (apList == null) {
			return null;
		} else if (apList.size() == 0) {
			return null;
		}
		for (int i = 0; i < apList.size(); i++) {
			ScanResult ap = apList.get(i);
			xml += "<ap>";
			xml += "<mac>" + ap.BSSID + "</mac>";
			xml += "<strength>" + ap.level + "</strength>";
			int channel = WifiScanner.frequencyToChannel(ap.frequency);
			xml += "<channel>" + channel + "</channel>";
			if (ap.capabilities.contains("[IBSS]")) {
				xml = xml + "<mode>Ad-hoc</mode>";
			} else {
				xml = xml + "<mode>Master</mode>";
			}
			xml += "</ap>";
		}
		xml += "</scan></scans>";
		return xml;
	}

}
