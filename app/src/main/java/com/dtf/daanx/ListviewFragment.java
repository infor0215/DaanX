package com.dtf.daanx;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_listview,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);
        String str = (String)getArguments().get("type");
        timeout=0;
        preference=getActivity().getSharedPreferences("setting", 0);
        if(str==null) str="";
        if(str.equals("forum")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //region forum
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<ForumList>>() {
                        }.getType();
                        forumLists = gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/forum/main/"+page), listType);
                        forumAdapter = new ForumAdapter(getActivity(),forumLists);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                listView.setAdapter(forumAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                            long arg3) {
                                        // TODO Auto-generated method stub
                                        ListView listView = (ListView) arg0;
                                        Toast.makeText(
                                                getActivity(),
                                                "ID：" + arg3 +
                                                        "   選單文字："+ listView.getItemAtPosition(arg2).toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                                listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                    @Override
                                    public void onRefresh() {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                page=1;
                                                Gson gson = new Gson();
                                                Type listType = new TypeToken<ArrayList<ForumList>>() {}.getType();
                                                forumLists = gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/forum/main/"+page), listType);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        forumAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                listView.setupMoreListener(new OnMoreListener() {
                                    @Override
                                    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                page++;
                                                Gson gson = new Gson();
                                                Type listType = new TypeToken<ArrayList<ForumList>>() {}.getType();
                                                ArrayList<ForumList> temps=gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/forum/main/"+page), listType);
                                                if(!temps.get(0).getContent().equals(" ")) {
                                                    for (ForumList obj : temps) {
                                                        forumLists.add(obj);
                                                    }
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            forumAdapter.notifyDataSetChanged();
                                                            listView.hideMoreProgress();
                                                        }
                                                    });
                                                }else {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listView.hideMoreProgress();
                                                            Snackbar.make(view, "已經載入到底.....", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();
                                    }
                                }, 2);
                            }
                        });
                    }catch (Exception e){
                        Log.e("TAG", e.getMessage(), e);
                    }
                    //endregion
                }
            }).start();
        }else if(str.equals("week")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //region week
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                        postLists = gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/post/week/"+page), listType);
                        postAdapter=new PostAdapter(getActivity(),postLists);
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
                                        ListView listView = (ListView) arg0;
                                        Toast.makeText(
                                                getActivity(),
                                                "ID：" + arg3 +
                                                        "   選單文字："+ listView.getItemAtPosition(arg2).toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                                listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                    @Override
                                    public void onRefresh() {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                page=1;
                                                Gson gson = new Gson();
                                                Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                                                postLists = gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/post/week/"+page), listType);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        postAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                listView.setupMoreListener(new OnMoreListener() {
                                    @Override
                                    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                page++;
                                                Gson gson = new Gson();
                                                Type listType = new TypeToken<ArrayList<PostList>>() {}.getType();
                                                ArrayList<PostList> temps=gson.fromJson(networkRun(view,"https://api.dacsc.club/daanx/post/week/"+page), listType);
                                                if(!temps.get(0).getBody().equals(" ")) {
                                                    for (PostList obj : temps) {
                                                        postLists.add(obj);
                                                    }
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            postAdapter.notifyDataSetChanged();
                                                            listView.hideMoreProgress();
                                                        }
                                                    });
                                                }else {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            listView.hideMoreProgress();
                                                            Snackbar.make(view, "已經載入到底.....", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();
                                    }
                                }, 2);
                            }
                        });
                    }catch (Exception e){
                        Log.e("TAG", e.getMessage(), e);
                    }
                    //endregion
                }
            }).start();
        }
        return view;
    }

    public String networkRun(final View view,String url){
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .data("auth", preference.getString("auth", ""))
                    .get();
            Log.i("json", doc.select("body").text());
            return doc.select("body").text();
        }catch (Exception e){
            timeout++;
            if (timeout < 5) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException c) {/**/}
                return networkRun(view,url);
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                    }
                });
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
            Document doc = Jsoup.parse(forumList.getContent());
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
}

//                RequestQueue mQueue = Volley.newRequestQueue(getActivity());
//                StringRequest stringRequest = new StringRequest("",
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Gson gson = new Gson();
//                                Type listType = new TypeToken<ArrayList<Post>>() {
//                                }.getType();
//                                ArrayList<Post> jsonArr = gson.fromJson(response, listType);
//                                ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
//                                int ret=0;
//                                for (Post obj:jsonArr) {
//                                    HashMap<String, Object> temp = new HashMap<>();
//                                    temp.put("title", obj.getTitle());
//                                    ret=R.drawable.post_bg_green;
//                                    temp.put("writer", ret);
//                                    temp.put("body", obj.getContent());
//                                    itemList.add(temp);
//                                }
//                                SimpleAdapter adapter = new SimpleAdapter(
//                                        getActivity(),
//                                        itemList,
//                                        R.layout.post_row,
//                                        new String[]{"title", "writer", "body"},
//                                        new int[]{R.id.title, R.id.image, R.id.content}
//                                );
//                                ListView listView = (ListView) view.findViewById(R.id.list);
//                                listView.setAdapter(adapter);
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("TAG", error.getMessage(), error);
//                    }
//                });
//                mQueue.add(stringRequest);

