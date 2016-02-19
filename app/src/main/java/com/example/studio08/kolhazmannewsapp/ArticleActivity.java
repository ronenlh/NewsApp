package com.example.studio08.kolhazmannewsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView webView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        String url = intent.getStringExtra("link");
        webView.loadUrl(url);
    }
}
