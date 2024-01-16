package com.daqin.medicinegod.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daqin.medicinegod.Adspter.MedicineSearchWebDetailAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.SearchResultCheckActivity;
import com.daqin.medicinegod.Utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchResultWebFragment extends Fragment {

    static ArrayList<Map<String, Object>> search_detail = new ArrayList<>();
    static String searchKey = null;

    static String s_web_choose = "IsWestPatMedicine";
    static int page = 1;
    View root;
    static boolean hasRes = false;
    public static Fragment newInstance() {
        return new SearchResultWebFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_search_result_web, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchKey = Utils.getString(requireContext(), Constant.SEARCHKEY, "none");
        Spinner spinner = (Spinner) root.findViewById(R.id.id_searchres_webres_choose);//初始化控件
        String[] choose = getResources().getStringArray(R.array.medicine_method);//建立数据源
        ArrayAdapter<String> adapter_web_choose = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, choose);
//        ArrayAdapter<String> adapter_web_choose = new ArrayAdapter<String>(getContext(), R.layout.spanner_style, choose);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        spinner.setAdapter(adapter_web_choose);//绑定Adapter到控件
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        s_web_choose = "IsWestPatMedicine";
                        break;
                    case 1:
                        s_web_choose = "IsChnPatMedicine";
                        break;
                    case 2:
                        s_web_choose = "IsHcMedicine";
                        break;
                    case 3:
                        s_web_choose = "IsCnMedicine";
                        break;
                }
                page = 1;
                TextView view_page = root.findViewById(R.id.id_searchres_page);
                view_page.setText(String.valueOf(page));
                startSearchOnWeb();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button btn_down = root.findViewById(R.id.id_searchres_pagedown);
        TextView view_page = root.findViewById(R.id.id_searchres_page);
        Button btn_up = root.findViewById(R.id.id_searchres_pageup);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page <= 1) {
                    page = 1;
                    btn_down.setEnabled(false);
                } else {
                    page--;
                    btn_up.setEnabled(true);
                }
                view_page.setText(String.valueOf(page));
                startSearchOnWeb();
            }
        });
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasRes) {
                    btn_up.setEnabled(false);
                } else {
                    page++;
                    btn_down.setEnabled(true);
                }
                view_page.setText(String.valueOf(page));
                startSearchOnWeb();
            }
        });

        startSearchOnWeb();

        return root;
    }

    //    网站资讯仅供参考，身体若有不适，请及时到医院就诊。
    public void startSearchOnWeb() {
        /**
         * HttpUrlConnection
         */
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://ypk.familydoctor.com.cn/search/so/?" + s_web_choose + "=true&KeyWord=" + Utils.getUrlEncode(searchKey) + "&page=" + page + "&amp;");
                } catch (MalformedURLException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        InputStreamReader is = new InputStreamReader(httpURLConnection.getInputStream());
                        int i = 0;
                        StringBuffer sb = new StringBuffer();
                        while ((i = is.read()) != -1) {
                            sb.append((char) i);
                        }
                        Document doc = Jsoup.parse(sb.toString());
                        TextView search_title = root.findViewById(R.id.id_searchres_webtitle);

                        System.out.println(doc.getElementsByClass("result-show").get(0).getElementsByTag("em").get(1).text());
                        //TODO:网页有错误，个数不匹配，重新找个数

 //                        String search_num= doc.getElementsByClass("result-show").get(0).getElementsByTag("em").get(0).text();
//                        if (Integer.parseInt(search_num) <= 0) {
//                            search_title.setText("网络结果(未查找到结果)");
//                        } else {
                            search_title.setText("网络结果");
                            Elements search_result = doc.getElementsByClass("search-result");
                            if (search_result != null ) {
                                hasRes = true;
                                search_detail.clear();
                                Elements dl = search_result.get(0).getElementsByTag("dl");
                                Log.d("TAG httpUrlConnection : ", "解析到第" + page + "页有" + dl.size());
                                for (Element element : dl) {
                                    Map<String, Object> map = new HashMap<>();
                                    Elements imglink = element.getElementsByTag("img");
                                    String imgpath = imglink.attr("src");
                                    Elements titlelink = element.getElementsByTag("h4").get(0).getElementsByTag("a");
                                    String link = titlelink.attr("href");
                                    String name = titlelink.text();
                                    Elements companylink = element.getElementsByTag("p").get(0).getElementsByTag("a");
                                    String company = companylink.text();
                                    Elements desplink = element.getElementsByTag("p").get(1).getElementsByTag("i");
                                    String desp = desplink.text().replace("详细»", "");
                                    map.put(Constant.COLUMN_S_RESULT_NAME, name);
                                    map.put(Constant.COLUMN_S_RESULT_LINK, link);
                                    map.put(Constant.COLUMN_S_RESULT_IMG, imgpath);
                                    map.put(Constant.COLUMN_S_RESULT_COMPANY, company);
                                    map.put(Constant.COLUMN_S_RESULT_DESP, desp);
                                    search_detail.add(map);
                                }
                                MedicineSearchWebDetailAdapter adapter = new MedicineSearchWebDetailAdapter(getContext(), R.layout.list_searchres_web, search_detail);
                                ListView listView = (ListView) root.findViewById(R.id.id_searchres_webres);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String uri = (String) search_detail.get(position).get(Constant.COLUMN_S_RESULT_LINK);
                                        Utils.putString(requireContext(),Constant.SEARCHCHECKKEY,uri);
                                        Intent intent = new Intent(getContext(), SearchResultCheckActivity.class);
                                        startActivity(intent);


                                    }
                                });
                                listView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listView.setAdapter(adapter);
                                    }
                                });


                            }else {
                                hasRes = false;

                            }
//                        }


                    } else {
                        Log.d("TAG httpUrlConnection : ", httpURLConnection.getResponseCode() + "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}