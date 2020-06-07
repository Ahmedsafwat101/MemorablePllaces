package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    public void centerMapOnLoction(Location location,String title)
    {

       LatLng userLocation= new LatLng(location.getLatitude(),location.getLongitude());
       mMap.addMarker((new MarkerOptions().position(userLocation).title(title)));


       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults!=null && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100000,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        if(intent.getIntExtra("PlaceNumber",0)==0)
        {
            locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(location!=null) {
                        centerMapOnLoction(location, "Your Location");
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if(Build.VERSION.SDK_INT<23)
            {

            }else{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,100000,0,locationListener);
                    Location lastLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastLocation!=null) {
                        centerMapOnLoction(lastLocation, "Your Location");
                    }
                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }
            }
        }
        else{
            int value=intent.getIntExtra("PlaceNumber",0);
            mMap.addMarker((new MarkerOptions().position(MainActivity.placesCoordinates.get(value)).title(MainActivity.places.get(value))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.placesCoordinates.get(value),15));
        }
    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        String Address="";
        try {
            List<Address> addresses= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addresses!=null && addresses.size()>0)
            {
                if(addresses.get(0).getThoroughfare()!=null)
                    Address=addresses.get(0).getThoroughfare()+",";
                if(addresses.get(0).getSubThoroughfare()!=null)
                    Address+=addresses.get(0).getSubThoroughfare()+",";
                if(addresses.get(0).getLocality()!=null)
                    Address+=addresses.get(0).getLocality()+",";
                if(addresses.get(0).getCountryName()!=null)
                    Address+=addresses.get(0).getCountryName();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Address=="")
            Address="Not Detected";
        Intent intent= new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("address",Address);
        mMap.addMarker((new MarkerOptions().position(latLng).title(Address)));
        MainActivity.places.add(Address);
        MainActivity.placesCoordinates.add(latLng);
        MainActivity.placesArrayAdapter.notifyDataSetChanged();
        // Using SharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("package com.example.myapplication",MODE_PRIVATE);
        try {
            ArrayList<String>latLngLatitude= new ArrayList<>();
            ArrayList<String>latLngLongitude= new ArrayList<>();

            for(LatLng coordinates: MainActivity.placesCoordinates )
            {
                latLngLatitude.add(Double.toString(coordinates.latitude));
                latLngLongitude.add(Double.toString(coordinates.longitude));

            }
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latitude",ObjectSerializer.serialize(latLngLatitude)).apply();
            sharedPreferences.edit().putString("longitude",ObjectSerializer.serialize(latLngLongitude)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
          //empty
    }
}
