package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
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
        SharedPreferences sharedPreferences = this.getSharedPreferences("package com.example.myapplication",MODE_PRIVATE);
        ArrayList<String>latitude= new ArrayList<>();
        ArrayList<String>longitude= new ArrayList<>();
        try {
            places= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitude=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(places.size()>0 && latitude.size()>0 && longitude.size()>0)
        {
            if(places.size()== latitude.size() && latitude.size()== longitude.size())
            {
                for ( int i=0 ;i<latitude.size();i++)
                {
                    placesCoordinates.add(new LatLng( Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }
        else {

            places.add("Please Add new Place .....");
            placesCoordinates.add(new LatLng(0, 0));
        }
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
