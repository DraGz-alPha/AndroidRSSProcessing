package ca.dragz.a7b_RSS_Processing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private TextView tvSettingsTitle;
    private TextView tvDefaultFeed;
    private TextView tvFieldsToDisplay;
    private TextView tvTextColor;
    private TextView tvRed;
    private TextView tvGreen;
    private TextView tvBlue;

    private RadioGroup radgrpDefaultFeed;
    private RadioButton radCarsTrucks;
    private RadioButton radPets;
    private RadioButton radVacations;

    private CheckBox chkDescription;
    private CheckBox chkLink;
    private CheckBox chkPrice;
    private CheckBox chkPubDate;

    private Switch swtSimpleView;

    private EditText etRed;
    private EditText etGreen;
    private EditText etBlue;

    private Button btnUpdateColor;

    private int color;

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

        color = getTextColor();

        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);
        tvSettingsTitle.setTextColor(color);

        tvDefaultFeed = findViewById(R.id.tvDefaultFeed);
        tvDefaultFeed.setTextColor(color);

        tvFieldsToDisplay = findViewById(R.id.tvFieldsToDisplay);
        tvFieldsToDisplay.setTextColor(color);

        tvTextColor = findViewById(R.id.tvTextColor);
        tvTextColor.setTextColor(color);

        tvRed = findViewById(R.id.tvRed);
        tvRed.setTextColor(color);

        tvGreen = findViewById(R.id.tvGreen);
        tvGreen.setTextColor(color);

        tvBlue = findViewById(R.id.tvBlue);
        tvBlue.setTextColor(color);

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

        etRed = findViewById(R.id.etRed);
        etGreen = findViewById(R.id.etGreen);
        etBlue = findViewById(R.id.etBlue);

        btnUpdateColor = findViewById(R.id.btnUpdateColor);
        btnUpdateColor.setOnClickListener(eventHandler);

        LoadDefaultFeed();
        LoadSimpleView();
        LoadDisplayAttributes();
        LoadTextColor();

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
                returnVal = true;
                break;
            case R.id.action_settings:
                Log.d("DGM", "settings menu item");
                returnVal = true;
                break;
        }
        return returnVal;
    }

    private class EventHandler implements RadioGroup.OnCheckedChangeListener, Switch.OnCheckedChangeListener, Button.OnClickListener {

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

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnUpdateColor:
                    SaveTextColor();
                    break;
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

    private void SaveTextColor() {
        editor = sharedPreferences.edit();

        boolean redValid = etRed.getText().toString().trim().length() > 0;
        boolean greenValid = etGreen.getText().toString().trim().length() > 0;
        boolean blueValid = etBlue.getText().toString().trim().length() > 0;

        if (redValid && greenValid && blueValid) {
            int red = Integer.parseInt(etRed.getText().toString());
            int green = Integer.parseInt(etGreen.getText().toString());
            int blue = Integer.parseInt(etBlue.getText().toString());

            if (red < 256 && green < 256 && blue < 256) {
                editor.putInt("color_red", red);
                editor.putInt("color_green", green);
                editor.putInt("color_blue", blue);

                boolean successful = editor.commit();

                if (successful) {
                    Toast.makeText(this, "Values R:" + red + " G: " + green + " B: " + blue + " have been updated... Color will updated on next refresh!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sorry, there was a problem updating the text color...", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Values cannot exceed 255!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Values cannot be empty!", Toast.LENGTH_LONG).show();
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

    private void LoadTextColor() {
        int red = sharedPreferences.getInt("color_red", 0);
        int green = sharedPreferences.getInt("color_green", 0);
        int blue = sharedPreferences.getInt("color_blue", 0);

        etRed.setText(Integer.toString(red));
        etGreen.setText(Integer.toString(green));
        etBlue.setText(Integer.toString(blue));
    }

    private int getTextColor() {
        int red = sharedPreferences.getInt("color_red", 0);
        int green = sharedPreferences.getInt("color_green", 0);
        int blue = sharedPreferences.getInt("color_blue", 0);

        return Color.rgb(red, green, blue);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}
