package com.dtf.daanx;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class AttendFragment extends Fragment {


    ProgressDialog dialog;
    SharedPreferences preference;
    private int timeout;
    private TinyDB cache;

    ArrayList<Object> attends;

    int public_leave;
    int sick_leave;
    int thing_leave;
    int dead_leave;
    int absence;
    int late;
    int cutting;

    LinearLayout linearLayout;
    private Thread thread;
    TinyDB first;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_attend, container, false);
        cache=new TinyDB("attend-cache",getActivity());
        if(((MainActivity)getActivity()).networkInfo()) {
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        networkRun(view);
                    }catch (Exception e){/**/}
                }
            });
            thread.start();
        }else {
            //cache
            Type listType = new TypeToken<Attend>() {}.getType();
            attends=cache.getListObject("attends", listType);
            //body = cache.getListString("body");


            public_leave=cache.getInt("public_leave");
            sick_leave=cache.getInt("sick_leave");
            thing_leave=cache.getInt("thing_leave");
            dead_leave=cache.getInt("dead_leave");
            absence=cache.getInt("absence");
            late=cache.getInt("late");
            cutting=cache.getInt("cutting");

            writeInUi(view);
        }
        return view;
    }

    //網路連線
    private void networkRun(final View view) {
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(getActivity(), "讀取網路中", "請稍後");
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException c) {/**/}

            try {
                ((MainActivity) getActivity()).trustTaivs();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
                //region 連線
                preference = getActivity().getSharedPreferences("setting", 0);
                String stu_id = preference.getString("stu_id", "");
                String stu_pwd = ((MainActivity)getActivity()).pwdNoSecure(preference.getString("stu_pwd", ""));
                Connection.Response res = Jsoup
                        .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                        .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                        .method(Connection.Method.POST)
                        .timeout(5000)
                        .execute();

                Map<String, String> loginCookies = res.cookies();
                //抓取資料分析並儲存
                Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/work.asp")
                        .cookies(loginCookies)
                        .timeout(5000)
                        .get();
                //Log.i("status",doc.text());

                Elements temps = doc.select("tr[onmouseout=OMOut(this);]");

                Elements temp;


                attends = new ArrayList<Object>() {
                };

                for (int i = 0; i < temps.size(); i++) {
                    temp = temps.get(i).select("td");
                    Attend attend = new Attend();
                    attend.year = temp.get(0).text();
                    attend.date = temp.get(3).text();
                    ArrayList<String> tmp = new ArrayList<String>() {
                    };
                    for (int y = 5; y < 19; y++) {
                        tmp.add(temp.get(y).text());
                    }
                    attend.body = tmp;

                    attends.add(attend);
                }


                public_leave = NumberOfKeywords(temps.text(), "公");
                sick_leave = NumberOfKeywords(temps.text(), "病");
                thing_leave = NumberOfKeywords(temps.text(), "事");
                dead_leave = NumberOfKeywords(temps.text(), "喪");
                absence = NumberOfKeywords(temps.text(), "缺");
                late = NumberOfKeywords(temps.text(), "遲");
                cutting = NumberOfKeywords(temps.text(), "曠");
                //endregion

                //region cacheWrite
                cache.putListObject("attends", attends);
                cache.putInt("public_leave", public_leave);
                cache.putInt("sick_leave", sick_leave);
                cache.putInt("thing_leave", thing_leave);
                cache.putInt("dead_leave", dead_leave);
                cache.putInt("absence", absence);
                cache.putInt("late", late);
                cache.putInt("cutting", cutting);
                //endregion

                //region 填入UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeInUi(view);
                    }
                });
                dialog.dismiss();
                //endregion
            } catch (IOException e) {
                //region retry
                dialog.dismiss();
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
                        networkRun(view);
                    } catch (Exception c) {/**/}
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                //endregion
            }
        }
    }

    private void writeInUi(final View view){

        //region 上面欄位
        TextView textView=(TextView) view.findViewById(R.id.public_leave);
        textView.setText(String.valueOf(public_leave));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.sick_leave);
        textView.setText(String.valueOf(sick_leave));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.thing_leave);
        textView.setText(String.valueOf(thing_leave));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.dead_leave);
        textView.setText(String.valueOf(dead_leave));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.absence);
        textView.setText(String.valueOf(absence));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.late);
        textView.setText(String.valueOf(late));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        textView=(TextView) view.findViewById(R.id.cutting);
        textView.setText(String.valueOf(cutting));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v,view);
            }
        });
        //endregion

        writeInList(view,"");
    }

    public int NumberOfKeywords(String strKeywords, String strkey){
        int spcount =0;
        String strTmp;
        for(int j=0;j<strKeywords.length();j++){
            strTmp = String.valueOf(strKeywords.charAt(j));
            if(strTmp.equals(strkey)){
                spcount++;
            }
        }
        return spcount;
    }

    private void select(View view,View viewm){
        TextView textView=(TextView) view;
        if(textView.getTag().toString().equals("公假")){
            writeInList(viewm, "公");
        }else if(textView.getTag().toString().equals("病假")){
            writeInList(viewm,"病");
        }else if(textView.getTag().toString().equals("事假")){
            writeInList(viewm,"事");
        }else if(textView.getTag().toString().equals("喪假")){
            writeInList(viewm,"喪");
        }else if(textView.getTag().toString().equals("缺席")){
            writeInList(viewm,"缺");
        }else if(textView.getTag().toString().equals("遲到")){
            writeInList(viewm,"遲");
        }else if(textView.getTag().toString().equals("曠課")){
            writeInList(viewm,"曠");
        }
    }

    private void writeInList(View view,String key){
        if(getActivity()!=null) {
            int pixels;
            linearLayout = (LinearLayout) view.findViewById(R.id.list_attend);
            if (!key.equals("")) linearLayout.removeAllViews();
            for (int i = 0; i < attends.size(); i++) {
                if (key.equals("") || ((Attend) (attends.get(i))).body.contains(key)) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.prize_bg_list));
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(((Attend) (attends.get(i))).year);
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(((Attend) (attends.get(i))).date);
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    LinearLayout linearLayout_100 = new LinearLayout(getActivity());
                    linearLayout_100.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layout_100 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_100.setMargins(pixels, 0, pixels, pixels);
                    linearLayout_100.setLayoutParams(layout_100);
                    for (int y = 0; y < ((Attend) (attends.get(i))).body.size(); y++) {
                        TextView textView_408 = new TextView(getActivity());
                        textView_408.setText(((Attend) (attends.get(i))).body.get(y));
                        textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        textView_408.setGravity(Gravity.CENTER);
                        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
                        textView_408.setWidth(pixels);
                        int draw = drawer(((Attend) (attends.get(i))).body.get(y));
                        textView_408.setBackground(ContextCompat.getDrawable(getActivity(), draw));
                        LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        textView_408.setLayoutParams(layout_951);
                        linearLayout_100.addView(textView_408);
                    }
                    linearLayout_479.addView(linearLayout_987);
                    linearLayout_479.addView(linearLayout_100);
                    linearLayout.addView(linearLayout_479);
                }
            }
        }
        first=new TinyDB("first-attend",getActivity());
        first.putInt("num", first.getInt("num") + 1);
        if(first.getInt("num")==1){
            FramgentTarget target = new FramgentTarget(R.id.public_leave,view);
            new ShowcaseView.Builder(getActivity())
                    .setTarget(target)
                    .withNewStyleShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentTitle("小紅點")
                    .setContentText("紅色圓圈點擊\n" +
                            "可以過濾下面列表")
                    .hideOnTouchOutside()
                    .blockAllTouches()
                    .build();
        }
    }

    private int drawer(String text){
        int draw=R.drawable.attend_bg_green;
        if(text.equals("曠")){
            draw=R.drawable.attend_bg_red;
        }else if(text.equals("遲")){
            draw=R.drawable.attend_bg_yellow;
        }else if(text.equals("缺")) {
            draw=R.drawable.attend_bg_yellow;
        }else if(text.equals("　")){
            draw=R.drawable.attend_bg_white;
        }
        return draw;
    }


    public class Attend{
        public Attend () {
            this.year = "";
            this.date = "";
        }
        public String year;
        public String date;
        public ArrayList<String> body;
    }

    @Override
    public void onDestroyView() {
        try {
            Thread.sleep(5);
            thread.interrupt();
        }catch (Exception e){/**/}
        super.onDestroyView();
    }
}