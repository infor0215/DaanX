package com.dtf.daanx;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        final ProgressBar bar = (ProgressBar)findViewById(R.id.myProgressBar);

        Bundle bundle = this.getIntent().getExtras();
        final WebView webView=(WebView)findViewById(R.id.webView);
        if(Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(true);//开启缩放支持
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);//开启缩放支持
        webView.getSettings().setDisplayZoomControls(false); //隐藏webview缩放按钮
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams tL = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    webView.setLayoutParams(tL);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
                webView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        webView.loadUrl(bundle.getString("src"));
    }

}
