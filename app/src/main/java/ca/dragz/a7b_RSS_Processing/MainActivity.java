package ca.dragz.a7b_RSS_Processing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//see https://developer.android.com/training/appbar

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    private final int STANDARD_REQUEST_CODE = 222;
    private final String KIJIJI_CARS_TRUCKS_URL = "https://www.kijiji.ca/rss-srp-cars-trucks/manitoba/c174l9006";
    private final String KIJIJI_PETS_URL = "https://www.kijiji.ca/rss-srp-pets/manitoba/c112l9006";
    private final String KIJIJI_VACATION_URL = "https://www.kijiji.ca/rss-srp-vacation-rentals/c800l9006";

    private String feed_name;
    private String feed_url;
    private String categoryImageName;

    private Boolean isSimpleView;
    private boolean descriptionEnabled;
    private boolean linkEnabled;
    private boolean priceEnabled;
    private boolean pubDateEnabled;

    private ListView lvTitles;
    private TextView tvCategory;

    private SharedPreferences sharedPreferences;

    private MyAsyncTask myAsyncTask;

    private NewsItem newsItem;
    private ArrayList<NewsItem> newsItemList;
    private NewsItemAdapter newsItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make sure to use a theme with no action bar (set in your manifest)
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("RSS Processing", MODE_PRIVATE);

        lvTitles = findViewById(R.id.lvTitles);

        tvCategory = findViewById(R.id.tvCategory);
        tvCategory.setTextColor(getTextColor());

        categoryImageName = sharedPreferences.getString("default_feed_image", "car_logo");
        feed_name = sharedPreferences.getString("default_feed", "Cars + Trucks");
        feed_url = getFeedURL();
        isSimpleView = getSimpleView();
        StartParsing();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
            startActivity(getIntent());
        } else {
            Log.d("DM", "Data is not okay!");
        }

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
                tvCategory.setText("Refreshing...");
                StartParsing();
                returnVal = true;
                break;
            case R.id.action_settings:
                Log.d("DGM", "settings menu item");
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, STANDARD_REQUEST_CODE);
                returnVal = true;
                break;
            case R.id.feed_cars_trucks:
                Log.d("DGM", "settings menu item");
                tvCategory.setText("Loading...");
                feed_name = "Cars + Trucks";
                categoryImageName = "car_logo";
                StartParsing();
                returnVal = true;
                break;
            case R.id.feed_pets:
                Log.d("DGM", "settings menu item");
                tvCategory.setText("Loading...");
                feed_name = "Pets";
                categoryImageName = "pet_logo";
                StartParsing();
                returnVal = true;
                break;
            case R.id.feed_vacations:
                Log.d("DGM", "settings menu item");
                tvCategory.setText("Loading...");
                feed_name = "Vacations";
                categoryImageName = "vacation_logo";
                StartParsing();
                returnVal = true;
                break;
        }
        return returnVal;
    }

    //AsyncTask to parse an RSS feed using a
    //SAXParser
    class MyAsyncTask extends AsyncTask {

        String feed = getFeedURL();

        //runs first - has access to the UI thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DGM", "onPreExecute");
        }

        //like the "main" method of the task -- does NOT have UI thread access
        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d("DGM", "doInBackground");

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = null;
            try {
                saxParser = spf.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            // create InputStream to connect to RSS resource at URL
            URL url = null;
            try {
                url = new URL(feed);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            InputStream inputStream = null;
            try {
                inputStream = url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // inputstream - what to parse
            // globalHandler (DefaultHandler) _ how to parse the data
            // create instance of DefaultHandler subclass
            GlobalHandler globalHandler = new GlobalHandler();
            try {
                saxParser.parse(inputStream, globalHandler);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return null;
        }

        //publish results of the task to the UI
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("DGM", "onPostExecute");

            newsItemAdapter = new NewsItemAdapter(MainActivity.this, R.layout.list_item, newsItemList);
            lvTitles.setAdapter(newsItemAdapter);
            updateListView();
        }
    }

    private void StartParsing() {
        boolean networkAvailable = isNetworkAvailable();

        if (networkAvailable) {

            descriptionEnabled = sharedPreferences.getBoolean("display_description", true);
            linkEnabled = sharedPreferences.getBoolean("display_link", true);
            priceEnabled = sharedPreferences.getBoolean("display_price", true);
            pubDateEnabled = sharedPreferences.getBoolean("display_pub_date", true);

            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();
        } else {
            Toast.makeText(this, "Network required to continue...", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //a sax handler that is designed to parse a CBC news feed
    class GlobalHandler extends DefaultHandler {

        private boolean inItem, inTitle, inDescription, inPubDate, inLink, inPrice;

        private StringBuilder stringBuilder;

        {
            newsItemList = new ArrayList<>(20);
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            Log.d("DGM", "startDocument");
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            Log.d("DGM", "endDocument");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            Log.d("DGM", "startElement: " + qName);

            stringBuilder = new StringBuilder(30);

            if(qName.equals("item")) {
                inItem = true;
                newsItem = new NewsItem("", "", "", "", "", "Please contact");
            }
            else if(qName.equals("title")) {
                inTitle = true;
            }
            else if(qName.equals("description")) {
                inDescription = true;
            }
            else if(qName.equals("pubDate")) {
                inPubDate = true;
            }
            else if(qName.equals("link")) {
                inLink = true;
            }
            else if(qName.equals("g-core:price")) {
                inPrice = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            Log.d("DGM", "endElement: " + qName);

            if (qName.equals("title") && !inItem) {
                tvCategory.setText(stringBuilder.toString());
            }
            if(qName.equals("item")) {
                inItem = false;
                newsItemList.add(newsItem);
                Log.d("DGMM", "Title: " + newsItem.getTitle());
                Log.d("DGMM", "Description: " + newsItem.getDescription());
                Log.d("DGMM", "pubDate: " + newsItem.getPubDate());
                Log.d("DGMM", "pubDate: " + newsItem.getLink());
                Log.d("DGMM", "price: " + newsItem.getPrice());
            }
            else if(qName.equals("title") && inItem) {
//                inItem = false;
                newsItem.setTitle(stringBuilder.toString());
            }
            else if(qName.equals("description") && inItem) {
//                inItem = false;
                newsItem.setDescription(stringBuilder.toString());
            }
            else if(qName.equals("pubDate") && inItem) {
//                inItem = false;
                newsItem.setPubDate(stringBuilder.toString());
            }
            else if(qName.equals("link") && inItem) {
//                inItem = false;
                newsItem.setLink(stringBuilder.toString());
            }
            else if(qName.equals("g-core:price") && inItem) {
//                inItem = false;
                newsItem.setPrice("$" + stringBuilder.toString() + "0");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            stringBuilder.append(ch, start, length);
        }
    }

    //custom ArrayAdapter for our ListView
    private class NewsItemAdapter extends ArrayAdapter<NewsItem> {

        private ArrayList<NewsItem> newsItems;

        public NewsItemAdapter(Context context, int textViewResourceId, ArrayList<NewsItem> newsItems) {
            super(context, textViewResourceId, newsItems);
            this.newsItems = newsItems;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            NewsItem o = newsItems.get(position);
            if (o != null) {
                TextView tt = v.findViewById(R.id.toptext);
                TextView bt = v.findViewById(R.id.bottomtext);

                ImageView img = v.findViewById(R.id.imgListItem);
                img.setImageDrawable(getCategoryImage(isSimpleView));

                if (tt != null) {
                    tt.setTextColor(getTextColor());
                    tt.setText(o.getTitle());
//                    tt.setText("Article Title");
                }
                if (bt != null && priceEnabled) {
//                    bt.setText("Age: " + o.getTitle());
                    bt.setText(o.getPrice());
                }
            }
            return v;
        }
    }

    private String getFeedURL() {

        String val = KIJIJI_CARS_TRUCKS_URL;

        switch (feed_name) {
            case "Pets":
                val = KIJIJI_PETS_URL;
                break;
            case "Vacations":
                val = KIJIJI_VACATION_URL;
                break;
        }
        return val;
    }

    private Boolean getSimpleView() {
        return sharedPreferences.getBoolean("simple_view", false);
    }

    private Drawable getCategoryImage(boolean isSimpleView) {

        Drawable image = getDrawable(R.drawable.car_logo);
        if (!isSimpleView) {
            switch (categoryImageName) {
                case "pet_logo":
                    image = getDrawable(R.drawable.pet_logo);
                    break;
                case "vacation_logo":
                    image = getDrawable(R.drawable.vacation_logo);
                    break;
            }
        } else {
            image = getDrawable(R.drawable.transparent_logo);
        }
        return image;
    }

    private int getTextColor() {
        int red = sharedPreferences.getInt("color_red", 0);
        int green = sharedPreferences.getInt("color_green", 0);
        int blue = sharedPreferences.getInt("color_blue", 0);

        return Color.rgb(red, green, blue);
    }

    private void updateListView() {
        //have listview respond to selected items
        lvTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {

                NewsItem item = (NewsItem) adapterView.getItemAtPosition(i);
                //we could start an activity to display details of the selected item
                Intent intent = new Intent(MainActivity.this, ShowItem.class);

                intent.putExtra("title", item.getTitle());
                intent.putExtra("description", descriptionEnabled ? item.getDescription() : "");
                intent.putExtra("pubDate", pubDateEnabled ? item.getPubDate() : "");
                intent.putExtra("link", linkEnabled ? item.getLink() : "");
                intent.putExtra("price", priceEnabled ? item.getPrice() : "");

                startActivityForResult(intent, STANDARD_REQUEST_CODE);
//                Toast.makeText(MainActivity.this, "Index: " + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class NewsItem {

        private String title;
        private String pubDate;
        private String description;
        private String category;
        private String link;
        private String price;

        private NewsItem(String title, String pubDate, String description, String category, String link, String price) {
            this.title = title;
            this.pubDate = pubDate;
            this.description = description;
            this.category = category;
            this.link = link;
            this.price = price;
        }
//        private NewsItem(String title) {
//            this.title = title;
//        }

        private String getTitle() {
            return title;
        }
        private String getPubDate() { return pubDate; }
        private String getDescription() { return description; }
        private String getCategory() { return category; }
        private String getLink() { return link; }
        private String getPrice() { return price; }

        private void setTitle(String title) { this.title = title; }
        private void setPubDate(String pubDate) { this.pubDate = pubDate; }
        private void setDescription(String description) { this.description = description; }
        private void setCategory(String category) { this.category = category; }
        private void setLink(String link) { this.link = link; }
        private void setPrice(String price) { this.price = price; }
    }
}
