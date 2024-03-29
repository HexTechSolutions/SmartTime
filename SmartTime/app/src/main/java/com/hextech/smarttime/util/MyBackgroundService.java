package com.hextech.smarttime.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hextech.smarttime.DetailActivity;
import com.hextech.smarttime.MainActivity;
import com.hextech.smarttime.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyBackgroundService extends Service {

    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.hextech.smarttime.util"
            +".started_from_notification";

    private final IBinder iBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL = UPDATE_INTERVAL_IN_MIL/2;
    private static final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    private double currentLongitude, currentLatitude;

    private ArrayList<String> notificationsSentList;

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread("SmartTime");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if(startedFromNotification){
            removeLocationUpdates();
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void removeLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            stopSelf();
        } catch (SecurityException ex){
            Log.e("SmartTime", "Lost location permission. Could not remove updates. "+ex);
        }
    }

    private void getLastLocation() {
        try{
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() != null)
                                mLocation = task.getResult();
                            else
                                Log.e("SmartTime", "Failed to get location");

                        }
                    });
        } catch (SecurityException ex) {
            Log.e("SmartTime", "Lost location permission. "+ex);
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MIL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
    }

    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));

        if(serviceIsRunningInForeground(this))
            getNotification();
    }

    private String getChannelId(String category) {
        return category + "_id";
    }

    private int getNotificationId(String category) {
        switch (category.toLowerCase()) {
            case "restaurant":
                return 1111;
            case "park":
                return 2222;
            case "coffee":
                return 3333;
            case "library":
                return 4444;
            case "shopping":
                return 5555;
        }
        return -1;
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        String text = Common.getLocationText(mLocation);

        if (mLocation.getLongitude() != currentLatitude && mLocation.getLongitude() != currentLongitude) {
            currentLatitude = mLocation.getLatitude();
            currentLongitude = mLocation.getLongitude();
            notificationsSentList = new ArrayList<>();

            Log.i("SmartTime", text);

            //TODO
            ArrayList<ToDoItem> toDoItems = DBHelper.getAllData(getApplicationContext());
            for (ToDoItem item: toDoItems) {
                sendRequest(currentLatitude, currentLongitude, item.getCategory().toLowerCase(), getChannelId(item.getCategory()), getNotificationId(item.getCategory()));
            }

        }

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this,0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_baseline_launch_24, "Launch", activityPendingIntent)
                .addAction(R.drawable.ic_baseline_cancel_24, "Remove", servicePendingIntent)
                .setContentTitle("SmartTime is running in background.")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    private void sendRequest(double currentLatitude, double currentLongitude, final String placeType, final String CHANNEL_ID, final int notificationID) {

        if (notificationsSentList == null) {
            notificationsSentList = new ArrayList<>();
        }

        if (notificationsSentList.contains(placeType)) {
            return;
        }
        notificationsSentList.add(placeType);

        LocationServiceHandler.sendRequest(getApplicationContext(), currentLatitude, currentLongitude, placeType, new VolleyCallback() {
            @Override
            public void onSuccess() {
                ArrayList<Location> nearbyLocations = LocationServiceHandler.nearbyLocations;
                Log.i("SmartTime", "Locations Acquired.");
                if (nearbyLocations.size() > 0) {
                    createNotification(placeType, CHANNEL_ID, notificationID, nearbyLocations.get(0).getLongitude(), nearbyLocations.get(0).getLatitude());
                }
            }
        });
    }

    private void createNotification(String category, String CHANNEL_ID, int notificationId, double longitude, double latitude) {

        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("category", category);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(DetailActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(category)
                .setContentText("Content")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(category + " Found. Click to view details."))
                .setContentIntent(resultPendingIntent)
                .setGroup("NOTIF_GROUP")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SmartTime",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;
        return false;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        startService(new Intent(getApplicationContext(), MyBackgroundService.class));
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }catch (SecurityException ex){
            Log.e("SmartTime", "Lost location permission. Could not request it "+ ex);
        }
    }

    public class LocalBinder extends Binder {
        public MyBackgroundService getService(){
            return MyBackgroundService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        return iBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration)
            startForeground(NOTI_ID, getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}
