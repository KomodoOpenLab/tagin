#!/usr/env/python

class Location():

    def getRoom(self):
        return self.room


    def getUnit(self):
        return self.unit


    def getFloor(self):
        return self.floor


    def getBuilding(self):
        return self.building


    def getStreet(self):
        return self.street


    def getCity(self):
        return self.city


    def getProvince(self):
        return self.province


    def getCountry(self):
        return self.country


    def getPostal(self):
        return self.postal


    def setRoom(self, value):
        self.room = value


    def setUnit(self, value):
        self.unit = value


    def setFloor(self, value):
        self.floor = value


    def setBuilding(self, value):
        self.building = value


    def setStreet(self, value):
        self.street = value


    def setCity(self, value):
        self.city = value


    def setProvince(self, value):
        self.province = value


    def setCountry(self, value):
        self.country = value


    def setPostal(self, value):
        self.postal = value
    
    
    def __init__(self, room, unit, floor, buildingName, street, city, province ,country, postal):
        self.room = room
        self.unit = unit
        self.floor = floor
        self.building = buildingName
        self.street = street
        self.city = city
        self.province = province
        self.country = country
        self.postal = postal        