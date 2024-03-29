package com.hextech.smarttime;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hextech.smarttime.util.MyBackgroundService;
import com.hextech.smarttime.util.SendLocationToActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button viewListButton, addToDoItemButton;

    MyBackgroundService mService = null;
    boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewListButton = findViewById(R.id.viewEvents);
        addToDoItemButton = findViewById(R.id.addEvent);

        viewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 openViewTodoList();
            }
        });

        addToDoItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateToDoItem.class));
            }
        });

        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                        if(mService != null) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mService.requestLocationUpdates();
                                                }
                                            });
                                            break;
                                        }
                                    } catch (Exception e) {
                                        Log.e("SmartTime", "Cannot start the service.");
                                    }
                                }
                            }
                        }).start();

                        bindService(new Intent(MainActivity.this,
                                        MyBackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void openViewTodoList(){
        Intent intent = new Intent(this, ToDoListActivity.class);
        startActivity(intent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListnerLocation(SendLocationToActivity event){
        if(event != null){
            String data = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            Log.i("SmartTime", data);
        }

    }
}