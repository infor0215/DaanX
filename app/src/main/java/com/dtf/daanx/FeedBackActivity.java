package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class FeedBackActivity extends BaseActivity {

    Thread thread;
    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("意見回饋");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preference=getSharedPreferences("setting", 0);

    }

    public void btn_feedback(View view) {
        final ProgressDialog dialog = ProgressDialog.show(FeedBackActivity.this, "送出中", "請稍後");
        String feedClass="";
        RadioGroup rg = (RadioGroup)findViewById(R.id.rd_group);
        switch(rg.getCheckedRadioButtonId()){
            case R.id.rd_lag:
                feedClass="lag問題";
                break;
            case R.id.rd_crash:
                feedClass="crash問題";
                break;
            case R.id.rd_lose:
                feedClass="功能失效";
                break;
            case R.id.rd_sug:
                feedClass="建議";
                break;
        }
        final String feedClasss=feedClass;
        final String commit=((EditText)findViewById(R.id.fb_commit)).getText().toString();

        String systemtmp="";
        systemtmp+="安卓版本："+Build.VERSION.RELEASE+"\n";
        systemtmp+="制造商：" + Build.MANUFACTURER + "\n手机型号：" + Build.MODEL + " " + Build.BRAND+"\n";
        systemtmp+="設備識別碼："+Build.FINGERPRINT;

        final String system=systemtmp;

        Log.i("status",feedClass);
        Log.i("status",commit);
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response res = Jsoup
                            .connect("https://api.dacsc.club/daanx/feedback")
                            .data("auth", preference.getString("auth", ""), "class", feedClasss, "commit", commit,"system",system)
                            .method(Connection.Method.POST)
                            .timeout(5000)
                            .execute();
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "傳送成功", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            FeedBackActivity.this.finish();
                        }
                    });
                }catch (final Exception e){
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "傳送失敗 請稍後再試\n" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }
}
