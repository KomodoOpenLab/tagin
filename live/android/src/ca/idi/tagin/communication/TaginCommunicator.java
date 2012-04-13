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

import java.util.concurrent.ConcurrentLinkedQueue;

import ca.idi.tagin.jsinterface.JavaToJSInterface;
import ca.idi.tagin.wifi.WifiScanner;
import ca.idi.tagin.xmlutil.NoWifiDataException;
import ca.idi.tagin.xmlutil.XMLProcesser;

public class TaginCommunicator {
	// extends Service

	protected ConcurrentLinkedQueue<Request> requestQueue = null;
	private static TaginCommunicator communicatorInstance = null;
	private XMLProcesser xmlProcessor = new XMLProcesser();
	private volatile Thread serviceThread = null;
	private WifiScanner wifiScanner = null;
	private JavaToJSInterface javaToJSInterface = null;
	private boolean isCommunicatorPaused = false; 

	private TaginCommunicator() {
		javaToJSInterface = JavaToJSInterface.getInstance(null);
		requestQueue = new ConcurrentLinkedQueue<Request>();
		// Intent serviceIntent = new Intent(getApplicationContext(),
		// TaginCommunicator.class);
		// getApplicationContext().startService(serviceIntent);
	}

	public void startCommunitator() {
		if (isCommunicatorPaused) {
			resumeCommunicator();
		}
		serviceThread = new Thread(new CommunicationService());
		serviceThread.start();
	}

	public static TaginCommunicator getInstance() {
		if (communicatorInstance == null) {
			communicatorInstance = new TaginCommunicator();
		}
		return communicatorInstance;
	}

	public void stopCommunicatorServices() {
		serviceThread = null;
	}

	/*
	 * @Override public IBinder onBind(Intent arg0) { // TODO Auto-generated
	 * method stub return null; }
	 * 
	 * @Override public void onCreate() { super.onCreate();
	 * System.out.println("Communication serivce started..."); javaToJSInterface
	 * = JavaToJSInterface.getInstance(null); //serviceThread = new Thread(new
	 * CommunicationService()); //serviceThread.start(); }
	 * 
	 * @Override public void onDestroy() { communicatorInstance = null;
	 * super.onDestroy();
	 * System.out.println("Communication service stopped..."); }
	 */

	public void requestRefreshedData() throws NoWifiScannerException {
		requestAllVisibleTags();
		requestCurrentLocation();
	}

	public void requestAllVisibleTags() throws NoWifiScannerException {
		putRequestOnQueue("request_all_visible_tags", null, true);
	}

	public void requestCurrentLocation() throws NoWifiScannerException {
		putRequestOnQueue("request_current_location", null, true);
	}

	public void addTags(String tagsXML) throws NoWifiScannerException {
		putRequestOnQueue("add_tags", tagsXML, true);
	}
	
	public void addLocation(String locationXML) throws NoWifiScannerException {
		putRequestOnQueue("add_location", locationXML, true);
	}

	private void putRequestOnQueue(String requestType, String xmlBodyParameter,
			boolean shouldIncludeWifi) throws NoWifiScannerException {
		Request r = new Request();
		r.setType(requestType);
		if (xmlBodyParameter != null) {
			r.setXmlBodyParameter(xmlBodyParameter);
		}
		if (shouldIncludeWifi) {
			if (wifiScanner == null) {
				wifiScanner = WifiScanner.getInstance(null, null);
				if (wifiScanner == null) {
					throw new NoWifiScannerException();
				}
			}
			r.setFingerPrint(wifiScanner.getLatestScan());
			r.setShouldIncludeWifiData(true);
		} else {
			r.setShouldIncludeWifiData(false);
		}
		requestQueue.add(r);
		// serviceThread.notify();
	}

	public void pauseCommunicator() {
		System.out.println("Communicator paused.");
		isCommunicatorPaused = true;
	}
	
	public void resumeCommunicator() {
		System.out.println("Communicator resumed.");
		isCommunicatorPaused = false;
	} 

	public boolean isCommunicatorPaused() {
		return isCommunicatorPaused;
	}

	protected class CommunicationService implements Runnable {

		public synchronized void sendRequest(Request r) {
			if(isCommunicatorPaused()) {
				System.out.println("Reveived request for sending. Communicator is paused..silently aborting request. Request type: " + r.getType());
				return;
			}
			try {
				String xmlRequest = xmlProcessor.generateRequestXML(r);
				// TCPSSLConnector ssl = new TCPSSLConnector();
				// ssl.setRequestString(xmlRequest);
				// new Thread(ssl).start();
				//UDPConnector udp = new UDPConnector();
				//udp.setRequestString(xmlRequest);
				TCPConnector tcp = new TCPConnector();
				tcp.setRequestString(xmlRequest);
				javaToJSInterface.setStatusMessage("Contacting server...");
				new Thread(tcp).start();
			} catch (NoWifiDataException e) {
				javaToJSInterface.noWiFiNotification();
			} catch (Exception e) {
				javaToJSInterface.communicationErrorNoficication();
			}
		}

		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();

			while (serviceThread == thisThread) {
				if (requestQueue.isEmpty()) {
					try {
						synchronized (this) {
							wait(1000);
						}
					} catch (Exception e) {
						javaToJSInterface.setStatusMessage("Error: " + e);
						e.printStackTrace();
					}
				} else {
					sendRequest(requestQueue.poll());
				}
			}
			System.out.println("Communication service stopped.");

		}

	}

}
