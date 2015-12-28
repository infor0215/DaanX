package com.dtf.daanx;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class CalcFragment extends Fragment {


    private int timeout;
    Thread thread;
    String jsonTemp="";
    boolean eventLock=false;
    ArrayList<CalcDay> calcDays;
    CalcAdapter calcAdapter;
    private int year;
    private int month;
    TextView txt_year;
    TextView txt_month;
    LinearLayout calc_ym;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout=(LinearLayout) getActivity().findViewById(R.id.main_layout);
        linearLayout.setPadding(0,0,0,0);
        calc_ym=(LinearLayout) getActivity().findViewById(R.id.calc_ym);
        calc_ym.setVisibility(View.VISIBLE);
        txt_year=(TextView) getActivity().findViewById(R.id.txt_year);
        txt_month=(TextView) getActivity().findViewById(R.id.txt_month);
        timeout=0;

        Calendar calendar = Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;

        final View view = inflater.inflate(R.layout.fragment_listview,container, false);
        if(((MainActivity)getActivity()).networkInfo()){
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<CalcDay>>() {}.getType();
                        jsonTemp=networkRun(view,"https://api.dacsc.club/daanx/calc/"+year+"/"+month);
                        if(!jsonTemp.equals("")) {
                            calcDays = gson.fromJson(jsonTemp, listType);
                            calcAdapter = new CalcAdapter(getActivity(), calcDays);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SuperListview listView = (SuperListview) view.findViewById(R.id.list);
                                    listView.setAdapter(calcAdapter);
                                    listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                        @Override
                                        public void onRefresh() {
                                            thread=new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if((month-1)>0){
                                                        month--;
                                                    }else {
                                                        year--;
                                                        month=12;
                                                    }
                                                    Gson gson = new Gson();
                                                    Type listType = new TypeToken<ArrayList<CalcDay>>() {
                                                    }.getType();
                                                    jsonTemp=networkRun(view, "https://api.dacsc.club/daanx/calc/"+year+"/"+month);
                                                    if(!jsonTemp.equals("")) {
                                                        ArrayList<CalcDay> listTemps=gson.fromJson(jsonTemp, listType);
                                                        if (!listTemps.get(0).day.equals(" ")) {
                                                            calcDays.addAll(0,listTemps);
                                                            if (getActivity() != null) {
                                                                getActivity().runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        calcAdapter.notifyDataSetChanged();
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
                                                        if(month<12){
                                                            month++;
                                                        }else {
                                                            year++;
                                                            month=1;
                                                        }
                                                        Gson gson = new Gson();
                                                        Type listType = new TypeToken<ArrayList<CalcDay>>() {
                                                        }.getType();
                                                        jsonTemp = networkRun(view, "https://api.dacsc.club/daanx/calc/"+year+"/"+month);
                                                        if (!jsonTemp.equals("")) {
                                                            ArrayList<CalcDay> temps = gson.fromJson(jsonTemp, listType);
                                                            if (!temps.get(0).commit.equals(" ")) {
                                                                calcDays.addAll(temps);
                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            calcAdapter.notifyDataSetChanged();
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
                                    }, 1);
                                }
                            });
                        }
                    }catch (Exception e){/**/}
                }
            });
            thread.start();
        }else {/**/}
        return view;
    }

    public String networkRun(final View view,String url){
        try {
            ((MainActivity)getActivity()).trustDacsc();
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
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

    private class CalcDay{
        @SerializedName("year")
        public String year;
        @SerializedName("month")
        public String month;
        @SerializedName("day")
        public String day;
        @SerializedName("commit")
        public String commit;
    }

    public class CalcAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<CalcDay> list;

        public CalcAdapter(Context context,ArrayList<CalcDay> lists){
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
                convertView=inflater.inflate(R.layout.calc_row,null);
                //Log.i("status","if");

                holder=new ViewHolder();
                holder.day=(TextView) convertView.findViewById(R.id.day);
                holder.commit=(TextView) convertView.findViewById(R.id.commit);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                //Log.i("status","else");
            }

            CalcDay calcDay=list.get(position);
            //Log.i("status",String.valueOf( postList.getWriter().charAt(0)));
            txt_year.setText(String.valueOf(calcDay.year+"年"));
            txt_month.setText(calcDay.month);
            if(!eventLock) {
                year = Integer.parseInt(calcDay.year);
                month = Integer.parseInt(calcDay.month);
            }
            holder.day.setText(calcDay.day);
            holder.commit.setText(calcDay.commit);

            Log.i("status",calcDay.month);

            return convertView;
        }

        private class ViewHolder{
            TextView day;
            TextView commit;
        }
    }

    @Override
    public void onDestroyView() {
        try {
            Thread.sleep(50);
            //thread.interrupt();
        }catch (Exception e){/**/}
        super.onDestroyView();
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        LinearLayout linearLayout=(LinearLayout) getActivity().findViewById(R.id.main_layout);
        linearLayout.setPadding(pixels,pixels,pixels,pixels);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        calc_ym.setVisibility(View.GONE);
    }
}


