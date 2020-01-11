#!/usr/bin/env python3

import requests
from datetime import datetime
from bs4 import BeautifulSoup
import gspread
from oauth2client.service_account import ServiceAccountCredentials


homestation = '8013472'

def gspread_data(date,trainnumber,traindeparture,traintarget,traindelay,traintext):
    row = [date,trainnumber,traindeparture,traintarget,traindelay,traintext]
    scope = ['https://spreadsheets.google.com/feeds','https://www.googleapis.com/auth/drive']
    creds = ServiceAccountCredentials.from_json_keyfile_name('gspread-secret.json', scope)
    client = gspread.authorize(creds)
    sheet = client.open('trainqa').sheet1
    sheet.insert_row(row)
    return

def parse_departures1(html,dt):

    soup = BeautifulSoup(html, "html.parser")
    date = dt.strftime("%d.%m.%y")

    # print ("Date\t\tTrain\t\tDeparture\tTarget\t\t\t\tDelay\tText")
    for row in soup.find_all('div', attrs={'class': 'sqdetailsDep trow'}):
        trainnumber = row.span.contents[0]
        trainurl = row.a['href']
        traintarget = row.br.previous_element.strip()[3:]
        traindeparture = (row.find_all('span')[-1].get_text())
        traindelay, traintext = traindetails(date,trainnumber,trainurl)
        gspread_data(date,trainnumber,traindeparture,traintarget,traindelay,traintext)
        #print ("%s\t%s\t%s\t\t%s\t\t\t\t%s\t%s" % (date,trainnumber,traindeparture,traintarget,traindelay,traintext))
    return

def parse_train(html):

    soup = BeautifulSoup(html, "html.parser")
    traindelay = '0'
    traintext = ''

    for row in soup.find_all("st"):
        station = row.get('evaid')
        if station == homestation:
          traindelay = row.get('arrdelay')
          traintext = row.get('freitext')

    return traindelay,traintext

def traindetails(date,trainnumber,trainurl):

    trainurl = trainurl.replace('dox','dn')
    rsp = requests.get(trainurl)
    traindelay, traintext = parse_train(rsp.text)
    return traindelay,traintext

def departures(origin, dt=datetime.now()):

    query = {
        'si': origin,
        'bt': "dep",
        'date': dt.strftime("%d.%m.%y"),
        'ti': dt.strftime("%H:%M"),
        'p': '1111101',
        'rt': 1,
        'max': 7,
        'use_realtime_filter': 1,
        'start': "yes",
        'L': 'vs_java3'
    }
    rsp = requests.get('http://mobile.bahn.de/bin/mobil/bhftafel.exe/dox?', params=query)
    return parse_departures1(rsp.text,dt)

if __name__ == "__main__":
    #gspread_data()
    departures(homestation)
