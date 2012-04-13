#!/usr/env/python

'''
WIPS, a WiFi-Based Indoor Positioning System
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
'''

import xml.dom.minidom as DOM
from lxml import etree
import re
from clientrequest import ClientRequest
from location import Location
macAddressPatter =  re.compile('^([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}$')

def generateElement(name, value):
	if (value == None):
		return ''
	return '<' + name + '>' +  str(value) + '</'+name+'>'

def generateResponse(responseType, bodyXMLParameters):
	xml = '<?xml version="1.0"?><response>'
	xml += '<response_type>' + responseType + '</response_type>'
	for parm in bodyXMLParameters:
		xml += parm
	xml += '</response>'
	return xml


def generateLocationXML(room, floor, building, street, city, province, country, postal):
	xml = ''
	xml +=	generateElement('room', room)
	xml +=	generateElement('floor', floor)
	xml +=	generateElement('building', building)
	xml +=	generateElement('street', street)
	xml +=	generateElement('city', city)
	xml +=	generateElement('province', province)
	xml +=	generateElement('country', country)
	xml +=  generateElement('postal', postal)
	return '<location>' + xml + '</location>'


def generateTagsXML(tagList):
	'''
		Given a dictionary of tagWeights puts eatch tag into a tag element, 
		along with corrosponding weight
	'''
	xml = '<tags>'
	for tag, info in tagList.iteritems():
		print tag, str(info.getCount()), str(info.getAvgDistance())
		xml += '<tag>' + \
		              '<value>' + str(tag) + '</value>' + \
		              '<distance>' + str(info.getAvgDistance()) + '</distance>' + \
		              '<frequency>' + str(info.getCount()) + '</frequency>' + \
		        '</tag>'
		        
	xml += '</tags>'
	return xml

def generateTagWeightXML(tagWeight):
	'''
		Given a dictionary of tagWeights puts eatch tag into a tag element, 
		along with corrosponding weight
	'''

	xml = '<tags>'
	for tag, weight in tagWeight.iteritems():
		print tag, str(weight)
		
		
		xml += "<tag>" + \
                    "<value>" + str(tag) + "</value>" + \
                    "<weight>" + str(weight) + "</weight>" + \
			   "</tag>"

	
	xml += "</tags>"
	#for test purpose only: 
	#'<?xml version="1.0"?><tags><tag><value>idl</value><weight>1</weight></tag><tag><value>343</value><weight>5</weight></tag></tags>'
	return xml
	


def extractElementData(xml, elementName):
	try:
		ele = xml.getElementsByTagName(elementName)
		data = ele.item(0).firstChild.data
		return data
	except:
		return None

def extractLocationInfo(xml, requestVersion):
	try:
		loc = xml.getElementsByTagName('location')
		if len(loc) == 1:
			floor = extractElementData(xml, 'floor')
			buildingName = extractElementData(xml, 'building_name')
			street = extractElementData(xml, 'street')
			city  = extractElementData(xml, 'city')
			province = extractElementData(xml, 'province')
			country = extractElementData(xml, 'country')
			postal = extractElementData(xml, 'postal')
			if requestVersion:
				room = extractElementData(xml, 'room')
				unit = extractElementData(xml, 'unit')
				if room or unit or floor or buildingName or street or city or province or country or postal:
					return Location(room, unit, floor, buildingName, street, city, province ,country, postal)
				else:
					return None
			else:
				room = extractElementData(xml, 'unit')				
				if room or floor or buildingName or street or city or province or country or postal:
					return Location(room, None, floor, buildingName, street, city, province ,country, postal)
				else:
					return None
		else:
			return None
	except:
		return None
	
def extractTags(xml):
	try:
		 tagElements = xml.getElementsByTagName('tag')
		 tags = []
		 for tag in tagElements:
		 	tags.append(tag.firstChild.data)
		 return tags
	except:
		return None
	
def extractFirstAttribute(xml, elementName):
	try:
		elementList = xml.getElementsByTagName(elementName)
		(attribute, value) = modUserEle.item(0).attributes.items()
		return (attribute, value)
	except:
		return None
	
def getPostRequestAction(xml, postRequestType):
	try:
		attr = extractFirstAttribute(xml, postRequestType)
		if attr == None:
			return None
		else:
			(attribute, value) = attr
		if attribute == 'action' and value == 'add':
			return 'add'
		elif attribute == 'action' and value == 'modify':
			return 'modify'
		else:
			return None
	except:
		return None
	
	
def isInvalidStrength(strength):
	#if str(strength) == '0':
	#	print "Invalid signal strength: 0"
	#	return True
	#else:
	try:
		float(strength)
	except:
		print "Invalid signal strength:"
		print strength
		return True
	return False
	
def isInvalidMac(mac):
	if macAddressPatter.match(mac) == None:
		return True
	if str(mac) == '0000000000000' or str(mac) == '000000000000':
		return True
	else:
		return False
	

def ParseXML(xml):

	xmlData = []
	WifiIter = int
	userId = None
	key = None
	version = None
	postData = None
	invalidDataFound = False

	if xml:
		xml = xml.replace('\n', '')
		xml = DOM.parseString(xml)
		
		try:
			versionElement = xml.getElementsByTagName('version')
			version = versionElement.item(0).firstChild.data
			print "Request version: ", version
		except:
			print "Unknown request version..assuming old request"
			pass
		
		
		type = xml.getElementsByTagName('message_type')
		dataType = type.item(0).firstChild.data
		
	
		user = 'public'
		key = 'public'
		auth = xml.getElementsByTagName('authentication')
		if len(auth) == 1:
			authElement = auth.item(0)
			user = authElement.childNodes[0].firstChild.data
			key = authElement.childNodes[1].firstChild.data
			
		if dataType == 'add_tags':
			
			tags = extractTags(xml)
			if tags:
				postData = (tags)
			else:
				return None
		if dataType == 'add_location':
			locationInfo = extractLocationInfo(xml, version)
			if locationInfo:
				postData = (locationInfo)
			else:
				return None
		
		if dataType == 'add_location' or dataType == 'add_tags' or dataType == 'request_all_visible_tags' or dataType == 'request_current_location':
			WifiIter = len(xml.getElementsByTagName('scan'))
			aps = xml.getElementsByTagName('ap')
			if aps == None or len(aps) == 0:
				return None
			
			for ap in aps:
				mac = ap.childNodes[0].firstChild.data
				invalidDataFound = invalidDataFound or isInvalidMac(mac)
				#ssid = ap.chilNodes[0].firstChild.data
				strength = ap.childNodes[1].firstChild.data
				invalidDataFound = invalidDataFound or isInvalidStrength(strength)
				channel = ap.childNodes[2].firstChild.data
				mode = ap.childNodes[3].firstChild.data
				xmlData.append((mac, strength, channel, mode))
				if invalidDataFound:
					break;
		else:
			return None

	else:
		print 'Failed to load xml for validation and parsing, returning null'
		return None

	return ClientRequest(xmlData, WifiIter, dataType, version, user, key, postData, invalidDataFound)
