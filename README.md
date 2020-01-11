# trainqa
Quality measurements about DB trains.

This project contains at least only the data collector, which collects
Deutsche Bahn timetable in realtime for station Brieselang.
The data are stored on a Google Spreadsheet. Travellers can adjust the data
based on their personal experiences and visualized on Tableau.

A first draft is here: https://public.tableau.com/profile/frank.kloeker#!/vizhome/trainqa/Blatt1

The project is more described here: https://github.com/eumel8/trainqa/projects/1



```plantuml

title Train QA

actor "Traveller" as traveller
participant "DB station realtime API" as db
participant "Data collector" as trains2
participant "Google Spreadsheet" as google
participant "Tableau data viz" as tableau


db->collector: collector pulls periodically timetable from train station
activate collector
collector->google: collector writes pull results in Google Spreadsheet
activate google
google->tableau: data are visualized in Tableau
activate tableau
traveller->google: Traveller can add more data based on the journey
