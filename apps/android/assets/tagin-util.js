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
/*
function addTag(event) {
	tag = document.getElementById("tagfield").value;
	if (tag == null || tag == "") {
		alert("Please enter a tag!");
		return;
	}
	wifiData = generateFingerPrintXML(latestAccessPoints);
	xmlRequest = '<?xml version="1.0"?>'+ DTD + '<client_message><message_type>post</message_type>';
	xmlRequest += '<tag>' + tag + '</tag>';
    	xmlRequest += wifiData + '</client_message>';

	communicate(xmlRequest);
}*/

var sortBy = "distance";
var isDebugMode = false;

function sortByDistance(a, b) {
    var x = parseFloat(a.distance);
    var y = parseFloat(b.distance);
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByFrequency(a, b) {
    var x = parseFloat(a.frequency);
    var y = parseFloat(b.frequency);
    return ((x < y) ? 1 : ((x > y) ? -1 : 0));
}


function processServerResponse(xml) {
	worldTagsStatus = document.getElementById("worldTagsStatus");
	docStatus = document.getElementById("status");
	docStatus.innerHTML = "";
	messageType = getElementValue(xml, "response_type");
	if (messageType == null) {
		docStatus.innerHTML = "Received invalid response.";
		return;
	} else if (messageType == 'all_visible_tags') {
		tags = getTags(xml);
		if (tags == null) {
			docStatus.innerHTML = "Error retriving tags from response.";
			return;
		}
		refreshTags(tags);
	} else if (messageType == 'current_location') {
		currentLocation = getCurrentLocation(xml);
		if (currentLocation == null) {
			docStatus.innerHTML = "Error retriving current location from response.";
			return;				
		}
		refreshLocation(currentLocation);
	} else if (messageType == 'message') {
		response = getElementValue(xml, "message");
		if (response == null) {
			docStatus.innerHTML = "Error retriving message from response.";
			return;
		}
		docStatus.innerHTML = response;
	} else if (messageType == 'error') {
	err = getElementValue(xml, "error");
		if (err == null) {
			docStatus.innerHTML = "Error retriving information from response.";
			return;				
		}
		docStatus.innerHTML = "Server error: " + err;
	} else {
		docStatus.innerHTML = "Invalid response from server.";
			
	}
}


function addSeperator(shouldAdd) {
	if (shouldAdd == null) {
		return null;
	}
	if (shouldAdd) {
		return ', '
	}
	return '';
}

this.refreshLocation = function (loc) {
	$('#locationStatus').empty();
	$('#currentLocation').empty();
	if (loc == null || loc == undefined || loc == '') {
		$('#locationStatus').append("<p>No location information available.</p>");
		displayMapButton(false);
		return;
	}
	
	var shouldAddSeperators = false;
//  Display available indoor location info
	indoorInfoHtml = "<p>"
    if (loc.room != undefined && loc.room != '') {
		shouldAddSeperators = true;
		indoorInfoHtml += "Room: " + loc.room;
		$('#room').val(loc.room);
	}
    if (loc.floor != undefined && loc.floor != '') {
		indoorInfoHtml += addSeperator(shouldAddSeperators);
		shouldAddSeperators = true;
		indoorInfoHtml += "Floor: " + loc.floor;
		$('#floor').val(loc.floor);
	}
    if (loc.building != undefined && loc.building != '') {
		indoorInfoHtml += addSeperator(shouldAddSeperators);
		shouldAddSeperators = false;
		indoorInfoHtml += "<a id=\"building-d\" target=\"_blank\" href=\"http://www.google.com/search?q=" + loc.building + "\">" + loc.building + "</a>";
		$('#building').val(loc.building);
	}
	indoorInfoHtml += "</p>"
    $('#currentLocation').append(indoorInfoHtml);

	shouldAddSeperators = false;
//  Display available address info
    query_maps = '';
    query_html = '';
    if (loc.street != undefined && loc.street != '') {
		shouldAddSeperators = true;
        query_html += "<span id=\"street-d\">" + loc.street + "</span>";
        query_maps += loc.street + "+";
		$('#street').val(loc.street);
    }
    if (loc.city != undefined && loc.city != '') {
		query_html += addSeperator(shouldAddSeperators);
		shouldAddSeperators = true;
        query_html += "<span id=\"city-d\">" + loc.city + "</span>";
        query_maps += loc.city + "+";
		$('#city').val(loc.city);
    }
    if (loc.province != undefined && loc.province != '') {
		query_html += addSeperator(shouldAddSeperators);
		shouldAddSeperators = true;
        query_html += "<span id=\"province-d\">" + loc.province + "</span>";
        query_maps += loc.province + "+";
		$('#state').val(loc.province);
    }
    if (loc.country != undefined && loc.country != '') {
		query_html += addSeperator(shouldAddSeperators);
		shouldAddSeperators = true;
        query_html += "<span id=\"country-d\">" + loc.country  + "</span>";
        query_maps += loc.country + "+";
		$('#country').val(loc.country);
    }
	if (loc.postal != undefined && loc.postal != '') {
		query_html += addSeperator(shouldAddSeperators);
		shouldAddSeperators = false;
		query_html += "<span id=\"postal-d\">" + loc.postal  + "</span>";
        query_maps += loc.postal;
		$('#postal').val(loc.postal);
	}
    $('#currentLocation').append("<p><a target=\"_blank\" href=\"http://maps.google.com/maps?q=" + query_maps + "\">" + query_html + "</a></p>");
    //displayMapButton(true);
}

this.refreshTags = function (tags) {
	if (tags == null) {
		return;
	}
	$('#worldTagsStatus').empty();
	$('#worldTags').empty();
	numOfTags = tags.length;
	if (numOfTags == 0) {
		$('#worldTagsStatus').append("<p>No tags found!</p>");
		return;
	}
	if (sortBy == 'count') {
		tags = tags.sort(sortByFrequency);
		maxFrequncy = tags[0].frequency;
		minFrequency = tags[numOfTags -1].frequency;
	} else {
		tags = tags.sort(sortByDistance);
		minDistance = tags[0].distance;
		maxDistance = tags[numOfTags - 1].distance;
	}
	
	//for loop for refreshing tags
	for (i=0; i < numOfTags; i++) {
		//alert(tags[i].value + ": " +tags[i].distance);
		//DO NOT, under any circumstances remove the space between the </a> and the </span> elements below. It is essential to ensure word wrapping in the tag cloud
		if (sortBy == 'count') {
			$('#worldTags').append("<span class=\"tagCloud-item\"><a id=\"tagid" + i + "\" target=\"_blank\" href=\"http://www.google.com/search?q=" + tags[i].value + "\">" + tags[i].value + "</a><span class=\"small\">["+parseInt(tags[i].frequency)+"]</span> </span>");
		}
		else {
			$('#worldTags').append("<span class=\"tagCloud-item\"><a id=\"tagid" + i + "\" target=\"_blank\" href=\"http://www.google.com/search?q=" + tags[i].value + "\">" + tags[i].value + "</a> </span>");
		}
		if (sortBy == 'distance') {
			if (maxDistance == minDistance) {
				fontsize = 2;
			}
			else {
				maxFontSize = 2.8;
				if (tags[i].distance == minDistance) {
					fontsize = maxFontSize;
				}
				else {
					;
					//since the tags array is sorted by distance, as we itterate down the list
					//and i increases the distance at the i-th element also increaes,
					//so from the maxFontSize we subtract the i-th element's distance to find
					//the font for i-th element
					fontsize = maxFontSize - (1.5 * tags[i].distance);
				}
				
			//fontsize = (((tags[i].distance - minDistance) / (maxDistance - minDistance)) * (2.8 - 0.8)) + 0.8
			}
		} else if (sortBy == 'count'){
			fontsize = (((tags[i].frequency - minFrequency) / (maxFrequncy - minFrequency)) * (2.8 - 0.8)) + 0.8
		} else {
			fontsize = 1;
		}
		
		$('#tagid' + i).css("font-size", fontsize + "em");
		
	} //end for (refreshing tags)
}

function createElementFor(elementName, aliasName) {
	if (elementName == null) {
		return "";
	}
	value = $('#' + elementName).val();
	if (value == "" || value == null || value == undefined) {
		return "";
	}
	if (aliasName == "" || aliasName == null || aliasName == undefined) {
		aliasName = elementName;
	}
	return "<" + aliasName + ">" + value + "</" + aliasName + ">"
}

this.getLocationElement = function() {
	ele = "";
	ele += createElementFor("room", "unit");
	ele += createElementFor("floor");
	ele += createElementFor("building", "building_name");
	ele += createElementFor("street");
	ele += createElementFor("city");
	ele += createElementFor("state", "province");
	ele += createElementFor("country");
	ele += createElementFor("postal")
	if (ele == "") {
		return ele;
	}
	return "<location>" + ele + "</location>";
	
}

this.getTagsElement = function(tagvals) {
	if (tagvals == null || tagvals == '' || tagvals == undefined) {
		return null;
	}
	tags = tagvals.split(/,/);
	tagsElement = '<tags>';
	for (i=0; i < tags.length; i++) {
		tagsElement += '<tag>' + tags[i].trim() + '</tag>';
	}
	tagsElement += '</tags>';
	return tagsElement;
}

this.displayMapButton = function (shouldDisplay) {
    if (shouldDisplay) {
        $('#mapAddress-button').css("visibility", "visible");
    } else {
        $('#mapAddress-button').css("visibility", "hidden");
    }
}


this.setDebugMode = function(isDebug) {
	if (isDebug) {
		isDebugMode = true;
		$('#debug-widget').css("visibility", "visible");
	} else {
		isDebugMode = false;
		$('#debug-widget').css("visibility", "hidden");
	}
}

this.setAtUnload = function() {
	$(window).unload(function () {
		 //stopGeoLocationWatch();
	});
}

this.setWritingMode = function(mode) {
	if (mode) {
		androidInterface.setWritingModeOn();
	} else {
		androidInterface.setWritingModeOff();		
	}
}

$(document).ready(function() {

	$('#tag-settings-widget').hide();
	setDebugMode(true);
	//refreshTags(null);
	getLocationElement();
	getTagsElement(null);
	displayMapButton(false);
	androidInterface.pageLoadedNotification();
	//wifiChanged();
	//wifi_service.startWatching(wifi_listener);
	//initGeoLocationWatch();
	setAtUnload();
	
	$('#test').click(function(event) {
		//communicate("hi");
		//wifiChanged();
		//refreshTags([]);
		//alert(getLocationElement());
		//$('#room').val("")
	});
	
	$(".fl-textfield").focus(function(event){
		setWritingMode(true);
	 });
	 $(".fl-textfield").blur(function(event){
		setWritingMode(false);
	 });

	
	$('#tagSettingButton').click(function(event) {
		event.preventDefault();
		if ($('#tag-settings-widget').is(':visible')) {
			$('#sortbyimg').attr("src", "skin/dropsidearrow.jpg");
			$('#tag-settings-widget').hide();
		} else {
			$('#sortbyimg').attr("src", "skin/dropdownarrow.jpg");
			$('#tag-settings-widget').show();
		}
	});
	
	$("input[name='sort_tag_by']:radio").click(function(){
	    if ($("input[@name='sort_tag_by']:checked").val() == 'distance') {
			sortBy = 'distance';
		}
		else if ($("input[@name='rdio']:checked").val() == 'count') {
			sortBy = 'count';		
		}
		androidInterface.requestRefreshedData();
	});

	
	$('#mapAddress-button').click(function(event){
		street = $('#street-d').html().replace(/,/, "");
		city = $('#city-d').html().replace(/,/, "");
		state = $('#province-d').html().replace(/,/, "");
		country = $('#country-d').html().replace(/,/, "");
		
		if (street != "" || city != "" || state != "" || country != "") {
			street = street.replace(/\s/, "+");
			city = city.replace(/\s/, "+");
			state = state.replace(/\s/, "+");
			country = country.replace(/\s/, "+");
			query = street + ",+" + city + ",+" + state + ",+" + country;
			window.open("http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q="+query);
		} else {
			alert("No address given for mapping");
		}
	});

	$('#addTag-button').click(function(event) {
            tags = $('#tagfield').val();
            if (tags == null || tags == "") {
                    alert("Nothing to add!");
                    return;
            }
		    xmlRequest = androidInterface.addTags(getTagsElement(tags));
			/*if (xmlRequest == null) {
				alert("Cannot add tag(s).");
				return;
			}*/
			$('#tagfield').val("");
            //communicate(xmlRequest);
            //alert("hehe");

            setTimeout ("androidInterface.requestRefreshedData()", 3000);
	} );
	
	$('#saveLoc-button').click(function(event) {
            roomi = $('#room').val();
            floori = $('#floor').val();
            buildingi = $('#building').val();
            streeti = $('#street').val();
            cityi = $('#city').val();
            statei = $('#state').val();
            countryi = $('#country').val();
            postali = $('#postal').val();
            if ((roomi == null || roomi == "") && (floori == null || floori == "") && (buildingi == null || buildingi == "") && (streeti == null || streeti == "") && (cityi == null || cityi == "") && (statei == null || statei == "") && (countryi == null || countryi == "") && (postali == null || postali == "")) {
                    alert("Nothing to save!");
                    return;
            }
		    androidInterface.addLocation(getLocationElement());
			/*if (xmlRequest == null) {
				alert("Cannot add location information.");
				return;
			}*/
			
            $('#room').val("");
            $('#floor').val("");
            $('#building').val("");
            $('#street').val("");
            $('#city').val("");
            $('#state').val("");
            $('#country').val("");
			$('#postal').val("");
			setTimeout ("androidInterface.requestRefreshedData()", 3000); 
	} );
	$('#clearTag-button').click(function(event) {
			$('#tagfield').val("");
	});
	$('#clearLoc-button').click(function(event) {
            $('#room').val("");
            $('#floor').val("");
            $('#building').val("");
            $('#street').val("");
            $('#city').val("");
            $('#state').val("");
            $('#country').val("");
			$('#postal').val("");
	}
	);
	
	$('#addlocationbutton').click(function() {
	    $.blockUI({ 
			message: $('#addtagwidget'), 
			showOverlay: true, 
			overlayCSS: {
				backgroundColor:'#000',
				opacity: '0.6',
				cursor: 'default'
				     }, 
			centerY: 0,
			centerX: 0,
			css: {
				//backgroundColor:'#000',
				top: '100px', left: '5px', right: '10px',
				width: '275px',
				cursor: 'default'
			    }
		  });
	$('.blockOverlay')
	});

	$('#closeaddbox').click(function() {
	    $.unblockUI();
	    return false;
	});

});

