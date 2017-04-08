package com.mciot.cw.enarte01.indoorlocatr;

/**
 * Created by enarte01 on 04/04/2017.
 */

public class UserLocation {


    public double latitude;
    public double longitude;
    public long time;

    public UserLocation() {
    }

    //constructor to initialize user location object
    public UserLocation(double latitude, double longitude,long time) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
