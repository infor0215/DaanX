package com.dtf.daanx;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class ListviewFragment extends Fragment {

    SharedPreferences preference;
    ArrayList<PostList> postLists;
    PostAdapter postAdapter;
    ArrayList<ForumList> forumLists;
    ForumAdapter forumAdapter;
    private int timeout=1;
    int page=1;
    String jsonTemp="";
    boolean eventLock=false;
    private Thread thread;
    TinyDB first;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout) getActivity().findViewById(R.id.main_layout);
        linearLayout.setPadding(0,0,0,0);
        page=1;
        final View view = inflater.inflate(R.layout.fragment_listview,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);
        final String str = (String)getArguments().get("type");
        timeout=0;
        preference=getActivity().getSharedPreferences("setting", 0);
        if(str.equals("forum")){
            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ForumCommitActivity.class);
                    startActivity(intent);
                }
            });
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    //region forum
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<ForumList>>() {}.getType();
                        jsonTemp=networkRun(view,"https://api.dacsc.club/daanx/forum/main/"+page);
                        if(!jsonTemp.equals("")) {
                            forumLists = gson.fromJson(jsonTemp, listType);
                            forumAdapter = new ForumAdapter(getActivity(), forumLists);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                    listView.setAdapter(forumAdapter);
                                    first=new TinyDB("first-list",getActivity());
                                    first.putInt("num", first.getInt("num") + 1);
                                    if(first.getInt("num")==1){
                                        ViewTarget target = new ViewTarget(R.id.main_layout, getActivity());
                                        new ShowcaseView.Builder(getActivity())
                                                .setTarget(target)
                                                .withNewStyleShowcase()
                                                .setStyle(R.style.CustomShowcaseTheme2)
                                                .setContentTitle("列表")
                                                .setContentText("最上面下拉可以更新列表\n滑到最下面會自動載入")
                                                .hideOnTouchOutside()
                                                .build();
                                    }
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                                long arg3) {
                                            // TODO Auto-generated method stub
                                            ListView listView = (ListView) arg0;
                                            Toast.makeText(
                                                    getActivity(),
                                                    "ID：" + arg3 +
                                                            "   選單文字：" + listView.getItemAtPosition(arg2).toString(),
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                        @Override
                                        public void onRefresh() {
                                            thread=new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    page = 1;
                                                    Gson gson = new Gson();
                                                    Type listType = new TypeToken<ArrayList<ForumList>>() {
                                                    }.getType();
                                                    jsonTemp=networkRun(view, "https://api.dacsc.club/daanx/forum/main/" + page);
                                                    if(!jsonTemp.equals("")) {
                                                        ArrayList<ForumList> listTemps=gson.fromJson(jsonTemp, listType);
                                                        forumLists.clear();
                                                        forumLists.addAll(listTemps);
                                                        if (getActivity() != null) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    forumAdapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                            thread.start();
                                        }
                                    });
                                    listView.setupMoreListener(new OnMoreListener() {
                                        @Override
                                        public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                                            if(!eventLock) {
                                                eventLock=true;
                                                thread=new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        page++;
                                                        Gson gson = new Gson();
                                                        Type listType = new TypeToken<ArrayList<ForumList>>() {
                                                        }.getType();
                                                        jsonTemp = networkRun(view, "https://api.dacsc.club/daanx/forum/main/" + page);
                                                        if (!jsonTemp.equals("")) {
                                                            ArrayList<ForumList> temps = gson.fromJson(jsonTemp, listType);
                                                            if (!temps.get(0).getContent().equals(" ")) {
                                                                forumLists.addAll(temps);
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            forumAdapter.notifyDataSetChanged();
                                                                            listView.hideMoreProgress();
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            listView.hideMoreProgress();
                                                                            try {
                                                                                Snackbar.make(view, "已經載入到底.....", Snackbar.LENGTH_LONG).show();
                                                                            }catch (Exception e){/**/}
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                        eventLock=false;
                                                    }
                                                });
                                                thread.start();
                                            }
                                        }
                                    }, 2);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e("TAG", e.getMessage(), e);
                    }
                    //endregion
                }
            });
            thread.start();
        }else{
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    //region week
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                        jsonTemp=networkRun(view,"https://api.dacsc.club/daanx/post/"+str+"/"+page);
                        if(!jsonTemp.equals("")) {
                            postLists = gson.fromJson(jsonTemp, listType);
                            if(postLists.get(0).getBody().equals(" ")){
                                postLists.clear();
                                PostList postList=new PostList();
                                postList.setTitle("無資料");
                                postList.setContent("無資料");
                                postList.setWriter(" ");
                                postList.setDate(" ");
                                postList.setFile("");
                                postList.setImage("");
                                postList.setLink("");
                                postLists.add(postList);
                            }
                            postAdapter = new PostAdapter(getActivity(), postLists);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                    listView.setAdapter(postAdapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                                long arg3) {
                                            // TODO Auto-generated method stub
                                            Intent intent = new Intent();
                                            intent.setClass(getActivity(), PostContentActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("title", postLists.get((int)arg3).getTitle());
                                            bundle.putString("writer",postLists.get((int)arg3).getWriter());
                                            bundle.putString("date",postLists.get((int)arg3).getDate());
                                            bundle.putString("image",postLists.get((int)arg3).getImage());
                                            bundle.putString("link",postLists.get((int)arg3).getLink());
                                            bundle.putString("file",postLists.get((int)arg3).getFile());
                                            String base64="";
                                            try {
                                                base64=new String(Base64.decode(postLists.get((int)arg3).getBody().getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
                                            }catch (Exception e){/**/}
                                            bundle.putString("content",base64);
                                            intent.putExtras(bundle);
                                            startActivity(intent);

                                        }
                                    });
                                    listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                        @Override
                                        public void onRefresh() {
                                            thread=new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    page = 1;
                                                    Gson gson = new Gson();
                                                    Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                                                    jsonTemp=networkRun(view, "https://api.dacsc.club/daanx/post/"+str+"/" + page);
                                                    if(!jsonTemp.equals("")) {
                                                        ArrayList<PostList> listTemps=gson.fromJson(jsonTemp, listType);
                                                        postLists.clear();
                                                        postLists.addAll(listTemps);
                                                        if (getActivity() != null) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    postAdapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                            thread.start();
                                        }
                                    });
                                    listView.setupMoreListener(new OnMoreListener() {
                                        @Override
                                        public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                                            if(!eventLock) {
                                                eventLock=true;
                                                thread=new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        page++;
                                                        Gson gson = new Gson();
                                                        Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                                                        jsonTemp = networkRun(view, "https://api.dacsc.club/daanx/post/"+str+"/" + page);
                                                        if (!jsonTemp.equals("")) {
                                                            ArrayList<PostList> temps = gson.fromJson(jsonTemp, listType);
                                                            if (!temps.get(0).getBody().equals(" ")) {
                                                                postLists.addAll(temps);
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            postAdapter.notifyDataSetChanged();
                                                                            listView.hideMoreProgress();
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            listView.hideMoreProgress();
                                                                            try {
                                                                                Snackbar.make(view, "已經載入到底.....", Snackbar.LENGTH_LONG).show();
                                                                            }catch (Exception e){/**/}
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                        eventLock=false;
                                                    }
                                                });
                                                thread.start();
                                            }
                                        }
                                    }, 2);
                                }
                            });
                        }
                    }catch (Exception e){
                        Log.e("TAG", e.getMessage(), e);
                    }
                    //endregion
                }
            });
            thread.start();
        }
        return view;
    }

    public String networkRun(final View view,String url){
        try {
            ((MainActivity)getActivity()).trustDacsc();
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .data("auth", preference.getString("auth", ""))
                    .get();
            Log.i("json", doc.select("body").text());
            return doc.select("body").text();
        }catch (Exception e){
            timeout++;
            if (timeout < 5) {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                            }catch (Exception e){/**/}
                        }
                    });
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException c) {/**/}
                return networkRun(view,url);
            } else {
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                            }catch (Exception e){/**/}
                        }
                    });
                }
                return "";
            }
        }
    }

    public class ForumList {

        @SerializedName("title")
        private String title;

        @SerializedName("body")
        private String content;

        @SerializedName("writer")
        private String writer;

        @SerializedName("day")
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String writer) {
            this.writer = writer;
        }

    }

    public class PostList {

        @SerializedName("title")
        private String title;

        @SerializedName("body")
        private String content;

        @SerializedName("writer")
        private String writer;

        @SerializedName("file")
        private String file;

        @SerializedName("image")
        private String image;

        @SerializedName("link")
        private String link;

        @SerializedName("day")
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String writer) {
            this.writer = writer;
        }

    }

    public class PostAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<PostList> list;

        public PostAdapter(Context context,ArrayList<PostList> lists){
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
                convertView=inflater.inflate(R.layout.post_row,null);
                //Log.i("status","if");

                holder=new ViewHolder();
                holder.image=(TextView) convertView.findViewById(R.id.image1);
                holder.title=(TextView) convertView.findViewById(R.id.title1);
                holder.content=(TextView) convertView.findViewById(R.id.content1);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                //Log.i("status","else");
            }

            PostList postList=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.image.setText(String.valueOf(postList.getWriter().charAt(0)));
            switch (postList.getWriter().charAt(0)){
                case '人':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_people));
                    break;
                case '圖':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_pic));
                    break;
                case '實':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_practice));
                    break;
                case '學':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_school));
                    break;
                case '輔':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_support));
                    break;
                case '教':
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_taech));
                    break;
            }
            holder.title.setText(postList.getTitle());
            String base64="";
            try{
            base64=new String(Base64.decode(postList.getBody().getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
            }catch (Exception e){/**/}
            Document doc = Jsoup.parse(base64);
            String tempBody=doc.text();
            holder.content.setText(tempBody);

            return convertView;
        }

        private class ViewHolder{
            TextView image;
            TextView title;
            TextView content;
        }
    }

    public class ForumAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<ForumList> list;

        public ForumAdapter(Context context,ArrayList<ForumList> lists){
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
                convertView=inflater.inflate(R.layout.post_row,null);
                //Log.i("status","if");

                holder=new ViewHolder();
                holder.image=(TextView) convertView.findViewById(R.id.image1);
                holder.title=(TextView) convertView.findViewById(R.id.title1);
                holder.content=(TextView) convertView.findViewById(R.id.content1);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                //Log.i("status","else");
            }

            ForumList forumList=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.image.setText(String.valueOf(forumList.getWriter().charAt(0)));
            holder.title.setText(forumList.getTitle());
            String base64="";
            try{
                base64=new String(Base64.decode(forumList.getContent().getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
            }catch (Exception e){/**/}
            Document doc = Jsoup.parse(base64);
            String tempBody=doc.text();
            holder.content.setText(tempBody);

            return convertView;
        }

        private class ViewHolder{
            TextView image;
            TextView title;
            TextView content;
        }
    }

    @Override
    public void onDestroyView() {
        try {
            Thread.sleep(50);
            thread.interrupt();
        }catch (Exception e){/**/}
        super.onDestroyView();
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        LinearLayout linearLayout=(LinearLayout) getActivity().findViewById(R.id.main_layout);
        linearLayout.setPadding(pixels,pixels,pixels,pixels);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }
}


