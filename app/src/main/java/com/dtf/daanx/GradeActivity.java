package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.quentindommerc.superlistview.SuperListview;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GradeActivity extends BaseActivity {

    TabLayout mTabs;
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
    ArrayList<Grades> gradesFont;
    ArrayList<Grades> gradesLast;
    boolean repeat;

    private String[] choose = {"一年級","二年級","三年級","四年級","畢業"};
    private String[] post={"1","2","3","4","G"};
    private String choosen;

    GradeAdapter gradeAdapter;
    TinyDB first;

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
        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(tab.getText().toString().equals("學期成績")){
                            if(first.getInt("num")==1){
                                ViewTarget target = new ViewTarget(R.id.frontList,GradeActivity.this);
                                new ShowcaseView.Builder(GradeActivity.this)
                                        .setTarget(target)
                                        .withNewStyleShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme2)
                                        .setContentTitle("條列")
                                        .setContentText("上學期下學期 可點擊摺疊查看\n下拉選單可選擇年級\n綠色整列為拿到學分\n紅色為沒拿到學分\n藍色為統計")
                                        .hideOnTouchOutside()
                                        .blockAllTouches()
                                        .build();
                            }
                        }
                    }
                }, 100);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //endregion

        first=new TinyDB("first-grade",this);
        first.putInt("num",first.getInt("num")+1);
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
                            try {
                                networkRun(view, position1);
                            }catch (Exception e){/**/}
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
                final View view = getLayoutInflater().inflate(R.layout.tab_grade_sence,
                        container, false);

                Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                ArrayAdapter<String> List = new ArrayAdapter<String>(GradeActivity.this, android.R.layout.simple_spinner_dropdown_item, choose){};
                spinner.setAdapter(List);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        choosen=post[position];
                        timeout = 0;
                        //網路連線
                        if(networkInfo()) {
                            new Thread(new Runnable() {
                                @Override
                                public final void run() {
                                    try {
                                        networkRun(view, 1);
                                    }catch (Exception e){/**/}
                                }
                            }).start();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {}
                });
                container.addView(view);
                return view;
            }
        }
    }

    //網路連線
    private void networkRun(final View view, final int postion) {
        if (postion == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(GradeActivity.this, "讀取網路中", "請稍後");
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException c) {/**/}
            try {
                trustTaivs();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
                //region 連線
                preference = getSharedPreferences("setting", 0);
                String stu_id = preference.getString("stu_id", "");
                String stu_pwd = pwdNoSecure(preference.getString("stu_pwd", ""));
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

                        if(first.getInt("num")==1){
                            ViewTarget target = new ViewTarget(R.id.front1,GradeActivity.this);
                            new ShowcaseView.Builder(GradeActivity.this)
                                    .setTarget(target)
                                    .withNewStyleShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme2)
                                    .setContentTitle("圓圈")
                                    .setContentText("你的成績將出現在圈圈裡\n紅色為不及格\n黃色為60~70\n綠色為70~100")
                                    .hideOnTouchOutside()
                                    .blockAllTouches()
                                    .build();
                        }
                    }
                });
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                //dialog.dismiss();
                //endregion
            } catch (IOException e) {
                //region retry
                dialog.dismiss();
                timeout++;
                if (timeout < 5) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        Thread.sleep(5000);
                        networkRun(view, postion);
                    } catch (Exception c) {/**/}
                } else {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }catch (Exception q){/**/}
                }
                //endregion
            }
        }else{
            try {
                repeat=false;
                if(!dialog.isShowing()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            repeat=true;
                            dialog = ProgressDialog.show(GradeActivity.this, "讀取網路中", "請稍後");
                        }
                    });
                }
                trustTaivs();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
                //region 連線
                preference = getSharedPreferences("setting", 0);
                String stu_id = preference.getString("stu_id", "");
                String stu_pwd = pwdNoSecure(preference.getString("stu_pwd", ""));
                Response res = Jsoup
                        .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                        .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                        .method(Method.POST)
                        .timeout(5000)
                        .execute();

                Map<String, String> loginCookies = res.cookies();
                //抓取資料分析並儲存
                Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/stusn.asp")
                        .cookies(loginCookies)
                        .data("GRA", choosen)
                        .post();

                Elements temps=doc.select("tr[onmouseout=OMOut(this);]");

                gradesFont=new ArrayList<Grades>(){};
                gradesLast=new ArrayList<Grades>(){};
                Grades grdTemp = new Grades();
                grdTemp.subject="課程";
                grdTemp.subClass="類別";
                grdTemp.subNum="學分";
                grdTemp.score="分數";
                grdTemp.reScore="補考";
                grdTemp.reStudy="重修";
                grdTemp.minScore="及格";
                gradesFont.add(grdTemp);
                gradesLast.add(grdTemp);

                Elements temp;

                for(int i=0;i<temps.size();i++){
                    temp=temps.get(i).select("td");
                    if(!(temp.get(1).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(2).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(3).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(4).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(5).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(6).text().trim().replaceAll("\\s+", "").equals(""))) {
                        grdTemp = new Grades();
                        grdTemp.subject = temp.get(0).text().trim().replaceAll(" ", "");
                        grdTemp.subClass = temp.get(1).text().trim().replaceAll("\\s+", "");
                        grdTemp.subNum = temp.get(2).text().trim().replaceAll("\\s+", "");
                        grdTemp.score = temp.get(3).text().trim().replaceAll("\\s+", "");
                        grdTemp.reScore = temp.get(4).text().trim().replaceAll("\\s+", "");
                        grdTemp.reStudy = temp.get(5).text().trim().replaceAll("\\s+", "");
                        grdTemp.minScore = temp.get(6).text().trim().replaceAll("\\s+", "");
                        gradesFont.add(grdTemp);
                    }
                    if(!(temp.get(7).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(8).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(9).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(10).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(11).text().trim().replaceAll("\\s+", "").equals("")&&temp.get(12).text().trim().replaceAll("\\s+", "").equals(""))) {
                        grdTemp = new Grades();
                        grdTemp.subject = temp.get(0).text().trim().replaceAll("\\s+", "");
                        grdTemp.subClass = temp.get(7).text().trim().replaceAll("\\s+", "");
                        grdTemp.subNum = temp.get(8).text().trim().replaceAll("\\s+", "");
                        grdTemp.score = temp.get(9).text().trim().replaceAll("\\s+", "");
                        grdTemp.reScore = temp.get(10).text().trim().replaceAll("\\s+", "");
                        grdTemp.reStudy = temp.get(11).text().trim().replaceAll("\\s+", "");
                        grdTemp.minScore = temp.get(12).text().trim().replaceAll("\\s+", "");
                        gradesLast.add(grdTemp);
                    }
                }
                Log.i("status", gradesFont.get(0).minScore);

                //endregion

                //region 填入UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeList(view);
                    }
                });
                if(repeat){
                    dialog.dismiss();
                }
                //endregion
            } catch (IOException e) {
                //region retry
                dialog.dismiss();
                timeout++;
                if (timeout < 5) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        Thread.sleep(5000);
                        networkRun(view, postion);
                    } catch (Exception c) {/**/}

                } else {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }catch (Exception q){/**/}
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
                    txtsubject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    txtsubject.setText(gradet.get(i));
                    txtsubject.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.black));
                    txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    line.addView(txtsubject);

                    TextView txtgrade = new TextView(GradeActivity.this);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    txtParams = new LinearLayout.LayoutParams(pixels, pixels);
                    pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
                    txtParams.setMargins(pixels, -pixels, pixels, pixels);
                    txtgrade.setLayoutParams(txtParams);
                    txtgrade.setText(frontt.get(i));
                    if(frontt.get(i).length()>2){
                        txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    }else {
                        txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    }
                    txtgrade.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.white));
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
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        txtgrade.setBackgroundDrawable(ContextCompat.getDrawable(GradeActivity.this, draw));
                    } else {
                        txtgrade.setBackground(ContextCompat.getDrawable(GradeActivity.this, draw));
                    }
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

    private void writeList(View view){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, metrics.heightPixels/2);
        SuperListview listView = (SuperListview) view.findViewById(R.id.frontList);
        gradeAdapter=new GradeAdapter(getApplication(),gradesFont);
        listView.setLayoutParams(layoutParams);
        listView.setAdapter(gradeAdapter);
        listView.setVisibility(View.VISIBLE);
        listView = (SuperListview) view.findViewById(R.id.lastList);
        gradeAdapter=new GradeAdapter(getApplication(),gradesLast);
        listView.setLayoutParams(layoutParams);
        listView.setAdapter(gradeAdapter);
        listView.setVisibility(View.GONE);
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

    boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static class Grades{
        public String subject;
        public String subClass;
        public String subNum;
        public String score;
        public String reScore;
        public String reStudy;
        public String minScore;
    }

    public class GradeAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Grades> list;

        public GradeAdapter(Context context,ArrayList<Grades> lists){
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
                convertView=inflater.inflate(R.layout.grade_row,null);
                Log.i("status","if");

                holder=new ViewHolder();
                holder.line=(LinearLayout) convertView.findViewById(R.id.line);
                holder.subject=(TextView) convertView.findViewById(R.id.subject);
                holder.subClass=(TextView) convertView.findViewById(R.id.subClass);
                holder.subNum=(TextView) convertView.findViewById(R.id.subNum);
                holder.score=(TextView) convertView.findViewById(R.id.score);
                holder.reScore=(TextView) convertView.findViewById(R.id.reScore);
                holder.reStudy=(TextView) convertView.findViewById(R.id.reStudy);
                holder.minScore=(TextView) convertView.findViewById(R.id.minScore);

                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
                Log.i("status","else");
            }

            Grades gradeList=list.get(position);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            float d=metrics.widthPixels;

            holder.subject.setText(gradeList.subject);
            if(gradeList.subject.length()>4) {holder.subject.setTextSize(15);}
            else if(gradeList.subject.length()>6) {holder.subject.setTextSize(10);}
            else {holder.subject.setTextSize(20);}
            holder.subject.setWidth((int) d / 8);
            holder.subClass.setText(gradeList.subClass);
            holder.subClass.setWidth((int) d / 8);
            holder.subNum.setText(gradeList.subNum);
            holder.subNum.setWidth((int) d / 8);
            holder.score.setText(gradeList.score);
            holder.score.setWidth((int) d / 8);
            holder.reScore.setText(gradeList.reScore);
            holder.reScore.setWidth((int) d / 8);
            holder.reStudy.setText(gradeList.reStudy);
            holder.reStudy.setWidth((int) d / 8);
            holder.minScore.setText(gradeList.minScore);
            holder.minScore.setWidth((int)d/8);
            Double lastScore=0.0;
            if(tryParseDouble(gradeList.reStudy)){
                lastScore=Double.parseDouble(gradeList.reStudy);
            }else if(tryParseDouble(gradeList.reScore)){
                lastScore=Double.parseDouble(gradeList.reScore);
            }else if(tryParseDouble(gradeList.score)){
                lastScore=Double.parseDouble(gradeList.score);
            }
            if(lastScore==0.0){
                holder.line.setBackgroundColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
            }
            else if(tryParseDouble(gradeList.minScore)){
                if(lastScore>=Double.parseDouble(gradeList.minScore)){
                    holder.line.setBackgroundColor(ContextCompat.getColor(GradeActivity.this,R.color.grade_go));
                }else {
                    holder.line.setBackgroundColor(ContextCompat.getColor(GradeActivity.this,R.color.red_A200));
                }
            }
            if(gradeList.subject.equals("課程")){
                holder.subject.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.subClass.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.subNum.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.score.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.reScore.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.black));
                holder.reStudy.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.minScore.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.black));
            }else{
                holder.subject.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.black));
                holder.subClass.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
                holder.subNum.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
                holder.score.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
                holder.reScore.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
                holder.reStudy.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
                holder.minScore.setTextColor(ContextCompat.getColor(GradeActivity.this,R.color.white));
            }
            if(gradeList.minScore.equals("")&&!gradeList.score.equals("")){
                holder.line.setBackgroundColor(ContextCompat.getColor(GradeActivity.this, R.color.grade_sence));
            }
            return convertView;
        }

        private class ViewHolder{
            LinearLayout line;
            TextView subject;
            TextView subClass;
            TextView subNum;
            TextView score;
            TextView reScore;
            TextView reStudy;
            TextView minScore;
        }
    }

    public void tabSwitch(View v){
        SuperListview listView = (SuperListview) findViewById(R.id.frontList);
        CardView cardView;
        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        layoutParams2.setMargins(0,pixels,0,0);
        if(listView.isShown()){
            listView = (SuperListview) findViewById(R.id.frontList);
            listView.setVisibility(View.GONE);
            listView = (SuperListview) findViewById(R.id.lastList);
            listView.setVisibility(View.VISIBLE);
            cardView=(CardView) findViewById(R.id.card_front);
            cardView.setLayoutParams(layoutParams1);
            cardView=(CardView) findViewById(R.id.card_last);
            cardView.setLayoutParams(layoutParams2);
        }else{
            listView = (SuperListview) findViewById(R.id.frontList);
            listView.setVisibility(View.VISIBLE);
            listView = (SuperListview) findViewById(R.id.lastList);
            listView.setVisibility(View.GONE);
            cardView=(CardView) findViewById(R.id.card_front);
            cardView.setLayoutParams(layoutParams1);
            cardView=(CardView) findViewById(R.id.card_last);
            cardView.setLayoutParams(layoutParams2);
        }
    }
}
