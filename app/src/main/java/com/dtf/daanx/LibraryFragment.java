package com.dtf.daanx;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by yoyo930021 on 2015/11/9.
 */
public class LibraryFragment extends Fragment {

    SharedPreferences preferences;
    private ProgressDialog dialog;
    private int timeout;

    WebView webView;
    private Thread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_webview,container, false);
        preferences=getActivity().getSharedPreferences("setting",0);
        if(((MainActivity)getActivity()).networkInfo()) {
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        networkRun(view);
                    }catch (Exception e){/**/}
                }
            });
            thread.start();
        }else {
            ((MainActivity) getActivity()).networkAlert();
        }

        return view;
    }

    //取得課表並填入
    private void networkRun(final View view){
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = ProgressDialog.show(getActivity(), "讀取網路中", "請稍後");
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
                Element head = doc.select("head").get(0);
                Element temp = doc.select("#seatTable").get(0);
                Element javascript = doc.select("script").get(3);
                Log.i("status", javascript.outerHtml());
                final String html = "<html>" + head.outerHtml() + "<body>" + temp.outerHtml() + javascript.outerHtml() + "</body></html>";
                //temp.attr("width","100%");

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(view, "系統連線失敗 5秒後自動重試中.....", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    try {
                        Thread.sleep(5000);
                        networkRun(view);
                    } catch (Exception c) {/**/}
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
    }

    private void writeInUi(View view,String html){
        webView=(WebView) view.findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
                webView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        if(Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

    }

    @Override
    public void onDestroyView() {
        try {
            Thread.sleep(5);
            thread.interrupt();
        }catch (Exception e){/**/}
        super.onDestroyView();
        if (webView != null) {
            webView.clearCache(true);
            webView.destroy();
        }
    }
}
