package com.dtf.daanx;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        TextView main_testlast=(TextView) view.findViewById(R.id.main_textlast);
        main_testlast.setText("距離 統測 還有"+String.valueOf(testLastDay("2016-4-30")+"天"));
        return view;
    }

    private long testLastDay(String testDate){
        Date nowDate = new Date();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date beginDate;
        long day=0;
        try {
            beginDate= format.parse(testDate);
            day=Math.abs((beginDate.getTime()-nowDate.getTime())/(24*60*60*1000));
        }catch (Exception e){/**/}
        return day;
    }

}