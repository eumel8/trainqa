#!/usr/bin/env python3

import requests
from datetime import datetime
from bs4 import BeautifulSoup
import gspread
from oauth2client.service_account import ServiceAccountCredentials


homestation = '8013472'

def gspread_data(date,trainnumber,traindeparture,traintarget,traindelay,traintext,trainnote):
    row = [date,trainnumber,traindeparture,traintarget,traindelay,traintext,trainnote]
    scope = ['https://spreadsheets.google.com/feeds','https://www.googleapis.com/auth/drive']
    creds = ServiceAccountCredentials.from_json_keyfile_name('gspread-secret.json', scope)
    client = gspread.authorize(creds)
    sheet = client.open('trainqa').sheet1
    #sheet.update_cell(1, 1, "Date")
    #sheet.update_cell(1, 2, "Train")
    #sheet.update_cell(1, 3, "Departure")
    #sheet.update_cell(1, 4, "Target")
    #sheet.update_cell(1, 5, "Delay")
    #sheet.update_cell(1, 6, "Text")
    sheet.insert_row(row)
    removeDuplicates(sheet)
    return

def removeDuplicates(sheet):
  data = sheet.get_all_values()
  datalen = len(data)
  newData = []
  for i in range(len(data)):
      row = data[i]
      for j in range(len(newData)):
          #if row.join() == newData[j].join():
          if row == newData[j]:
              sheet.delete_row(i)

  return


def parse_departures1(html,dt):

    soup = BeautifulSoup(html, "html.parser")
    date = dt.strftime("%d.%m.%y")

    for row in soup.find_all('div', attrs={'class': 'sqdetailsDep trow'}):
        trainnumber = row.span.contents[0]
        trainurl = row.a['href']
        traintarget = row.br.previous_element.strip()[3:]
        traindeparture = (row.find_all('span', attrs={'class': 'bold'})[-1].get_text())
        traindelay, traintext, trainnote = traindetails(date,trainnumber,trainurl)
        gspread_data(date,trainnumber,traindeparture,traintarget,traindelay,traintext,trainnote)
    return

def parse_train(html):

    soup = BeautifulSoup(html, "html.parser")
    traindelay = '0'
    traintext = ''
    trainnote = ''

    for row in soup.find_all("st"):
        station = row.get('evaid')
        if station == homestation:
          traindelay = row.get('arrdelay')
          traintext = row.get('freitext')
    for row in soup.find_all("himmessage", attrs={'display': 'QFT'}):
    #print (soup.find("himmessage",display='QFT').header)
        trainnote = row.get('header')

    return traindelay,traintext,trainnote

def traindetails(date,trainnumber,trainurl):

    trainurl = trainurl.replace('dox','dn')
    rsp = requests.get(trainurl)
    traindelay, traintext, trainnote = parse_train(rsp.text)
    return traindelay,traintext, trainnote

def departures(origin, dt=datetime.now()):

    query = {
        'si': origin,
        'bt': "dep",
        'date': dt.strftime("%d.%m.%y"),
        'ti': dt.strftime("%H:%M"),
        'p': '1111101',
        'rt': 1,
        'max': 4,
        'use_realtime_filter': 1,
        'start': "yes",
        'L': 'vs_java3'
    }
    rsp = requests.get('http://mobile.bahn.de/bin/mobil/bhftafel.exe/dox?', params=query)
    return parse_departures1(rsp.text,dt)

if __name__ == "__main__":
    #gspread_data()
    departures(homestation)
