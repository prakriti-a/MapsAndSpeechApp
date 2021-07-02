package com.prakriti.mapsandspeechapp;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prakriti.mapsandspeechapp.model.CountryDataSource;

import java.io.IOException;
import java.util.List;
import java.util.zip.CheckedOutputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String receivedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // receive intent data from MainActivity before map is ready
        receivedCountry = getIntent().getStringExtra(CountryDataSource.COUNTRY_KEY);
        if(receivedCountry == null) { // null check
            receivedCountry = CountryDataSource.DEFAULT_COUNTRY_NAME;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     This callback is triggered when the map is ready to be used
     This method will only be triggered if the user has installed Google Play services
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double countryLatitude = CountryDataSource.DEFAULT_COUNTRY_LATITUDE;
        double countryLongitude = CountryDataSource.DEFAULT_COUNTRY_LONGITUDE;

        CountryDataSource countryDataSource = MainActivity.countryDataSource; // static var
        String countryMessage = countryDataSource.getCountryInfo(receivedCountry);
        // use GeoCoding
        Geocoder geocoder = new Geocoder(this);
        try {
            String countryAddress = receivedCountry;
            List<Address> countryAddresses = geocoder.getFromLocationName(countryAddress, 7); // Address - set of strings describing a location
            if(countryAddresses != null) {
                countryLatitude = countryAddresses.get(0).getLatitude(); // first val is most precise
                countryLongitude = countryAddresses.get(0).getLongitude();
            }
            else {
                receivedCountry = CountryDataSource.DEFAULT_COUNTRY_NAME;
            }
        }
        catch (IOException e) {
            receivedCountry = CountryDataSource.DEFAULT_COUNTRY_NAME;
            e.printStackTrace();
        }
        // add info to map
        LatLng myCountryLoc = new LatLng(countryLatitude, countryLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCountryLoc, 12.0f));
        mMap.addMarker(new MarkerOptions().position(myCountryLoc).title(countryMessage));
        mMap.addCircle(new CircleOptions().center(myCountryLoc).radius(500).strokeWidth(14.0f).strokeColor(Color.CYAN));


        // map test codes
//        LatLng bangalore = new LatLng(12.972026940967297, 77.59178964251116);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(bangalore, 10.0f);
//        mMap.moveCamera(cameraUpdate);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(bangalore);
//        markerOptions.title("Bangalore");
//        markerOptions.snippet("Capital of Karnataka");
//        mMap.addMarker(markerOptions);
//
//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(bangalore);
//        circleOptions.radius(500);
//        circleOptions.strokeWidth(20.0f);
//        circleOptions.strokeColor(Color.YELLOW);
//        mMap.addCircle(circleOptions);

    }
}