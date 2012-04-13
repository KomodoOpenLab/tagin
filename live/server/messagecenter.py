#!/usr/bin/python
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

import sys, traceback
import time, zlib
from xmlProcessor import *
from tag import Tag
from tagindbinterface import TagINDBInterface
from taginoperationsutil import *
#import pickle
#from WifiUtilities import *
#from WifiFinder import *
#sys.path.append('./DatabaseHandlers')
#from WipsDB import *
#from Search import *
#from Scan import *

def parserClientsRequest(data):
	xml = data
	#print xml
	try:
		clientRequest = ParseXML(str(xml))
		#time_xml = time.time()
		#time_xml_parsed = time_xml - time_start
		print 'Received and parsed xml' # in %s seconds' %(time_xml_parsed)
		return clientRequest
	except:
		print "Couldn't parse XML from client" 
		traceback.print_exc(file=sys.stdout)
		
		print "Attempting decompression..." 
		try:
			xml = zlib.decompress(data)
			try:
				clientRequest = ParseXML(str(xml))
				print 'Successfully received and parsed xml' # in %s seconds' %(time_xml_parsed)
				return clientRequest
			except:
				print "Couldn't parse XML from client. Giving up!" 
				return None
		except:
			print "Decompress failed"
			#time_xml = time.time()
			#time_xml_parsed = time_xml - time_start
	return None


def processClientRequestXML(clientRequestXML):
	clientRequest = parserClientsRequest(clientRequestXML)
	if (clientRequest == None):
		return generateResponse("error", "<error>Unable to parse the WiFi data!</error>")
	else:
		#(fingerPrints, numOfScans, messageType, user, key, postData, invalidDataFound) = theData
		if clientRequest.getInvalidDataFound():
			return generateResponse("error", "<error>Invalid data given.</error>")
		else:
			return processClientRequest(clientRequest)

def processClientRequest(clientRequest):
	#fingerPrint, numOfScans, messageType, user, key, postData
	if clientRequest.getRequestType() == 'request_all_visible_tags':
		return generateResponse('all_visible_tags', [findAnyTagsCloseToHere(clientRequest.getFingerPrints())])
	elif clientRequest.getRequestType() == 'request_current_location':
		return generateResponse('current_location', [findCurrentLocation(clientRequest.getFingerPrints())])
	elif clientRequest.getRequestType() == 'add_tags':
		return addNewFingerPrintClient(clientRequest.getFingerPrints(), clientRequest.getUser(), clientRequest.getKey(), clientRequest.getPostData(), None)
	elif clientRequest.getRequestType() == 'add_location':
		return addNewFingerPrintClient(clientRequest.getFingerPrints(), clientRequest.getUser(), clientRequest.getKey(), None, clientRequest.getPostData())
	else:
	   return generateResponse('error', '<error>Invalid request type given!!!</error>')


def extractInfoAboutScanFor(scanData, typeOfInfo, params):
	tagINDb = TagINDBInterface()
	scan = getScanRank(scanData)
	fingerPrints = tagINDb.findRelaventFingerPrints(scanData)
	fpDistances = {}
	if typeOfInfo == 'tags' or typeOfInfo == 'all':
		tagsList = {}
	if typeOfInfo == 'location' or typeOfInfo == 'all':
		fpLocation = {}
		
	for fId in fingerPrints:
		fingerPrint = fingerPrintStrengthToRank(tagINDb.getFingerPrintDetails(fId))
		fpDistances[fId] = findDistanceBetweenFingerPrintAndScan(scan, fingerPrint)
		if typeOfInfo == 'location' or typeOfInfo == 'all':
			fpLocation[fId] = tagINDb.getLocationForFingerPrint(fId)
			
		if typeOfInfo == 'tags' or typeOfInfo == 'all':
			currentTags = tagINDb.getTagsForFingerprint(fId)
			for currentTag in currentTags:
				currentTag = currentTag[0]
				if tagsList.has_key(currentTag):
					t = tagsList[currentTag]
				else:
					t = Tag()
					t.setValue(currentTag)
					tagsList[currentTag] = t
				t.addCount()
				t.addDistance(fpDistances[fId])
				
				
	#normalize the distances
#	allDistances = fpDistances.values()
#	for fId, distance in fpDistances.iteritems():
#		fpDistances[fId] = normalize2range(distance, allDistances, 1.0, 0.1)

	print "Distances:", fpDistances
		
	if typeOfInfo == 'tags':
		return (tagsList)
	elif typeOfInfo == 'location':
		return (fingerPrints, fpDistances, fpLocation)
	

def generateNeighbourLocation(knownLocation, minFId, fingerPrints, fpLocation):
	if knownLocation == None:
		room = ''
		floor = ''
		building = ''
		street = ''
		city = ''
		province = ''
		country = ''
		postal = ''
	else:
		(room, floor, building, street, city, province, country, postal) = knownLocation
		
	for fId in fingerPrints:
		if minFId != fId:
			location = fpLocation[fId]
			if location != []:
				(croom, cfloor, cbuilding, cstreet, ccity, cprovince, ccountry, cpostal) = location[0]
				if room == '' or room == None:
					room = croom
				if floor == '' or floor  == None:
					floor = cfloor
				if building == '' or building  == None:
					building = cbuilding
				if street == '' or street  == None:
					street = cstreet
				if city == '' or city  == None:
					city = ccity
				if province == '' or province  == None:
					province = cprovince
				if country == '' or country  == None:
					country = ccountry
				if postal == '' or postal  == None:
					postal = cpostal
	return (room, floor, building, street, city, province, country, postal)

def sortfingerPrintsbyDistance(fingerPrints,fpDistances):
    fingerPrints.sort(reverse=True) # sort from newest to oldest
    # sort from closest to furthest
    fpSorted = fingerPrints[:]
    for i in range(len(fingerPrints)):
        for j in range(len(fingerPrints) - i - 1):
            k = j + i + 1
            #print i,k
            if fpDistances[fingerPrints[i]] > fpDistances[fingerPrints[k]]:
                fpSorted[k] = fingerPrints[i]
                fpSorted[i] = fingerPrints[k]
                fingerPrints = fpSorted[:]
    return fpSorted


def findCurrentLocation(scanData):
	#first we extract information about the scan i.e known fingerprints, the distace for each fingerprint WRT scan and location information
	(fingerPrints, fpDistances, fpLocation) = extractInfoAboutScanFor(scanData, 'location', None)
	#in the event no known fingerprints match scan data, an empty list is returned
	if len(fingerPrints) == 0:
		print 'No known fingerprints found...'
		return generateLocationXML(None, None, None, None, None, None, None, None)
	print 'Locations:', fpLocation
	# sort by distance and index (where the greater index represents a more recent fingerprint)
	fingerPrints = sortfingerPrintsbyDistance(fingerPrints,fpDistances)
	print 'Order:', fingerPrints
	# minDistanceFId is the first element in the sorted fingerPrints list
	minDistanceFId = fingerPrints[0]	
	minDistance = fpDistances[minDistanceFId]
	print 'minDistance:', minDistance
	#pickle.dump([fingerPrints, fpDistances, fpLocation], open('/home/tagin/data.pkl', 'w')) # just for debugging
	minDisLocInfo = fpLocation[minDistanceFId]
	if minDisLocInfo == []:
		(room, floor, building, street, city, province, country, postal) = \
		              generateNeighbourLocation(None, minDistanceFId, fingerPrints, fpLocation)
	else:
		minDisLocInfo = minDisLocInfo[0] # Why is this needed? What is it doing?
		(room, floor, building, street, city, province, country, postal) = minDisLocInfo # I don't think this line is needed, doesn't the next line overwrites these variables?
		(room, floor, building, street, city, province, country, postal) = \
		              generateNeighbourLocation(minDisLocInfo, minDistanceFId, fingerPrints, fpLocation)
	print 'Location:', room, floor, building, street, city, province, country, postal
	return generateLocationXML(room, floor, building, street, city, province, country, postal)

		
def	findAnyTagsCloseToHere(scanData):
	(tagsList) = extractInfoAboutScanFor(scanData, 'tags', None)
	
	
#	tagCounts = {}
#	for tag in tags:
#		tagCounts[tag[0]] = (tagCounts[tag[0]] if tagCounts.has_key(tag[0]) else 0) + 1
#	print "Tag counts.."
#	print tagCounts
	
	
#	tagFreqRanks = {}
#	allTagsFrequency = tagCounts.values()
#	for tag, count in tagCounts.iteritems():
#		tagFreqRanks[tag] = normalize2range(count, allTagsFrequency, 1.0, 0.1)
#	
#	tagWeights = {}
#	for fId in fingerPrints:
#		tagDistRank = fpDistances[fId]
#		for tag in fpTags[fId]:
#			tagFreqRank = tagFreqRanks[tag[0]]
#			#tagWeights[tag[0]] = tagFreqRank * tagDistRank
#			tagWeights[tag[0]] = (0.25 * tagFreqRank) + (0.75 * tagDistRank)
	
	
	return generateTagsXML(tagsList)
	
def AuthenticationList():
	f = open('/usr/local/wips/AuthenticationList', 'r')
	keyList = f.readlines()
	f.close()
	for i in range(len(keyList)):
		keyItem = keyList[i].strip('\n')
		keyList[i] = keyItem[0:keyItem.find(' ')]
	return keyList
	
def addNewFingerPrintClient(fingerPrint, userId, key, tags, locationInfo):
	tagINDb = TagINDBInterface()
	
	#************************************
	#for testing purposes only:
	#tagINDb.dropAllTables()
	#tagINDb.createTables()
	#print "Adding the public user to database:"
	#print tagINDb.addUser("public"," ")
	#print tagINDb.setUserAuthentication("public","public")
	#print "Adding test data:"
	#print "Adding user:"
	#print tagINDb.addUser(userId,"susahosh r")
	#print "Adding authentication:"
	#print tagINDb.setUserAuthentication(userId,key)
	#************************************
	
	if tagINDb.isAuthenticatedUser(userId, key):
		return '<?xml version="1.0"?><response>' + tagINDb.registerFingerPrint(fingerPrint, userId, tags, locationInfo) + '</response>'
	else:
		return generateResponse('error', '<error>Authentication failed</error>')
	
	#print key
	#print AuthenticationList()	
	#if key is None:
#		return '<error>No key given</error>'
	#elif key not in AuthenticationList():
	#	return '<error>Authentication failed</error>'
	
	

	


def prepareResponseForClient(macs, strengths, channels, modes):
	time_request = time.time()
	strengths = GetAbsStrengths(strengths)
	try:
		scan = GetFingerprint(macs, strengths, channels, modes)
	except:
		print "Couldn't GetFingerprint()"
		return	"<error>Couldn't get finger print!</error>"
	try:
		print '\n\n\n'
		print scan
		print '\n\n\n'
		requestData = WifiFind(scan)
	except: 
		print "WifiFind Failed"
		return "<error>WifiFind Failed!</error>"
	point_both = requestData[0]
	point_me = requestData[1]
	point_jorge = requestData[2]
	buildingName = requestData[3]
	floorNumber = requestData[4]
	r1 = requestData[5]
	r2 = requestData[6]
	rcomb = requestData[7]
	time_request_generated = time.time()

	db = WipsDB()
	matchingBuilding = db.FindBuildingCodeNamePairs(buildingName)[0]
	path = db.ReturnPaths(db.ReturnBuildingFloorId(db.ReturnBuildingIdsFromCodeNamePairs(matchingBuilding), floorNumber))[0]
	try:
		xml_answer = GenerateRequestAnswer(buildingName, floorNumber, path, point_both, rcomb, None)
		return xml_answer
	except e:
		print e
		return "<error>Failed to generate response!</error>"
