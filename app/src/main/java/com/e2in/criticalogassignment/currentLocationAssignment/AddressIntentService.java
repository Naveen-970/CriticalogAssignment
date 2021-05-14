package com.e2in.criticalogassignment.currentLocationAssignment;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddressIntentService extends IntentService {

    private static final String TAG = "AddressIntentService";
    private static final String IDENTIFIER = "GetAddressIntentService";
    private ResultReceiver addressResultReceiver;

    public AddressIntentService() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        addressResultReceiver = Objects.requireNonNull(intent).getParcelableExtra("add_receiver");
        if (addressResultReceiver == null) {
            Log.e(TAG, "No receiver, not processing the request further");
            return;
        }
        Location location = intent.getParcelableExtra("add_location");
        if (location == null) {
            sendResultsToReceiver(0, null);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (Exception ioException) {
            Log.e(TAG, "Error in getting address for the location");
        }
        if (addresses == null || addresses.size() == 0) {
            sendResultsToReceiver(1, null);
        } else {
            Address address = addresses.get(0);
            AddressResult addressResult = new AddressResult();

            addressResult.setAddress_id(address.getFeatureName());
            addressResult.setAddress_country(address.getCountryName());
            addressResult.setAddress_area(address.getLocality());
            addressResult.setAddress_district(address.getSubAdminArea());
            addressResult.setAddress_state(address.getAdminArea());
            addressResult.setAddress_zipcode(address.getPostalCode());
            addressResult.setAddress_lat(address.getLatitude());
            addressResult.setAddress_long(address.getLongitude());
            addressResult.setFull_address(address.getAddressLine(0));

            sendResultsToReceiver(2, addressResult);
        }
    }

    private void sendResultsToReceiver(int resultCode, AddressResult addressResult) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("address_result", addressResult);
        addressResultReceiver.send(resultCode, bundle);
    }
}
