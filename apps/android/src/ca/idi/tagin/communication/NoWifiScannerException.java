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

public class NoWifiScannerException extends Exception {

	public NoWifiScannerException() {
		super("WiFi scanner has not been setup.");
	}

}
