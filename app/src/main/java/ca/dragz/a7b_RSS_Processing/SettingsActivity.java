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

        loadDefaultFeed();
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
            switch (checkedId) {
                case R.id.radCarsTrucks:
                    SaveDefaultFeed("Cars + Trucks");
                    Log.d("DGM", "Cars + Trucks");
                    break;
                case R.id.radPets:
                    SaveDefaultFeed("Pets");
                    Log.d("DGM", "Pets");
                    break;
                case R.id.radVacations:
                    SaveDefaultFeed("Vacations");
                    Log.d("DGM", "Vacations");
                    break;
            }
        }
    }

    private void SaveDefaultFeed(String feed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("default_feed", feed);

        boolean successful = editor.commit();
        if (successful) {
            Toast.makeText(this, "Default feed updated to " + feed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the default feed...", Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {

//        if (etName.toString().trim().length() > 0 && etAge.getText().toString().trim().length() > 0) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            String name = etName.getText().toString();
//            int age = Integer.parseInt(etAge.getText().toString());
//            editor.putString("name", name);
//            editor.putInt("age", age);
//
//            boolean successful = editor.commit();
//            if (successful) {
//                Toast.makeText(this, "Data save successful", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Sorry, there was a problem saving your data...", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(this, "Please enter required fields before attempting to save!", Toast.LENGTH_LONG).show();
//        }
    }

    private void loadDefaultFeed() {

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
