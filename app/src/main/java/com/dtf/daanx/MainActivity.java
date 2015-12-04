package com.dtf.daanx;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.xml.xpath.XPath;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preference=getSharedPreferences("setting",0);

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



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest("",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Post>>() {}.getType();
                        ArrayList<Post> jsonArr = gson.fromJson(response, listType);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);*/


        //TextView lbl_name=(TextView) findViewById(R.id.lbl_name);
        //lbl_name.setText(preference.getString("stu_name",""));
        //lbl_name.setText("你好");



        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, new MainFragment(), "f_m");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                ft.replace(R.id.main_layout,fg);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_forum:
                fg = new ListviewFragment();
                bundle = new Bundle();
                bundle.putString("type", "forum");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_grade:
                intent=new Intent();
                intent.setClass(MainActivity.this,GradeActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_prize:
                fg = new PrizeFragment();
                bundle = new Bundle();
                bundle.putString("type", "prize");
                fg.setArguments(bundle);
                ft.replace(R.id.main_layout, fg);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.nav_config:
                intent=new Intent();
                intent.setClass(MainActivity.this,ConfigActivity.class);
                startActivity(intent);
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}




