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
public class TimeTableFragment extends Fragment {

    private SharedPreferences preferences;
    private ProgressDialog dialog;
    private int timeout;
    private TinyDB cache;

    WebView webView;
    private Thread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_webview, container, false);
        preferences = getActivity().getSharedPreferences("setting", 0);
        cache = new TinyDB("timetable-cache", getActivity());
        if (((MainActivity) getActivity()).networkInfo()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        networkRun(view);
                    }catch (Exception e){/**/}
                }
            });
            thread.start();
        } else {
            //cacheRead
            String html = cache.getString("html");
            writeInUi(view, html);
        }

        return view;
    }

    //取得課表並填入
    private void networkRun(final View view) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(getActivity(), "讀取網路中", "請稍後");
                }
            });
            String stu_class = preferences.getString("stu_class", "");
            String stu_year = preferences.getString("stu_year", "");
            String stu_timatable = stu_class.substring(0, 2) + stu_year + stu_class.substring(3);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException c) {/**/}
            try {
                Log.i("status", stu_timatable);
                //連線
                stu_timatable = URLEncoder.encode(stu_timatable, "big5");
                Document doc = Jsoup.connect("http://ta.taivs.tp.edu.tw/contact/show_class.asp?classn=" + stu_timatable)
                        .timeout(5000)
                        .get();
                //修改
                final Element temp = doc.select("table").get(1);
                temp.attr("width", "100%");
                temp.attr("align", "");
                Elements tr = temp.select("td");
                tr.attr("align", "center");
                tr.attr("width", "");
                tr = temp.select("font");
                tr.attr("style", "font-size:1em;display:block;");
                //region 刪除老師
                for (int y = 0; y < tr.size(); y++) {
                    int end = 0;
                    for (int i = 0; i < tr.get(y).html().length(); i++) {
                        if (tr.get(y).html().charAt(i) == '<' || tr.get(y).html().charAt(i) == '?') {
                            end = i;
                            break;
                        }
                    }
                    tr.get(y).html(tr.get(y).html().substring(0, end));
                }
                //endregion
                tr = temp.select("tr");
                tr.attr("valign", "");
                tr.attr("align", "");
                tr = temp.select("td");
                tr.attr("style", "font-size:12px;");
                final String html = temp.outerHtml();//.replace("，"," ");

                //region cacheWrite
                cache.putString("html", html);
                //endregion

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeInUi(view, html);
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                //region retry
                dialog.dismiss();
                timeout++;
                if (timeout < 5) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException c) {/**/}
                    networkRun(view);
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "系統連線失敗 嘗試5次失敗", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                //endregion
            }
        }
    }

    private void writeInUi(View view, String html) {
        webView = (WebView) view.findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        //webView.load(null,html);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onDestroyView() {
        try {
            Thread.sleep(5);
            thread.interrupt();
        } catch (Exception e) {/**/}
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();

        }
    }
}
