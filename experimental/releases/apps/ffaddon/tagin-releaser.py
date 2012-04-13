#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import httplib, urllib, sys
import time
#import json


instructions =	"tagin-releaser: Simple firefox add-on release creation script.\n" + \
				"\tChecks out specified source, removes unnecessary files,\n" + \
				"\tmakes version changes in installation files and recursively\n" + \
				"\tcompresses all required files into a xpi archive.\n\n" + \
				"\tUsage:  ./tagin-releaser.py [api version] [ui version] [minified? (0=No/1=Yes)] [target version]\n"

def grabLicense(filePath):
	filebuff = open(filePath, 'r')
	contents = filebuff.read()
	filebuff.close()
	return contents[contents.find('/**') : contents.find('**/') + 3]

def grabHeader(filePath):
	filebuff = open(filePath, 'r')
	contents = filebuff.read()
	filebuff.close()
	return contents[0 : contents.find('(function')]

def grabFunction(filePath):
	filebuff = open(filePath, 'r')
	contents = filebuff.read()
	filebuff.close()
	return contents[contents.find('(function') : len(contents)]

def append2file(filePath, contents):
	filebuff = open(filePath, 'a')
	filebuff.write(contents)
	filebuff.close()

def CleanRecursive(dirpath):
    dir_list = os.listdir(dirpath)
    for item in dir_list:
        item = dirpath + "/" + item
        if os.path.isdir(item):
            cmd = "rm -rf " + item + "/.svn"
            os.system(cmd)
            print cmd
            CleanRecursive(item)
        else:
            if item[len(item) - 1] == "~":
                cmd = "rm " + item
                os.system(cmd)
                print cmd

# Notice the __name__=="__main__"
# this is used to control what Python does when it is called from the
# command line.
if __name__=="__main__":
	if len(sys.argv) == 5:
		
		apiver = sys.argv[1]
		uiver = sys.argv[2]
		minifiedBool = int(sys.argv[3])
		relVersion = sys.argv[4]
		
		tempFolder = 'taginrel_temp'
		releaseFileName = "tagin-" + relVersion + ".xpi"
		apiFileName = 'tagin-' + apiver + '.js'
		contentpath = tempFolder + '/chrome/content/'
		scriptsPath = contentpath + 'ui/scripts/'
		apifilepath = scriptsPath + apiFileName
		uifilepath = scriptsPath + 'taginUI.js'
		wififilepath = scriptsPath + 'firefoxWifiInterface.js'
		initfilepath = scriptsPath + 'taginInit.js'
		allfilepath = scriptsPath + 'taginAll.js'
		
		if os.path.isdir(tempFolder):
			os.system('rm -rf ' + tempFolder)

		if not minifiedBool:
			print 'Will not attempt to optimize source...'

		# Define commands to grab source for addon, UI, API, and custom scripts
		getaddon = 'svn co https://svn.atrc.utoronto.ca/repos/bul/scyp/tagin/apps/ffaddon/src ' + tempFolder
		getui = 'svn co https://svn.atrc.utoronto.ca/repos/bul/scyp/tagin/releases/ui/web/' + uiver + ' ' + contentpath + 'ui'
		getapi = 'svn co https://svn.atrc.utoronto.ca/repos/bul/scyp/tagin/releases/api/js/' + apiver + '/src' + ' ' + contentpath + 'api'
		getFF = 'svn co https://svn.atrc.utoronto.ca/repos/bul/scyp/tagin/apps/ffaddon/scripts' + ' ' + contentpath + 'firefox'
		
		# Get all source and arrange in a single scripts folder
		os.system(getaddon)
		os.system(getui)
		os.system(getapi)
		os.system(getFF)
		
		os.system('mv ' + contentpath + 'api/' + apiFileName + ' ' + scriptsPath)
		os.system('mv ' + contentpath + 'firefox/firefoxWifiInterface.js ' + scriptsPath)
		os.system('mv ' + contentpath + 'firefox/taginInit.js ' + scriptsPath)
		os.system('rm -rf ' + contentpath + 'api')
		os.system('rm -rf ' + contentpath + 'firefox')
		
		print 'Creating single javascript source...'
		# Grab and write header
		append2file(allfilepath, grabHeader(initfilepath))
		# Grab and write wifi code
		append2file(allfilepath, grabFunction(wififilepath))
		# Grab and write api code
		append2file(allfilepath, grabFunction(apifilepath))
		# Grab and write UI code
		append2file(allfilepath, grabFunction(uifilepath))
		# Write init code
		append2file(allfilepath, grabFunction(initfilepath))
		

		if minifiedBool:
			print 'Optimizing javascript code...'
			# Read source file
			filebuff = open(allfilepath, 'r')
			contents = filebuff.read()
			filebuff.close()
			# Define the parameters for the POST request and encode them in
			# a URL-safe format.
			params = urllib.urlencode([
				('js_code', contents),
				('output_format', 'text'),
				('output_info', 'compiled_code'),
			])
			# Always use the following value for the Content-type header.
			headers = { "Content-type": "application/x-www-form-urlencoded" }
			conn = httplib.HTTPConnection('closure-compiler.appspot.com')
			
			minified = ''
			i = 0
			while (len(minified) == 0) and (i < 3):
				i = i + 1;
				print 'attempt', i, 'of 3...'
				conn.request('POST', '/compile', params, headers)
				response = conn.getresponse()
				minified = response.read()
				conn.close
				minified = minified.replace('\n', '')
				time.sleep(1)
			if (len(minified) == 0) or (minified.find('Error(') != -1): #optimization failed
				print 'Optimization failed!', 'closure-compiler.appspot.com says:', minified
				print 'Using non-optimized source...'
			else:
				# Re-write source file
				filebuff = open(allfilepath, 'w')
				filebuff.close()
				append2file(allfilepath, grabLicense(initfilepath) + '\n')
				append2file(allfilepath, minified)
		
		print 'Deleting unused javascript files...'
		os.system('rm ' + initfilepath)
		os.system('rm ' + wififilepath)
		os.system('rm ' + apifilepath)
		os.system('rm ' + uifilepath)
		
		# Clean source
		print 'Deleting svn and backup files...'
		CleanRecursive(tempFolder)
		
		print 'Updating tagin.html file...'
		# Modify html
		filebuff = open(contentpath + 'ui/tagin.html', 'r')
		contents = filebuff.read()
		filebuff.close()
		str2replace = contents[contents.find("	<!-- tagin code -->") : contents.find("<!-- tagin code ends -->") + 24]
		strreplacement = "\t<!-- tagin code -->\n\t<script type=\"text/javascript\" src=\"scripts/taginAll.js\"></script>\n\t<!-- tagin code ends -->"
		contents = contents.replace(str2replace, strreplacement)
		filebuff = open(contentpath + 'ui/tagin.html', 'w')
		filebuff.write(contents)
		filebuff.close()
		
		print 'Updating install.rdf file...'
		os.chdir(tempFolder)
		filebuff = open('install.rdf', 'r')
		contents = filebuff.read()
		filebuff.close()
		str2replace = contents[contents.find("<em:version>") : contents.find("</em:version>") + 13]
		strreplacement = "<em:version>" + relVersion + "</em:version>"
		contents = contents.replace(str2replace, strreplacement)
		filebuff = open('install.rdf', 'w')
		filebuff.write(contents)
		filebuff.close()
		
		print 'Updating tagin-sidebar.properties file...'
		filebuff = open('chrome/locale/en-US/tagin-sidebar.properties', 'w')
		contents = "version = " + relVersion + "\n"
		filebuff.write(contents)
		filebuff.close()
		
		print 'Creating archive...'
		if os.path.isdir(releaseFileName):
			os.system('rm -rf ' + releaseFileName)
			sys.exit(0)
		os.system("zip -r " + releaseFileName + " chrome chrome.manifest install.rdf")
		os.system("mv " + releaseFileName + " ../")
		
		print 'Deleting temp folder...'
		os.chdir("../")
		os.system('rm -rf ' + tempFolder)
	
		print 'done!'
	else:
		print instructions

