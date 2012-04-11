#!/usr/env/python

class ClientRequest():

    def getFingerPrints(self):
        return self.fingerPrints


    def getNumOfScans(self):
        return self.numOfScans


    def getRequestType(self):
        return self.requestType


    def getUser(self):
        return self.user


    def getKey(self):
        return self.key


    def getPostData(self):
        return self.postData


    def getInvalidDataFound(self):
        return self.invalidDataFound


    def setFingerPrints(self, value):
        self.fingerPrints = value


    def setNumOfScans(self, value):
        self.numOfScans = value


    def setRequestType(self, value):
        self.requestType = value


    def setUser(self, value):
        self.user = value


    def setKey(self, value):
        self.key = value


    def setPostData(self, value):
        self.postData = value


    def setInvalidDataFound(self, value):
        self.invalidDataFound = value
    
    def __init__(self, fingerPrints, numOfScans, requestType, requestVersion, user, key, postData, invalidDataFound):
        self.fingerPrints = fingerPrints
        self.numOfScans = numOfScans
        self.requestType = requestType
        self.requestVersion = requestVersion
        self.user = user
        self.key = key
        self.postData = postData
        self.invalidDataFound = invalidDataFound