package com.dtf.daanx;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class GradeActivity extends AppCompatActivity {

    private TabLayout mTabs;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

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

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setText("月考成績"));
        mTabs.addTab(mTabs.newTab().setText("學期成績"));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
    }




    class SamplePagerAdapter extends PagerAdapter {

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
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                int pixels =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())/10;
                final View view = getLayoutInflater().inflate(R.layout.tab_grade,
                        container, false);

                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fount1);


                for (int i = 0; i < 6; i++) {
                    LinearLayout temp = new LinearLayout(GradeActivity.this);
                    LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    temp.setLayoutParams(txtParams);
                    temp.setOrientation(LinearLayout.VERTICAL);

                    TextView txtsubject = new TextView(GradeActivity.this);
                    txtParams = new LinearLayout.LayoutParams(50*pixels, 50*pixels);
                    txtParams.setMargins(10*pixels, 0, 10*pixels, 0);
                    txtsubject.setLayoutParams(txtParams);
                    txtsubject.setText("國文");
                    txtsubject.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.black));
                    txtsubject.setTextSize(20*pixels);
                    txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    temp.addView(txtsubject);

                    TextView txtgrade = new TextView(GradeActivity.this);
                    txtParams = new LinearLayout.LayoutParams(50*pixels, 50*pixels);
                    txtParams.setMargins(10*pixels, 10*pixels, 10*pixels, 10*pixels);
                    txtgrade.setLayoutParams(txtParams);
                    txtgrade.setText("85");
                    txtgrade.setTextColor(ContextCompat.getColor(GradeActivity.this, R.color.white));
                    txtgrade.setTextSize(30*pixels);
                    txtgrade.setBackground(ContextCompat.getDrawable(GradeActivity.this, R.drawable.grade_bg_green));
                    txtsubject.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    temp.addView(txtgrade);
                    linearLayout.addView(temp);
                }

                container.addView(view);
                return view;
            }
            return 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
