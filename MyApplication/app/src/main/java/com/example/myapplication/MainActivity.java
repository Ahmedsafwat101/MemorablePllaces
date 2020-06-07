package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   static  ArrayList<String> places= new ArrayList<String>();
   static  ArrayList<LatLng> placesCoordinates= new ArrayList<>();
   static ArrayAdapter<String> placesArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView= (ListView)findViewById(R.id.ListView);
        if(places.size()==0) {

            places.add("Please Add new Place .....");
        }
            placesCoordinates.add(new LatLng(0, 0));
            placesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, places);
            listView.setAdapter(placesArrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                 intent.putExtra("PlaceNumber",position);
                startActivity(intent);

            }
        });
    }
}
