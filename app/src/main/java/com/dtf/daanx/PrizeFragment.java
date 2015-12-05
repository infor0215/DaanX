package com.dtf.daanx;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class PrizeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_prize,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);

        return view;
    }


}