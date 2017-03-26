package com.mciot.cw.enarte01.indoorlocatr;



public class UserLocation {


    public double latitude;
    public double longitude;
    public long time;

    public UserLocation() {
    }

    public UserLocation(double latitude, double longitude,long time) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
