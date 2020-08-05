package com.hextech.smarttime.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationServiceHandler {

    public JSONArray locations;
    public ArrayList<Location> nearbyLocations;

    public String categoryType;

    public LocationServiceHandler(String categoryType) {
        this.categoryType = categoryType;
    }

    public void sendRequest(final Context context, final double currentLatitude, final double currentLongitude, final String placeType, final VolleyCallback volleyCallback) {

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyDpFXzTaxUzi0r6RJj1UzUflij-JIiQ0oY&location=" + currentLatitude + "," + currentLongitude + "&radius=1000&type=" + placeType;
                Log.i("SmartTime", url);
                RequestQueue queue = Volley.newRequestQueue(context);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    locations = json.getJSONArray("results");

                                    populateLocationsArray();

                                    volleyCallback.onSuccess();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("SmartTime", "ERROR: " + error.getMessage());
                    }
                });

                queue.add(stringRequest);
            }
        });

        thread.start();
    }

    private void populateLocationsArray() {
        nearbyLocations = new ArrayList<>();

        for (int i = 0; i < locations.length(); i++) {
            Location location = new Location("test");
            try {
                JSONObject obj = locations.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                double tempLatitude = obj.getDouble("lat");
                double tempLongitude = obj.getDouble("lng");

                location.setLatitude(tempLatitude);
                location.setLongitude(tempLongitude);

                nearbyLocations.add(location);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Location> getNearbyLocations() {
        return nearbyLocations;
    }
}