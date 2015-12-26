package com.dtf.daanx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfigActivity extends BaseActivity {

    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        preference=getSharedPreferences("setting",0);

        int pixels;
        LinearLayout.LayoutParams txtParams;

        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.config);

        LinearLayout line = new LinearLayout(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        line.setLayoutParams(txtParams);
        line.setOrientation(LinearLayout.VERTICAL);

        TextView txtsubject = new TextView(ConfigActivity.this);
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, 0, pixels, 0);
        txtsubject.setLayoutParams(txtParams);
        txtsubject.setText("學號");
        txtsubject.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.black));
        txtsubject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtsubject);


        TextView txtgrade = new TextView(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, -pixels, pixels, pixels);
        txtgrade.setLayoutParams(txtParams);
        txtgrade.setText(preference.getString("stu_id", ""));
        //txtgrade.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.white));
        txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        int draw = R.drawable.grade_bg_black;
        txtgrade.setBackground(ContextCompat.getDrawable(ConfigActivity.this, draw));
        txtgrade.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtgrade);
        linearLayout.addView(line);

        line = new LinearLayout(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        line.setLayoutParams(txtParams);
        line.setOrientation(LinearLayout.VERTICAL);

        txtsubject = new TextView(ConfigActivity.this);
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, 0, pixels, 0);
        txtsubject.setLayoutParams(txtParams);
        txtsubject.setText("班級");
        txtsubject.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.black));
        txtsubject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtsubject);

        txtgrade = new TextView(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, -pixels, pixels, pixels);
        txtgrade.setLayoutParams(txtParams);
        txtgrade.setText(preference.getString("stu_class", ""));
        //txtgrade.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.white));
        txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        draw = R.drawable.grade_bg_black;
        txtgrade.setBackground(ContextCompat.getDrawable(ConfigActivity.this, draw));
        txtgrade.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtgrade);
        linearLayout.addView(line);

        line = new LinearLayout(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        line.setLayoutParams(txtParams);
        line.setOrientation(LinearLayout.VERTICAL);

        txtsubject = new TextView(ConfigActivity.this);
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, 0, pixels, 0);
        txtsubject.setLayoutParams(txtParams);
        txtsubject.setText("老師");
        txtsubject.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.black));
        txtsubject.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtsubject);

        txtgrade = new TextView(ConfigActivity.this);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        txtParams = new LinearLayout.LayoutParams(pixels, pixels);
        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        txtParams.setMargins(pixels, -pixels, pixels, pixels);
        txtgrade.setLayoutParams(txtParams);
        txtgrade.setText(preference.getString("stu_tea", ""));
        //txtgrade.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.white));
        txtgrade.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        draw = R.drawable.grade_bg_black;
        txtgrade.setBackground(ContextCompat.getDrawable(ConfigActivity.this, draw));
        txtgrade.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        line.addView(txtgrade);
        linearLayout.addView(line);
    }

}
