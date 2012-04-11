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
 **/

//
// $Id: tagin-sidebar.js 213 2008-04-29 00:00:31Z ATRC $
//
var tagin = {
	DEFAULT_KEY:				"g",
	DEFAULT_MODS:				"alt shift",
	DEFAULT_MODS_MAC:			"alt shift",
	DEFAULT_URL_MAIN_WINDOW:	"chrome://tagin-sidebar/content/ui/tagin.html",
	DEFAULT_URL_SIDEBAR:		"chrome://tagin-sidebar/content/ui/tagin.html",

	init: function() {
		tagin.firstStart();
		tagin.fixStyles();
		tagin.fixKey();
	},

	fixStyles: function() {
		// Apply platform-specific stylesheet
		var platform = tagin.getPlatform();
		var sheets = document.styleSheets;
		var useMac = (platform == "mac" && tagin.getAppVersion().indexOf("2.") != 0);

		for (var i = 0; i < sheets.length; ++i) {
			if (sheets[i].href == "chrome://tagin-sidebar/skin/tagin-sidebar.css")
				sheets[i].disabled = useMac;
			else if (sheets[i].href == "chrome://tagin-sidebar/skin/tagin-sidebar-mac.css")
				sheets[i].disabled = !useMac;
		}
	},

	firstStart: function() {
		var bundle = document.getElementById("bundleTaginSidebar");
		var pref = tagin.getPref();
		var thisVer = bundle.getString("version");
		var firstTime = false;

		try {
			var installedVer = pref.getCharPref("tagin-sidebar.version");
			if (installedVer != thisVer) {
				pref.setCharPref("tagin-sidebar.version", thisVer);
				firstTime = true;
			}
		}
		catch (e) {
			pref.setCharPref("tagin-sidebar.version", thisVer);
			firstTime = true;
		}

		if (firstTime) {
			// Set pref defaults
			pref.setCharPref("tagin-sidebar.key", tagin.DEFAULT_KEY);

			pref.setCharPref("tagin-sidebar.modifiers",
				tagin.getPlatform() == "mac" ? tagin.DEFAULT_MODS_MAC : tagin.DEFAULT_MODS);

			pref.setCharPref("tagin-sidebar.url.mainWindow", tagin.DEFAULT_URL_MAIN_WINDOW);

			pref.setCharPref("tagin-sidebar.url.sidebar", tagin.DEFAULT_URL_SIDEBAR);

			// Add buttons
			var toolbox = document.getElementById("navigator-toolbox");
			var toolboxDocument = toolbox.ownerDocument;
	
			var hasButton = false;

			for (var i = 0; i < toolbox.childNodes.length; ++i) {
				var toolbar = toolbox.childNodes[i];
				if (toolbar.localName == "toolbar" && toolbar.getAttribute("customizable") == "true") {	
					if (toolbar.currentSet.indexOf("buttonTaginSidebar") > -1) hasButton = true;
				}
			}

			if (!hasButton) {
				for (var i = 0; i < toolbox.childNodes.length; ++i) {
					toolbar = toolbox.childNodes[i];
					if (toolbar.localName == "toolbar" && toolbar.getAttribute("customizable") == "true" && toolbar.id == "nav-bar") {
						var newSet = [];
						var child = toolbar.firstChild;
						while (child) {
							if (!hasButton && (child.id == "buttonTaginSidebar" || child.id == "urlbar-container")) {
								newSet.push("buttonTaginSidebar");
								hasButton = true;
							}

							newSet.push(child.id);
							child = child.nextSibling;
						}

						newSet = newSet.join(",");
						toolbar.currentSet = newSet;

						toolbar.setAttribute("currentset", newSet);
						toolboxDocument.persist(toolbar.id, "currentset");
						BrowserToolboxCustomizeDone(true);
						break;
					}
				}
			}

			// Open Instructions Url
			var mainWindow = window.QueryInterface(Components.interfaces.nsIInterfaceRequestor).getInterface(Components.interfaces.nsIWebNavigation).QueryInterface(Components.interfaces.nsIDocShellTreeItem).rootTreeItem.QueryInterface(Components.interfaces.nsIInterfaceRequestor).getInterface(Components.interfaces.nsIDOMWindow);
			
			mainWindow.gBrowser.selectedTab = mainWindow.gBrowser.addTab("http://scyp.atrc.utoronto.ca/pub/pages/ffthanks.html");

		}
	},

	getPlatform: function() {
		var platform = new String(navigator.platform);
		var str = "unix";

		if (!platform.search(/^Mac/))
			str = "mac";
		else if (!platform.search(/^Win/))
			str = "win";

		return str;
	},

	getAppVersion: function() {
		var pref = tagin.getPref();
		var num = "";

		try {
			num = pref.getCharPref("general.useragent.vendorSub");
		}
		catch (e) {}

		try {
			if (num.length == 0) {
				var str = pref.getCharPref("general.useragent.extra.firefox");
				var pos = str.indexOf("/");

				num = pos > -1 ? str.substring(pos + 1) : str;
			}
		}
		catch (e) {}

		return num;
	},

	getExtVersion: function() {
		return document.getElementById("bundleTaginSidebar").getString("version");
	},

	getPref: function() {
		return Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	},

	fixKey: function() {
		var pref = tagin.getPref();

		try {
			var keyElement = document.getElementById("keyTaginSidebar");

			var key = pref.getCharPref("tagin-sidebar.key");
			var modifiers = pref.getCharPref("tagin-sidebar.modifiers");

			if (key && key.length && modifiers && modifiers.length) {
				keyElement.setAttribute("key", key);
				keyElement.setAttribute("modifiers", modifiers);
			}
			else {
				// Remove accelerator if either is not specified
				document.getElementById("menuTaginSidebar").removeAttribute("key");
				keyElement.parentNode.removeChild(keyElement);
			}
		}
		catch (e) {
			// Silent fail
		}
	},

	click: function(event) {
		var pref = tagin.getPref();
		var URL = pref.getCharPref("tagin-sidebar.url.mainWindow");

		if (event.button == 1) {
			if (top.content.document.location == 'about:blank')
				top.content.document.location = URL;
			else {
				var browser = getBrowser();
				browser.selectedTab = browser.addTab(URL);
			}
			return true;
		}

		return false;
	},

	toggle: function() {
		toggleSidebar("viewTaginSidebar");
	}
};

// init
window.addEventListener("load", tagin.init, false);

