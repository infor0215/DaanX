package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    SharedPreferences preference;
    ProgressDialog dialog;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TinyDB cache=new TinyDB("main-cache",this);
        cache.putString("main","main");
        preference=getSharedPreferences("setting",0);
        String stu_id=preference.getString("stu_id","");
        if(!stu_id.equals("")){//判斷是否第一次開啟App
            //過場動畫
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_login);
            setContentView(contentView=View.inflate(this, R.layout.activity_flag, null));

            //切換到主界面
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    LoginActivity.this.finish();
                }
            }, 1000);
        }else{
            //註冊畫面
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_login);
            setContentView(contentView=View.inflate(this, R.layout.activity_login, null));
            if(getSupportActionBar()!=null) getSupportActionBar().hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }

    }

    public void btn_login(View view){
        //註冊
        Log.i("status","btn");
        //region 使用者輸入資料
        final String stu_id=((TextView)findViewById(R.id.stu_id)).getText().toString();
        final String stu_pwd=((TextView)findViewById(R.id.stu_pwd)).getText().toString();
        final String stu_year=((TextView)findViewById(R.id.stu_year)).getText().toString();
        final String stu_nick=((TextView)findViewById(R.id.stu_nick)).getText().toString();
        final String stu_email=((TextView)findViewById(R.id.stu_email)).getText().toString();
        //endregion
        //region 連線
        if(networkInfo()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("status", "thread");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog = ProgressDialog.show(LoginActivity.this, "驗證中", "請稍後");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException c) {/**/}
                    try {
                        trustEveryone();
                        Connection.Response res = Jsoup
                                .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                                .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                                .method(Connection.Method.POST)
                                .timeout(5000)
                                .execute();
                        if (Integer.parseInt(res.header("Content-Length")) < 2500) {
                            Map<String, String> loginCookies = res.cookies();
                            Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/stu_0.ASP")
                                    .cookies(loginCookies)
                                    .timeout(5000)
                                    .get();
                            Elements temp = doc.select("b");
                            String stu_class = temp.get(1).text().substring(3, temp.get(1).text().length());
                            String stu_name = temp.get(4).text().substring(3, temp.get(4).text().length());
                            String stu_tea = temp.get(0).text().substring(3, temp.get(0).text().length());
                            String stu_num = temp.get(2).text().substring(3, temp.get(2).text().length());
                            //送往伺服器
                            Document server=Jsoup.connect("https://api.dacsc.club/daanx/register")
                                    .data("stu_name",stu_name,"stu_id",stu_id,"stu_nick",stu_nick,"stu_email",stu_email)
                                    .timeout(5000)
                                    .post();
                            String auth=server.text();
                            //寫入設定檔
                            writepreference(stu_id, stu_pwd, stu_year, stu_nick, stu_class, stu_name, stu_tea, stu_num, stu_email,auth);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(contentView, "學號或密碼錯誤 請重新嘗試", Snackbar.LENGTH_LONG).show();
                                    //Toast.makeText(LoginActivity.this, "學號或密碼錯誤 請重新嘗試", Toast.LENGTH_LONG).show();
                                    Log.i("status", "pwd error");
                                }
                            });
                        }
                        dialog.dismiss();
                    } catch (final IOException e) {
                        dialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(contentView, "系統錯誤 請重新嘗試\n"+e.toString(), Snackbar.LENGTH_LONG).show();
                                //Toast.makeText(LoginActivity.this, "系統錯誤 請重新嘗試", Toast.LENGTH_LONG).show();
                                Log.i("status", "system error");
                            }
                        });
                    }
                }
            }).start();
        }else{
            networkAlert();
        }
        //endregion
    }

    //寫入設定檔
    public void writepreference(String stu_id,String stu_pwd,String stu_year,String stu_nick,String stu_class,String stu_name,String stu_tea,String stu_num,String stu_email,String auth){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(contentView, "登入成功 1秒後轉入主界面", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_LONG).show();
                Log.i("status","success");
            }
        });
        SharedPreferences.Editor editor=preference.edit();
        editor.putString("stu_id",stu_id);
        editor.putString("stu_pwd",stu_pwd);
        editor.putString("stu_year",stu_year);
        editor.putString("stu_nick",stu_nick);
        editor.putString("stu_class",stu_class);
        editor.putString("stu_name",stu_name);
        editor.putString("stu_tea",stu_tea);
        editor.putString("stu_num",stu_num);
        editor.putString("stu_email",stu_email);
        editor.putString("auth",auth);
        editor.apply();
        //region 進入主界面
        try{
            Thread.sleep(1000);
        }catch(InterruptedException c){/**/}
        Intent intent=new Intent();
        intent.setClass(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
        //endregion
    }

}
