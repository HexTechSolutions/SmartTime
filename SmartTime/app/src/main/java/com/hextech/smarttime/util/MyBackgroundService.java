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
import androidx.core.app.NotificationManagerCompat;

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

import java.util.ArrayList;

public class MyBackgroundService extends Service {

    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.hextech.smarttime.util"
            + ".started_from_notification";

    private final IBinder iBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL = UPDATE_INTERVAL_IN_MIL / 2;
    private static final int NOTI_ID = 1223;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    private double currentLongitude, currentLatitude;
    //List of locations that corresponds to every category in the To-Do list
    ArrayList<ArrayList<Location>> locations= new ArrayList<>();

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

    LocationServiceHandler locationServiceHandlerCoffee;
    LocationServiceHandler locationServiceHandlerPark;
    LocationServiceHandler locationServiceHandlerLibrary;
    LocationServiceHandler locationServiceHandlerShopping;

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
        createNotificationChannel();

        HandlerThread handlerThread = new HandlerThread("SmartTime");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if (startedFromNotification) {
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
        } catch (SecurityException ex) {
            Log.e("SmartTime", "Lost location permission. Could not remove updates. " + ex);
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null)
                                mLocation = task.getResult();
                            else
                                Log.e("SmartTime", "Failed to get location");

                        }
                    });
        } catch (SecurityException ex) {
            Log.e("SmartTime", "Lost location permission. " + ex);
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

        if (serviceIsRunningInForeground(this))
            getNotification();
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        final String text = Common.getLocationText(mLocation);

        if (mLocation.getLongitude() != currentLatitude && mLocation.getLongitude() != currentLongitude) {
            currentLatitude = mLocation.getLatitude();
            currentLongitude = mLocation.getLongitude();

            Log.i("SmartTime", text);

            //Returns all the To-Do items in the database
            ArrayList data = DBHelper.getAllData(getApplicationContext());



//            for (int i = 0; i<data.size(); i++){
//                ToDoItem td = (ToDoItem) data.get(i);
//
//                sendRequest(td.getCategory(),td.getRecordID());
//
//                System.out.println("Item ID  :" + td.getRecordID() );
//            }

            sendRequest("Coffee");
            sendRequest("Library");
            sendRequest("Shopping");
            sendRequest("Park");

        }

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    private void sendRequest(final String location){

        if(location.equals("Coffee")){
            locationServiceHandlerCoffee = new LocationServiceHandler("Coffee");
            SendIndividualRequests(locationServiceHandlerCoffee,location);
        }
        if(location.equals("Library")){
            locationServiceHandlerLibrary = new LocationServiceHandler("Library");
            SendIndividualRequests(locationServiceHandlerLibrary,location);
        }
        if(location.equals("Shopping")){
            locationServiceHandlerShopping = new LocationServiceHandler("Shopping");
            SendIndividualRequests(locationServiceHandlerShopping,location);
        }
        if(location.equals("Park")){
            locationServiceHandlerPark = new LocationServiceHandler("Park");
            SendIndividualRequests(locationServiceHandlerPark,location);
        }



//        locationServiceHandlerCoffee.sendRequest(getApplicationContext(), currentLatitude, currentLongitude, location, new VolleyCallback() {
//            @Override
//            public void onSuccess() {
//                //ArrayList<Location> nearbyLocations = locationServiceHandler.nearbyLocations;
//                //locations.add(nearbyLocations);
//                Log.i("SmartTime", "Locations Acquired.");
//
//                CreatePushNotification(location,locations.get(0).get(0).getLongitude(),locations.get(0).get(0).getLatitude());
//
//            }
//        });


    }

    private void SendIndividualRequests(final LocationServiceHandler locationServiceHandler, final String location){
        locationServiceHandler.sendRequest(getApplicationContext(), currentLatitude, currentLongitude, location, new VolleyCallback() {
            @Override
            public void onSuccess() {
                ArrayList<Location> nearbyLocations = locationServiceHandler.nearbyLocations;
                locations.add(nearbyLocations);
                Log.i("SmartTime", "Locations Acquired.");

                CreatePushNotification(location,locations.get(0).get(0).getLongitude(),locations.get(0).get(0).getLatitude());

            }
        });
    }

    private void CreatePushNotification(String title, double longitude, double latitude){

        //Setting DetailActivity As the Pending Intent
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra("longitude",longitude);
        detailActivity.putExtra("latitude", latitude);
        detailActivity.putExtra("category",title);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(detailActivity);

        PendingIntent DetailPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //Creating Notification
        if(title.equals("Coffee")){
            CreateIndividualNotifications(title,000001,DetailPendingIntent);
        }
        if(title.equals("Shopping")){
            CreateIndividualNotifications(title,000002,DetailPendingIntent);
        }
        if(title.equals("Library")){
            CreateIndividualNotifications(title,000003,DetailPendingIntent);
        }
        if(title.equals("Park")){
            CreateIndividualNotifications(title,000004,DetailPendingIntent);
        }

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle(title + " Found")
//                .setContentText("Test Notification")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Click to see Details"))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(DetailPendingIntent)
//                .setGroup(GROUP_KEY_WORK_EMAIL);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//        notificationManager.notify(000001, builder.build());
    }

    private void CreateIndividualNotifications(String category, int nofiId, PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(category + " Found")
                .setContentText("Test Notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Click to see Details"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY_WORK_EMAIL);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(nofiId, builder.build());
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (getClass().getName().equals(service.service.getClassName()))
                if (service.foreground)
                    return true;
        return false;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        startService(new Intent(getApplicationContext(), MyBackgroundService.class));
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException ex) {
            Log.e("SmartTime", "Lost location permission. Could not request it " + ex);
        }
    }

    public class LocalBinder extends Binder {
        public MyBackgroundService getService() {
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
        if (!mChangingConfiguration)
            startForeground(NOTI_ID, getNotification());
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}
