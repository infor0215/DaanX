package com.dtf.daanx;

import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

/**
 * Created by koneko on 2015/12/29.
 */
public class FramgentTarget implements Target {

    private final View mView;

    public FramgentTarget(View view) {
        mView = view;
    }

    public FramgentTarget(int viewId,View view) {
        mView = view.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2;
        int y = location[1] + mView.getHeight() / 2;
        return new Point(x, y);
    }
}