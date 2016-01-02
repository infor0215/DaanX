package com.dtf.daanx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ForumContentActivity extends BaseActivity {

    SharedPreferences preference;
    private int timeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Bundle bundle = this.getIntent().getExtras();
        toolbar.setTitle(bundle.getString("title"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preference=getSharedPreferences("setting",0);

        TextView content_title=(TextView)findViewById(R.id.content_title);
        content_title.setText(bundle.getString("title"));
        TextView content_writer=(TextView)findViewById(R.id.content_writer);
        content_writer.setText(bundle.getString("writer"));
        TextView content_date=(TextView)findViewById(R.id.content_date);
        content_date.setText(bundle.getString("date"));
        final WebView webView=(WebView)findViewById(R.id.txt_body);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
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
                "}</style></head><body>"+bundle.getString("content")+"</body></html>";
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ForumContentActivity.this, ForumCommitActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "commit");
                bundle1.putString("id",bundle.getString("id"));
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        final View view=findViewById(android.R.id.content);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<ForumContent>>() {}.getType();
                ArrayList<ForumContent> forumContents=new ArrayList<ForumContent>(){};
                try {
                    forumContents = gson.fromJson(networkRun(view, "https://api.dacsc.club/daanx/forum/main/id/" + bundle.getString("id")), listType);
                }catch (Exception e){/**/}
                forumContents.remove(0);

                final CommitAdapter commitAdapter=new CommitAdapter(ForumContentActivity.this,forumContents);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SuperListview superListview=(SuperListview)findViewById(R.id.commit_list);
                        superListview.setAdapter(commitAdapter);
                    }
                });
            }
        }).start();
    }

    public String networkRun(final View view,String url){
        Log.i("status",url);
        try {
            trustDacsc();
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .data("auth", preference.getString("auth", ""))
                    .get();
            Log.i("json", doc.select("body").text());
            return doc.select("body").text();
        }catch (Exception e){
            timeout++;
            if (timeout < 5) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {/**/}
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException c) {/**/}
                return networkRun(view,url);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {/**/}
                    }
                });
                return "";
            }
        }
    }

    public class ForumContent{

        @SerializedName("title")
        public String title;

        @SerializedName("body")
        public String content;

        @SerializedName("writer")
        public String writer;

        @SerializedName("file")
        public String file;
    }


    public class CommitAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<ForumContent> list;

        public CommitAdapter(Context context,ArrayList<ForumContent> lists){
            this.inflater=LayoutInflater.from(context);
            this.list=lists;
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int position){
            return list.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            ViewHolder holder;

            if(convertView==null){
                convertView=inflater.inflate(R.layout.commit_row,null);
                //Log.i("status","if");

                holder=new ViewHolder();
                holder.writer=(TextView) convertView.findViewById(R.id.list_writer);
                holder.body=(TextView) convertView.findViewById(R.id.list_body);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                //Log.i("status","else");
            }

            ForumContent forumContent=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.writer.setText(forumContent.writer);
            holder.body.setText(Html.fromHtml(forumContent.content));

            return convertView;
        }

        private class ViewHolder{
            TextView writer;
            TextView body;
        }
    }

}
