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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import ca.idi.tagin.jsinterface.JavaToJSInterface;

public class TCPConnector implements Runnable {
	
	private String serverAddress = "noodle.idrc.ocad.ca"; 
	private int port = 9135;
	private JavaToJSInterface javaToJSInterface = null;
	private String requestBody = null;
	public static final int CONNECTION_TIMEOUT = 20000;

	
	public void setRequestString(String request) {
		requestBody = request;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		try {
			javaToJSInterface = JavaToJSInterface.getInstance(null);
			socket = new Socket(serverAddress, port);
			socket.setSoTimeout(CONNECTION_TIMEOUT);
			byte reqBytes[] = requestBody.getBytes();
            OutputStream outputstream = socket.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
            BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
            PrintWriter out = new PrintWriter(bufferedwriter);
            
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println(reqBytes.length + '\0');
            out.println(reqBytes.length + '\0');
            out.flush();
        	out.println(requestBody);
        	out.flush();

			String responseString = "";
            String line = null;
            while ((line = in.readLine()) != null) {
            	responseString += line;
            }
            socket.close();
            System.out.println(URLEncoder.encode(responseString, "UTF-8"));
            
			javaToJSInterface.processResponse(URLEncoder.encode(responseString, "UTF-8"));
			
		} catch(UnknownHostException e) {
			e.printStackTrace();
			javaToJSInterface.setStatusMessage("Error: Unknown server address!");
		} catch(ConnectException e) {
			e.printStackTrace();
			javaToJSInterface.setStatusMessage("Error: server is down!!!");			
		} catch (IOException e) {
			e.printStackTrace();
			javaToJSInterface.setStatusMessage("Failed to create connection socket.");			
		} catch (Exception e) {
			e.printStackTrace();
			javaToJSInterface.setStatusMessage("Error connecting to TCP server!");
		}
		
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}		
	}
}
