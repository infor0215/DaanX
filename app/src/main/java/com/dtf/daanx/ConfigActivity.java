package com.dtf.daanx;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ConfigActivity extends BaseActivity {

    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preference=getSharedPreferences("setting",0);


        findViewById(R.id.btn_year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText=new EditText(ConfigActivity.this);
                new AlertDialog.Builder(ConfigActivity.this)
                        .setTitle("請輸入畢業學年")
                        .setView(editText)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor=preference.edit();
                                editor.putString("stu_year",editText.getText().toString().replace("\n",""));
                                editor.apply();
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.btn_nick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText=new EditText(ConfigActivity.this);
                new AlertDialog.Builder(ConfigActivity.this)
                        .setTitle("請輸入修改暱稱")
                        .setView(editText)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Connection.Response res = Jsoup
                                                    .connect("https://api.dacsc.club/daany/register/nick")
                                                    .data("auth", preference.getString("auth", ""),"nick",editText.getText().toString())
                                                    .method(Connection.Method.POST)
                                                    .timeout(5000)
                                                    .execute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }catch (final Exception e){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "修改失敗 請稍後再試\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                }).start();
                            }
                        })
                        .show();
            }
        });
    }

}
