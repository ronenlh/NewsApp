package com.example.studio08.kolhazmannewsapp;

import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by studio08 on 2/16/2016.
 */
public class XmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            if(parser != null) Log.d("parser", "Xml.newPullParser() ");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            Log.d("parser", " parser.setFeature(...) ");
            parser.setInput(in, null);
            Log.d("parser", " parser.setInput(...) ");
            parser.nextTag();
            Log.d("parser", " parser.nextTag(...) ");
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        // Test if the current event is of the given type and if the namespace and name do match.
        // null will match any namespace and any name. If the test is not passed, an exception is thrown.
        // The exception text indicates the parser position, the expected event and the current event that is not meeting the requirement.
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        Log.d("parser", " parser.require(...) ");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            Log.d("parser", " NAME: "+name+" ");
            if (name.equals("channel")){

            } else if (name.equals("item")) {
                entries.add(readEntry(parser));
                Log.d("entries", " entries.add(...)");
            } else {
                skip(parser);
                Log.d("readFeed", " skip(parser)");
            }
        }
        return entries;
    }

    public static class Item {
        public final String title;
        public final String link;
        public final String summary;
        public final String imageLink;

        private Item(String title, String summary, String link, String imageLink) {
            this.title = title;
            this.summary = summary;
            this.link = link;
            this.imageLink = imageLink;
        }

    }

    // Parses the contents of an item. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Item readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("readEntry", " Entered readEntry");
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String link = null;
        String imageLink = null;
        String[] descriptionArray = new String[2];
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("readEntry","name: " + name + "");
            if (name.equals("title")) {
                title = readTitle(parser);
                Log.d("readEntry", title);
            } else if (name.equals("description")) {
                System.arraycopy(readDescription(parser), 0, descriptionArray, 0, 2);
                imageLink = descriptionArray[0];
                description = descriptionArray[1];
                Log.d("readEntry", description);
            } else if (name.equals("link")) {
                link = readLink(parser);
                Log.d("readEntry", link);
            } else {
                Log.d("readEntry", name + " skipped");
                skip(parser);
            }
        }
        return new Item(title, description, link, imageLink);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    Pattern pattern;
    Matcher matcher;

    private String[] readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String rawDescription = readText(parser);
        String[] result = new String[2];
        Log.d("readDescription", "1: " + rawDescription);

        result[0] = "";
        pattern = Pattern.compile("src=\"(.*?)\"");
        matcher = pattern.matcher(rawDescription);
        if (matcher.find()) result[0] = matcher.group(1);
        Log.d("readDescription", "2: " + result[0]);

        // not working:
        result[1] = "";
        pattern = Pattern.compile("<p>(.*)(<\\/p>)");
        matcher = pattern.matcher(rawDescription);
        if (matcher.find()) result[1] = matcher.group(1);
        Log.d("readDescription", "3: " + result[1]);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return result;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            Log.d("skip", " IllegalStateException ");
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    Log.d("skip", " XmlPullParser.END_TAG: depth:"+depth+ " name: </"+ parser.getName()+"> ");
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    Log.d("skip", " XmlPullParser.START_TAG: depth:"+depth+ " name: <"+ parser.getName()+">");
                    depth++;
                    break;
            }
        }
    }
}
