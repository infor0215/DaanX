package com.dtf.daanx;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class ListviewFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview,container, false);
        //TextView tvObj = (TextView)view.findViewById(R.id.info);
        //String str = (String)getArguments().get("type");
        //tvObj.setText(str);
        return view;
    }


}