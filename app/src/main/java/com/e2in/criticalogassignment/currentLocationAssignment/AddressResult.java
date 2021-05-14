package com.e2in.criticalogassignment.currentLocationAssignment;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressResult implements Parcelable {
    private double address_lat, address_long;
    private String address_id, address_area, address_country,
            address_locality, address_state, address_zipcode, address_district, full_address;

    protected AddressResult(Parcel in) {
        address_lat = in.readDouble();
        address_long = in.readDouble();
        address_id = in.readString();
        address_area = in.readString();
        address_country = in.readString();
        address_locality = in.readString();
        address_state = in.readString();
        address_zipcode = in.readString();
        address_district = in.readString();
        full_address = in.readString();
    }

    public AddressResult() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(address_lat);
        dest.writeDouble(address_long);
        dest.writeString(address_id);
        dest.writeString(address_area);
        dest.writeString(address_country);
        dest.writeString(address_locality);
        dest.writeString(address_state);
        dest.writeString(address_zipcode);
        dest.writeString(address_district);
        dest.writeString(full_address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressResult> CREATOR = new Creator<AddressResult>() {
        @Override
        public AddressResult createFromParcel(Parcel in) {
            return new AddressResult(in);
        }

        @Override
        public AddressResult[] newArray(int size) {
            return new AddressResult[size];
        }
    };

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setAddress_district(String address_district) {
        this.address_district = address_district;
    }

    public String getAddress_district() {
        return address_district;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_area(String address_area) {
        this.address_area = address_area;
    }

    public String getAddress_area() {
        return address_area;
    }

    public void setAddress_country(String address_country) {
        this.address_country = address_country;
    }

    public String getAddress_country() {
        return address_country;
    }

    public void setAddress_locality(String address_locality) {
        this.address_locality = address_locality;
    }

    public String getAddress_locality() {
        return address_locality;
    }

    public void setAddress_state(String address_state) {
        this.address_state = address_state;
    }

    public String getAddress_state() {
        return address_state;
    }

    public void setAddress_zipcode(String address_zipcode) {
        this.address_zipcode = address_zipcode;
    }

    public String getAddress_zipcode() {
        return address_zipcode;
    }

    public void setAddress_lat(double address_lat) {
        this.address_lat = address_lat;
    }

    public double getAddress_lat() {
        return address_lat;
    }

    public void setAddress_long(double address_long) {
        this.address_long = address_long;
    }

    public double getAddress_long() {
        return address_long;
    }
}

