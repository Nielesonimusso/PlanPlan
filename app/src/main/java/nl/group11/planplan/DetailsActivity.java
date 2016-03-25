package nl.group11.planplan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DetailsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject json; //holds the JSON needed to build the item to be displayed.
    TextView titleText, dateText, descriptionText, priceText, addressText;
    CardView favoritesButton, planningButton;
    ImageView imgView;
    LinearLayout layout;
    Item i; //The item to be displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //find UI elements
        titleText = (TextView) findViewById(R.id.title);
        dateText = (TextView) findViewById(R.id.startEndTime);
        descriptionText = (TextView) findViewById(R.id.description);
        priceText = (TextView) findViewById(R.id.price);
        addressText = (TextView) findViewById(R.id.address);
        imgView = (ImageView) findViewById(R.id.image);
        layout = (LinearLayout) findViewById(R.id.infoLayout);
        favoritesButton = (CardView) findViewById(R.id.addFavoritesButtonDetails);
        planningButton = (CardView) findViewById(R.id.addPlanningButtonDetails);

        //Get the item json from the intent
        JSONParser parser = new JSONParser();
        try {
            json = (JSONObject) parser.parse(getIntent().getStringExtra("json"));
        } catch (Exception e) {

        }
        json.toString();

        //determine type of item
        if (json.toString().contains(Type.EVENT.toString())) {
            i = new EventItem(json, this);
        } else {
            if (json.toString().contains(Type.RESTAURANT.toString())) {
                i = new RestaurantItem(json, this);
            } else {
                i = new OtherItem(json, this);
            }
        }

        //display item
        titleText.setText(i.getTitle());
        setTitle(i.getTitle());

        if (i.getStartTime() == null || i.getEndTime() == null) {
            layout.removeView(dateText);
        } else if (i.getStartTime().equals(i.getEndTime())) {
            dateText.setText(df.format(i.getStartTime()));
        } else {
            dateText.setText(df.format(i.getStartTime()) + " till " + df.format(i.getEndTime()));
        }

        if (i.getType().equals(Type.EVENT)){
            descriptionText.setText(Html.fromHtml(i.getDescription()));
        } else {
            layout.removeView(descriptionText);
        }
        priceText.setText(i.getPrice());
        addressText.setText(i.getAddress());

        if(i.hasPassed()) {
            favoritesButton.setOnClickListener(null);
            planningButton.setOnClickListener(null);
            favoritesButton.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayedOut));
            planningButton.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayedOut));
        } else {
            favoritesButton.setOnClickListener(i);
            planningButton.setOnClickListener(i);
            favoritesButton.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cardview_light_background));
            planningButton.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.cardview_light_background));
        }

        final String imgUrl = i.getImage();
        imgView.setImageResource(R.drawable.imgnotfound);
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    System.out.println("Loading image with url " + imgUrl);
                    URL url = new URL(imgUrl);
                    return BitmapFactory.decodeStream(url.openStream());
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imgView.setImageBitmap(bitmap);
                }
            }
        }.execute();

        //update buttons to represent state in database
        i.checkInFavorites(new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {
                if (result) {
                    ((TextView) favoritesButton.getChildAt(0)).setText("Remove from favorites");
                } else {
                    ((TextView) favoritesButton.getChildAt(0)).setText("Add to favorites");
                }
            }
        });

        i.checkInPlanning(new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {
                if (result) {
                    ((TextView) planningButton.getChildAt(0)).setText("Remove from planning");
                } else {
                    ((TextView) planningButton.getChildAt(0)).setText("Add to planning");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent intent = new Intent();
            intent.putExtra("item",i.getID());
            setResult(RESULT_OK, intent);
            finish();
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
