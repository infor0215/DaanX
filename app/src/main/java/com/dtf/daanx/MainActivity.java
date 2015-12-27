package com.dtf.daanx;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences preference;
    private TinyDB cache;
    ArrayList<Integer> lastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region viewStart
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cache=new TinyDB("main-cache",this);

        if (!networkInfo()) {
            networkAlert();
        }
        //endregion

        //region preference
        preference = getSharedPreferences("setting", 0);
        //endregion


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);


        //region Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView lbl_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.lbl_name);
        lbl_name.setText(preference.getString("stu_name", ""));
        TextView lbl_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.lbl_email);
        lbl_email.setText(preference.getString("stu_email", ""));
        //endregion

        String mainFt=cache.getString("main");
        switch (mainFt) {
            case "main":
                SwitchFramgent(R.id.nav_main);
                break;
            case "calc":
                SwitchFramgent(R.id.nav_calendar);
                break;
            case "forum":
                SwitchFramgent(R.id.nav_forum);
                break;
            case "timetable":
                SwitchFramgent(R.id.nav_timetable);
                break;
            case "attend":
                SwitchFramgent(R.id.nav_attend);
                break;
            case "prize":
                SwitchFramgent(R.id.nav_prize);
                break;
            case "week":
                SwitchFramgent(R.id.nav_nowpost);
                break;
            case "newsstu":
                SwitchFramgent(R.id.nav_stupost);
                break;
            case "term":
                SwitchFramgent(R.id.nav_newpost);
                break;
            case "race":
                SwitchFramgent(R.id.nav_racepost);
                break;
            case "bonus":
                SwitchFramgent(R.id.nav_bonus);
                break;
            case "library":
                SwitchFramgent(R.id.nav_library);
                break;
            case "DaanAbout":
                SwitchFramgent(R.id.nav_daanabout);
                break;
            case "About":
                SwitchFramgent(R.id.nav_about);
                break;
            default:
                SwitchFramgent(R.id.nav_main);
                break;
        }
        lastItem=new ArrayList<Integer>(){};
        lastItem.add(R.id.nav_main);
    }


    //覆寫返回鍵事件
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() != 1) {//回上一層Fg
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setCheckedItem(lastItem.get(lastItem.size()-2));
            lastItem.remove(lastItem.size()-1);
            getFragmentManager().popBackStack();
        } else {
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
        final int id = item.getItemId();


        SwitchFramgent(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        lastItem.add(id);


        return true;
    }

    private void SwitchFramgent(final int id){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle bundle;
                Fragment fg;
                Intent intent;
                switch (id) {
                    case R.id.nav_main:
                        fg = new MainFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "main");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("main");
                        ft.commit();
                        break;
                    case R.id.nav_calendar:
                        fg = new CalcFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "calc");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("calc");
                        ft.commit();
                        break;
                    case R.id.nav_forum:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "forum");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("forum");
                        ft.commit();
                        break;
                    case R.id.nav_timetable:
                        fg = new TimeTableFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "timetable");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("timetable");
                        ft.commit();
                        break;
                    case R.id.nav_grade:
                        intent = new Intent();
                        intent.setClass(MainActivity.this, GradeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_attend:
                        fg = new AttendFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "attend");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("attend");
                        ft.commit();
                        break;
                    case R.id.nav_prize:
                        fg = new PrizeFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "prize");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("prize");
                        ft.commit();
                        break;
                    case R.id.nav_nowpost:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "week");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("week");
                        ft.commit();
                        break;
                    case R.id.nav_stupost:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "newsstu");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("newsstu");
                        ft.commit();
                        break;
                    case R.id.nav_newpost:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "term");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("term");
                        ft.commit();
                        break;
                    case R.id.nav_racepost:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "race");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("race");
                        ft.commit();
                        break;
                    case R.id.nav_bonus:
                        fg = new ListviewFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "bonus");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("bonus");
                        ft.commit();
                        break;
                    case R.id.nav_library:
                        fg = new LibraryFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "library");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("library");
                        ft.commit();
                        break;
                    case R.id.nav_rule:
                        intent = new Intent();
                        intent.setClass(MainActivity.this, WebviewActivity.class);
                        bundle = new Bundle();
                        bundle.putString("src", "http://drive.google.com/viewerng/viewer?embedded=true&url=http://military.taivs.tp.edu.tw/sites/military.taivs.tp.edu.tw/files/pictures/%E5%A4%A7%E5%AE%89%E9%AB%98%E5%B7%A5%E5%AD%B8%E7%94%9F%E7%8D%8E%E6%87%B2%E5%AF%A6%E6%96%BD%E8%A6%8F%E5%AE%9A.doc");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
//                    case R.id.nav_config:
//                        intent = new Intent();
//                        intent.setClass(MainActivity.this, ConfigActivity.class);
//                        startActivity(intent);
//                        break;
                    case R.id.nav_daanabout:
                        fg = new DaanAboutFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "DaanAbout");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("DaanAbout");
                        ft.commit();
                        break;
                    case R.id.nav_about:
                        fg = new UsFragment();
                        bundle = new Bundle();
                        bundle.putString("type", "About");
                        fg.setArguments(bundle);
                        ft.replace(R.id.main_layout, fg, "f_m");
                        ft.addToBackStack("About");
                        ft.commit();
                        break;
                    case R.id.nav_logout:
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("stu_id", "");
                        editor.putString("stu_pwd", "");
                        editor.putString("stu_year", "");
                        editor.putString("stu_nick", "");
                        editor.putString("stu_class", "");
                        editor.putString("stu_name", "");
                        editor.putString("stu_tea", "");
                        editor.putString("stu_num", "");
                        editor.putString("stu_email", "");
                        editor.apply();
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                        break;
                }
            }
        }, 300);
    }

    @Override
    protected void onStop() {
        Fragment ft = getFragmentManager().findFragmentByTag("f_m");
        if(ft.getArguments()!=null) {
            String str = (String) ft.getArguments().get("type");
            cache = new TinyDB("main-cache", this);
            cache.putString("main", str);
        }
        super.onStop();
        Log.i("status","onStop");
    }


}




