package com.example.studio08.kolhazmannewsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by studio08 on 2/16/2016.
 */
public class NetworkActivity extends Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static String urlString = "http://kolhazman.co.il/feed";

//    // Whether there is a Wi-Fi connection.
//    private static boolean wifiConnected = false;
//    // Whether there is a mobile connection.
//    private static boolean mobileConnected = false;
//    // Whether the display should be refreshed.
//    public static boolean refreshDisplay = true;
//    public static String sPref = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // Uses AsyncTask to download the XML feed from kolhazman.co.il.
//    public void loadPage() {
//
////        if ((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
////            new DownloadXmlTask().execute(urlString);
////        } else if ((sPref.equals(WIFI)) && (wifiConnected)) {
////            new DownloadXmlTask().execute(urlString);
////        } else {
////            // show error
////            new DownloadXmlTask().execute(urlString); // implemented it anyway
////        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadPage();
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
        downloadXmlTask.execute(urlString);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // I have to inflate article_row to access its Views
        LayoutInflater inflater = this.getLayoutInflater();
        View articleRow = inflater.inflate(R.layout.article_row, null);
        ImageView imageView = (ImageView) articleRow.findViewById(R.id.imageView);

        // Sample image to download and place in the imageView, not working right now
        new DownloadImageTask(imageView).execute("https://www.google.co.il/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Network Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.example.studio08.kolhazmannewsapp/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Network Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.example.studio08.kolhazmannewsapp/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if(entries == null)
                return;
            ListView listView = (ListView) findViewById(R.id.listView);
            NewsAdapter newsAdapter = new NewsAdapter(NetworkActivity.this, entries);
            listView.setAdapter(newsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(NetworkActivity.this,ArticleActivity.class);
                    intent.putExtra("link",entries.get(position).link);
                    startActivity(intent);
                }
            });

        }

        List<XmlParser.Item> entries;

        // Uploads XML from kolhazman.co.il/feed, parses it, and combines it with
        // HTML markup. Returns HTML string.
        private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            XmlParser xmlParser = new XmlParser();
            if(xmlParser != null) Log.d("parser", "new parser");

            // what is a StringBuilder and why do I have to return it?
            StringBuilder htmlString = new StringBuilder();

            try {
                stream = downloadUrl(urlString);
                if(stream != null)
                    Log.d("stream", stream.toString() + "");

                // XmlParser returns a List (called "entries") of Item objects.
                // Each Entry object represents a single post in the XML feed.
                entries = xmlParser.parse(stream);
                if(entries != null)
                    Log.d("entries", "size: " + entries.size() + "");

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null)
                    stream.close();
            }

            return htmlString.toString();
        }

        // Given a string representation of a URL, sets up a connection and gets an input stream.

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            // Starts the query
            connection.connect();
            return connection.getInputStream();
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
