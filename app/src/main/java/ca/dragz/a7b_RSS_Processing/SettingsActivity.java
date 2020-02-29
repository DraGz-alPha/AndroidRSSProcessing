package ca.dragz.a7b_RSS_Processing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    Toolbar toolbar;

    RadioGroup radgrpDefaultFeed;
    RadioButton radCarsTrucks;
    RadioButton radPets;
    RadioButton radVacations;

    boolean defaultLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //make sure to use a theme with no action bar (set in your manifest)
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("RSS Processing", MODE_PRIVATE);

        EventHandler eventHandler = new EventHandler();

        radgrpDefaultFeed = findViewById(R.id.radgrpDefaultFeed);
        radgrpDefaultFeed.setOnCheckedChangeListener(eventHandler);

        radCarsTrucks = findViewById(R.id.radCarsTrucks);
        radPets = findViewById(R.id.radPets);
        radVacations = findViewById(R.id.radVacations);

        LoadDefaultFeed();
        defaultLoad = false;
    }

    //to inflate the xml menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    //to handle events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        boolean returnVal = false;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d("DGM", "refresh menu item");
                returnVal = true;
                break;
            case R.id.action_settings:
                Log.d("DGM", "settings menu item");
                returnVal = true;
                break;
        }
        return returnVal;
    }

    private class EventHandler implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!defaultLoad) {
                switch (checkedId) {
                    case R.id.radCarsTrucks:
                        SaveDefaultFeed("Cars + Trucks", "car_logo");
                        Log.d("DGM", "Cars + Trucks");
                        break;
                    case R.id.radPets:
                        SaveDefaultFeed("Pets", "pet_logo");
                        Log.d("DGM", "Pets");
                        break;
                    case R.id.radVacations:
                        SaveDefaultFeed("Vacations", "vacation_logo");
                        Log.d("DGM", "Vacations");
                        break;
                }
            }
        }
    }

    private void SaveDefaultFeed(String feed, String image) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("default_feed", feed);
        editor.putString("default_feed_image", image);

        boolean successful = editor.commit();
        if (successful) {
            Toast.makeText(this, "Default feed updated to " + feed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the default feed...", Toast.LENGTH_LONG).show();
        }
    }

    private void LoadDefaultFeed() {

        String feed = sharedPreferences.getString("default_feed", "Cars + Trucks");

        switch (feed) {
            case "Cars + Trucks":
                radCarsTrucks.toggle();
                break;
            case "Pets":
                radPets.toggle();
                break;
            case "Vacations":
                radVacations.toggle();
                break;
            default:
                break;
        }
    }
}
