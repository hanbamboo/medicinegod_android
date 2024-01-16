package com.daqin.medicinegod;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.Utils.AsynImageLoader;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivitySearchResultCheckBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * 网络查询细节
 */

public class SearchResultCheckActivity extends AppCompatActivity {

    private ActivitySearchResultCheckBinding binding;
    View root;
    Map<String, Object> map = new HashMap<>();
    List<String> list = new ArrayList<>();
    Random random = new Random();
    final DatabaseHelper dbHelper = new DatabaseHelper(SearchResultCheckActivity.this);
    static Bitmap img = null;
    final Handler handler = new Handler();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchResultCheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        String uri = Utils.getString(getApplicationContext(), Constant.SEARCHCHECKKEY, "none");
        if (uri.equals("none")) {
            finish();
        }
        ImageView searchres_check_img = root.findViewById(R.id.id_searchres_check_img);
        TextView searchres_check_name = root.findViewById(R.id.id_searchres_check_name);
        TextView searchres_check_desp = root.findViewById(R.id.id_searchres_check_desp);
        TextView searchres_check_usage = root.findViewById(R.id.id_searchres_check_usage);
        TextView searchres_check_company = root.findViewById(R.id.id_searchres_check_company);
        ImageButton searchres_check_save = root.findViewById(R.id.id_searchres_check_save);
        ImageButton searchres_check_back = root.findViewById(R.id.id_searchres_check_back);
        searchres_check_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(uri);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    map.clear();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader is = new InputStreamReader(httpURLConnection.getInputStream());
                        int i = 0;
                        StringBuffer sb = new StringBuffer();
                        while ((i = is.read()) != -1) {
                            sb.append((char) i);
                        }
                        Document doc = Jsoup.parse(sb.toString());
                        Element searchDetail = doc.getElementsByClass("detail").get(0).getElementsByClass("table-1").get(0);
                        Elements searchTr = searchDetail.getElementsByTag("tr");
                        String context = "";
                        String name = "查询失败，请重试";
                        String company = "查询失败，请重试";
                        String desp = "查询失败，请重试";
                        String usage = "查询失败，请重试";
                        for (int j = 0; j < searchTr.size(); j++) {
                            context = searchTr.get(j).getElementsByTag("th").get(0).text();
                            if (context.equals("【药品名称】")) {
                                name = searchTr.get(j).getElementsByTag("td").get(0).text();
                                map.put(Constant.COLUMN_M_NAME, name);
                                continue;
                            }
                            if (context.equals("【生产企业】")) {
                                company = searchTr.get(j).getElementsByTag("td").get(0).getElementsByTag("a").get(0).text();
                                map.put(Constant.COLUMN_M_COMPANY, company);
                                continue;
                            }
                            if (context.equals("【适 应 症】")) {
                                desp = searchTr.get(j).getElementsByTag("td").text();
                                map.put(Constant.COLUMN_M_DESCRIPTION, desp);
                                continue;
                            }
                            if (context.equals("【用法用量】")) {
                                usage = searchTr.get(j).getElementsByTag("td").get(0).text();
                                map.put(Constant.COLUMN_M_MUSE, usage);
                            }
                        }
                        searchDetail = doc.getElementsByClass("main").get(0).getElementsByClass("table-1").get(0);
                        searchTr = searchDetail.getElementsByTag("tr");

                        Elements elabel = null;
                        for (int j = searchTr.size() - 1; j >= 0; j--) {
                            context = searchTr.get(j).getElementsByTag("th").get(0).text();
                            if (context.equals("治疗疾病：")) {
                                elabel = searchTr.get(j).getElementsByTag("td");
                                break;
                            }
                        }
                        if (elabel != null) {
                            list.clear();
                            for (Element e : elabel) {
                                list.add(e.text());
                            }
                            System.out.println(list.toString() + "-" + list.size());
                        }

                        searchDetail = doc.getElementsByClass("sidebar").get(0).getElementsByClass("pic").get(0).getElementsByAttribute("src").get(0);
                        String urlImg = searchDetail.attr("src");
//                        map.put(Constant.COLUMN_M_IMAGE, urlImg);
                        System.out.println(urlImg);

                        System.out.println(name + "-" + company + "-" + desp + "-" + usage);
                        String finalName = name;
                        searchres_check_name.post(new Runnable() {
                            @Override
                            public void run() {
                                searchres_check_name.setText(finalName);
                            }
                        });
                        String finalCompany = company;
                        searchres_check_company.post(new Runnable() {
                            @Override
                            public void run() {
                                searchres_check_company.setText(finalCompany);
                            }
                        });
                        String finalDesp = desp;
                        searchres_check_desp.post(new Runnable() {
                            @Override
                            public void run() {
                                searchres_check_desp.setText(finalDesp);
                            }
                        });
                        String finalUsage = usage;
                        searchres_check_usage.post(new Runnable() {
                            @Override
                            public void run() {
                                searchres_check_usage.setText(finalUsage);
                            }
                        });
                        searchres_check_img.post(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (urlImg.contains("disease_default.jpg")) {
                                            Looper.prepare();
//                                    searchres_check_img.setImageResource(R.mipmap.add_imgdefault);
                                            img = Utils.getBitmapFromResourse(SearchResultCheckActivity.this, R.mipmap.add_imgdefault);
                                            Looper.loop();
                                        } else {
                                            Looper.prepare();
                                            img = Utils.netToLoacalBitmap(urlImg);
                                            AsynImageLoader asynImageLoader = new AsynImageLoader();
                                            asynImageLoader.showImageAsyn(searchres_check_img, urlImg, R.mipmap.search_result_loading);
                                            Looper.loop();
                                        }
                                    }
                                }).start();

                            }
                        });


                        if (list.size() != 0) {
                            StringBuilder lib = new StringBuilder();
                            FlowLayout searchres_check_elabel = root.findViewById(R.id.id_searchres_check_elabel);
                            searchres_check_elabel.removeAllViewsInLayout();
                            String[] strList = list.get(0).split(" ");
                            for (String str : strList) {
                                lib.append(str).append("@@");
                                int viewId = random.nextInt(8999) * 123;
                                //设置一个标签
                                TextView textView = new TextView(SearchResultCheckActivity.this);
                                textView.setId(viewId);
                                textView.setText(str);
                                textView.setTextSize(14);
                                textView.setTextColor(Color.rgb(255, 255, 255));
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                textView.setPadding(20, 5, 20, 5);
                                textView.setBackground(SearchResultCheckActivity.this.getDrawable(R.drawable.bg_search_history_btn_blue));
                                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(10, 10, 10, 10);
                                //添加到布局中
                                searchres_check_elabel.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchres_check_elabel.addView(textView, layoutParams);
                                    }
                                });

                            }
                            map.put(Constant.COLUMN_M_ELABEL, lib.toString());
                        }
                    } else {
                        Log.d("TAG httpUrlConnection : ", httpURLConnection.getResponseCode() + "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        searchres_check_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyid = Utils.getRandomKeyId();
                String name = (String) map.get(Constant.COLUMN_M_NAME);
                while (isPresentkeyId(keyid)) {
                    keyid = Utils.getRandomKeyId();
                }
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Constant.COLUMN_M_KEYID, keyid);
                values.put(Constant.COLUMN_M_NAME, name);
                values.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(img));
                values.put(Constant.COLUMN_M_DESCRIPTION, (String) map.get(Constant.COLUMN_M_DESCRIPTION));
                values.put(Constant.COLUMN_M_OUTDATE, 9999999999999L);
                values.put(Constant.COLUMN_M_OTC, "未知");
                values.put(Constant.COLUMN_M_BARCODE, "未知");
                values.put(Constant.COLUMN_M_YU, 0);
                values.put(Constant.COLUMN_M_ELABEL, (String) map.get(Constant.COLUMN_M_ELABEL));
                values.put(Constant.COLUMN_M_LOVE, 0);
                values.put(Constant.COLUMN_M_SHARE, "无");
                values.put(Constant.COLUMN_M_MUSE, (String) map.get(Constant.COLUMN_M_MUSE));
                values.put(Constant.COLUMN_M_COMPANY, (String) map.get(Constant.COLUMN_M_COMPANY));
                values.put(Constant.COLUMN_M_DELFLAG, 0);
                values.put(Constant.COLUMN_M_SHOWFLAG, 0);
                values.put(Constant.COLUMN_M_FROMWEB, 1);
                values.put(Constant.COLUMN_M_GROUP, Constant.COLUMN_G_WEB);
                values.put(Constant.COLUMN_M_UID, Utils.getInt(SearchResultCheckActivity.this, Constant.COLUMN_M_UID, 0));
//				values.put(Constant.COLUMN_M_MD5KEY, Utils.getMedicineMD5(keyid, name));
                long status = db.insert(Constant.TABLE_NAME_MEDICINE, null, values);
                searchres_check_save.setEnabled(false);
                if (status >= 0) {
                    AlertDialog tips = new AlertDialog.Builder(SearchResultCheckActivity.this)
                            .setTitle("提示:")
                            .setMessage("保存成功！")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();
                    tips.show();
//                    Toast.makeText(SearchResultCheckActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TABLE_NAME_MEDICINE, "insert successful");
                    db.close();
                } else {
                    searchres_check_save.setEnabled(true);
                    AlertDialog tips = new AlertDialog.Builder(SearchResultCheckActivity.this)
                            .setTitle("提示:")
                            .setMessage("保存失败！")
                            .setPositiveButton("确认", null)
                            .create();
                    tips.show();
                    Toast.makeText(SearchResultCheckActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean isPresentkeyId(String keyid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select `" + Constant.COLUMN_M_KEYID +
                "` from " + Constant.TABLE_NAME_MEDICINE +
                " where `" + Constant.COLUMN_M_KEYID + "` = ?", new String[]{keyid});
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }


    }


}

