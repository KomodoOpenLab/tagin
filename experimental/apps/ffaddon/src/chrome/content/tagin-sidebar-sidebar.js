/**
 * tagin!, a WiFi-based point-of-interest (POI) service
 * for tagging outdoor and indoor locations
 * Copyright (c) 2009, University of Toronto
 * scyp@atrc.utoronto.ca
 *
 * Dual licensed under the MIT and GPL licenses.
 * http://scyp.atrc.utoronto.ca/projects/tagin/license
 * 
 * @author Jorge Silva
***/

//
// $Id: tagin-sidebar.js 209 2008-04-12 21:04:30Z ATRC $
//

var tagin = {
	init: function() {
		var pref = tagin.getPref();

                //Load URL into sidebar
		document.getElementById("browserTaginSidebar").setAttribute("src", pref.getCharPref("tagin-sidebar.url.sidebar"));
                //Change sidebar width to make sure content fits
                setSidebarWidth(285);
	},

	getPref: function() {
		return Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	}
};

//Sidebar width change function
function setSidebarWidth(newwidth) {
  window.top.document.getElementById("sidebar-box").width=newwidth;
}

// init
window.addEventListener("load", tagin.init, false);

