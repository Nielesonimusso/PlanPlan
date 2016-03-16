package nl.group11.planplan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject json;
    TextView titleText, dateText, descriptionText, priceText, addressText;
    ImageView imgView;
    Item i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        titleText = (TextView) findViewById(R.id.title);
        dateText = (TextView) findViewById(R.id.startEndTime);
        descriptionText = (TextView) findViewById(R.id.description);
        priceText = (TextView) findViewById(R.id.price);
        addressText = (TextView) findViewById(R.id.address);
        imgView = (ImageView) findViewById(R.id.image);

        JSONParser parser = new JSONParser();
        try {
            json = (JSONObject) parser.parse(getIntent().getStringExtra("json"));
        } catch (Exception e) {

        }

        if (json.toString().contains(Type.EVENT.toString())) {
            i = new EventItem(json);
        } else {
            if (json.toString().contains(Type.RESTAURANT.toString())) {
                i = new RestaurantItem(json);
            } else {
                i = new OtherItem(json);
            }
        }

        titleText.setText(i.getTitle());
        dateText.setText(df.format(i.getStartTime())+ " till " + df.format(i.getEndTime()));
        descriptionText.setText(Html.fromHtml(i.getDescription()));
        priceText.setText(i.getPrice());
        addressText.setText(i.getAddress());
        //TODO set image
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
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("showDialog", true);
            startActivity(intent);

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
}
