#!/usr/bin/python

'''
tagin!, a WiFi-based point-of-interest (POI) service
for tagging outdoor and indoor locations
Copyright (c) 2009, University of Toronto
scyp@atrc.utoronto.ca

Dual licensed under the MIT and GPL licenses.
http://scyp.atrc.utoronto.ca/projects/tagin/license

@author Susahosh Rahman, Jorge Silva
@contributor Jamon Camisso, Yura Zenevich
@email scyp@atrc.utoronto.ca
'''

# Server program

import os, sys, time, datetime
from signal import SIGTERM
import zlib
from networks import TCPServer, UDPServer, HTTPServer
from tagindbinterface import TagINDBInterface
import configurations as config

''' Set the socket parameters '''

##first find the server's ip address, this assumes a public facing ip
##if using NAT, ensure that router/firewall has a rule to pass traffic through
host = os.popen("/sbin/ifconfig eth0|grep inet |awk '{print $2}' |sed -e 's/addr://g'").readline().strip()

def daemonize(stdout='/dev/null', stderr=None, stdin='/dev/null', pidfile=None, startmsg = 'started with pid %s' ):
	'''
		This forks the current process into a daemon.
		The stdin, stdout, and stderr arguments are file names that
		will be opened and be used to replace the standard file descriptors
		in sys.stdin, sys.stdout, and sys.stderr.
		These arguments are optional and default to /dev/null.
		Note that stderr is opened unbuffered, so
		if it shares a file with stdout then interleaved output
		may not appear in the order that you expect.
	'''

	# Do first fork.
	try:
		pid = os.fork()
		if pid > 0: sys.exit(0) # Exit first parent.
	except OSError, e:
		sys.stderr.write("fork #1 failed: (%d) %s\n" % (e.errno, e.strerror))
		sys.exit(1)

	# Decouple from parent environment.
	os.chdir("/")
	os.umask(0)
	os.setsid()
	## Do second fork.
	try:
		pid = os.fork()
		if pid > 0: sys.exit(0) # Exit second parent.
	except OSError, e:
		sys.stderr.write("fork #2 failed: (%d) %s\n" % (e.errno, e.strerror))
		sys.exit(1)

	# Open file descriptors and print start message
	if not stderr: stderr = stdout
	si = file(stdin, 'r')
	so = file(stdout, 'a+')
	se = file(stderr, 'a+', 0)
	pid = str(os.getpid())
	sys.stdout.write("%s\n" % startmsg % pid)
	sys.stdout.flush()
	if pidfile: file(pidfile,'w+').write("%s\n" % pid)

	# Redirect standard file descriptors.
	os.dup2(si.fileno(), sys.stdin.fileno())
	os.dup2(so.fileno(), sys.stdout.fileno())
	os.dup2(se.fileno(), sys.stderr.fileno())


def startstop(stdout='/dev/null', stderr=None, stdin='/dev/null', pidfile=config.pidfile, startmsg = 'started with pid %s' ):
	if len(sys.argv) > 1:
		action = sys.argv[1]
		try:
			pf  = file(pidfile,'r')
			pid = int(pf.read().strip())
			pf.close()
		except IOError:
			pid = None
	
		if 'stop' == action or 'restart' == action:
			if not pid:
				mess = "Could not stop, pid file '%s' missing.\n"
				sys.stderr.write(mess % pidfile)
			else:
				try:
					while True: # keep sending the SIGTERM signal until it throws an exception
						os.kill(pid,SIGTERM) #Need to close sockets too, instead of just killing the process
						time.sleep(1) # why do we wait 1 sec?
				except OSError, err: # if os.kill() failed...
					err = str(err)
					if err.find("No such process") > 0: # and if process does not exist anymore...
						os.remove(pidfile)	# clean up
						if os.path.isfile(stdout): # and rename log
							now = datetime.datetime.now()
							os.rename(stdout, stdout + '.' + now.strftime("%Y%m%d%H%M%S") + str(now.microsecond))
					else:
						print str(err)
						sys.exit(1)

		if 'stop' == action:
			sys.exit(0)

		if 'restart' == action:
			action = 'start'
			pid = None

		if 'start' == action:
			if pid:
				mess = "Start aborted since pid file '%s' exists.\n"
				sys.stderr.write(mess % pidfile)
				sys.exit(1)
			daemonize(stdout,stderr,stdin,pidfile,startmsg)
			return
	print "usage: %s start|stop|restart" % sys.argv[0]
	sys.exit(2)

if __name__ == "__main__":
    db = TagINDBInterface()
    db.configureDB(True)
    startstop(stdout=config.logfile, pidfile=config.pidfile)
    theTCPServer = TCPServer()
    theTCPServer.start()
    theTCPServer = TCPServer(sslSupport=False)
    theTCPServer.start()    
    theUDPServer = UDPServer()
    theUDPServer.start()
    theHTTPServer = HTTPServer()
    theHTTPServer.start()
    #run the process forever
    while (True):
        time.sleep(1000)
	##don't forget to clean up after ourselves!
	#UDPSock_server.close()
