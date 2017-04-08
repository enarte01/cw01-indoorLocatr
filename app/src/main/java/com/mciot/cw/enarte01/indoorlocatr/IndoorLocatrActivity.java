package com.mciot.cw.enarte01.indoorlocatr;


import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

import java.util.UUID;

/**
 * Created by enarte01 on 04/04/2017.
 */

public class IndoorLocatrActivity extends AppCompatActivity
        implements
        IALocationListener {


    public final static int SECOND_IN_MILLIS = 1000;
    private final int PERMISSIONS = 0;
    private double currentLatitude;
    private double currentLongitude;
    private long currentTime;
    private IALocationManager locationManager;
    private IALocationRequest iaLocationRequest;
    private static final String TAG = IndoorLocatrActivity.class.getSimpleName();
    private final static Double GEOFENCE_LONG =  -0.13088517;
    private final static Double GEOFENCE_LAT = 51.52232672;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_locatr);
        Log.d(TAG, "onCreate()");

        //ask permissions
        String[] needPermissions = {

                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, needPermissions, PERMISSIONS);

        //initialize Firebase App
        FirebaseApp.initializeApp(this);

        locationManager = IALocationManager.create(this);
        iaLocationRequest = IALocationRequest.create();
        iaLocationRequest.setFastestInterval(SECOND_IN_MILLIS);
    }

    //check user response
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {

        TextView textView = (TextView) findViewById(R.id.textViewId);
        textView.setText(String.valueOf(iaLocation.getLatitude() + ", " +
                iaLocation.getLongitude()) + ", " + iaLocation.getTime());

        currentLatitude = iaLocation.getLatitude();
        currentLongitude = iaLocation.getLongitude();
        currentTime = iaLocation.getTime();

        //save user's current location and time to firebase database
        saveToFirebase(currentLatitude,currentLongitude,currentTime);

        //check user's distance from POI
        checkDistanceToGeofence(currentLatitude,
                currentLongitude, GEOFENCE_LAT, GEOFENCE_LONG);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        locationManager.requestLocationUpdates(iaLocationRequest, this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");
        locationManager.requestLocationUpdates(iaLocationRequest, this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        locationManager.removeLocationUpdates(this);
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        locationManager.destroy();
        super.onDestroy();

    }


    //save user location and time to firebase
    private void saveToFirebase(double cLat, double cLong, long cTime) {

        //create a userLocation object
        UserLocation userLocation = new UserLocation(cLat, cLong, cTime);
        //generate random location id
        String id = UUID.randomUUID().toString().substring(0,5);

        //create firebase database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference();
        dbReference.child("userlocation").child(id).setValue(userLocation);

    }


    //check user's distance from POI
    protected void checkDistanceToGeofence(double latitude, double longitude,
                                      Double geofenceLat, Double geofenceLong) {
        Log.d(TAG, "checkDistanceToGeofence()");
        final int RADIUS = 6371;//radius of earth in KM

        Double latitudeDistance = Math.toRadians(geofenceLat - latitude);
        Double longitudeDistance = Math.toRadians(geofenceLong - longitude);
        Double j = Math.pow(Math.sin(latitudeDistance / 2),2)
                + Math.pow(Math.sin(longitudeDistance / 2),2)
                * Math.cos(latitude) * Math.cos(geofenceLat);
        Double k = 2 * Math.asin(Math.sqrt(j));

        double distanceFromPOI =  RADIUS * k * 1000; //convert distance ot meters

        //toast if user's location distance is within 3meters from POI
        if (distanceFromPOI < 3){
            Toast.makeText(this, "You are " + distanceFromPOI + " meters from Entrance of Room 355",
                    Toast.LENGTH_SHORT).show();
        }
    }


}
