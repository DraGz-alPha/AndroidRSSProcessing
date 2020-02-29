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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private RadioGroup radgrpDefaultFeed;
    private RadioButton radCarsTrucks;
    private RadioButton radPets;
    private RadioButton radVacations;

    private CheckBox chkDescription;
    private CheckBox chkLink;
    private CheckBox chkPrice;
    private CheckBox chkPubDate;

    private Switch swtSimpleView;

    boolean defaultLoad = true;
    boolean simpleViewModified = false;

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

        chkDescription = findViewById(R.id.chkDescription);
        chkDescription.setOnCheckedChangeListener(eventHandler);
        chkLink = findViewById(R.id.chkLink);
        chkLink.setOnCheckedChangeListener(eventHandler);
        chkPrice = findViewById(R.id.chkPrice);
        chkPrice.setOnCheckedChangeListener(eventHandler);
        chkPubDate = findViewById(R.id.chkPubDate);
        chkPubDate.setOnCheckedChangeListener(eventHandler);

        swtSimpleView = findViewById(R.id.swtSimpleView);
        swtSimpleView.setOnCheckedChangeListener(eventHandler);

        LoadDefaultFeed();
        LoadSimpleView();
        LoadDisplayAttributes();

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

    private class EventHandler implements RadioGroup.OnCheckedChangeListener, Switch.OnCheckedChangeListener {

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

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!defaultLoad) {
                Log.d("DGM", "chkDescriptionId: " + R.id.chkDescription);
                Log.d("DGM", "chkLinkId: " + R.id.chkLink);
                Log.d("DGM", "chkPriceId: " + R.id.chkPrice);
                Log.d("DGM", "chkPubDateId: " + R.id.chkPubDate);
                Log.d("DGM", "Button view: " + buttonView.getId());

                switch (buttonView.getId()) {
                    case R.id.swtSimpleView:
                        SaveSimpleView(isChecked);
                        break;
                    case R.id.chkDescription:
                        SaveDisplayField("display_description", "Description", isChecked);
                        Log.d("DGM", "Description " + isChecked);
                        break;
                    case R.id.chkLink:
                        SaveDisplayField("display_link", "Link", isChecked);
                        Log.d("DGM", "Link " + isChecked);
                        break;
                    case R.id.chkPrice:
                        SaveDisplayField("display_price", "Price", isChecked);
                        Log.d("DGM", "Price " + isChecked);
                        break;
                    case R.id.chkPubDate:
                        SaveDisplayField("display_pub_date", "Publish Date", isChecked);
                        Log.d("DGM", "PubDate " + isChecked);
                        break;
                }
            }
        }
    }

    private void SaveDefaultFeed(String feed, String image) {
        editor = sharedPreferences.edit();
        editor.putString("default_feed", feed);
        editor.putString("default_feed_image", image);

        boolean successful = editor.commit();
        if (successful) {
            Toast.makeText(this, "Default feed updated to " + feed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the default feed...", Toast.LENGTH_LONG).show();
        }
    }

    private void SaveSimpleView(boolean isEnabled) {
        editor = sharedPreferences.edit();
        editor.putBoolean("simple_view", isEnabled);

        String state = isEnabled == true ? "enabled" : "disabled";
        boolean successful = editor.commit();

        if (successful) {
            Toast.makeText(this, "Simple view has been " + state, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the default feed...", Toast.LENGTH_LONG).show();
        }

        simpleViewModified = true;
    }

    private void SaveDisplayField(String name, String displayText, boolean isEnabled) {
        editor = sharedPreferences.edit();
        editor.putBoolean(name, isEnabled);

        String state = isEnabled == true ? "enabled" : "disabled";
        boolean successful = editor.commit();

        if (successful) {
            Toast.makeText(this, displayText + " attribute has been " + state, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the " + name + "...", Toast.LENGTH_LONG).show();
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

    private void LoadSimpleView() {
        boolean isEnabled = sharedPreferences.getBoolean("simple_view", false);
        swtSimpleView.setChecked(isEnabled);
    }

    private void LoadDisplayAttributes() {

        boolean descriptionEnabled = sharedPreferences.getBoolean("display_description", true);
        boolean linkEnabled = sharedPreferences.getBoolean("display_link", true);
        boolean priceEnabled = sharedPreferences.getBoolean("display_price", true);
        boolean pubDateEnabled = sharedPreferences.getBoolean("display_pub_date", true);

        chkDescription.setChecked(descriptionEnabled);
        chkLink.setChecked(linkEnabled);
        chkPrice.setChecked(priceEnabled);
        chkPubDate.setChecked(pubDateEnabled);
    }

    @Override
    public void onBackPressed() {

        Intent data = new Intent();

        data.putExtra("simple_view_modified", simpleViewModified);

        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
