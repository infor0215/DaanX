package com.dtf.daanx;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class ListviewFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_listview,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);
        String str = (String)getArguments().get("type");
        if(str==null) str="";
        if(str.equals("forum")){
            try {
                RequestQueue mQueue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest("",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Post>>() {
                                }.getType();
                                ArrayList<Post> jsonArr = gson.fromJson(response, listType);
                                ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
                                int ret=0;
                                for (Post obj:jsonArr) {
                                    HashMap<String, Object> temp = new HashMap<>();
                                    temp.put("title", obj.getTitle());
                                    if(obj.getWriter().equals("教")) ret=R.drawable.post_bg_green;
                                    temp.put("writer", ret);
                                    temp.put("content", obj.getContent());
                                    itemList.add(temp);
                                }
                                SimpleAdapter adapter = new SimpleAdapter(
                                        getActivity(),
                                        itemList,
                                        R.layout.post_row,
                                        new String[]{"title", "writer", "content"},
                                        new int[]{R.id.title, R.id.image, R.id.content}
                                );
                                ListView listView = (ListView) view.findViewById(R.id.list);
                                listView.setAdapter(adapter);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
                mQueue.add(stringRequest);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return view;
    }


}

