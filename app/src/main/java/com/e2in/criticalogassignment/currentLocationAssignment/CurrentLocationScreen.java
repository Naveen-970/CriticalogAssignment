package com.e2in.criticalogassignment.currentLocationAssignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.e2in.criticalogassignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrentLocationScreen extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private TextView currentAddTv;
    private Location currentLocation;
    private LocationCallback locationCallback;

    private static final String TAG = "CurrentLocationScreen";
    private final Context context = this;
    private DBHelper dbHelper;
    private TextView locationsFromDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location_screen);

        locationsFromDb = (TextView) findViewById(R.id.db_locations);
        dbHelper = new DBHelper(this);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    JSONObject jsonObject = dbHelper.getAllAddress();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            locationsFromDb.setText(jsonObject.toString());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 10, 30, TimeUnit.MINUTES);
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        currentAddTv = findViewById(R.id.textview);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                currentLocation = locationResult.getLocations().get(0);
                getAddress();

            }
        };
        startLocationUpdates();
    }

    public boolean checkGpsStatus(){

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
            else {
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(2000);
                locationRequest.setFastestInterval(2000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (checkGpsStatus()){
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }else {
                    Log.e(TAG,"Location is disabled");
                    Toast.makeText(context,"Location is disabled",Toast.LENGTH_LONG).show();
                }
            }
        }else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(60000*10);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (checkGpsStatus()){
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }else {
                Log.e(TAG,"Location is disabled");
                Toast.makeText(context,"Location is disabled",Toast.LENGTH_LONG).show();
            }
        }

    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(context, "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, AddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
            else {
                Toast.makeText(this, "Location permission not granted, " +
                        "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }
            if (resultCode == 1) {
                Toast.makeText(context, "Address not found, ", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 2){
                AddressResult addressResult = resultData.getParcelable("address_result");
                Log.e(TAG,"Address Time: "+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                dbHelper.insertAddress(addressResult);
                String resultAddress = "Current Location: "+"\n"
                        +"Area: "+addressResult.getAddress_area()+"\n"
                        +"District: "+addressResult.getAddress_district()+"\n"
                        +"Zip Code: "+addressResult.getAddress_zipcode()+"\n"
                        +"State: "+addressResult.getAddress_state()+"\n"
                        +"Country: "+addressResult.getAddress_country()+"\n"
                        +"Lattitude: "+addressResult.getAddress_lat()+"\n"
                        +"Longitude: "+addressResult.getAddress_long()+"\n"
                        +"Full Address: "+addressResult.getFull_address();
                showResults(resultAddress);
            }
        }
    }
    private void showResults(String currentAdd) {
        currentAddTv.setText(currentAdd);
    }

}