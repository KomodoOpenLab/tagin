/**
 * tagin!, a WiFi-based point-of-interest (POI) service
 * for tagging outdoor and indoor locations
 * Copyright (c) 2009, University of Toronto
 * scyp@atrc.utoronto.ca
 *
 * Dual licensed under the MIT and GPL licenses.
 * http://scyp.atrc.utoronto.ca/projects/tagin/license
 * 
 * @author: Jorge Silva, Susahosh Rahman
***/

var tagin = tagin || {};
var jQuery = jQuery || {};
var taginUI = taginUI || {};
/*global jQuery, taginUI, tagin*/

(function ($, taginUI, tagin) {

	var resizeTimer = null;

	var getFrameWidth = function (object) {
		if (object.outerWidth && object.width) { //if supported
			return object.outerWidth(true) - object.width();
		} else {
			return 0;
		}
	};
	
	var sortByDistance =  function (a, b) {
	    var x = parseFloat(a.distance);
	    var y = parseFloat(b.distance);
	    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
	};
	
	var sortByFrequency = function (a, b) {
	    var x = parseFloat(a.frequency);
	    var y = parseFloat(b.frequency);
	    return ((x < y) ? 1 : ((x > y) ? -1 : 0));
	};


	taginUI.focusOn = function(object) {
		object.focus();
	}
	taginUI.linkHandler = function (object) {
		var ostr = $(object).text();
		ostr = ostr.replace(/[\n|\t]/g, "");
		if (taginUI.sortBy === 'count') {
			ostr = ostr.substring(0, ostr.length - 4);
		}
		window.open('http://www.google.com/search?q=' + ostr);
		//alert("Function linkHandler called with:\n\n" + url);
	};

	taginUI.mapHandler = function (object) {
		var ostr = $(object).text();
		ostr = ostr.replace(/[\n|\t]/g, "");
		window.open('http://maps.google.com/maps?q=' + ostr.replace(/\s/g, '+'));
	};

	taginUI.sortByHandler = function (order) {
		//alert("Function sortByHandler called with:\n\n" + order);
		taginUI.sortBy = order;
		tagin.requestAllVisibleTags();
	};

	taginUI.saveAddressHandler = function () {
		var room = $('#room').val();
		var floor = $('#floor').val();
		var building = $('#building').val();
		var street = $('#street').val();
		var city = $('#city').val();
		var province = $('#province').val();
		var country = $('#country').val();
		var postal = $('#postal').val();
		tagin.addLocation(new tagin.dataManager.Location(room, floor, building, street, city, province, country, postal));
		setTimeout(tagin.requestCurrentLocation, 2000);
	};

	taginUI.addTagsHandler = function () {
		var tags = $('#tags-field').val();
		if (!tags) {
			alert("The tags field is empty!\nPlease add your tags separated by commas in the field provided.", 'title', '');
			$('#tags-field').focus();
			return;
		}
		tagin.addTags(tags);
		$('#tags-field').val('');
		setTimeout(tagin.requestAllVisibleTags, 2000);
	};
	
	taginUI.configHandler = function () {
		$('#config-dialog').dialog('open');
	};

	taginUI.editLocHandler = function () {
		$('#room').val($('#room_d').text());
		$('#floor').val($('#floor_d').text());
		$('#building').val($('#building_d').text());
		$('#street').val($('#street_d').text());
		$('#city').val($('#city_d').text());
		$('#province').val($('#province_d').text());
		$('#country').val($('#country_d').text());
		$('#postal').val($('#postal_d').text());
		$('#editLoc-dialog').dialog('open');
	};

	taginUI.adjustUI = function () {

		taginUI.ActiveWidth = Math.round(0.94 * Math.min(Math.max(document.documentElement.clientWidth, window.innerWidth), 800));

		//width logic
		$('#tags-field').width(10);
		$('#all-content').width(taginUI.ActiveWidth);
		$('#currentLocation').width($('#location-content').width() - $('#locationButtons').outerWidth(true) - getFrameWidth($('#currentLocation')) - 8);
		$('#tags-field').width((Math.round($('#addTags-section').width()) * 0.98) - $('#add-button').outerWidth(true) - getFrameWidth($('#tags-field')));
		$('#status-message').width($('#status-bar').width() - $('#cfg-button').outerWidth(true) - getFrameWidth($('#status-message')) - 10);
		if (taginUI.ActiveWidth <= 400) {
			$('#editLoc-dialog').dialog('option', 'width', Math.floor(0.9 * taginUI.ActiveWidth));
			$('#config-dialog').dialog('option', 'width', Math.floor(0.9 * taginUI.ActiveWidth));
		}

		//height logic
		$('#status-message').css('margin', '0');
		$('#status-bar').height(Math.max($('#status-message').outerHeight(true),$('#cfg-button').outerHeight(true)));
		var statusMargin = Math.floor( ($('#status-bar').height() - $('#status-message').outerHeight(true)) / 2.0) + 'px';
		$('#status-message').css('margin-bottom', statusMargin);
		$('#status-message').css('margin-top', statusMargin);
		$('#location-content').css('min-height', Math.max($('#locationButtons').outerHeight(true), $('#currentLocation').outerHeight(true)) + 'px');
		$('#addTags-section').height($('.square-button img').height());
		$('#tags-field').css('font-size', Math.round(0.5 * $('.square-button img').height()) + 'px');
		$('#tags-field').css('margin-top', Math.floor(($('.square-button img').height() - $('#tags-field').outerHeight()) / 2.0) + 'px');
		$('#tags-field').css('margin-bottom', Math.floor(($('.square-button img').height() - $('#tags-field').outerHeight()) / 2.0) + 'px');
	};
	
	var clearStatus = function () {
		$('#status-message').html('Done');
	};
	var displayMapButton = function (shouldDisplay) {
	    if (shouldDisplay) {
	        $('#mapAddress-button').show();
	    } else {
	        $('#mapAddress-button').hide();
	    }
		taginUI.adjustUI();
	};
	var addSeperator = function (shouldAdd) {
		if (shouldAdd) {
			return ', ';
		}
		return '';
	};
	
	var refreshLocation = function (location) {
		$('#indoor_d').html('');
		$('#address_d').html('');
		$('#locationButtons').show();
		displayMapButton(false);
		taginUI.adjustUI();
		if (!location) {
			$('#indoor_d').html('<p class="status">This location has not been added. <a href="javascript:taginUI.editLocHandler();"> Save this location!</a></p>');
			return;
		}

		var shouldAddSeperators = false;
		var indoorInfoHtml = '<p tabindex="0">';
	    if (location.room) {
			shouldAddSeperators = true;
			indoorInfoHtml += 'Room: <span id="room_d" class="loc-item">' + location.room + '</span>';
		}
	    if (location.floor) {
			indoorInfoHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = true;
			indoorInfoHtml += 'Floor: <span id="floor_d" class="loc-item">' + location.floor + '</span>';
		}
	    if (location.building) {
			indoorInfoHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = false;
			indoorInfoHtml += '<a href="javascript:taginUI.linkHandler($(\'#building_d\'));" ><span id="building_d" class="loc-item">' + location.building + '</span>';
		}
		indoorInfoHtml += "</p>";
	    $('#indoor_d').append(indoorInfoHtml);
		
		var outDoorHtml = '';
		shouldAddSeperators = false;

	    if (location.street) {
			shouldAddSeperators = true;
	        outDoorHtml += '<span id="street_d" class="loc-item">' + location.street + '</span>';
	    }
	    if (location.city) {
			outDoorHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = true;
	        outDoorHtml += '<span id="city_d" class="loc-item">' + location.city + '</span>';
	    }
	    if (location.province) {
			outDoorHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = true;
	        outDoorHtml += '<span id="province_d" class="loc-item">' + location.province + '</span>';
	    }
	    if (location.country) {
			outDoorHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = true;
	        outDoorHtml += '<span id="country_d" class="loc-item">' + location.country + '</span>';
	    }
	    if (location.postal) {
			outDoorHtml += addSeperator(shouldAddSeperators);
			shouldAddSeperators = false;
	        outDoorHtml += '<span id="postal_d" class="loc-item">' + location.postal + '</span>';
	    }		
		if (outDoorHtml) {
			displayMapButton(true);			
		}
		$('#address_d').append("<p><a href=\"javascript:taginUI.mapHandler($('#address_d'));\">" + outDoorHtml + "</a></p>");
		taginUI.adjustUI();
	};
	var refreshTags = function (tags) {
		if (!tags) {
			return;
		}
		$('#addTags-section').show();
		$('#cfg-button').show();
		taginUI.adjustUI();
		$('#tagList').html('');
        var numOfTags = tags.length;
		if (numOfTags === 0) {
			$('#tagList').append('<p class="status">No tags found. <a href="javascript:taginUI.focusOn($(\'#tags-field\'));"> Be the first to tag this place!</a></p>');
			return;
		}
		//sort the tags...
		if (taginUI.sortBy === 'count') {
			tags = tags.sort(sortByFrequency);
		} else {
			tags = tags.sort(sortByDistance);
		}
		//display the tags..
		for (var i = 0; i < numOfTags; i++) {
			if (taginUI.sortBy === 'count') {
				$('#tagList').append('<li class="tagList-item"><a id="tagid' + i + '" class="tagList-button ui-state-default ui-corner-all" href="javascript:taginUI.linkHandler($(\'#tagid' + i + '\'));">' + tags[i].value + '<span class="small"> [' + parseInt(tags[i].frequency, 10) + ']</span></a></li>');
			} else {
				$('#tagList').append('<li class="tagList-item"><a id="tagid' + i + '" class="tagList-button ui-state-default ui-corner-all" href="javascript:taginUI.linkHandler($(\'#tagid' + i + '\'));">' + tags[i].value + '</a></li>');
			}
		}
	};
	
	/**tagin! Client Interface*/
	taginUI.clientInterface = function () {};
	taginUI.clientInterface.responseHandler = function (response) {
		$('#status-message').html("Processing response...");
		var messageType = response.getResponseType();
		switch (messageType) {
			case 'all_visible_tags':
				refreshTags(response.getResponseData());
				clearStatus();
				break;
			case 'current_location':
				refreshLocation(response.getResponseData());
				clearStatus();
				break;
			case 'message':
				tagin.clientInterface.updateStatus(response.getResponseData());
				setTimeout(clearStatus,  10000);
				break;
			case 'error':
				tagin.clientInterface.updateStatus(response.getResponseData());
				$('#status-message').focus();
				break;
			default:
				tagin.clientInterface.updateStatus("Received invalid response.");
				$('#status-message').focus();
		}
	};
		
	taginUI.clientInterface.errorHandler = function (error) {
		$('#status-message').html(error);
		if ('No WiFi data available.' === error) {
			$('#status-message').html('Unable to tag location, no WiFi data available');
			$('#status-message').focus();
			$('#addTags-section').hide();
			$('#cfg-button').hide();
			$('#locationButtons').hide();
			taginUI.adjustUI();
		}
	};
		
	taginUI.clientInterface.updateStatus = function (ustatus) {
		$('#status-message').html(ustatus);
		taginUI.adjustUI();
	};


	taginUI.initUI = function () {
		
		//Hide stuff
		displayMapButton(false);

		// SetUp Tabs
		$('#editLoc-dialog').tabs();

		// SetUp Dialogs
		$('#config-dialog').dialog({
			modal: true,
			autoOpen: false,
			title: 'Configuration',
			buttons: {
				"Cancel": function () {
					$(this).dialog('close');
				},
				"Ok": function () {
					taginUI.sortByHandler($('#tagsort-form input:radio:checked').val());
					$(this).dialog('close');
				}
			},
			open: function (event, ui) {
				if (taginUI.sortBy === 'distance') {
					$('#dradio').focus();
				}
				else {
					$('#cradio').focus();
				}
			},
			beforeclose: function (event, ui) {
				if (taginUI.sortBy === 'distance') {
					$('#dradio').attr('checked', 'checked');
				}
				else {
					$('#cradio').attr('checked', 'checked');
				}
			}
		});
		$('#editLoc-dialog').dialog({
			modal: true,
			autoOpen: false,
			title: 'Edit Location',
			buttons: {
				"Cancel": function () {
					$(this).dialog('close');
				},
				"Save": function () {
					$(this).dialog('close');
					taginUI.saveAddressHandler();
				}
			}
		});

		//Assign variables
		$('#dradio').attr('checked', 'checked');
		taginUI.sortBy = $('#tagsort-form input:radio:checked').val();

	};

	$(window).resize(function () {
		if (resizeTimer) {
			clearTimeout(resizeTimer);
		}
		resizeTimer = setTimeout(taginUI.adjustUI, 250);
	});

}(jQuery, taginUI, tagin));
