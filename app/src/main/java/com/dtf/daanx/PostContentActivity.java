package com.dtf.daanx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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

        String html=bundle.getString("content");
        if(!bundle.getString("image").equals("")) {html+="<br><img src=\""+bundle.getString("image")+"\" alt=\"pageNo\" height=\"100%\" width=\"100%\">";}
        if(!bundle.getString("link").equals("")) {html+="<br>參考網址：<a href=\""+bundle.getString("link")+"\">"+bundle.getString("link")+"</a>";}
        if(!bundle.getString("file").equals("")) {
            html+="<br>附件檔案：<br>";
            String[] file=bundle.getString("file").split("\\|\\|\\|");
            for(int i=0;i<file.length;i++){
                if(!file[i].equals("")){
                    html+="<a href=\"http://drive.google.com/viewerng/viewer?embedded=true&url="+file[i].split("\\/\\/\\/")[0]+"\>"+file[i].split("\\/\\/\\/")[1]+"</a><br>";
                }
            }
            //<a href=\""+bundle.getString("link")+"\">"+bundle.getString("link")+"</a>";
        }

        WebView webView=(WebView)findViewById(R.id.content_main);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings=webView.getSettings();
        settings.setSupportZoom(true);//开启缩放支持
        settings.setBuiltInZoomControls(true);//开启缩放支持
        settings.setDisplayZoomControls(false); //隐藏webview缩放按钮
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent();
                intent.setClass(PostContentActivity.this, WebviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("src", url);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
        //Toast.makeText(this,html,Toast.LENGTH_LONG).show();
    }


}
