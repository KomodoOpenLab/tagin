'''
Created on 21-Jul-09

@author: susahosh
'''
import psycopg2
from psycopg2 import ProgrammingError, IntegrityError
import psycopg2.extras
import sys, traceback, datetime
from synchronizationutil import *
import configurations

class DatabaseUtility():
    
    def escape(self, val):
        val = str(val).replace("'", "\\\'")
        return val
        

    
    def connectToDatabase(self):
        user = configurations.username
        password = configurations.password
        dbname = configurations.db
        #host = configurations.host
        try:
            connectStatment = 'dbname=' + dbname + ' user=' + user + ' password=' + password
            #connectStatment = 'dbname=' + dbname + ' user=' + user + ' host=' + host
            con = psycopg2.connect(connectStatment)
            return con
        except:
            print "Failed to connect to database"
            return None

    
    @synchronized        
    def executeQuery(self, query, isolation=psycopg2.extensions.ISOLATION_LEVEL_READ_COMMITTED):
        con = self.connectToDatabase()
        con.set_isolation_level(isolation)
        cur = con.cursor()
        data = None
        try:
            cur.execute(query)
            try:
                data = cur.fetchall()
            except ProgrammingError:
                #No data available...return None
                data = None
            con.commit()
        except:
            traceback.print_exc(file=sys.stdout)
            print "error occured while executing sql query"
            
        cur.close()
        con.close()
        return data

    @synchronized
    def insertIntoTableValues(self, tablename, columns, values, returnFields):
        #insert into fingerprintscollection (creationtime) VALUES ('2038-01-09 03:14:07') returning fingerprint;
        if tablename == None or tablename == '' or values == None:
            print "No data given to insert!!!"
            return -1
        cols = ''
        if columns != None:
            for column in columns:
                cols += column + ','
            cols = cols[0:len(cols) - 1]
            cols = '(' + cols + ')'
        
        query = 'INSERT INTO ' + tablename + cols + ' VALUES('
        numOfValues = len(values)
    
        i = 0
        for v in values:
            i+=1
            try:
                (val,insertRawValue)  =  v
                if insertRawValue:
                    v = "E\'" + self.escape(val) + "\'"
                    query += v
                else: 
                    query += "'" + str(val) + "'"
            except:
                query += "'" + str(v) + "'"
            
            if i < numOfValues:
                query += ',' 
        query += ')'
        if returnFields != None and returnFields != '':
            query += ' RETURNING ' + returnFields
        #print query
        
        con = self.connectToDatabase()
        if con == None:
            return -1
        
        cur = con.cursor()
        newField = None
        try:
            print "Attemping to insert into table " + tablename
            cur.execute(query)
            con.commit()
            if returnFields != None and returnFields != '':
                newField = cur.fetchone()
        except IntegrityError:
            pass
            print "Warning: IntegrityError occured. returning None"
            con.rollback()
        except:
            newField = -1
            traceback.print_exc(file=sys.stdout)
            con.rollback()
        
        cur.close()
        con.close()
        return newField
