package com.studyjam4.emergency.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.studyjam4.emergency.R;
import com.studyjam4.emergency.utilities.AlertDialogManager;
import com.studyjam4.emergency.utilities.ConnectionDetector;
import com.studyjam4.emergency.utilities.GPSTracker;
import com.studyjam4.emergency.utilities.GooglePlaces;
import com.studyjam4.emergency.utilities.Place;
import com.studyjam4.emergency.utilities.PlacesList;

import java.util.ArrayList;
import java.util.HashMap;


public class HospitalListActivity extends ActionBarActivity {

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Places List
    PlacesList nearPlaces;

    // GPS Location
    GPSTracker gps;

    // Button
    FloatingActionButton btnShowOnMap;

    // Progress dialog
    ProgressDialog pDialog;

    //Material Dialog
    MaterialDialog mDialog;

    // Places Listview
    ListView lv;

    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name
    public static String PLACES_TYPE; // Listing places only cafes, restaurants
    public static int radius;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        //Activity toolbar setup
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            mDialog = new MaterialDialog.Builder(this)
                    .title("Connection Error")
                    .content("Could not connect to the internet check network settings and try again")
                    .positiveText("SETTINGS")
                    .negativeText("BACK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    })
                    .show();
            return;
        }

        // creating GPS Class object
        gps = new GPSTracker(this);

        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            mDialog = new MaterialDialog.Builder(this)
                    .title("Location Error")
                    .content("Could not get user Location check GPS settings and try again")
                    .positiveText("SETTINGS")
                    .negativeText("BACK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    })
                    .show();
            return;
        }

        // Getting listview
        lv = (ListView) findViewById(R.id.list);


        // button show on map
        btnShowOnMap = (FloatingActionButton) findViewById(R.id.btn_show_map);

        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        new LoadPlaces().execute();

        /** Button click event for shown on map */
        btnShowOnMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(),
                        MapPane.class);
                // Sending user current geo location
                i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
                i.putExtra("user_longitude", Double.toString(gps.getLongitude()));

                // passing near places to map activity
                i.putExtra("near_places", nearPlaces);
                // staring activity
                startActivity(i);
            }
        });


        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SinglePlaceActivity.class);

                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Background Async Task to Load Google places
     */
    class LoadPlaces extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pDialog = new ProgressDialog(HospitalListActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Searching</b><br/>Please Wait"));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show(); */

            mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                    .content(R.string.dialog_title)
                    .widgetColorRes(R.color.accentColor)
                    .progress(true, 0)
                    .show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();

            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                // Check list of types supported by google
                //

                // Radius in meters - increase this value if you don't find any places
                radius = 10000; // 1000 meters

                // get nearest places
                nearPlaces = googlePlaces.search(gps.getLatitude(),
                        gps.getLongitude(), radius, PLACES_TYPE);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            mDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;

                    // Check for all possible status
                    if (status.equals("OK")) {
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Place p : nearPlaces.results) {
                                HashMap<String, String> map = new HashMap<String, String>();

                                // Place reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.reference);

                                // Place name
                                map.put(KEY_NAME, p.name);

                                // adding HashMap to ArrayList
                                placesListItems.add(map);
                            }
                            // list adapter
                            ListAdapter adapter = new SimpleAdapter(HospitalListActivity.this, placesListItems,
                                    R.layout.hospital_list_item,
                                    new String[]{KEY_REFERENCE, KEY_NAME}, new int[]{
                                    R.id.reference, R.id.name});

                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                    } else if (status.equals("ZERO_RESULTS")) {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Sorry no places found. Try to increase search radius from settings")
                                .show();
                    } else if (status.equals("UNKNOWN_ERROR")) {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Sorry unknown error occured. Try again")
                                .show();
                    } else if (status.equals("OVER_QUERY_LIMIT")) {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Query over limit. Check back in a few minutes")
                                .show();
                    } else if (status.equals("REQUEST_DENIED")) {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Sorry error occured. Request denied")
                                .show();
                    } else if (status.equals("INVALID_REQUEST")) {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Sorry error occured. Request invalid")
                                .show();
                    } else {
                        mDialog = new MaterialDialog.Builder(HospitalListActivity.this)
                                .title("Near Places")
                                .content("Sorry, Unknown error occured")
                                .show();
                    }
                }
            });

        }

    }


}

