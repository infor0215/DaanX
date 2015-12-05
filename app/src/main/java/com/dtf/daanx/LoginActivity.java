package com.dtf.daanx;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends BaseActivity {

    SharedPreferences preference;
    ProgressDialog dialog;
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preference=getSharedPreferences("setting",0);
        String stu_id=preference.getString("stu_id","noid");
        if(!stu_id.equals("noid")){
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_login);
            setContentView(contentView=View.inflate(this, R.layout.activity_flag, null));
            /*
            try{
                Thread.sleep(1500);
            }catch(InterruptedException c){}
            Intent intent=new Intent();
            intent.setClass(LoginActivity.this,MainActivity.class);
            LoginActivity.this.finish();
            startActivity(intent);*/
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
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_login);
            setContentView(contentView=View.inflate(this, R.layout.activity_login, null));
            if(getSupportActionBar()!=null) getSupportActionBar().hide();
        }

    }

    public void btn_login(View view){
        Log.i("status","btn");
        final String stu_id=((TextView)findViewById(R.id.stu_id)).getText().toString();
        final String stu_pwd=((TextView)findViewById(R.id.stu_pwd)).getText().toString();
        final String stu_year=((TextView)findViewById(R.id.stu_year)).getText().toString();
        final String stu_nick=((TextView)findViewById(R.id.stu_nick)).getText().toString();
        final String stu_email=((TextView)findViewById(R.id.stu_email)).getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("status","thread");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog= ProgressDialog.show(LoginActivity.this, "驗證中", "請稍後");
                    }
                });
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException c){/**/}
                try {
                    trustEveryone();//關掉ssl憑証檢查 與確定使用ssl加密協定版本
                    /*連線 開始*/
                    Connection.Response res = Jsoup
                            .connect("https://stuinfo.taivs.tp.edu.tw/Reg_Stu.ASP")
                            .data("txtS_NO", stu_id, "txtPerno", stu_pwd)
                            .method(Connection.Method.POST)
                            .timeout(5000)
                            .execute();
                    if(Integer.parseInt(res.header("Content-Length"))<2500){
                        Map<String, String> loginCookies = res.cookies();
                        Document doc = Jsoup.connect("https://stuinfo.taivs.tp.edu.tw/stu_0.ASP")
                                .cookies(loginCookies)
                                .get();
                        Elements temp=doc.select("b");
                        String stu_class=temp.get(1).text().substring(3, temp.get(1).text().length());
                        String stu_name=temp.get(4).text().substring(3, temp.get(4).text().length());
                        String stu_tea=temp.get(0).text().substring(3, temp.get(0).text().length());
                        String stu_num=temp.get(2).text().substring(3, temp.get(2).text().length());
                        /*
                        System.out.println("班級"+temp.get(1).text().substring(3, temp.get(1).text().length()));
                        System.out.println("姓名"+temp.get(4).text().substring(3, temp.get(4).text().length()));
                        System.out.println("老師"+temp.get(0).text().substring(3, temp.get(0).text().length()));
                        System.out.println("座號"+temp.get(2).text().substring(3, temp.get(2).text().length()));*/
                        writepreference(stu_id,stu_pwd,stu_year,stu_nick,stu_class,stu_name,stu_tea,stu_num,stu_email);
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(contentView, "學號或密碼錯誤 請重新嘗試", Snackbar.LENGTH_LONG).show();
                                //Toast.makeText(LoginActivity.this, "學號或密碼錯誤 請重新嘗試", Toast.LENGTH_LONG).show();
                                Log.i("status","pwd error");
                            }
                        });
                    }
                    /*連線 結束*/
                    dialog.dismiss();
                    /*填入UI中 結束*/
                }catch (IOException e){
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(contentView, "系統錯誤 請重新嘗試", Snackbar.LENGTH_LONG).show();
                            //Toast.makeText(LoginActivity.this, "系統錯誤 請重新嘗試", Toast.LENGTH_LONG).show();
                            Log.i("status","system error");
                        }
                    });
                }
            }
        }).start();
    }

    public void writepreference(String stu_id,String stu_pwd,String stu_year,String stu_nick,String stu_class,String stu_name,String stu_tea,String stu_num,String stu_email){
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
        editor.apply();
        try{
            Thread.sleep(1000);
        }catch(InterruptedException c){/**/}
        Intent intent=new Intent();
        intent.setClass(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }




    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLSV1");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

}
