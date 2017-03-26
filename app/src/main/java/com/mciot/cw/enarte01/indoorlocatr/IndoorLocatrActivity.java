package com.mciot.cw.enarte01.indoorlocatr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

public class IndoorLocatrActivity extends AppCompatActivity {


    public final static int SECOND_IN_MILLIS = 1000;

    private double currentLat;
    private double currentLong;
    private long currentTime;
     IALocationManager locationManager;
     IALocationListener locationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation iaLocation) {

            TextView textView = (TextView) findViewById(R.id.textViewId);
            textView.setText(String.valueOf(iaLocation.getLatitude() + ", " + iaLocation.getLongitude()) + ", " + iaLocation.getTime());

            currentLat = iaLocation.getLatitude();
            currentLong = iaLocation.getLongitude();
            currentTime = iaLocation.getTime();


            saveToFirebase(currentLat,currentLong,currentTime);



        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_locatr);

        locationManager = IALocationManager.create(this);

      //  locationRequest = new IALocationRequest().create();
       // locationRequest.setFastestInterval(SECOND_IN_MILLIS);
        saveToFirebase(34.222, 45.9877, 1000000);


    }

    protected void onResume(){

        super.onResume();
        locationManager.requestLocationUpdates(IALocationRequest.create().setFastestInterval(SECOND_IN_MILLIS),locationListener);



    }

    protected void onPause(){
        super.onPause();
        locationManager.removeLocationUpdates(locationListener);
    }

    protected void onDestroy(){
        locationManager.destroy();
        super.onDestroy();

    }


    private void saveToFirebase(double cLat, double cLong, long cTime) {

        UserLocation userLocation = new UserLocation(cLat, cLong, cTime);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference("userLocation");
        dbReference.setValue(userLocation);



    }


}
