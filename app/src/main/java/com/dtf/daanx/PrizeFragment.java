package com.dtf.daanx;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class PrizeFragment extends Fragment {


    ProgressDialog dialog;
    SharedPreferences preference;
    private int timeout;

    ArrayList<String> year;
    ArrayList<String> date;
    ArrayList<String> status;
    ArrayList<String> because;
    int smallcite;
    int middlecite;
    int bigcite;
    int smallfault;
    int middlefault;
    int bigfault;
    LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_prize, container, false);

        if(((MainActivity)getActivity()).networkInfo()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    networkRun(view);
                }
            }).start();
        }
        return view;
    }

    //網路連線
    private void networkRun(final View view) {
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
            ((MainActivity)getActivity()).trustEveryone();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
            //region 連線
            preference = getActivity().getSharedPreferences("setting", 0);
            String stu_id = preference.getString("stu_id", "");
            String stu_pwd = preference.getString("stu_pwd", "");
            Connection.Response res = Jsoup
                    .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                    .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                    .method(Connection.Method.POST)
                    .timeout(5000)
                    .execute();

            Map<String, String> loginCookies = res.cookies();
            //抓取資料分析並儲存
            Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/ds.asp")
                    .cookies(loginCookies)
                    .timeout(5000)
                    .get();

            Elements temps=doc.select("tr[onmouseout=OMOut(this);]");

            Elements temp;


            year = new ArrayList<String>() {};
            date = new ArrayList<String>() {};
            status = new ArrayList<String>() {};
            because = new ArrayList<String>() {};

            for(int i=0;i<temps.size();i++){
                temp=temps.get(i).select("td");
                year.add(temp.get(0).text());
                date.add(temp.get(3).text());
                status.add(temp.get(5).text());
                because.add(temp.get(6).text().replace("、",""));
            }
            smallcite=countsum(status,"嘉獎");
            smallfault=countsum(status,"警告");
            middlecite=countsum(status,"小功");
            middlefault=countsum(status,"小過");
            bigcite=countsum(status,"大功");
            bigfault=countsum(status,"大過");
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
                } catch (InterruptedException c) {/**/}
                networkRun(view);
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

    private void writeInUi(final View view){

        //region 上面欄位
        TextView textView=(TextView) view.findViewById(R.id.smallcite);
        textView.setText(String.valueOf(smallcite));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        textView=(TextView) view.findViewById(R.id.middlecite);
        textView.setText(String.valueOf(middlecite));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        textView=(TextView) view.findViewById(R.id.bigcite);
        textView.setText(String.valueOf(bigcite));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        textView=(TextView) view.findViewById(R.id.smallfault);
        textView.setText(String.valueOf(smallfault));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        textView=(TextView) view.findViewById(R.id.middlefault);
        textView.setText(String.valueOf(middlefault));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        textView=(TextView) view.findViewById(R.id.bigfault);
        textView.setText(String.valueOf(bigfault));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        //endregion

        //region 全部列表
        int pixels;//dp
        linearLayout = (LinearLayout) view.findViewById(R.id.list_prize);
        for(int i=0;i<year.size();i++) {

            LinearLayout linearLayout_479 = new LinearLayout(getActivity());
            linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
            linearLayout_479.setOrientation(LinearLayout.VERTICAL);
            linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
            LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            linearLayout_479.setLayoutParams(layout_84);

            LinearLayout linearLayout_987 = new LinearLayout(getActivity());
            linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout_987.setLayoutParams(layout_128);

            TextView textView_1 = new TextView(getActivity());
            textView_1.setText(year.get(i));
            textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            layout_830.setMargins(pixels,pixels,pixels,pixels);
            textView_1.setLayoutParams(layout_830);
            linearLayout_987.addView(textView_1);

            TextView textView_727 = new TextView(getActivity());
            textView_727.setText(date.get(i));
            textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            layout_966.setMargins(pixels,pixels,pixels,pixels);
            textView_727.setLayoutParams(layout_966);
            linearLayout_987.addView(textView_727);

            TextView textView_408 = new TextView(getActivity());
            textView_408.setText(status.get(i));
            textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
            LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            layout_951.setMargins(pixels,pixels,pixels,pixels);
            textView_408.setLayoutParams(layout_951);
            linearLayout_987.addView(textView_408);
            linearLayout_479.addView(linearLayout_987);

            TextView textView_298 = new TextView(getActivity());
            textView_298.setText(because.get(i));
            textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            textView_298.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
            layout_588.topMargin = pixels;
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
            layout_588.leftMargin = pixels;
            pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            layout_588.rightMargin = pixels;
            textView_298.setLayoutParams(layout_588);
            linearLayout_479.addView(textView_298);

            linearLayout.addView(linearLayout_479);
        }
        //endregion
    }

    public int countsum(ArrayList<String> from, String key) {
        int sum=0;
        for(int i=0;i<from.size();i++){
            if(from.get(i).contains(key)){
                sum+=Integer.parseInt(alltohalf(String.valueOf(from.get(i).charAt(from.get(i).indexOf(key)+2))));
            }
        }
        return sum;
    }

    public static String alltohalf(String str){
        for(char c:str.toCharArray()){
            str = str.replaceAll("　", " ");
            if((int)c >= 65281 && (int)c <= 65374){
                str = str.replace(c, (char)(((int)c)-65248));
            }
        }
        return str;
    }

    private void select(View view){
        int pixels;
        TextView textView=(TextView) view;
        if(textView.getTag().toString().equals("嘉獎")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("嘉獎")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }else if(textView.getTag().toString().equals("小功")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("小功")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }else if(textView.getTag().toString().equals("大功")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("大功")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }else if(textView.getTag().toString().equals("警告")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("警告")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }else if(textView.getTag().toString().equals("小過")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("小過")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }else if(textView.getTag().toString().equals("大過")){
            linearLayout.removeAllViews();
            //region AddView
            for(int i=0;i<year.size();i++) {
                if(status.get(i).contains("大過")) {
                    LinearLayout linearLayout_479 = new LinearLayout(getActivity());
                    linearLayout_479.setBackgroundResource(R.drawable.prize_bg_list);
                    linearLayout_479.setOrientation(LinearLayout.VERTICAL);
                    linearLayout_479.setGravity(Gravity.CENTER_HORIZONTAL);
                    LayoutParams layout_84 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_479.setLayoutParams(layout_84);

                    LinearLayout linearLayout_987 = new LinearLayout(getActivity());
                    linearLayout_987.setOrientation(LinearLayout.HORIZONTAL);
                    LayoutParams layout_128 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    linearLayout_987.setLayoutParams(layout_128);

                    TextView textView_1 = new TextView(getActivity());
                    textView_1.setText(year.get(i));
                    textView_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_830 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_830.setMargins(pixels, pixels, pixels, pixels);
                    textView_1.setLayoutParams(layout_830);
                    linearLayout_987.addView(textView_1);

                    TextView textView_727 = new TextView(getActivity());
                    textView_727.setText(date.get(i));
                    textView_727.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    LinearLayout.LayoutParams layout_966 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_966.setMargins(pixels, pixels, pixels, pixels);
                    textView_727.setLayoutParams(layout_966);
                    linearLayout_987.addView(textView_727);

                    TextView textView_408 = new TextView(getActivity());
                    textView_408.setText(status.get(i));
                    textView_408.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    LinearLayout.LayoutParams layout_951 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    layout_951.setMargins(pixels, pixels, pixels, pixels);
                    textView_408.setLayoutParams(layout_951);
                    linearLayout_987.addView(textView_408);
                    linearLayout_479.addView(linearLayout_987);

                    TextView textView_298 = new TextView(getActivity());
                    textView_298.setText(because.get(i));
                    textView_298.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView_298.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layout_588 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics());
                    layout_588.topMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                    layout_588.leftMargin = pixels;
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layout_588.rightMargin = pixels;
                    textView_298.setLayoutParams(layout_588);
                    linearLayout_479.addView(textView_298);

                    linearLayout.addView(linearLayout_479);
                }
            }
            //endregion
        }
    }
}