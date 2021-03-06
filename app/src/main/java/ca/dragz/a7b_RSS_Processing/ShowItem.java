package ca.dragz.a7b_RSS_Processing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class ShowItem extends AppCompatActivity {

    Toolbar toolbar;

    private SharedPreferences sharedPreferences;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPubDate;
    private TextView tvLink;
    private TextView tvPrice;

    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        //make sure to use a theme with no action bar (set in your manifest)
        toolbar = findViewById(R.id.myToolbar2);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("RSS Processing", MODE_PRIVATE);

        color = getTextColor();

        Intent intent = getIntent();

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setTextColor(color);
        tvDescription = findViewById(R.id.tvDescription);
        tvDescription.setTextColor(color);
        tvPubDate = findViewById(R.id.tvPubDate);
        tvPubDate.setTextColor(color);
        tvLink = findViewById(R.id.tvLink);
        tvPrice = findViewById(R.id.tvPrice);
        tvPrice.setTextColor(color);

        tvTitle.setText(intent.getStringExtra("title"));
        tvDescription.setText(intent.getStringExtra("description"));
        tvPubDate.setText(intent.getStringExtra("pubDate"));
        tvLink.setText(intent.getStringExtra("link"));
        tvPrice.setText(intent.getStringExtra("price"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("A7a - RSS Processing");
    }

    //to inflate the xml menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    private int getTextColor() {
        int red = sharedPreferences.getInt("color_red", 0);
        int green = sharedPreferences.getInt("color_green", 0);
        int blue = sharedPreferences.getInt("color_blue", 0);

        return Color.rgb(red, green, blue);
    }
}
