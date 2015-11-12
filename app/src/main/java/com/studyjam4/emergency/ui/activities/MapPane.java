package com.studyjam4.emergency.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.studyjam4.emergency.R;
import com.studyjam4.emergency.utilities.Place;
import com.studyjam4.emergency.utilities.PlacesList;

public class MapPane extends ActionBarActivity {
    GoogleMap mMap;
    PlacesList nearPlaces;
    double latitude,longitude;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_places);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Activity Slide animation
        this.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // Getting intent data
        Intent i = getIntent();
        // Users current geo location
        String user_latitude = i.getStringExtra("user_latitude");
        String user_longitude = i.getStringExtra("user_longitude");
        final LatLng user_location = new LatLng(Double.parseDouble(user_latitude),Double.parseDouble(user_longitude));
        // Nearplaces list
        nearPlaces = (PlacesList) i.getSerializableExtra("near_places");

        mMap.addMarker(new MarkerOptions()
                .position(user_location)
                .title("This is You")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.on)));

        // check for null in case it is null
        if (nearPlaces.results != null) {
            // loop through all the places
            for (Place place : nearPlaces.results) {
                latitude = place.geometry.location.lat; // latitude
                longitude = place.geometry.location.lng; // longitude
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(place.name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_accent)));
            }
        }

        // Move the camera instantly to Sydney with a zoom of 100.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 20));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //When Settings Menu is clicked
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}