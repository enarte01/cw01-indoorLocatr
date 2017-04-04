package com.mciot.cw.enarte01.indoorlocatr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by genie on 01/04/2017.
 */

public class GFTransitionIntentService extends IntentService{

    protected static final String TAG = "GFTransitionIService";

    public GFTransitionIntentService(){
        super(TAG);

        Log.d(TAG, "GFTransitionIService");


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()){

            Log.e(TAG, "GeofencingEvent Error: " + event.getErrorCode());

            return;
        }
        String eventString = createGeofenceTransitionDescription(event);
        sendNotification(eventString);

    }


    private static String createGeofenceTransitionDescription(GeofencingEvent geofencingEvent){

        int transitionCode = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeo = new ArrayList<>();

        if (transitionCode == Geofence.GEOFENCE_TRANSITION_ENTER ||
                transitionCode == Geofence.GEOFENCE_TRANSITION_EXIT) {
            triggeringGeo = geofencingEvent.getTriggeringGeofences();

        }

        ArrayList<String> geoIDs = new ArrayList<>();
        for (Geofence geo : triggeringGeo){
            geoIDs.add(geo.getRequestId());
        }

        String transition = null;
        if (transitionCode == Geofence.GEOFENCE_TRANSITION_ENTER){

            transition = "You enter Geofence";
        }else if (transitionCode == Geofence.GEOFENCE_TRANSITION_EXIT){

            transition = "You exited Geofence";
        }

            return transition + TextUtils.join(", ", geoIDs);
    }



    private void sendNotification(String noteString){
        Log.d(TAG, "sendNotification :" + noteString);
        Intent noteIntent = new Intent(getApplicationContext(), IndoorLocatrActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(IndoorLocatrActivity.class).addNextIntent(noteIntent);
        PendingIntent notePendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);




                NotificationManager noteManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(0,getNotified(noteString,notePendingIntent));


    }

    private Notification getNotified(String noteString, PendingIntent notePendingIntent) {

        NotificationCompat.Builder notebuilder = new NotificationCompat.Builder(this);
        notebuilder
                .setColor(Color.BLUE)
                .setContentTitle(noteString)
                .setContentIntent(notePendingIntent)
                .setContentText("You have reached your Objective!!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS |
                        Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        return notebuilder.build();
    }
}
