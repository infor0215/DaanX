package com.dtf.daanx;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
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
                    try {
                        Document doc = Jsoup.connect("https://api.dacsc.club/daanx/forum/main/"+page)
                                .timeout(5000)
                                .data("auth",preference.getString("auth",""))
                                .get();
                        String data=doc.text();
                        Log.i("json",data);

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Post>>() {
                        }.getType();
                        ArrayList<Post> jsonArr = gson.fromJson(data, listType);
                        ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
                        for (Post obj:jsonArr) {
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
                }
            }).start();
        }
        return view;
    }

    public class Post {

        @SerializedName("title")
        private String title;

        @SerializedName("body")
        private String content;

        @SerializedName("writer")
        private String writer;

        @SerializedName("file")
        private String file;

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

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
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

