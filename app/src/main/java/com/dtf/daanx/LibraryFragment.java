package com.dtf.daanx;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class LibraryFragment extends Fragment {

    private SharedPreferences preferences;
    private ProgressDialog dialog;
    private int timeout;

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_webview,container, false);
        preferences=getActivity().getSharedPreferences("setting",0);
        if(((MainActivity)getActivity()).networkInfo()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    networkRun(view);
                }
            }).start();
        }else {
            ((MainActivity) getActivity()).networkAlert();
        }

        return view;
    }

    //取得課表並填入
    private void networkRun(final View view){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog= ProgressDialog.show(getActivity(), "讀取網路中", "請稍後");
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException c) {/**/}
        try {
            //連線
            Document doc = Jsoup.connect("http://libregist.taivs.tp.edu.tw/currstat")
                    .timeout(5000)
                    .get();
            //修改
            Element head=doc.select("head").get(0);
            Element temp=doc.select("#seatTable").get(0);
            Element javascript=doc.select("script").get(3);
            Log.i("status",javascript.outerHtml());
            final String html="<html>"+head.outerHtml()+temp.outerHtml()+javascript.outerHtml()+"</html>";
            //temp.attr("width","100%");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    writeInUi(view,html);
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            //region retry
            dialog.dismiss();
            timeout++;
            if (timeout < 5) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException c) {/**/}
                networkRun(view);
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            //endregion
        }
    }

    private void writeInUi(View view,String html){
        webView=(WebView) view.findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, html, "text/html",  "utf-8", null);
        //webView.loadData( html, "text/html",  "utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.load(null,html);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
            webView.clearCache(true);
        }
    }
}
