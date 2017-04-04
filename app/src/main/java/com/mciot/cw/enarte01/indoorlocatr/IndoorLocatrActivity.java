package com.mciot.cw.enarte01.indoorlocatr;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.google.android.gms.location.Geofence;

public class IndoorLocatrActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {


    public final static int SECOND_IN_MILLIS = 1000;
    protected Geofence geofence;
    protected GoogleApiClient googleApiClient;
    private final int CODE_PERMISSIONS = 0;
    private double currentLat;
    private double currentLong;
    private long currentTime;
    private IALocationManager locationManager;
    private IALocationRequest iaLocationRequest;
    private static final String TAG = IndoorLocatrActivity.class.getSimpleName();
    private LocationListener lastLocation;
    private LocationRequest locationRequest;




private IALocationListener ialocationListener = new IALocationListener() {
    @Override
    public void onLocationChanged(IALocation iaLocation) {

        TextView textView = (TextView) findViewById(R.id.textViewId);
        textView.setText(String.valueOf(iaLocation.getLatitude() + ", " + iaLocation.getLongitude()) + ", " + iaLocation.getTime());

        currentLat = iaLocation.getLatitude();
        currentLong = iaLocation.getLongitude();
        currentTime = iaLocation.getTime();

        //saveToFirebase(currentLat,currentLong,currentTime);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_locatr);

        String[] needPermissions = {

                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, needPermissions, CODE_PERMISSIONS);

        FirebaseApp.initializeApp(this);

        locationManager = IALocationManager.create(this);

        iaLocationRequest = IALocationRequest.create();
        iaLocationRequest.setFastestInterval(SECOND_IN_MILLIS);
        //saveToFirebase(24.222, 45.9877, 400000);
        createGoogleClient();
        createGeofence();

        //createGeofencingRequest();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Handle perimssions denied
    }

    private void createGeofence() {

        geofence = new Geofence.Builder()
                .setRequestId("POIwayPoint")
                .setCircularRegion(51.452659, -0.9500196, 100)
                .setExpirationDuration(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(0)
                .build();
    }

    //create GoogleApiClient
    protected synchronized void createGoogleClient() {

        Log.d(TAG, "Create GoogleAPIClient");

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();


        }

        if (googleApiClient.isConnected()) {

            Toast.makeText(this, "Google Api Client Connected!! ", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(iaLocationRequest, ialocationListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();


        }

        if (googleApiClient.isConnected()) {

            Toast.makeText(this, "Google Api Client Connected!! ", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(iaLocationRequest, ialocationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        locationManager.removeLocationUpdates(ialocationListener);
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");

        super.onStop();
        if (googleApiClient.isConnecting() || googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        locationManager.destroy();
        super.onDestroy();

    }


    public void saveToFirebase(double cLat, double cLong, long cTime) {


        UserLocation userLocation = new UserLocation(cLat, cLong, cTime);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference("userLocation");
        dbReference.setValue(userLocation);

    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected()");
        addWayPointGeofence();

        beginLocationUpdates();

        if (googleApiClient.isConnected()) {
            //let googleAPIClient check for location updates herre
            // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, iaLocationRequest, this);
            Toast.makeText(this, "Google Api Client onConnected!! ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "GoogleAPIClient connected");
        }
    }

    //start googleAPI locatiob updates
    private void beginLocationUpdates() {

        Log.d(TAG, "beginLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setInterval(SECOND_IN_MILLIS)
                .setFastestInterval(15)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "onConnectionSuspended()");

        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");

    }

    @Override
    public void onResult(Status status) {

        Log.d(TAG,"onResult()");

        if (status.isSuccess()){

            Log.d(TAG,"POIwayPoint added");

            Toast.makeText(this,"POIwayPoint added", Toast.LENGTH_SHORT).show();
        }else{

            GeofenceErrorMessages.getErrorToast(this, status.getStatusCode());
        }

    }

    public void addWayPointGeofence(){

        Log.d(TAG,"POIwayPoint added");



        try{
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    createGeofencingRequest(),
                    createGeofencePendingIntent()
            ).setResultCallback(this);
        }catch (SecurityException se){
            ///do sumthin here
            Log.d(TAG,"SecurityException se");
        }
    }

//create geofenceRequest to monitor created geofences
    private GeofencingRequest createGeofencingRequest(){

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);

        return builder.build();
    }

    private PendingIntent pendingIntent;
    private PendingIntent createGeofencePendingIntent(){
        Log.d(TAG,"createGeofencePendingIntent()");

        if (pendingIntent != null){
            return pendingIntent;
        }

        Intent intent = new Intent(this,GFTransitionIntentService.class);

        return PendingIntent.getService(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //googleAPI onLocationChanged
    @Override
    public void onLocationChanged(Location location) {

        TextView textView = (TextView) findViewById(R.id.textViewId);
        textView.setText(String.valueOf(location.getLatitude() + ", " + location.getLongitude()) + ", " + location.getTime());


        saveToFirebase(location.getLatitude(),location.getLongitude(),location.getTime());


    }
}
