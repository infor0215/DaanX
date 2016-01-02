package com.dtf.daanx;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class PostContentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = this.getIntent().getExtras();
        toolbar.setTitle(bundle.getString("title"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView content_title=(TextView)findViewById(R.id.content_title);
        content_title.setText(bundle.getString("title"));
        TextView content_writer=(TextView)findViewById(R.id.content_writer);
        content_writer.setText(bundle.getString("writer"));
        TextView content_date=(TextView)findViewById(R.id.content_date);
        content_date.setText(bundle.getString("date"));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float d=metrics.widthPixels;

        String html="<style>" +
                "img{" +
                "display: inline;\n" +
                " height: auto;\n" +
                " max-width: 100%;}\n" +
                "html, body {\n" +
                "width:100%;\n" +
                "height: 100%;\n" +
                "margin: 0px;\n" +
                "padding: 0px;\n" +
                "}</style></head><body>"+bundle.getString("content");
        if(!bundle.getString("image").equals("")) {html+="<br><img src=\""+bundle.getString("image")+"\" alt=\"pageNo\">";}
        if(!bundle.getString("link").equals("")) {html+="<br>參考網址：<a href=\""+bundle.getString("link")+"\">"+bundle.getString("link")+"</a>";}
        if(!bundle.getString("file").equals("")) {
            html+="<br><br>附件檔案：<br>";
            String[] file=bundle.getString("file").split("\\|\\|\\|");
            for(int i=0;i<file.length;i++){
                if(!file[i].equals("")){
                    html+="<li><a href=\"http://drive.google.com/viewerng/viewer?embedded=true&url="+file[i].split("///")[1]+"\">"+file[i].split("///")[0]+"</a> <a href=\""+file[i].split("///")[1]+"\">下載</a></li>";
                }
            }
            //<a href=\""+bundle.getString("link")+"\">"+bundle.getString("link")+"</a>";
        }
        html+="</body></html>";

        final WebView webView=(WebView)findViewById(R.id.content_main);
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings=webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);//开启缩放支持
        settings.setBuiltInZoomControls(true);//开启缩放支持
        settings.setDisplayZoomControls(false); //隐藏webview缩放按钮
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("drive.google.com/viewerng/viewer?embedded=true")) {
                    Intent intent = new Intent();
                    intent.setClass(PostContentActivity.this, WebviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("src", url);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
                webView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        if(Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }


}
