package nl.group11.planplan;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

//TODO comments

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RestaurantsFragment.OnFragmentInteractionListener,
        EventsFragment.OnFragmentInteractionListener,
        OtherFragment.OnFragmentInteractionListener, SearchDialog.SearchListener, GPSTracker.LocationAdapter {

    GPSTracker gps;
    static String location = "52.370216,4.895168";
    static int radius = 5;

    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    EventsFragment eventsFragment;
    RestaurantsFragment restaurantsFragment;
    OtherFragment otherFragment;
    Snackbar noLocation;

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (!gps.canGetLocation()) { //directly add tabs and notify about location
            restaurantsFragment = new RestaurantsFragment();
            viewPagerAdapter.addFragment(restaurantsFragment, "Restaurants");
            EventsFragment eventsFragment = new EventsFragment();
            viewPagerAdapter.addFragment(eventsFragment, "Events");
            OtherFragment otherFragment = new OtherFragment();
            viewPagerAdapter.addFragment(otherFragment, "Other");
            gps.showSettingsAlert();
        } else {
            gps.addListener(this);
            noLocation = Snackbar.make(findViewById(R.id.rootLinearLayout), "Getting location...", Snackbar.LENGTH_INDEFINITE);
            noLocation.show();
        }
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent().getBooleanExtra("showDialog",false)) {
            showSearchDialog();
        }
    }

    @Override
    public void onLocation(Location location, GPSTracker tracker) {
        tracker.removeListener(this);
        if (noLocation.isShown()) {
            noLocation.dismiss();
        }
        HomeActivity.location = APIHandler.locationToLatLngString(location);
        restaurantsFragment = new RestaurantsFragment();
        viewPagerAdapter.addFragment(restaurantsFragment, "Restaurants");
        eventsFragment = new EventsFragment();
        viewPagerAdapter.addFragment(eventsFragment, "Events");
        otherFragment = new OtherFragment();
        viewPagerAdapter.addFragment(otherFragment, "Other");
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

    void showSearchDialog() {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        SearchDialog newSearchDialog = new SearchDialog(this);
        newSearchDialog.setGPS(gps);
        newSearchDialog.show(trans, "search");
    }

    @Override
    public void onSearch(String location, int radius) {

        HomeActivity.location = location;
        HomeActivity.radius = radius;

        //update event view
        RecyclerView eventsView = (RecyclerView) findViewById(R.id.eventsRecycler);
        if (eventsView != null) {
            eventsView.setLayoutManager(new LinearLayoutManager(this));
            eventsFragment.adapter = new EventfulAdapter(this, new EventfulDynamicSearch(location, radius));
            eventsView.setAdapter(eventsFragment.adapter);
        }

        //update restaurant view
        final RecyclerView restaurantView = (RecyclerView) findViewById(R.id.restaurantsRecycler);
        if (restaurantView != null) {
            restaurantView.setLayoutManager(new LinearLayoutManager(this));
            restaurantsFragment.adapter = new GooglePlacesAdapter(this, location, radius * 1000, "restaurant");
            restaurantView.setAdapter(restaurantsFragment.adapter);
        }

        //update other view
        final RecyclerView otherView = (RecyclerView) findViewById(R.id.otherRecycler);
        if (otherView != null) {
            otherView.setLayoutManager(new LinearLayoutManager(this));
            otherFragment.adapter = new GooglePlacesAdapter(this, location, radius * 1000, "night_club");
            otherView.setAdapter(otherFragment.adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String changedID = data.getExtras().getString("item");
        SingleItemUpdateListener fragment = (SingleItemUpdateListener) viewPagerAdapter.getItem(viewPager.getCurrentItem());
        fragment.singleItemUpdated(changedID);
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
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
