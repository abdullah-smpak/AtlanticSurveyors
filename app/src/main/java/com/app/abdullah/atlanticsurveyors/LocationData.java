package com.app.abdullah.atlanticsurveyors;

public class LocationData {
    public double latitude;
    public double longitude;
    public String date;
    public String time;
    public String Name;


    public LocationData() {
    }

    public LocationData(double latitude, double longitude, String date ,String time, String Name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
        this.Name = Name;
    }
}
