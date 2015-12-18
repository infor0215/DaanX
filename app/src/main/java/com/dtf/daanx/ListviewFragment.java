package com.dtf.daanx;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class ListviewFragment extends Fragment {

    SharedPreferences preference;
    ArrayList<PostList> jsonArr;
    int page=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_listview,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);
        String str = (String)getArguments().get("type");
        preference=getActivity().getSharedPreferences("setting", 0);
        if(str==null) str="";
        if(str.equals("forum")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //region forum
                    try {
                        Document doc = Jsoup.connect("https://api.dacsc.club/daanx/forum/main/"+page)
                                .timeout(5000)
                                .data("auth",preference.getString("auth",""))
                                .get();
                        String data=doc.text();
                        Log.i("json",data);

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<ForumList>>() {
                        }.getType();
                        ArrayList<ForumList> jsonArr = gson.fromJson(data, listType);
                        ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
                        for (ForumList obj:jsonArr) {
                            HashMap<String, Object> temp = new HashMap<>();
                            temp.put("title", obj.getTitle());
                            temp.put("writer", obj.getWriter().charAt(0));
                            temp.put("body", obj.getContent());
                            itemList.add(temp);
                        }
                        final SimpleAdapter adapter = new SimpleAdapter(
                                getActivity(),
                                itemList,
                                R.layout.post_row,
                                new String[]{"title", "writer", "body"},
                                new int[]{R.id.title, R.id.image, R.id.content}
                        );
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                listView.setAdapter(adapter);
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
                        Document doc = Jsoup.connect("https://api.dacsc.club/daanx/post/week/"+page)
                                .timeout(5000)
                                .data("auth",preference.getString("auth",""))
                                .get();
                        String data=doc.select("body").text();
                        Log.i("json",data);

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<PostList>>() {
                        }.getType();
                        jsonArr = gson.fromJson(data, listType);
                        ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
                        for (PostList obj:jsonArr) {
                            HashMap<String, Object> temp = new HashMap<>();
                            temp.put("title", obj.getTitle());
                            temp.put("writer", obj.getWriter().charAt(0));

//                            String image[]=obj.image.split("|||");
//                            String images="<br>";
//                            for(int i=0;i<image.length;i++){
//                                images+="<img src="+image[i]+" /><br>";
//                            }
                            String base64=new String(Base64.decode(obj.getBody().getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
                            doc = Jsoup.parse(base64);
                            String tempBosy=doc.text();
                            temp.put("body",tempBosy);
                            itemList.add(temp);
                        }
                        final SimpleAdapter adapter = new SimpleAdapter(
                                getActivity(),
                                itemList,
                                R.layout.post_row,
                                new String[]{"title", "writer", "body"},
                                new int[]{R.id.title, R.id.image, R.id.content}
                        );
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                listView.setAdapter(adapter);
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

}

