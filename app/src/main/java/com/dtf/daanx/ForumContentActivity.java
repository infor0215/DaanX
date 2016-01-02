package com.dtf.daanx;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;

public class ForumContentActivity extends BaseActivity {

    SharedPreferences preference;
    private int timeout;
    ArrayList<ForumContent> forumContents;
    boolean commit=false;
    CommitAdapter commitAdapter;
    String id;

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
        commit=false;

        TextView content_title=(TextView)findViewById(R.id.content_title);
        content_title.setText(bundle.getString("title"));
        TextView content_writer=(TextView)findViewById(R.id.content_writer);
        content_writer.setText(bundle.getString("writer"));
        TextView content_view=(TextView)findViewById(R.id.content_view);
        content_view.setText(bundle.getString("view"));
        TextView content_date=(TextView)findViewById(R.id.content_date);
        content_date.setText(bundle.getString("date"));

        final RichEditor text=(RichEditor) findViewById(R.id.txt_body);
        text.setTextColor(Color.BLACK);
        text.setEditorBackgroundColor(ContextCompat.getColor(ForumContentActivity.this, android.R.color.transparent));
        text.setEnabled(false);
        text.setFocusable(false);
        if(Build.VERSION.SDK_INT >= 19) {
            text.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            text.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            text.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        text.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        text.setHtml(bundle.getString("content"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit=true;
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

        id=bundle.getString("id");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                final Type listType = new TypeToken<ArrayList<ForumContent>>() {}.getType();
                forumContents=new ArrayList<ForumContent>(){};
                try {
                    forumContents = gson.fromJson(networkRun(view, "https://api.dacsc.club/daany/forum/main/id/"+id), listType);
                }catch (Exception e){/**/}
                forumContents.remove(0);

                commitAdapter=new CommitAdapter(ForumContentActivity.this,forumContents);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView superListview=(ListView)findViewById(R.id.commit_list);
                        superListview.setAdapter(commitAdapter);
                        superListview.setDivider(ContextCompat.getDrawable(ForumContentActivity.this, R.color.grey_600));
                        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                        superListview.setDividerHeight(pixels);
                        setListViewHeightBasedOnChildren(superListview);
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
            Log.i("json", doc.select("body").html());
            return doc.select("body").html();
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

        @SerializedName("date")
        public String date;
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
                holder.body=(HtmlTextView) convertView.findViewById(R.id.list_body);
                holder.date=(TextView) convertView.findViewById(R.id.list_date);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                //Log.i("status","else");
            }

            ForumContent forumContent=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.writer.setText(forumContent.writer);
//            holder.body.setText(Html.fromHtml(forumContent.content));
            String base64="";
            try{
                base64=new String(Base64.decode(forumContent.content.getBytes("UTF-8"), Base64.DEFAULT),"UTF-8");
            }catch (Exception e) {/**/}
            holder.body.setHtmlFromString(base64,new HtmlTextView.RemoteImageGetter());
            holder.date.setText(forumContent.date);

            return convertView;
        }

        private class ViewHolder{
            TextView writer;
            HtmlTextView body;
            TextView date;
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        if(commit){
            Log.i("commmit","true");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    final Type listType = new TypeToken<ArrayList<ForumContent>>() {}.getType();
                    ArrayList<ForumContent> temps=new ArrayList<ForumContent>(){};
                    try {
                        temps = gson.fromJson(networkRun(findViewById(android.R.id.content), "https://api.dacsc.club/daany/forum/main/id/"+id), listType);
                    }catch (Exception e){/**/}
                    temps.remove(0);
                    forumContents.clear();
                    forumContents.addAll(temps);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commitAdapter.notifyDataSetChanged();
                            ListView superListview=(ListView)findViewById(R.id.commit_list);
                            setListViewHeightBasedOnChildren(superListview);
                        }
                    });
                }
            }).start();
        }
        super.onResume();
    }





}
