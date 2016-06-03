package me.crafter.android.zjsnviewer;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Webactivity extends Activity{

    @BindView(R.id.web_view)
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.layout_web);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("URL");
        WebSettings webSettings = webView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        webView.loadUrl(url);
    }
}
