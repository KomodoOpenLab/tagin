class Tag():
    
    def getValue(self):
        return self.value
    
    def getAvgDistance(self):
        return float(self.totalDistance/self.count)
    
    def getCount(self):
        return self.count
    
    def addCount(self):
        self.count += 1
        
    def getMinDistance(self):
        return self.minDistance
    
    def updateMinDistance(self, distance):
        if self.minDistance == None:
            self.minDistance = distance
        else:
            if self.minDistance < distance:
                self.minDistance = distance
    
    def addDistance(self, distance):
        self.updateMinDistance(distance)
        self.totalDistance += distance
    
    def setValue(self, value):
        self.value = value
    
    def __init__(self):
        self.count = 0.0
        self.totalDistance = 0.0
        self.minDistance = None