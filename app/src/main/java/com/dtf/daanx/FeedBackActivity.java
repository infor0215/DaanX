package com.dtf.daanx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class FeedBackActivity extends AppCompatActivity {

    Thread thread;
    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preference=getSharedPreferences("setting", 0);

    }

    public void btn_feedback(View view) {
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

        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.Response res = Jsoup
                            .connect("https://api.dacsc.club/daanx/feedback")
                            .data("auth", preference.getString("auth", ""), "class", feedClasss, "commit", commit)
                            .method(Connection.Method.POST)
                            .timeout(5000)
                            .execute();
                }catch (Exception e){
                    /**/
                }

            }
        });
        thread.start();
    }
}
