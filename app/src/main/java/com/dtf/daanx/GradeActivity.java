package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GradeActivity extends BaseActivity {

    private TabLayout mTabs;
    SharedPreferences preference;

    private ViewPager mViewPager;
    private ArrayList<String> grade;
    private ArrayList<String> front1;
    private ArrayList<String> front2;
    private ArrayList<String> front3;
    private ArrayList<String> frontusl;
    private ArrayList<String> frontavg;
    private ArrayList<String> back1;
    private ArrayList<String> back2;
    private ArrayList<String> back3;
    private ArrayList<String> backusl;
    private ArrayList<String> backavg;
    private int timeout;
    private ProgressDialog dialog;
    private TinyDB cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("成績查詢");
        cache=new TinyDB("grade-cache",this);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //region TabLayout
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(pagerAdapter);
        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);
        mTabs.getTabAt(0).setText("月考成績");
        mTabs.getTabAt(1).setText("學期成績");
        //endregion
    }




    class SamplePagerAdapter extends PagerAdapter {


        //region noUseFun
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        //endregion

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                //月考成績
                final View view = getLayoutInflater().inflate(R.layout.tab_grade,
                        container, false);
                timeout = 0;
                final int position1 = position;
                //網路連線
                if(networkInfo()) {
                    new Thread(new Runnable() {
                        @Override
                        public final void run() {
                            networkRun(view, position1);
                        }
                    }).start();
                }else {
                    grade=cache.getListString("grade");
                    front1=cache.getListString("front1");
                    front2=cache.getListString("front2");
                    front3=cache.getListString("front3");
                    frontusl=cache.getListString("frontusl");
                    frontavg=cache.getListString("frontavg");
                    back1=cache.getListString("back1");
                    back2=cache.getListString("back2");
                    back3=cache.getListString("back3");
                    backusl=cache.getListString("backusl");
                    backavg=cache.getListString("backavg");
                    //上學期第一次段考
                    writeInUI(view, grade, front1, R.id.front1);
                    //上學期第二次段考
                    writeInUI(view, grade, front2, R.id.front2);
                    //上學期第三次段考
                    writeInUI(view, grade, front3, R.id.front3);
                    //上學期平時成績
                    writeInUI(view, grade, frontusl, R.id.frontusl);
                    //上學期期末平均
                    writeInUI(view, grade, frontavg, R.id.frontavg);

                    //下學期第一次段考
                    writeInUI(view, grade, back1, R.id.back1);
                    //下學期第二次段考
                    writeInUI(view, grade, back2, R.id.back2);
                    //下學期第三次段考
                    writeInUI(view, grade, back3, R.id.back3);
                    //下學期平時成績
                    writeInUI(view, grade, backusl, R.id.backusl);
                    //下學期期末平均
                    writeInUI(view, grade, backavg, R.id.backavg);
                }
                container.addView(view);
                return view;
            } else {
                //學期成績
                final View view = getLayoutInflater().inflate(R.layout.tab_grade,
                        container, false);
                container.addView(view);
                return view;
            }
        }
    }

    //網路連線
    private void networkRun(final View view, final int postion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = ProgressDialog.show(GradeActivity.this, "讀取網路中", "請稍後");
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException c) {/**/}
        if (postion == 0) {
            try {
                trustEveryone();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
                //region 連線
                preference = getSharedPreferences("setting", 0);
                String stu_id = preference.getString("stu_id", "");
                String stu_pwd = preference.getString("stu_pwd", "");
                Response res = Jsoup
                        .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                        .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                        .method(Method.POST)
                        .timeout(5000)
                        .execute();

                Map<String, String> loginCookies = res.cookies();
                //抓取資料分析並儲存
                Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/stscore.asp")
                        .timeout(5000)
                        .cookies(loginCookies)
                        .get();
                Elements temps = doc.select("tbody");
                temps = temps.select("tr");
                grade = new ArrayList<String>() {};
                front1 = new ArrayList<String>() {};
                front2 = new ArrayList<String>() {};
                front3 = new ArrayList<String>() {};
                frontusl = new ArrayList<String>() {};
                frontavg = new ArrayList<String>() {};
                back1 = new ArrayList<String>() {};
                back2 = new ArrayList<String>() {};
                back3 = new ArrayList<String>() {};
                backusl = new ArrayList<String>() {};
                backavg = new ArrayList<String>() {};
                for (int i = 2; i < temps.size(); i++) {
                    Elements temp = temps.get(i).select("td");
                    grade.add(temp.get(0).text());
                    front1.add(temp.get(2).text());
                    front2.add(temp.get(3).text());
                    front3.add(temp.get(4).text());
                    frontusl.add(temp.get(5).text());
                    frontavg.add(temp.get(6).text());
                    back1.add(temp.get(7).text());
                    back2.add(temp.get(8).text());
                    back3.add(temp.get(9).text());
                    backusl.add(temp.get(10).text());
                    backavg.add(temp.get(10).text());
                }
                //endregion

                //region cacheWrite
                cache.putListString("grade",grade);
                cache.putListString("front1",front1);
                cache.putListString("front2",front2);
                cache.putListString("front3",front3);
                cache.putListString("frontusl",frontusl);
                cache.putListString("frontavg",frontavg);
                cache.putListString("back1",back1);
                cache.putListString("back2",back2);
                cache.putListString("back3",back3);
                cache.putListString("backusl",backusl);
                cache.putListString("backavg",backavg);
                //endregion

                //region 填入UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //上學期第一次段考
                        writeInUI(view, grade, front1, R.id.front1);
                        //上學期第二次段考
                        writeInUI(view, grade, front2, R.id.front2);
                        //上學期第三次段考
                        writeInUI(view, grade, front3, R.id.front3);
                        //上學期平時成績
                        writeInUI(view, grade, frontusl, R.id.frontusl);
                        //上學期期末平均
                        writeInUI(view, grade, frontavg, R.id.frontavg);

                        //下學期第一次段考
                        writeInUI(view, grade, back1, R.id.back1);
                        //下學期第二次段考
                        writeInUI(view, grade, back2, R.id.back2);
                        //下學期第三次段考
                        writeInUI(view, grade, back3, R.id.back3);
                        //下學期平時成績
                        writeInUI(view, grade, backusl, R.id.backusl);
                        //下學期期末平均
                        writeInUI(view, grade, backavg, R.id.backavg);
                    }
                });
                dialog.dismiss();
                //endregion
            } catch (IOException e) {
                //region retry
                dialog.dismiss();
                timeout++;
                if (timeout < 5) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException c) {/**/}
                    networkRun(view, postion);
                } else {
                    runOnUiThread(new Runnable() {
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

    //填入UI
    private void writeInUI(View view, ArrayList<String> grade, ArrayList<String> num, int postion) {
        int pixels;//dp
        LinearLayout linearLayout = (LinearLayout) view.findViewById(postion);
        int sum = 0;
        ArrayList<String> gradet = new ArrayList<String>() {
        };
        ArrayList<String> frontt = new ArrayList<String>() {
        };
        for (int i = 0; i < num.size(); i++) {
            if (tryParseInt(num.get(i))) {
                sum++;
                gradet.add(grade.get(i));
                frontt.add(num.get(i));
            }
        }
        int rows = sum / 5 + 1;
        for (int y = 0; y < rows; y++) {//forRow
            LinearLayout row = new LinearLayout(GradeActivity.this);
            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(txtParams);
            row.setGravity(Gravity.CENTER);
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = y * 5; i < y * 5 + 5; i++) {//forLine
                if (i < gradet.size()) {
                    //region forLine
                    LinearLayout line = new LinearLayout(GradeActivity.this);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    line.setLayoutParams(txtParams);
                    line.setOrientation(LinearLayout.VERTICAL);

                    TextView txtsubject = new TextView(GradeActivity.this);
                    txtParams = new LinearLayout.LayoutParams(pixels, pixels);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    txtParams.setMargins(pixels, 0, pixels, 0);
                    txtsubject.setLayoutParams(txtParams);
                    txtsubject.setText(gradet.get(i));
                    txtsubject.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.black));
                    txtsubject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    line.addView(txtsubject);

                    TextView txtgrade = new TextView(GradeActivity.this);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    txtParams = new LinearLayout.LayoutParams(pixels, pixels);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    txtParams.setMargins(pixels, -pixels, pixels, pixels);
                    txtgrade.setLayoutParams(txtParams);
                    txtgrade.setText(frontt.get(i));
                    txtgrade.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.white));
                    txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    int draw = R.drawable.grade_bg_black;
                    try {
                        if (Integer.parseInt(frontt.get(i)) < 60) {
                            draw = R.drawable.grade_bg_red;
                        } else if (Integer.parseInt(frontt.get(i)) < 70) {
                            draw = R.drawable.grade_bg_yellow;
                        } else if (Integer.parseInt(frontt.get(i)) <= 100) {
                            draw = R.drawable.grade_bg_green;
                        }
                    } catch (Exception e) {/**/}
                    txtgrade.setBackground(ContextCompat.getDrawable(GradeActivity.this, draw));
                    txtgrade.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    line.addView(txtgrade);
                    //endregion
                    row.addView(line);
                } else {
                    break;
                }
            }
            linearLayout.addView(row);
        }
    }

    //判斷數字
    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
