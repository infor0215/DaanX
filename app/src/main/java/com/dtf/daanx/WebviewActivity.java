package com.dtf.daanx;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
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
        WebSettings settings=webView.getSettings();
        settings.setSupportZoom(true);//开启缩放支持
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);//开启缩放支持
        settings.setDisplayZoomControls(false); //隐藏webview缩放按钮
        webView.loadUrl(bundle.getString("src"));
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                WebviewActivity.this.setProgress(newProgress*100);
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams tL=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
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
        Log.i("status",bundle.getString("src"));
    }

}
