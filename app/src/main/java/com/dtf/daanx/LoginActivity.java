package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

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
    TinyDB first;
    private int timeout;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TinyDB cache=new TinyDB("main-cache",this);
        cache.putString("main","main");
        first=new TinyDB("first-login",this);
        first.putInt("num",first.getInt("num")+1);
        preference=getSharedPreferences("setting",0);
        String stu_id=preference.getString("stu_id","");
        timeout=0;
        if(!stu_id.equals("")){//判斷是否第一次開啟App
            //過場動畫
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_login);
            setContentView(contentView=View.inflate(this, R.layout.activity_flag, null));

            time= System.currentTimeMillis();
            //切換到主界面
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if(networkInfo()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("json", networkRun(contentView, "https://api.dacsc.club/daanx/main"));
                        intent.putExtras(bundle);
                    }
                    long now=System.currentTimeMillis();
                    if(now-time<1500){
                        try {
                            Thread.sleep((1500-(now-time)));
                        }catch (Exception e){/**/}
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            LoginActivity.this.finish();
                        }
                    });
                }
            }).start();
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

            if(first.getInt("num")==1) {
                ViewTarget target = new ViewTarget(R.id.stu_id, this);
                new ShowcaseView.Builder(this)
                        .setTarget(target)
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .withNewStyleShowcase()
                        .blockAllTouches()
                        .setContentTitle("輸入說明")
                        .setContentText("學號與身分證字號為查詢成績用\n系統不會上傳身分證字號至伺服器\n畢業年分請打民國年\n現在三年級請填入105 依此類推\n其他為論壇使用")
                        .hideOnTouchOutside()
                        .build();
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
        if(networkInfo()&&!stu_year.trim().equals("")&&!stu_nick.trim().equals("")&&!stu_email.trim().equals("")) {
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
                        trustTaivs();
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
                            Log.i("status","login");
                            trustDacsc();
                            Document server=Jsoup.connect("https://api.dacsc.club/daany/register")
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
        }else if(!networkInfo()){
            networkAlert();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("欄位不能空白");
            builder.setTitle("空白");
            builder.setCancelable(false);
            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
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
                Log.i("status", "success");
            }
        });
        SharedPreferences.Editor editor=preference.edit();
        editor.putString("stu_id",stu_id);
        editor.putString("stu_year",stu_year);
        editor.putString("stu_nick",stu_nick);
        editor.putString("stu_class",stu_class);
        editor.putString("stu_name",stu_name);
        editor.putString("stu_tea",stu_tea);
        editor.putString("stu_num",stu_num);
        editor.putString("stu_email",stu_email);
        editor.putString("auth",auth);
        editor.apply();

        editor.putString("stu_pwd",pwdSecure(stu_pwd));
        editor.apply();
        //region 進入主界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("json",networkRun(contentView,"https://api.dacsc.club/daanx/main"));
                intent.putExtras(bundle);
                try {
                    Thread.sleep(500);
                }catch (Exception e){/**/}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        LoginActivity.this.finish();
                    }
                });
            }
        }).start();
        //endregion
    }

    public String networkRun(final View view,String url){
        try {
            trustDacsc();
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .data("auth", preference.getString("auth", ""))
                    .get();
            Log.i("json", doc.select("body").text());
            return doc.select("body").text();
        }catch (Exception e){
            Log.w("net error",e.toString());
            timeout++;
            if (timeout < 5) {
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                        }catch (Exception e){/**/}
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException c) {/**/}
                return networkRun(view,url);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                        }catch (Exception e){/**/}
                    }
                });
                return "";
            }
        }
    }

}
