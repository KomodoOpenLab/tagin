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

var wiFiChanges = 0;
function setNoWiFiData() {
	document.getElementById("status").innerHTML = "";
	document.getElementById("status").innerHTML = "No WiFi data available!";
}

function wifiChanged(){
	androidInterface.requestRefreshedData();
}

String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
}

function testAndroidConnection() {
	alert(androidInterface.getMyObject());
	//alert("Hi I was invoked by Android");
}
