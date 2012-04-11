from math import sqrt

def normalize2range(value, collection, rangeMax, rangeMin):
	valueMax = max(collection)
	valueMin = min(collection)
	if valueMax == valueMin:
		return 1.0
	
	return ((rangeMax - rangeMin) * (float(value - valueMin)/float(valueMax - valueMin))) + rangeMin
	
	
def fingerPrintStrengthToRank(fingerPrints):
	fpDict = dict(fingerPrints)
	fpStrengths = [float(x) for x in fpDict.values()]
	
	for mac, strength in fpDict.iteritems():
		fpDict[mac] = normalize2range(float(strength), fpStrengths, 1.0, 0.1)
	return fpDict

	
def getScanRank(scanData):
	
	allStrength = []
	for (mac, strength, channel, mode) in scanData:
		allStrength.append(float(strength))

	scanRankData = []		
	for (mac, strength, channel, mode) in scanData:
		s = normalize2range(float(strength), allStrength, 1.0, 0.1)
		scanRankData.append((mac, s))
	return dict(scanRankData)
	
	
def findDistanceBetweenFingerPrintAndScan(scan, fingerPrint):
	distance = 0
	for mac, scanRank in scan.iteritems():
		try:
			knownRank = fingerPrint[mac]
		except KeyError:
			knownRank = 0
		distance += (scanRank - knownRank)**2
	return sqrt(distance)


if __name__ == '__main__':
	collection = [1.0, 2.0, 1.0]
	print normalize2range(1.0, collection, 1.0, 0.1)
	
	