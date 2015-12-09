package com.dtf.daanx;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region viewStart
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(networkInfo()){
            //
        }else {
            networkAlert();
        }
        //endregion

        //region preference
        preference=getSharedPreferences("setting",0);
        //endregion


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

        //region Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView lbl_name=(TextView) navigationView.getHeaderView(0).findViewById(R.id.lbl_name);
        lbl_name.setText(preference.getString("stu_name",""));
        TextView lbl_email=(TextView) navigationView.getHeaderView(0).findViewById(R.id.lbl_email);
        lbl_email.setText(preference.getString("stu_email",""));
        //endregion


        //region defaultFg
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, new MainFragment(), "f_m");
        ft.addToBackStack("main");
        ft.commit();
        //endregion
    }


    //覆寫返回鍵事件
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(getFragmentManager().getBackStackEntryCount()!=1) {//回上一層Fg
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/
    //Drawer Select
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction ft=getFragmentManager().beginTransaction();

        Bundle bundle;
        Fragment fg;
        Intent intent;

        switch (id){
            case R.id.nav_main:
                fg=new MainFragment();
                bundle = new Bundle();
                bundle.putString("type","Main");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout,fg,"f_m");
                ft.addToBackStack("main");
                ft.commit();
                break;
            case R.id.nav_forum:
                fg = new ListviewFragment();
                bundle = new Bundle();
                bundle.putString("type", "forum");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg,"f_m");
                ft.addToBackStack("forum");
                ft.commit();
                break;
            case R.id.nav_timetable:
                fg = new TimeTableFragment();
                bundle = new Bundle();
                bundle.putString("type", "timetable");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg,"f_m");
                ft.addToBackStack("timetable");
                ft.commit();
                break;
            case R.id.nav_grade:
                intent=new Intent();
                intent.setClass(MainActivity.this,GradeActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_attend:
                fg = new AttendFragment();
                bundle = new Bundle();
                bundle.putString("type", "attend");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg,"f_m");
                ft.addToBackStack("attend");
                ft.commit();
                break;
            case R.id.nav_prize:
                fg = new PrizeFragment();
                bundle = new Bundle();
                bundle.putString("type", "prize");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg,"f_m");
                ft.addToBackStack("prize");
                ft.commit();
                break;
            case R.id.nav_config:
                intent=new Intent();
                intent.setClass(MainActivity.this,ConfigActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_daanabout:
                fg = new DaanAboutFragment();
                bundle = new Bundle();
                bundle.putString("type", "DaanAbout");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg,"f_m");
                ft.addToBackStack("DaanAbout");
                ft.commit();
                break;
            case R.id.nav_logout:
                SharedPreferences.Editor editor=preference.edit();
                editor.putString("stu_id","");
                editor.putString("stu_pwd","");
                editor.putString("stu_year","");
                editor.putString("stu_nick","");
                editor.putString("stu_class","");
                editor.putString("stu_name","");
                editor.putString("stu_tea","");
                editor.putString("stu_num","");
                editor.putString("stu_email","");
                editor.apply();
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}




