package nl.group11.planplan;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RestaurantsFragment.OnFragmentInteractionListener,
        EventsFragment.OnFragmentInteractionListener,
        OtherFragment.OnFragmentInteractionListener, DynamicSearch.SearchUpdateListener {

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_home);
        gps = new GPSTracker(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new RestaurantsFragment(), "Restaurants");
        viewPagerAdapter.addFragment(new EventsFragment(), "Events");
        viewPagerAdapter.addFragment(new OtherFragment(), "Other");
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            EVENTFUL APIHandler TESTS
         /
        //direct search test
        APIHandler.queryEventful("Eindhoven", 50, 3, new APIHandler.Callback<List<EventfulEvent>>() {
            @Override
            public void onItem(List<EventfulEvent> results) {
                System.out.println("printing eventful item title test");
                for (EventfulEvent event : results) {
                    System.out.println(event.getTitle());
                }
                System.out.println("end of printing test");
            }
        });

        //dynamic search test
        EventfulDynamicSearch dynamicSearch = new EventfulDynamicSearch("Eindhoven", 50);
        dynamicSearch.addListener(this);
        EventfulEvent event = dynamicSearch.get(25);
        System.out.println("First get: " + event);

        //latlong from string test
        APIHandler.stringToLocation("Eindhoven", new APIHandler.Callback<Location>() {
            @Override
            public void onItem(Location result) {
                System.out.println(APIHandler.locationToLatLngString(result));
            }
        });

        /*
            GOOGLEPLACES APIHandler TESTS
         /
        //direct search test (including location query)
        APIHandler.stringToLocation("Eindhoven", new APIHandler.Callback<Location>() {
            @Override
            public void onItem(Location result) {
                APIHandler.queryGooglePlaces(APIHandler.locationToLatLngString(result), 50000, "restaurant", new APIHandler.Callback<List<GooglePlace>>() {
                    @Override
                    public void onItem(List<GooglePlace> result) {
                        System.out.println("printing googleplaces item title test");
                        for (GooglePlace place : result) {
                            System.out.println(place);
                        }
                        System.out.println("end of printing test");
                    }
                });
            }
        });
        //*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //show search fragment
            showSearchDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Go to home activity
        } else if (id == R.id.nav_favorites) {
            // Go to favorites activity
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_planning) {
            // Go to planning activity
            Intent intent = new Intent(this, PlanningActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUpdate(DynamicSearch self, int start, int end) {
        System.out.println("Range from event:" + start + ", " + end);
        if (start <= 25 && end >= 25) {
            EventfulEvent event = ((DynamicSearch<EventfulEvent>) self).get(25);
            System.out.println("Second get from event: " + event);
        } else {
            System.out.println("invalid range");
        }
    }

    void showSearchDialog() {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        SearchDialog newSearchDialog = new SearchDialog();
        newSearchDialog.setGPS(gps);
        newSearchDialog.show(trans, "search");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
