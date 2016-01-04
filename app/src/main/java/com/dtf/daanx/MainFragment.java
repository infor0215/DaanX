package com.dtf.daanx;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class MainFragment extends Fragment {

    TinyDB first;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        final Bundle bundle=getArguments();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<MainContent>>() {}.getType();
                ArrayList<MainContent> mainContents;
                if(bundle.getString("json")!=null) {
                    mainContents = gson.fromJson(bundle.getString("json"), listType);
                    ArrayList<MainContent> postLists=new ArrayList<MainContent>(){};
                    postLists.add(mainContents.get(1));
                    postLists.add(mainContents.get(2));
                    ArrayList<MainContent> forumLists=new ArrayList<MainContent>(){};
                    forumLists.add(mainContents.get(3));
                    forumLists.add(mainContents.get(4));

                    final String lastHolText=mainContents.get(0).title;
                    final String lastHolDay=String.valueOf(testLastDay(mainContents.get(0).day));

                    final PostAdapter postAdapter=new PostAdapter(getActivity(),postLists);
                    final ForumAdapter forumAdapter=new ForumAdapter(getActivity(),forumLists);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SuperListview post_list=(SuperListview) view.findViewById(R.id.post_list);
                            post_list.setAdapter(postAdapter);
                            post_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                        long arg3) {
                                    // TODO Auto-generated method stub
                                    ((MainActivity)getActivity()).SwitchFramgent(R.id.nav_stupost);
                                }
                            });
                            SuperListview forum_list=(SuperListview) view.findViewById(R.id.forum_list);
                            forum_list.setAdapter(forumAdapter);
                            forum_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                                        long arg3) {
                                    // TODO Auto-generated method stub
                                    ((MainActivity)getActivity()).SwitchFramgent(R.id.nav_forum);
                                }
                            });
                            TextView main_testlast=(TextView) view.findViewById(R.id.main_textlast);
                            main_testlast.setText(String.valueOf("距離 "+lastHolText+" 還有"+lastHolDay+"天"));
                        }
                    });
                }
            }
        }).start();


        first=new TinyDB("first-main",getActivity());
        first.putInt("num", first.getInt("num") + 1);

        if(first.getInt("num")==1){
            new ShowcaseView.Builder(getActivity())
                    .setTarget(new PointTarget(new Point(10,20)))
                    .withNewStyleShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentTitle("NavigationDrawer")
                    .setContentText("所有的功能都在這裡")
                    .hideOnTouchOutside()
                    .blockAllTouches()
                    .build();
        }
        
        return view;
    }

    private long testLastDay(String testDate){
        Date nowDate = new Date();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date beginDate;
        long day=0;
        try {
            beginDate= format.parse(testDate);
            day=Math.abs((beginDate.getTime()-nowDate.getTime())/(24*60*60*1000))+1;
        }catch (Exception e){/**/}
        return day;
    }
    
    public class MainContent{
        @SerializedName("id")
        public String id;

        @SerializedName("title")
        public String title;

        @SerializedName("body")
        public String body;

        @SerializedName("writer")
        public String writer;

        @SerializedName("file")
        public String file;

        @SerializedName("image")
        public String image;

        @SerializedName("link")
        public String link;

        @SerializedName("day")
        public String day;

        @SerializedName("view")
        public String view;
    }

    public class PostAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MainContent> list;

        public PostAdapter(Context context, ArrayList<MainContent> lists){
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

            MainContent postList=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.image.setText(String.valueOf(postList.writer.charAt(0)));
            switch (postList.writer.charAt(0)){
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
            holder.title.setText(postList.title);
            String base64="";
            try{
                base64=new String(Base64.decode(postList.body.getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
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
        private ArrayList<MainContent> list;

        public ForumAdapter(Context context,ArrayList<MainContent> lists){
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

            MainContent forumList=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));

            holder.image.setText(String.valueOf(forumList.writer.charAt(0)));

            int view=Integer.parseInt(forumList.view);
            if(view>500){
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    holder.image.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.grade_bg_red));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.grade_bg_red));
                }
            }else if(view>200){
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    holder.image.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.post_bg_school));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_school));
                }
            }else if(view>100){
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    holder.image.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.post_bg_default));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_default));
                }
            }else {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    holder.image.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.post_bg_taech));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.post_bg_taech));
                }
            }

            holder.title.setText(forumList.title);
            String base64="";
            try{
                base64=new String(Base64.decode(forumList.body.getBytes("UTF-8"),Base64.DEFAULT),"UTF-8");
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

}