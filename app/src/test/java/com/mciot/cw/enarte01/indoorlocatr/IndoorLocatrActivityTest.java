package com.mciot.cw.enarte01.indoorlocatr;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Created by enarte01 on 04/04/2017.
 */
public class IndoorLocatrActivityTest  {


    @Before
    public void setUp() throws Exception {

    }



    @Test
    public void checkDistanceToGeofenceTest() throws Exception {

        try {
            // Test checkDistanceToGeofence() method
            new IndoorLocatrActivity().checkDistanceToGeofence(51.52220685,-0.13084543,51.59220685,-0.19084543);
        }catch (Exception e){
            e.printStackTrace();
            fail();// assert fail if exception occurs
        }

    }



    @After
    public void tearDown() throws Exception {

    }


}