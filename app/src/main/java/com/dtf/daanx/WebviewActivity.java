package com.dtf.daanx;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle bundle = this.getIntent().getExtras();
        WebView webView=(WebView)findViewById(R.id.webView);
        webView.loadDataWithBaseURL(bundle.getString("src"), null, "text/html", "utf-8", null);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings=webView.getSettings();
        settings.setSupportZoom(true);//开启缩放支持
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);//开启缩放支持
        settings.setDisplayZoomControls(false); //隐藏webview缩放按钮
        //默认对缩放比例有限制，导致用户体验不好，所以需要设置为使用任意比例缩放。
        settings.setUseWideViewPort(true);
        //设置webView自适应手机屏幕
        settings.setLoadWithOverviewMode(true);
    }

}
