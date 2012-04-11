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

from databaseutil import DatabaseUtility
import configurations
import sys, datetime

class TagINDBInterface():

    def addUser(self,userid, name):
        timestamp = datetime.datetime.now()
        retVal = self.dbutil.insertIntoTableValues('Users', None, [(userid, False), (name,True), (timestamp, False)], '*')
        if retVal == -1 or retVal == None:
            return False
        return True
    
    def isUniqueValueInTable(self, table, col, value):
        query = "SELECT " + col + " FROM " + table + " WHERE " + col + " = '" + value + "'"
        data = self.dbutil.executeQuery(query)
        if data == None or data == []:
            return False
        else:
            if len(data) == 1:
                return True
            else:
                print "Warning: possible database problem detected."
                print "Table " + table + " columb " + col + " has multiple entires for the value : " + value
                return False
    
    def setUserAuthentication(self, userId, key):
        if self.isUniqueValueInTable("Users", "userId", userId):
            if self.isUniqueValueInTable("UserAuthentication", "userId", userId):
                query = "UPDATE UserAuthentication set authkey='"+key+"' where userId='"+userId+"'"
                self.dbutil.executeQuery(query)
            else:
                 retVal = self.dbutil.insertIntoTableValues('UserAuthentication', None, [(userId, False), (key,True)], '*')
                 if retVal == -1 or retVal == None:
                     return False
                 else:
                     return True
        else:
            return False
    
    def addTag(self,tag):
        retVal = self.dbutil.insertIntoTableValues('Tags', None, [(tag,True)], '*')
        if retVal == -1:
            return False
        else:
            return True
        
    def addTagPrint(self,tag, fingerPrint):
        retVal = self.dbutil.insertIntoTableValues('TagPrints', None, [(tag, True), (fingerPrint, False)], '*')
        if retVal == -1 or retVal == None:
            return False
        else:
            return True
        
    def getTagsForFingerprint(self, fingerPrint):
        query = "SELECT Tag FROM TagPrints where FingerPrint='" + str(fingerPrint) + "'"
        data = self.dbutil.executeQuery(query)
        if data == None or data == -1:
            return []
        else:
            return data
        
    def getLocationForFingerPrint(self, fingerPrint):
        query = "SELECT roomnumber, floor, building, street, city, province, country, postalcode FROM LocationSpec where FingerPrint='" + str(fingerPrint) + "'"
        data = self.dbutil.executeQuery(query)
        if data == None or data == -1:
            return []
        else:
            return data
        
    
    def addLocationTag(self,tag, fingerprint):
        if tag:
            if self.addTag(tag) == -1:
                print "Error occured while adding location tag!"
                return ''
            else:
                return tag
             #   if self.addTagPrint(tag, fingerprint):
             #       return tag
             #   else:
             #       print "Error occured while adding Tag Print!"
             #       return None
        else:
            if self.addTag('') == -1:
                print "Error occured while adding location tag!"
                return None
        return ''


    def isAuthenticatedUser(self, userId, key):
        if not userId or not key:
            return False
        query = "SELECT userId, authkey from UserAuthentication where userId = '" + userId + "'"
        authInfo = self.dbutil.executeQuery(query)
        if authInfo == None or authInfo == []:
            return False
        else:
            (knownUser, knownKey) = authInfo[0]
            if userId == knownUser and knownKey == key:
                return True
            else:
                return False
                       
        
    def addFingerprintOwnership(self, userid, fingerprintid):
        retVal = self.dbutil.insertIntoTableValues('Ownership', None, [(userid, False), (fingerprintid, False),(True, False)], '*')
        if retVal == -1 or retVal == None:
            return False
        else:
            return True
      
    def addLocationSpec(self, fingerprint, roomnumber, unit, floor, building, street, city, province, country, postal):
        if fingerprint == None:
            return False
        
        roomnumber = self.addLocationTag(roomnumber, fingerprint)
        #adding unit code goes here
        floor = self.addLocationTag(floor, fingerprint)
        building = self.addLocationTag(building, fingerprint)
        street = self.addLocationTag(street, fingerprint)
        city = self.addLocationTag(city, fingerprint)
        province = self.addLocationTag(province, fingerprint)
        country = self.addLocationTag(country, fingerprint)
        postal = self.addLocationTag(postal, fingerprint)
    
        #unit has to be added to database too: (unit, True)
        retVal = self.dbutil.insertIntoTableValues('LocationSpec', None, \
            [(fingerprint,False), (roomnumber, True), (floor, True), (building, True), (street, True), (city,True), (province, True), (country, True), (postal, True)], '*')
        if retVal == -1 or retVal == None :
            return False
        else:
            return True
        
    def addFingerPrint(self, fingerPrint, mac, strength):
        retVal = self.dbutil.insertIntoTableValues('FingerPrints', None, [(fingerPrint,False), (mac,False), (strength, False)], '*')
        if retVal == None or retVal == -1:
            return False
        else:
            return True
        
    def hasMacDetailsChanged(self, mac, channel, mode):
        query = "select channel, mode from MacDetails where (mac='"+mac+"' and timestamp=(select MAX(TIMESTAMP) from MacDetails where mac='"+mac+"'));"
        data = self.dbutil.executeQuery(query)
        if data:
            try:
                knownChannel, knownMode = data[0]
                if channel == str(knownChannel) and mode == str(knownMode):
                    return False
                else:
                    return True
            except:
                return True
        else:
            return True    
        
        
    
    def updateMacDetails(self, mac, channel, mode):
        if self.hasMacDetailsChanged(mac, channel, mode):
               timestamp = datetime.datetime.now()
               retVal = self.dbutil.insertIntoTableValues('MacDetails', None, [(mac, False), (channel, True), (mode, True),(timestamp, False)], '*')
               if retVal == -1:
                   return False
               else:
                   return True
        else:
            return True
            
        
        
      
    def updateMacListWith(self, mac):
        '''
        "Update" the list of know Mac addresses with the given mac address
        By "update" here it is meant that if the given mac address does not exist in the list:
        insert it, and return True, however, if it already exists the "update" is considered
        successful and the function still return True. The "update" fails when some sort of 
        error occures and False is returned.
        '''
        
        retVal = self.dbutil.insertIntoTableValues('Macs', None, [(mac, False)], '*')
        if retVal == -1:
            return False
        else:
            #return value is either None, indicating the mac already exists or 
            #a non-negative value, indicating the mac has been successfully inserted.
            #in either case the 'update' is considered successfull
            return True
    
        
        
    def createNewFingerPrint(self):
        timestamp = datetime.datetime.now()
        val = self.dbutil.insertIntoTableValues('fingerprintscollection', ['creationTime'], [(timestamp, False)], 'fingerprint')
        if val == -1 or val == None:
            return val
        return val[0]
    
    
    def registerFingerPrint(self, fingerPrint, userId, tags, locationInfo):
        if fingerPrint == None:
            print "Error: No data given for the Finger Print!"
            return "<response_type>error</response_type><error>No data given for the Finger Print!</error>"
        
        if tags == None and locationInfo == None:
            print "Error: No tag or location data given for the Finger Print!"
            return "<response_type>error</response_type><error>No tag or location data given!</error>"
        
        newFingerPrint = self.createNewFingerPrint()
        if newFingerPrint == None or newFingerPrint == -1:
            print "Error: Failed to create new Finger Print entry!"
            return "<response_type>error</response_type><error>Failed to create new Finger Print entry!</error>"
        print "New Finger print: " 
        print newFingerPrint
        
        isOwnershipAdded = self.addFingerprintOwnership(userId, newFingerPrint)
        
        hasUpdateErrorOccured = False
        hasAddErrorOccured = False
        for (mac, strength, channel, mode) in fingerPrint:
            if self.updateMacListWith(mac):
                if not self.updateMacDetails(mac, channel, mode):
                    hasUpdateErrorOccured = True
                
                if not self.addFingerPrint(newFingerPrint, mac, strength):
                    hasAddErrorOccured = True
            else:
                hasUpdateErrorOccured = True
                
        if tags:
            areAllTagsAdded = True
            areAllTagPrintsAdded = True
            for tag in tags:
                isTagAdded = self.addTag(tag)
                areAllTagsAdded = isTagAdded and areAllTagsAdded
                isTagPrintAdded = False
                if isTagAdded:
                    isTagPrintAdded = self.addTagPrint(tag, newFingerPrint)
                    areAllTagPrintsAdded = areAllTagPrintsAdded and isTagPrintAdded
        
        isLocSpecAdded = False
        if locationInfo:
            l = locationInfo
            isLocSpecAdded = self.addLocationSpec(newFingerPrint, l.getRoom(), l.getUnit(), l.getFloor(), \
                        l.getBuilding(), l.getStreet(), l.getCity(), l.getProvince(), l.getCountry(), l.getPostal())
            
        
        errString = ''
        if tags:
            
            if not isTagAdded:
                errString += 'Failed to add Tag. '
            if not isTagPrintAdded:
                errString += 'Failed to add relationship between Tag and Finger print. '
        if not isOwnershipAdded:
            errString += 'Failed to add ownership. '
            if hasAddErrorOccured:
                errString += 'Failed to finger print. Partial FingerPrint maybe have been registered. '
        if hasUpdateErrorOccured:
            errString += 'Failed to update Mac address. Partial FingerPrint maybe have been registered. '
        if locationInfo and (not isLocSpecAdded):
            errString += 'Failed to add location relationship. '
        if errString == '':
            if tags:
                return '<response_type>message</response_type><message>Successfully added Tags</message>'
            else:
                return '<response_type>message</response_type><message>Successfully added Location</message>'
        else:
            return '<response_type>error</response_type><error>Added Tags. However, one or more error occured during the process. Details: ' + errString + '</error>'
            

    def findAssociatedFingerPrints(self, mac):
        #query = "SELECT fp.FingerPrint \
        #        FROM FingerPrints as fp, Ownership as own \
        #        where fp.mac = (?) and own.userid= (?) \
        #        and fp.fingerPrint = own.fingerprint"
        query = "SELECT DISTINCT FingerPrint from FingerPrints where MAC = '" + mac + "'"
        fingerPrintsList = self.dbutil.executeQuery(query)
        if fingerPrintsList == None or fingerPrintsList == -1:
            return []
        return fingerPrintsList


    def findRelaventFingerPrints(self, scanData):
        fingerPrints = []
        for (mac, strength, channel, mode) in scanData:
            fingerPrints = fingerPrints + self.findAssociatedFingerPrints(mac)
        uniqueFingerPrints = set(fingerPrints)
        fingerPrints = []
        for fingerPrint in uniqueFingerPrints:
            fingerPrints.append(fingerPrint[0])
            
        return fingerPrints

    def getFingerPrintDetails(self, fingerPrint):
        query = "SELECT Mac, Strength FROM FingerPrints where FingerPrint = '" + str(fingerPrint) + "'"
        fingerPrintDetails = self.dbutil.executeQuery(query)
        if fingerPrintDetails == None or fingerPrintDetails == -1:
            return []
        else:
            return fingerPrintDetails
      
    def dropTable(self, tablename):
        print "Warning: DROPPING TABLE " + tablename
        query = "DROP TABLE " + tablename + " CASCADE"
        self.dbutil.executeQuery(query)
        print tablename + " table dropped."
    
    def dropAllTables(self):
        self.dropTable('macs')
        self.dropTable('macdetails')
        self.dropTable('tags')
        self.dropTable('tagCluster')
        self.dropTable('fingerPrintsCollection')
        self.dropTable('tagPrints')
        self.dropTable('fingerPrints')
        self.dropTable('users')
        self.dropTable('userAuthentication')
        self.dropTable('ownership')
        self.dropTable('locationSpec')
        
    def createTables(self):
        macTable = 'CREATE TABLE Macs(mac MACADDR PRIMARY KEY)'
        macDetailsTable = 'CREATE TABLE MacDetails(mac MACADDR, channel INTEGER, mode TEXT, timestamp TIMESTAMP, FOREIGN KEY (mac) REFERENCES Macs(mac) on update cascade)'
        tagsTable = 'CREATE TABLE Tags(tag TEXT PRIMARY KEY)'
        tagClusterTable = 'CREATE TABLE TagCluster(tag TEXT, relatedTag TEXT, FOREIGN KEY (tag) REFERENCES Tags(tag), FOREIGN KEY (relatedTag) REFERENCES Tags(tag))'
        fingerPrintsCollectionTable = 'CREATE TABLE FingerPrintsCollection(fingerPrint SERIAL PRIMARY KEY, creationTime TIMESTAMP)'
        tagPrintsTable = 'CREATE TABLE TagPrints(tag TEXT, fingerPrint INTEGER, FOREIGN KEY (tag) REFERENCES Tags(tag), FOREIGN KEY (fingerPrint) REFERENCES fingerPrintsCollection(fingerPrint) on update cascade)'
        fingerPrintsTable = 'CREATE TABLE FingerPrints(fingerPrint INTEGER, Mac MACADDR, Strength REAL, FOREIGN KEY (fingerPrint) REFERENCES FingerPrintsCollection(fingerPrint), FOREIGN KEY (mac) REFERENCES Macs(mac))'
        usersTable = 'CREATE TABLE Users(userId TEXT PRIMARY KEY, name TEXT, registrationDate TIMESTAMP)'
        userAuthentication = 'CREATE TABLE UserAuthentication(userId TEXT, authkey TEXT, UNIQUE (userId), FOREIGN KEY(userId) REFERENCES Users(userId))'
        ownershipTable = 'CREATE TABLE Ownership(userid TEXT, fingerprint INTEGER, activeOwnership BOOLEAN DEFAULT True, FOREIGN KEY(userid) REFERENCES Users(userid) on update cascade, FOREIGN KEY(fingerprint) REFERENCES FingerPrintsCollection(fingerPrint) on update cascade)'
        locationSpecTable = 'CREATE TABLE LocationSpec(fingerprint INTEGER, roomnumber TEXT, floor TEXT, building TEXT, street TEXT, city TEXT, province TEXT, country TEXT, postalcode TEXT, FOREIGN KEY(fingerprint) REFERENCES FingerPrintsCollection(fingerprint) on update cascade, FOREIGN KEY(roomnumber) REFERENCES Tags(tag),FOREIGN KEY(floor) REFERENCES Tags(tag),FOREIGN KEY(building) REFERENCES Tags(tag),FOREIGN KEY(street) REFERENCES Tags(tag),FOREIGN KEY(city) REFERENCES Tags(tag),FOREIGN KEY(province) REFERENCES Tags(tag),FOREIGN KEY(country) REFERENCES Tags(tag), FOREIGN KEY (postalcode) REFERENCES Tags(tag))'
        
        self.dbutil.executeQuery(macTable)
        self.dbutil.executeQuery(macDetailsTable)
        self.dbutil.executeQuery(tagsTable)
        self.dbutil.executeQuery(tagClusterTable)
        self.dbutil.executeQuery(fingerPrintsCollectionTable)
        self.dbutil.executeQuery(tagPrintsTable)
        self.dbutil.executeQuery(fingerPrintsTable)
        self.dbutil.executeQuery(usersTable)
        self.dbutil.executeQuery(userAuthentication)
        self.dbutil.executeQuery(ownershipTable)
        self.dbutil.executeQuery(locationSpecTable)
        
    def configureDB(self, autoConfig):
        if not autoConfig:
            db = raw_input("Please enter DB name:")
            user =  raw_input("Please enter DB user:")
            usrpass = raw_input("Please enter DB password:")
            configurations.username = user
            configurations.password = usrpass
            configurations.db = db
        print "Checking database connection..."
        con = None
        con = self.dbutil.connectToDatabase()
        if con == None:
            print "Existing program"
            sys.exit()
        else:
            con.close()
            print "DB configuration and connection: OK"
            print "Connection closed"
        
    def __init__(self):
        self.dbutil = DatabaseUtility()

if __name__ == '__main__':
    
    t =TagINDBInterface()
    t.configureDB(False)
    t.dropAllTables()
    t.createTables()
    print "Adding the public user to database:"
    print t.addUser("public"," ")
    print t.setUserAuthentication("public","public")
    
    #print t.addTagPrint("IDL", "1")
    #print t.addLocationSpec(1, "123", "5","Fake building", "123 Fake st", "Fake", "Fake", "cake")
    
    #print t.dbutil.executeQuery("SELECT * FROM FingerPrints")
    
    #t.addTag("Here")
    #t.addTag("There")
    #t.addTag("Everywhere")
    #t.addTagPrint("Here", 1)
    #t.addTagPrint("There", 1)
    #t.addTagPrint("Everywhere", 1)
    #print t.getTagsForFingerprint(1)
    
    #t.dropAllTables()
    #t.createTables()
    #t.dbutil.insertIntoTableValues('FingerPrints', ['1', 'abc', '-87'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['8', 'abc', '-22'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['9', 'abc', '-37'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['5', 'abc', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['5', 'add', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['5', 'alk', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['34', 'dfc', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['3', 'avf', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['3', 'ttc', '-79'])
    #t.dbutil.insertIntoTableValues('FingerPrints', ['33', 'ttc', '-79'])
    #print t.dbutil.executeQuery('select * from FingerPrints')
    #l = []
    #for i in ('abc', 'ttc'):
    #    l = l + t.findAssociatedFingerPrints(i)
    #s = set(l)
    #l = []
    #for i in s:
    #    l.append(i[0])
        
    #print l
#    print t.addUser("susahosh", "susahosh r")
#    print t.setUserAuthentication("susahosh", "pass")
#    print "isAuthenticated User:"
#    print t.isAuthenticatedUser("susahosh", "pass")

#    fp = [("ab:cd:31", "-30", "3", "master"),
#    ("ad:33:32", "-33", "6", "ad-hoc"),
#    ("ab:cd:33", "-66", "2", "master"),
#    ("sd:33:cd", "-77", "5", "master")]
#    tag = "fake tag1"
#    loc = (None, "2", None, "Kingston Rd", "Toronto", "Ontario", "Canada")
#    t.addUser("susahosh", "susahosh r")
#    t.registerFingerPrint(fp, "susahosh", tag, loc)
#    print "Finger print collections:"
#    print t.dbutil.executeQuery("select * from FingerPrintsCollection")
#    print "Users"
#    print t.dbutil.executeQuery("select * from Users")    
#    print "Ownership"    
#    print t.dbutil.executeQuery("select * from Ownership")
#    print "Macs"
#    print t.dbutil.executeQuery("select * from Macs")
#    print "Mac details"
#    print t.dbutil.executeQuery("select * from MacDetails")
#    print "Finger Prints"
#    print t.dbutil.executeQuery("select * from FingerPrints")
#    print "Tags:"
#    print t.dbutil.executeQuery("select * from Tags")
#    print "Tag Prints"
#    print t.dbutil.executeQuery("select * from TagPrints")
#    print "Location Spec"
#    print t.dbutil.executeQuery("select * from LocationSpec")

    

    #print executeQuery("select * from Users")
    #print executeQuery("select * from Ownership")
    #addFingerprintOwnership('bla', 'blublue')
    #print addTagPrint("My First Tag", 5)
    #print executeQuery("select * from TagPrints")
    #print executeQuery("select * from FingerPrintsCollection")
    #print executeQuery("select * from Users")
    #print executeQuery("select * from Ownership")
   #addUser("susahosh", "susahosh")
    #newFingerPrintId = createNewFingerPrint()
    #addFingerprintOwnership("susahosh", newFingerPrintId)
    #print updateMacListWith('a123')
    #print executeQuery("select * from Users")
# print executeQuery("select * from FingerPrintsCollection")
