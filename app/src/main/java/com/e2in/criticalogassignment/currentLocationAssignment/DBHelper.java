package com.e2in.criticalogassignment.currentLocationAssignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "locations.db";
    private static final String TABLE_NAME = "address";

    private static final String AUTO_ID = "id";
    private static final String ADDRESS_ID = "addressid";
    private static final String ADDRESS_AREA = "areaname";
    private static final String ADDRESS_LOCALITY = "locality";
    private static final String ADDRESS_DISTRICT = "district";
    private static final String ADDRESS_STATE = "state";
    private static final String ADDRESS_COUNTRY = "country";
    private static final String ADDRESS_ZIPCODE = "zipcode";
    private static final String ADDRESS_LATTITUDE = "latittude";
    private static final String ADDRESS_LONGITTUDE = "longittude";
    private static final String FULL_ADDRESS = "fulladdress";

    public DBHelper(Context context){
        super(context,DB_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT,addressid TEXT,areaname TEXT," +
                "locality TEXT, district TEXT, state TEXT, country TEXT, zipcode TEXT, latittude TEXT, longittude TEXT, fulladdress TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAddress  (AddressResult addressResult) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADDRESS_ID, addressResult.getAddress_id());
        contentValues.put(ADDRESS_AREA, addressResult.getAddress_area());
        contentValues.put(ADDRESS_LOCALITY, addressResult.getAddress_locality());
        contentValues.put(ADDRESS_DISTRICT, addressResult.getAddress_district());
        contentValues.put(ADDRESS_STATE, addressResult.getAddress_area());
        contentValues.put(ADDRESS_COUNTRY, addressResult.getAddress_country());
        contentValues.put(ADDRESS_ZIPCODE, addressResult.getAddress_zipcode());
        contentValues.put(ADDRESS_LATTITUDE, addressResult.getAddress_lat());
        contentValues.put(ADDRESS_LONGITTUDE, addressResult.getAddress_long());
        contentValues.put(FULL_ADDRESS,addressResult.getFull_address());
        db.insert(TABLE_NAME, null, contentValues);
        Log.e(TAG,"Inserted");
        return true;
    }

    public JSONObject getAllAddress() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            JSONObject record = new JSONObject();
            record.put(AUTO_ID,res.getString(0));
            record.put(ADDRESS_ID,res.getString(1));
            record.put(ADDRESS_AREA,res.getString(2));
            record.put(ADDRESS_LOCALITY,res.getString(3));
            record.put(ADDRESS_DISTRICT,res.getString(4));
            record.put(ADDRESS_STATE,res.getString(5));
            record.put(ADDRESS_COUNTRY,res.getString(6));
            record.put(ADDRESS_ZIPCODE,res.getString(7));
            record.put(ADDRESS_LATTITUDE,res.getString(8));
            record.put(ADDRESS_LONGITTUDE,res.getString(9));
            record.put(FULL_ADDRESS,res.getString(10));
            array.put(record);
            res.moveToNext();
        }
        jsonObject.put("Previous Locations", array);
        return jsonObject;
    }



}
