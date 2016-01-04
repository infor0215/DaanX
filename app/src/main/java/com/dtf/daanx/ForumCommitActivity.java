package com.dtf.daanx;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Calendar;

import jp.wasabeef.richeditor.RichEditor;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ForumCommitActivity extends BaseActivity {

    private RichEditor mEditor;
    SharedPreferences preference;
    boolean click;
    TinyDB first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_commit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("發表主題");
        setSupportActionBar(toolbar);

        click=false;

        preference=getSharedPreferences("setting", 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Bundle bundle = this.getIntent().getExtras();
        if(bundle.getString("type").equals("commit")){
            EditText title=(EditText)findViewById(R.id.txt_title);
            title.setVisibility(View.GONE);
        }

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(250);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(ContextCompat.getColor(ForumCommitActivity.this, R.color.black));
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("請輸入內容.....");
        mEditor.getSettings().setJavaScriptEnabled(true);
        mEditor.getSettings().setAllowFileAccess(true);


        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AmbilWarnaDialog dialog=new AmbilWarnaDialog(ForumCommitActivity.this, Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mEditor.setTextColor(color);
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final EditText editText=new EditText(ForumCommitActivity.this);
                new AlertDialog.Builder(ForumCommitActivity.this)
                        .setTitle("請輸入：圖片網址")
                        .setView(editText)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEditor.insertImage(editText.getText().toString(),
                                        "img");
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final EditText editText=new EditText(ForumCommitActivity.this);
                new AlertDialog.Builder(ForumCommitActivity.this)
                        .setTitle("請輸入：連結網址")
                        .setView(editText)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEditor.insertLink(editText.getText().toString(), editText.getText().toString());
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.youtube).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                final int width=mEditor.getWidth()-400;
//                Log.i("width",String.valueOf(width));
//                final int height= (int)(width/1.777777778);
                final EditText editText=new EditText(ForumCommitActivity.this);
                new AlertDialog.Builder(ForumCommitActivity.this)
                        .setTitle("請輸入：youtube影片編號 網址v=後面")
                        .setView(editText)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(mEditor.getHtml()!=null){
                                    mEditor.setHtml(mEditor.getHtml()+"<iframe width=\"100%\" src=\"https://www.youtube.com/embed/"+editText.getText().toString()+"\" frameborder=\"0\" allowfullscreen></iframe>");
                                }else {
                                    mEditor.setHtml("<iframe width=\"100%\" src=\"https://www.youtube.com/embed/"+editText.getText().toString()+"\" frameborder=\"0\" allowfullscreen></iframe>");
                                }

                            }
                        })
                        .show();
            }
        });

//        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.insertTodo();
//            }
//        });
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!click) {
                    click=true;
                    final ProgressDialog dialog = ProgressDialog.show(ForumCommitActivity.this, "送出中", "請稍後");
                    Gson gson = new Gson();
                    ForumPost submit = new ForumPost();
                    if (bundle.getString("type").equals("topic")) {
                        EditText title = (EditText) findViewById(R.id.txt_title);
                        if(title.getText().toString().trim().length()>3) {
                            submit.title = title.getText().toString();
                            submit.auth = preference.getString("auth", "");
                            byte[] data;
                            String tmp = "";
                            try {
                                data = mEditor.getHtml().getBytes("UTF-8");
                                tmp = new String(Base64.encode(data, Base64.DEFAULT), "UTF-8");
                            } catch (Exception e) {/**/}
                            submit.body = tmp;
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH) + 1;
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            submit.day = String.valueOf(year + "/" + month + "/" + day);
                            final String result = gson.toJson(submit);
                            Log.i("status", result);
                            Log.i("status", preference.getString("auth", ""));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Connection.Response res = Jsoup
                                                .connect("https://api.dacsc.club/daanx/forum/main")
                                                .data("auth", preference.getString("auth", ""), "json", result)
                                                .method(Connection.Method.POST)
                                                .timeout(5000)
                                                .execute();
                                        dialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "傳送成功", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                                ForumCommitActivity.this.finish();
                                            }
                                        });
                                        click = false;
                                    } catch (final Exception e) {
                                        dialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "傳送失敗 請稍後再試\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        click = false;
                                    }
                                }
                            }).start();
                        }else {
                            Toast.makeText(getApplicationContext(), "標題請超過四個字", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        submit.auth = preference.getString("auth", "");
                        byte[] data = new byte[10];
                        String tmp = "";
                        try {
                            data = mEditor.getHtml().getBytes("UTF-8");
                            tmp = new String(Base64.encode(data, Base64.DEFAULT), "UTF-8");
                        } catch (Exception e) {/**/}
                        submit.body = tmp;
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        submit.day = String.valueOf(year + "/" + month + "/" + day);
                        final String result = gson.toJson(submit);
                        Log.i("status", result);
                        Log.i("status", preference.getString("auth", ""));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Connection.Response res = Jsoup
                                            .connect("https://api.dacsc.club/daanx/forum/main/id/" + bundle.getString("id"))
                                            .data("auth", preference.getString("auth", ""), "json", result)
                                            .method(Connection.Method.POST)
                                            .timeout(5000)
                                            .execute();
                                    dialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "傳送成功", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                            ForumCommitActivity.this.finish();
                                            click=false;
                                        }
                                    });
                                } catch (final Exception e) {
                                    dialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "傳送失敗 請稍後再試\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    click=false;
                                }
                            }
                        }).start();
                    }
                }
            }
        });

        first=new TinyDB("first-commit",ForumCommitActivity.this);
        first.putInt("num", first.getInt("num") + 1);

        if(first.getInt("num")==1){
            ViewTarget target = new ViewTarget(R.id.btn_submit, this);
            new ShowcaseView.Builder(ForumCommitActivity.this)
                    .setTarget(target)
                    .withNewStyleShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentTitle("送出")
                    .setContentText("考量舊版送出鍵容易誤按/n新版改至跟FB相同位置")
                    .hideOnTouchOutside()
                    .blockAllTouches()
                    .build();
        }
    }

    private class ForumPost{
        @SerializedName("title")
        public String title;
        @SerializedName("auth")
        public String auth;
        @SerializedName("body")
        public String body;
        @SerializedName("day")
        public String day;
    }

}
