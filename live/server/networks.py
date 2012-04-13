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
##############################################################################
TCPServer:
The TCP Server for handling multiple TCP request from client(s) that uses seperate threads
to deal with each client.
Requires: client sends two pieces of information:
1. the length of the data that would be sent to server for processing
2. the data itself 

UDPServer:
The UDP Server for handling multiple UDP request from client(s) that uses seperate threads
to deal with each client.

@author: Susahosh Rahman
##############################################################################
'''
import socket, time, threading
import sys, traceback
import ssl
from datetime import datetime
from wsgiref.simple_server import make_server
from cgi import parse_qs, escape
from messagecenter import *
import configurations as config

##############################################################################
class UDPServer(threading.Thread):
    
    class ClientProcessorThread(threading.Thread):
        
        def __init__(self, data, ip_client):
            self.data = data
            self.ip_client = ip_client
            threading.Thread.__init__(self)
            
        def run(self):
            print "UDP server recived client request..."
            self.startTime = datetime.now();
            print "The client's address is:"
            print self.ip_client
            print "Client's request:"
            print self.data

            responseData = processClientRequestXML(self.data)
            print responseData
           #responseData = "hi"

            self.endTime = datetime.now();

            print "Processed client's request in", self.endTime - self.startTime, 'seconds'

            
            #Open a new socket for talking to client, response to client with approprate message
            self.socketForWrttingtoClient = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            self.socketForWrttingtoClient.sendto(responseData , self.ip_client)
            time.sleep(1)
            #close the socket for talking to client
            self.socketForWrttingtoClient.close()
            
        
    
    def __init__(self, host = "0.0.0.0", serverPort =  config.udpPort):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.buf = 262144
        self.addr_server = (host,serverPort)
        self.socket.bind(self.addr_server)
        self.serverRunning = True
        threading.Thread.__init__(self)
        
    def stopServer(self):
        self.serverRunning = False
        
    def run(self):
        print "\nUDP Server running..."
        while self.serverRunning:
            
            data,clientAddr = self.socket.recvfrom(self.buf)
            self.ClientProcessorThread(data,clientAddr).start()
            #print "\nUDP Server waiting for client message..."
        self.socket.close()


class TCPServer(threading.Thread):

    
    class ClientProcessorThread (threading.Thread):
        
        def __init__(self, connection, sslSupport):
            self.connection = connection
            self.sslSupport = sslSupport
            threading.Thread.__init__(self)

        def receiveData(self, clientSock, dataSize):
            data = ''
            dataReceivedSize = 0
            #receive a "chunk" of size 4K of data at once
            while (dataReceivedSize < dataSize):
                if self.sslSupport:
                    quantum = clientSock.read(4096)
                else:
                    quantum = clientSock.recv(4096)
                print "Size of packet received:", len(quantum)
                data += quantum
                dataReceivedSize += len(quantum)
                print "dataSize:", dataSize, "dataRecivedSize", dataReceivedSize

            print "Done data receiving"
            return data
            
        def responseToClient(self, clientSock, data):
            if self.sslSupport:
                clientSock.write(data)
            else:
                clientSock.sendall(data)

        def isNumber(self, n):
            try:
                int(n)
                return True
            except ValueError:
                return False
            
        def getMessageSize(self, clientSock):
            size = ''
            while True:
                if self.sslSupport:
                    c = clientSock.read(1)
                else:
                    c = clientSock.recv(1)
                if self.isNumber(c):
                    size += c
                else:
                    break
            return size        

        def run(self):
            clientSocket, clientAddr = self.connection           
            #declear variables here so they are visible through out the function
            clientSocketWrapper = None
            clientSSLSocket = None
            
            if self.sslSupport:
                print "Secure connection request from client"
                print clientAddr

                try:
                    clientSSLSocket = ssl.wrap_socket(clientSocket, server_side=True, 
                                                      certfile="/home/tagin/tagin-server/cert.pem",keyfile="/home/tagin/tagin-server/key.pem", 
                                                      ssl_version=ssl.PROTOCOL_SSLv23)
                except:
                    print "Unable to establish secure connection due to error:"
                    traceback.print_exc(file=sys.stdout)

                    print "Attempting to inform client of the error over TCP (non-secure) connection"
                    #Open a new socket for talking to client, response to client with error message
                    try:
                        clientSocket.recv(10240) #read the nuffer to clear it (incase there is something)
                        clientSocket.sendall("<error>Unable to establish secure connection, due to SSL connection error.</error>")
                    except:
                        print "Failed to inform client of the error!"
                    print "aborting connection!"
                    clientSocket.close()
                    return
                    
                clientSocketWrapper = clientSSLSocket
            else:
                print "TCP Connection request from client"
                print clientAddr
                clientSocketWrapper = clientSocket

            try:
                msgSize = int(self.getMessageSize(clientSocketWrapper))
                print "Receiving message from client", clientAddr, "Total message size: ", msgSize
                msg = self.receiveData(clientSocketWrapper, msgSize)
                print "Total size received: %s", `len(msg)`
                print msg

                #test response data:          
                #responseData = "<location><building><name>John P. Robarts Library Building</name><streetnumber></streetnumber><streetaddress></streetaddress><city>Toronto</city><region>Ontario</region><country>Canada</country></building><floor><floornumber>1st</floornumber><floorplan>http://scyp.atrc.utoronto.ca/images/wifi/006/006-1st.png</floorplan></floor><coordinates><floorx>267</floorx><floory>348</floory><flooraccuracy>205</flooraccuracy></coordinates></location>"

                #code for creating proper response data:
                
                responseData = processClientRequestXML(msg)
                print "Response body: ", responseData
                    #                responseData = msg
                self.responseToClient(clientSocketWrapper, responseData)
            except ValueError:
                print "Did not receive expected size of message!"
                time.sleep(1)
                self.responseToClient(clientSocketWrapper, "Err: Invalid message length! The first message sent from client should indicate the length of the message that is to follow!")
                print "Aborting operation."
            
            print "Done processing client request"
            if self.sslSupport:
                clientSSLSocket.close()
            clientSocket.close()
                

    def __init__(self, serverAddr = "0.0.0.0", serverPort =  config.tcpSslPort, sslSupport=True):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sslSupport = sslSupport
        
        if not sslSupport and serverPort == config.tcpSslPort:
            print "Request has been made to start TCP Server WITHOUT SSL support, however " \
            "seperate port number has not been given! Setting new port at ", config.tcpSslPort
            serverPort = config.tcpPort
            
        #self.socket.bind((socket.gethostname(), serverPort))
        self.socket.bind((serverAddr, serverPort))
        self.socket.listen(5)
        self.serverRunning = True
        threading.Thread.__init__(self)

    def stopServer(self):
	print "requesting to stop TCP server"
	self.serverRunning = False


    def run(self):
        if self.sslSupport:
            print "TCP (SSL) Server running..."
        else:
            print "TCP Server running..."

        while self.serverRunning:
            c = self.socket.accept()
            print "Received request from client"
            self.ClientProcessorThread(c, self.sslSupport).start()
        self.socket.close()
        if self.sslSupport:
            print "Stopped TCP (SSL) server."
        else:
            print "Stopped TCP server."


class HTTPServer(threading.Thread):       
    
    def tagINServerApp(self, environ, start_response):
        startTime = datetime.now();
        try:
            request_body_size = int(environ.get('CONTENT_LENGTH', 0))
        except (ValueError):
            request_body_size = 0
            
        request_body = environ['wsgi.input'].read(request_body_size)
        #d = parse_qs(request_body)
        #print escape(request_body)
        #xmlRequest = d.get('xmldata', [''])[0] # Returns the first age value.
        #xmlRequest = escape(xmlRequest)
        xmlRequest = escape(request_body)
        xmlRequest = xmlRequest.replace("&lt;", "<")
        xmlRequest = xmlRequest.replace("&gt;", ">")
        xmlRequest = xmlRequest.replace('\\"', '"')
        print "XML Request"
        print xmlRequest
        responseBody = processClientRequestXML(xmlRequest)
        status = '200 OK' # HTTP Status
        print "Status:", status
        print "Response body:"
        print responseBody
        headers = [('Content-type', 'text/plain'), ('Content-Length', str(len(responseBody))), ("Access-Control-Origin", '*')] # HTTP Headers
        start_response(status, headers)
        endtime = datetime.now()
        print "Processed client's request in", endtime - startTime, 'seconds'
        return [responseBody]

    def __init__(self, serverAddr = "0.0.0.0", serverPort =  config.httpPort):
        self.serverRunning = True
        self.httpServer = make_server(serverAddr, serverPort, self.tagINServerApp)
        threading.Thread.__init__(self)

    def run(self):
        print "HTTP Server running..."
        self.httpServer.serve_forever()
        #self.httpTestServer.serve_forever()

