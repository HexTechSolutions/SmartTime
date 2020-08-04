package com.hextech.smarttime.util;

import android.content.Context;
import android.location.Location;

import java.text.DateFormat;
import java.util.Date;

public class Common {
    public static final String KEY_REQUESTING_LOCATION_UPDATES = "LocationUpdateEnable";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "Unknown Location" : new StringBuilder()
                .append(mLocation.getLatitude())
                .append("/")
                .append(mLocation.getLongitude())
                .toString();
    }

    public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService) {
        return String.format("Location updatedL %1$s", DateFormat.getDateInstance().format(new Date()));
    }

}
