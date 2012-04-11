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

package ca.idi.tagin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import ca.idi.tagin.communication.TaginCommunicator;
import ca.idi.tagin.jsinterface.JSToJavaInterface;
import ca.idi.tagin.jsinterface.JavaToJSInterface;
import ca.idi.tagin.wifi.WifiScanner;

public class Tagin extends Activity {
	private WebView webPage = null;
	private WifiScanner wifiScanner = null;
	private TaginCommunicator communicator = null;
	//public static final String taginGUIurl = "http://scyp.idrc.ocad.ca/devel/tagin/tagin.html";
	public static final String taginGUIurl = "file:///android_asset/tagin.html";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupWebView();
		
        Button refreshButton = (Button) findViewById(R.id.Button01);
    	refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startWifiScan();
            }
        });

		communicator = TaginCommunicator.getInstance();
		wifiScanner = WifiScanner.getInstance(getApplicationContext(), webPage);
	}

	@Override
	public void onDestroy() {
		wifiScanner.stopWifiScanner();
		communicator.stopCommunicatorServices();
		super.onDestroy();
	}

	public void setupWebView() {
		webPage = (WebView) findViewById(R.id.webpageContainer);
		webPage.setWebChromeClient(new TaginWebChromeClient());
		webPage.setWebViewClient(new TaginWebViewClient());
		WebSettings webSettings = webPage.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webPage.addJavascriptInterface(new JSToJavaInterface(),
				"androidInterface");
		JavaToJSInterface.getInstance(webPage);
		System.out.println("WebView has been setup. Loading url: "
				+ taginGUIurl);
		webPage.loadUrl(taginGUIurl);
	}

	public void startWifiScan() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// Enable wifi if necessary
		// TODO: Add dialog to confirm
		JavaToJSInterface javaToJSInterface;
		javaToJSInterface = JavaToJSInterface.getInstance(null);
		if (wifiManager.isWifiEnabled()) {
			wifiManager.startScan();
			javaToJSInterface.setStatusMessage(getText(R.string.refresh_requested).toString());			
        } else {
			javaToJSInterface.setStatusMessage("WiFi is not enabled!");			
        }
	}
	
	private class TaginWebChromeClient extends WebChromeClient {

		public TaginWebChromeClient() {
			super();
		}

		/*
		 * @Override public boolean onJsAlert(WebView view, String url, String
		 * message, JsResult result) { Log.d("tagin!", message);
		 * result.confirm(); return true; }
		 */

	}

	private class TaginWebViewClient extends WebViewClient {

		public TaginWebViewClient() {
			super();
		}

		/*
		 * @Override public boolean onJsAlert(WebView view, String url, String
		 * message, JsResult result) { Log.d("tagin!", message);
		 * result.confirm(); return true; }
		 */

		public void loadURLInBrowser(String url) {
			try {
				Intent viewIntent = new Intent("android.intent.action.VIEW",
						Uri.parse(url));
				startActivity(viewIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.equals(Tagin.taginGUIurl)) {
				return false;
			}
			loadURLInBrowser(url);
			return true;
		}
	}
}