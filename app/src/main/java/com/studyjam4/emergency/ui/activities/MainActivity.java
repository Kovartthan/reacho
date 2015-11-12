package com.studyjam4.emergency.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.studyjam4.emergency.R;


public class MainActivity extends ActionBarActivity {
    //Initialize text views used to set fonts..
    //Button hospital,education,shopping,sports,travel,services;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);


        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        //find drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);




        //Change the font types of text view for icons
       /* hospital = (Button) findViewById(R.id.hospital);
        education = (Button) findViewById(R.id.education);
        shopping = (Button) findViewById(R.id.shopping);
        sports = (Button) findViewById(R.id.sports);
        travel = (Button) findViewById(R.id.travel);
        services = (Button) findViewById(R.id.services);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        hospital.setTypeface(font);
        education.setTypeface(font);
        shopping.setTypeface(font);
        sports.setTypeface(font);
        travel.setTypeface(font);
        services.setTypeface(font); */


    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    //Setting up the drawer content
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //selectDrawerItem(menuItem);
                        switch(menuItem.getItemId()) {
                            case R.id.review_menu:
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                break;
                            case R.id.settings_menu:
                                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                                break;
                            case R.id.share_menu:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Nearest Places App for your smartphone. Download it today from -----link-----");
                                shareIntent.setType("text/plain");
                                startActivity(shareIntent);
                                break;
                            case R.id.info_menu:

                                break;
                            default:
                                //fragmentClass = FirstFragment.class;
                        }
                        return true;
                    }
                });
    }

    /**public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                startIntent(new Intent(this,MainActivity.class);
                break;
            case R.id.nav_second_fragment:
                //fragmentClass = SecondFragment.class;
                break;
            case R.id.nav_third_fragment:
                //fragmentClass = ThirdFragment.class;
                break;
            default:
                //fragmentClass = FirstFragment.class;
        }

      /**  try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    } */


    //Implement click event handler methods
    public void onHospitalClick(View v) {
        HospitalListActivity.PLACES_TYPE = "health|hospital|pharmacy|doctor|dentist";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onSchoolClick(View v) {
        HospitalListActivity.PLACES_TYPE = "school|university|library|physiotherapist";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onSportsClick(View v) {
        HospitalListActivity.PLACES_TYPE = "gym|stadium";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onShoppingClick(View v) {
        HospitalListActivity.PLACES_TYPE = "bicycle_store|book_store|clothing_store|convenience_store|department_store|electronics_store|furniture_store|hardware_store|home_goods_store|jewelry_store|liquor_store|pet_store|shoe_store|shopping_mall";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onTravelClick(View v) {
        HospitalListActivity.PLACES_TYPE = "train_station|travel_agency|subway_station|airport|embassy";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onRestaurantClick(View v) {
        HospitalListActivity.PLACES_TYPE = "restaurant|meal_delivery";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onFinanceClick(View v) {
        HospitalListActivity.PLACES_TYPE = "accounting|bank|atm|finance";
        startActivity(new Intent(this,HospitalListActivity.class));
    }

    public void onReligionClick(View v) {
        HospitalListActivity.PLACES_TYPE = "church|hindu_temple|mosque";
        startActivity(new Intent(this,HospitalListActivity.class));
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
        //when home icon is clicked
        if(id == R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        //When Settings Menu is clicked
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        if (id == R.id.share_app) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Nearest Places App for your smartphone. Download it today from -----link-----");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
