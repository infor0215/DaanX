package com.dtf.daanx;

        import android.app.Fragment;
        import android.app.ProgressDialog;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.os.Bundle;
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

    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_table,container, false);
        preferences=getActivity().getSharedPreferences("setting",0);
        final ProgressDialog dialog= ProgressDialog.show(getActivity(), "讀取網路中", "請稍後");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String stu_class=preferences.getString("stu_class","");
                String stu_year=preferences.getString("stu_year","");
                String stu_timatable=stu_class.substring(0,2)+stu_year+stu_class.substring(3);
                try {
                    Log.i("status",stu_timatable);
                    stu_timatable = URLEncoder.encode(stu_timatable, "big5");
                    Document doc = Jsoup.connect("http://ta.taivs.tp.edu.tw/contact/show_class.asp?classn=" + stu_timatable)
                            .timeout(5000)
                            .get();
                    final Element temp=doc.select("table").get(1);
                    temp.attr("width","100%");
                    temp.attr("align","");
                    Elements tr=temp.select("td");
                    //tr.attr("valign","baseline");
                    tr.attr("align","center");
                    tr.attr("width","");
                    tr=temp.select("font");
                    tr.attr("style","font-size:1em;display:block;");
                    for(int y=0;y<tr.size();y++) {
                        int end = 0;
                        for (int i = 0; i < tr.get(y).html().length(); i++) {
                            if (tr.get(y).html().charAt(i) == '<'||tr.get(y).html().charAt(i) == '?') {
                                end = i;
                                break;
                            }
                        }
                        tr.get(y).html(tr.get(y).html().substring(0, end));
                    }
                    tr=temp.select("tr");
                    tr.attr("valign","");
                    tr.attr("align","");
                    tr=temp.select("td");
                    tr.attr("style","font-size:12px;");
                    final String html=temp.outerHtml().replace("，"," ");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WebView webView=(WebView) view.findViewById(R.id.webView);
                            webView.loadDataWithBaseURL(null,html, "text/html",  "utf-8", null);
                            webView.setBackgroundColor(Color.TRANSPARENT);
                            dialog.dismiss();
                        }
                    });
                }catch (Exception e){/**/}
            }
        }).start();

        return view;
    }

}
