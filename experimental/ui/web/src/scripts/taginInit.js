/**
 * tagin!, a WiFi-based point-of-interest (POI) service
 * for tagging outdoor and indoor locations
 * Copyright (c) 2009, University of Toronto
 * scyp@atrc.utoronto.ca
 *
 * Dual licensed under the MIT and GPL licenses.
 * http://scyp.atrc.utoronto.ca/projects/tagin/license
 * 
 * @author: Susahosh Rahman
***/

var tagin = tagin || {};
var taginUI = taginUI || {};
var jQuery = jQuery || {};
/*global taginUI, jQuery*/

(function (tagin, taginUI, $) {
	//as soon as web document is loaded...
	$(document).ready(function () {

		//Initialize UI
		taginUI.initUI();
		taginUI.adjustUI();
		tagin.loadTagin(taginUI.clientInterface);
		
		//Assign focus (for screen readers)
		$('#pageTitle').focus();

	});
}(tagin, taginUI, jQuery));