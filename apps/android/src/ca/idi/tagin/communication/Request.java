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

package ca.idi.tagin.communication;

import java.util.List;

import android.net.wifi.ScanResult;

public class Request {

	private String type = null;
	private List<ScanResult> fingerPrint = null;
	private String xmlBodyParameter = null;
	private boolean shouldIncludeWifiData = false;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ScanResult> getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(List<ScanResult> fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public String getXmlBodyParameter() {
		return xmlBodyParameter;
	}

	public void setXmlBodyParameter(String xmlBodyParameter) {
		this.xmlBodyParameter = xmlBodyParameter;
	}

	public boolean shouldIncludeWifiData() {
		return shouldIncludeWifiData;
	}

	public void setShouldIncludeWifiData(boolean shouldIncludeWifiData) {
		this.shouldIncludeWifiData = shouldIncludeWifiData;
	}

}
