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

var req;//this is probably not needed


function setStatusMessage(message) {
	//document.getElementById("status").innerHTML = "";
	document.getElementById("status").innerHTML = new String(message);
}


function setCommunicationError() {
	document.getElementById("status").innerHTML = "";
	document.getElementById("status").innerHTML = "Error contacting server.";
}
	
function decodeJavaUTF8String(encodedString ) {
	try {
		/*According to Java 5.0 API the URLEncoder uses the following rule to encode:
		* http://java.sun.com/j2se/1.5.0/docs/api/java/net/URLEncoder.html
		* The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain the same.
		* The special characters ".", "-", "*", and "_" remain the same.
		* The space character " " is converted into a plus sign "+".
		* All other characters are unsafe and are first converted into one or more bytes using some encoding scheme. Then each byte is represented by the 3-character string "%xy", where xy is the two-digit hexadecimal representation of the byte. The recommended encoding scheme to use is UTF-8. However, for compatibility reasons, if an encoding is not specified, then the default encoding of the platform is used. 
		* and to decode:
		*  The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain the same.
		* The special characters ".", "-", "*", and "_" remain the same.
		* The plus sign "+" is converted into a space character " " .
		* A sequence of the form "%xy" will be treated as representing a byte where xy is the two-digit hexadecimal representation of the 8 bits. Then, all substrings that contain one or more of these byte sequences consecutively will be replaced by the character(s) whose encoding would result in those consecutive bytes. The encoding scheme used to decode these characters may be specified, or if unspecified, the default encoding of the platform will be used. 
		* Therefore to decode (assiming encoded in Java with UTF-8 format, the + signs are converted back to ' ' space then using javascript's decodeURIComponent the rest of the characters are decoded
		*/
		//regular expresstion for matching any global occurance of + sign within the the string /\+/g
		encodedString = encodedString.replace(/\+/g, ' ');
		decodedString = decodeURIComponent(encodedString);
	} catch (Exception) {
		return "Failed to decode string.";
	}
	return decodedString;
}

function responseHandler(responseText) {
	
	xml = parseXMLFromString(decodeJavaUTF8String(responseText));
	
	//alert(xml);
	//alert((xml == null));
	if (xml == null) {
		alert("Null xml");
		document.getElementById("status").innerHTML = "Error parsing response.";
		return;
	} else {
		processServerResponse(xml);
	}
}
