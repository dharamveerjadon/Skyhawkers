package com.skyhawk.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.skyhawk.R;
import com.skyhawk.customview.SpinnerView;

public class WebViewActivity extends AppCompatActivity {

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView w =  findViewById(R.id.webview);
        SpinnerView spinnerView =  findViewById(R.id.spinner);
        if(getIntent() != null)
            url = getIntent().getStringExtra("resume_url");

        w.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);

        // this will enable the javascipt.
        w.getSettings().setJavaScriptEnabled(true);

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        w.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                spinnerView.setVisibility(View.GONE);
            }
        });
    }
}