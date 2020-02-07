package com.eumelnet.bahn.spreadsheetinput;


public class MyDataModel {

    private String datum;
    private String zug;
    private String abfahrt;
    private String ziel;

    public String getDatum() { return datum; }
    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getZug() {
        return zug;
    }
    public void setZug(String zug) {
        this.zug = zug;
    }

    public String getAbfahrt() {
        return abfahrt;
    }
    public void setAbfahrt(String abfahrt) {
        this.abfahrt = abfahrt;
    }

    public String getZiel() {
        return ziel;
    }
    public void setZiel(String ziel) {
        this.ziel = ziel;
    }

}