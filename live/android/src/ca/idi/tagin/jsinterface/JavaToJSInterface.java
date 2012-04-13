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

package ca.idi.tagin.jsinterface;

import java.util.ArrayList;

import ca.idi.tagin.communication.TaginCommunicator;

import android.webkit.WebView;

public class JavaToJSInterface {
	private WebView webPage = null;
	private static JavaToJSInterface currentInstance = null;

	private JavaToJSInterface() {
	}

	public static JavaToJSInterface getInstance(WebView w) {

		if (currentInstance == null) {
			if (w == null) {
				// exception should be thrown here..for now return null
				return null;
			} else {
				currentInstance = new JavaToJSInterface();
				currentInstance.webPage = w;
				return currentInstance;
			}
		} else {
			if (w != null) {
				currentInstance.webPage = w;
			}
		}
		return currentInstance;
	}

	public synchronized void jsInvoker(String functionName, ArrayList<String> paramerter) {
		if (TaginCommunicator.getInstance().isCommunicatorPaused()) {
			System.out.println("JavaToJSInterace received request to invoke '" + functionName + "()' function.");
			System.out.println("Currently in writtin mode..silently aborting invoke request.");
			return;
		}
		String p = "";
		if (paramerter != null) {
			for (int i = 0; i < paramerter.size(); i++) {
				p += "'" + (String) paramerter.get(i) + "',";
			}
			p = p.substring(0, p.length() - 1);
		}
		try {
			String jsFunction = "javascript:" + functionName + "(" + p + ");";
			webPage.loadUrl(jsFunction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void noWiFiNotification() {
		jsInvoker("setNoWiFiData", null);
	}

	public void errorNotification() {
		jsInvoker("setCommnicationError", null);
	}

	public void communicationErrorNoficication() {
		jsInvoker("setCommunicationError", null);
	}

	public void setStatusMessage(String status) {
		ArrayList<String> a = (new ArrayList<String>());
		a.add(status);
		jsInvoker("setStatusMessage", a);
	}

	public void processResponse(String res) {
		ArrayList<String> a = (new ArrayList<String>());
		a.add(res);
		jsInvoker("responseHandler", a);
	}

}
