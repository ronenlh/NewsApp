package com.example.studio08.kolhazmannewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hackeru on 18/02/2016.
 */
public class NewsAdapter extends BaseAdapter {

    List<XmlParser.Item> items;
    Context context;

    public NewsAdapter(Context context, List<XmlParser.Item> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public XmlParser.Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = null;
        XmlParser.Item current = (XmlParser.Item) getItem(position);
        Holder holder;

        if (convertView == null) {
            holder = new Holder();
            row = inflater.inflate(R.layout.article_row, null);
            holder.titleTextView = (TextView) row.findViewById(R.id.titleTextView);
            holder.descriptionTextView = (TextView) row.findViewById(R.id.descriptionTextView);
//            holder.featuredImage = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (Holder) row.getTag();
        }

        holder.titleTextView.setText(current.title);
        holder.descriptionTextView.setText(current.summary);
//        holder.featuredImage.setImageResource();
        return row;
    }

    private class Holder {
        TextView titleTextView;
        TextView descriptionTextView;
//        ImageView featuredImage;
    }
}
