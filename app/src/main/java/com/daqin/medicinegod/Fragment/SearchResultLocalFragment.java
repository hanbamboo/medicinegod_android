package com.daqin.medicinegod.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daqin.medicinegod.Adspter.MedicineSearchAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimerTask;

public class SearchResultLocalFragment extends Fragment {
    String searchKey = null;
    static ArrayList<Map<String, Object>> medicine_res = new ArrayList<>();
    View root;
    Random random = new Random();
    boolean showshow;

    public static Fragment newInstance() {
        return new SearchResultLocalFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_search_result_local, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchKey = Utils.getString(requireContext(), Constant.SEARCHKEY, "none");
        showshow = Utils.getBoolean(requireContext(), Constant.SHOWSHOW, false);

        medicine_res = querySearchResult(searchKey);
        TextView sk_localtitle = root.findViewById(R.id.id_searchres_localtitle);
        if (medicine_res.size() == 0) {
            sk_localtitle.setText("本地结果(未查找到结果)");
        } else {
            sk_localtitle.setText("本地结果(" + medicine_res.size() + ")");
        }
        MedicineSearchAdapter adapter = new MedicineSearchAdapter(requireContext(), R.layout.list_searchres_local, medicine_res, showshow);
        ListView listView = (ListView) root.findViewById(R.id.id_searchres_localres);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = medicine_res.get(position);
                if (Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString()) == 1) {
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_medicine_web, null, false);
                    final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();
                    TextView name = v.findViewById(R.id.id_dialog_medicine_web_name);
                    ImageView img = v.findViewById(R.id.id_dialog_medicine_web_image);
                    TextView desp = v.findViewById(R.id.id_dialog_medicine_web_desp);
                    TextView company = v.findViewById(R.id.id_dialog_medicine_web_company);
                    TextView usage = v.findViewById(R.id.id_dialog_medicine_web_usage);
                    FlowLayout flowLayout = v.findViewById(R.id.id_dialog_medicine_web_elabel);
                    name.setText((String) map.get(Constant.COLUMN_M_NAME));
                    LinearLayout linearLayout = v.findViewById(R.id.id_dialog_medicine_web_action);
                    linearLayout.setVisibility(View.GONE);
//					Utils.setImageViewCorner(img, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                    img.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                    desp.setText((String) map.get(Constant.COLUMN_M_DESCRIPTION));
                    company.setText((String) map.get(Constant.COLUMN_M_COMPANY));
                    usage.setText((String) map.get(Constant.COLUMN_M_MUSE));
                    TextView close = v.findViewById(R.id.id_dialog_medicine_web_close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    String[] label = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_ELABEL))).split("@@");
                    if (label.length != 0) {
                        flowLayout.removeAllViewsInLayout();
                        for (String str : label) {
                            int viewId = random.nextInt(8999) * 123;
                            //设置一个标签
                            TextView textView = new TextView(getContext());
                            textView.setId(viewId);
                            textView.setText(str);
                            textView.setTextSize(12);
                            textView.setTextColor(Color.rgb(255, 255, 255));
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textView.setPadding(20, 5, 20, 5);
                            textView.setBackground(requireContext().getDrawable(R.drawable.bg_search_history_btn_blue));
                            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(10, 10, 10, 10);
                            //添加到布局中
                            flowLayout.addView(textView, layoutParams);

                        }
                    }
                    //清空背景黑框
                    dialog.getWindow().getDecorView().setBackground(null);
                    dialog.show();

                    //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                    dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));


                } else {
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_medicine_local, null, false);
                    final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();
                    TextView name = v.findViewById(R.id.id_dialog_medicine_name);
                    LinearLayout linearLayout = v.findViewById(R.id.id_dialog_medicine_action);
                    linearLayout.setVisibility(View.GONE);
                    TextView outdate = v.findViewById(R.id.id_dialog_medicine_outdate);
                    TextView yu = v.findViewById(R.id.id_dialog_medicine_yu);
                    FlowLayout flowLayout = v.findViewById(R.id.id_dialog_medicine_elabel);
                    TextView desp = v.findViewById(R.id.id_dialog_medicine_desp);
                    TextView otc = v.findViewById(R.id.id_dialog_medicine_otc);
                    ImageView img = v.findViewById(R.id.id_dialog_medicine_image);
                    ImageView barcode = v.findViewById(R.id.id_dialog_medicine_barcode);
                    ImageView share = v.findViewById(R.id.id_dialog_medicine_share);
                    TextView group = v.findViewById(R.id.id_dialog_medicine_group);
                    TextView close = v.findViewById(R.id.id_dialog_medicine_close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    String[] label = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_ELABEL))).split("@@");
                    if (label.length != 0) {
                        flowLayout.removeAllViewsInLayout();
                        for (String str : label) {
                            int viewId = random.nextInt(8999) * 123;
                            //设置一个标签
                            TextView textView = new TextView(getContext());
                            textView.setId(viewId);
                            textView.setText(str);
                            textView.setTextSize(12);
                            textView.setTextColor(Color.rgb(255, 255, 255));
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textView.setPadding(20, 5, 20, 5);
                            textView.setBackground(requireContext().getDrawable(R.drawable.bg_search_history_btn_blue));
                            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(10, 10, 10, 10);
                            //添加到布局中
                            flowLayout.addView(textView, layoutParams);

                        }
                    }

                    desp.setText("   " + map.get(Constant.COLUMN_M_DESCRIPTION));
                    String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
                    name.setText((String) map.get(Constant.COLUMN_M_NAME));
                    group.setText((String) map.get(Constant.COLUMN_M_GROUP));
                    String otc_name = (String) map.get(Constant.COLUMN_M_OTC);
                    switch (Objects.requireNonNull(otc_name)) {
                        case "NONE":
                            otc.setText("无");
                            otc.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                    R.drawable.bg_homepage_card_title_grey, null));
                            break;
                        case "OTC-G":
                            otc.setText("OTC");
                            otc.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                    R.drawable.bg_homepage_card_title_green_nohalf, null));
                            break;
                        case "OTC-R":
                            otc.setText("OTC");
                            otc.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                    R.drawable.bg_homepage_card_title_red_nohalf, null));
                            break;
                        case "RX":
                            otc.setText("Rx");
                            otc.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                    R.drawable.bg_homepage_card_title_red_nohalf, null));
                            break;
                    }
                    yu.setText("约可使用:" + Long.parseLong((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_YU))) / Integer.parseInt(muse[0]) + " " + muse[3]);
//					Utils.setImageViewCorner(img, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                    img.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));

                    Bitmap bitmap_bacrcode = Utils.createBarcode(String.valueOf(map.get(Constant.COLUMN_M_BARCODE)), 150, 80, false);
                    //TODO:分享码
                    Bitmap bitmap_share = CodeCreator.createQRCode("测试数据", 80, 80, null);
                    share.setImageBitmap(bitmap_share);
                    barcode.setImageBitmap(bitmap_bacrcode);

                    long date = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
                    outdate.setText(Utils.getOutDateString(date, 1));
                    //清空背景黑框
                    dialog.getWindow().getDecorView().setBackground(null);
                    dialog.show();

                    //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                    dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));

                }


            }
        });
        new Thread(new TimerTask() {
            @Override
            public void run() {
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
        return root;
    }

    @SuppressLint("SetTextI18n")
    public ArrayList<Map<String, Object>> querySearchResult(String str) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        str = "%" + str + "%";
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where "
                + Constant.COLUMN_M_DELFLAG + " = 0 and ("
//				+ Constant.COLUMN_M_UID + " = ? and ("
                + Constant.COLUMN_M_NAME + " LIKE ? or "
                + Constant.COLUMN_M_DESCRIPTION + " LIKE ? or "
                + Constant.COLUMN_M_OTC + " LIKE ? or "
                + Constant.COLUMN_M_ELABEL + " LIKE ? or "
                + Constant.COLUMN_M_GROUP + " LIKE ? or "
                + Constant.COLUMN_M_COMPANY + " LIKE ? " + ") ", new String[]{str, str, str, str, str, str});
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.COLUMN_M_KEYID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_KEYID)));
            map.put(Constant.COLUMN_M_NAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_NAME)));
            map.put(Constant.COLUMN_M_IMAGE, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_IMAGE)));
            map.put(Constant.COLUMN_M_UID, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_UID)));
            map.put(Constant.COLUMN_M_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DESCRIPTION)));
            map.put(Constant.COLUMN_M_OUTDATE, cursor.getLong(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OUTDATE)));
            map.put(Constant.COLUMN_M_OTC, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OTC)));
            map.put(Constant.COLUMN_M_BARCODE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_BARCODE)));
            map.put(Constant.COLUMN_M_YU, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_YU)));
            map.put(Constant.COLUMN_M_ELABEL, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_ELABEL)));
            map.put(Constant.COLUMN_M_LOVE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_LOVE)));
            map.put(Constant.COLUMN_M_SHARE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHARE)));
            map.put(Constant.COLUMN_M_MUSE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_MUSE)));
            map.put(Constant.COLUMN_M_COMPANY, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_COMPANY)));
            map.put(Constant.COLUMN_M_DELFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DELFLAG)));
            map.put(Constant.COLUMN_M_SHOWFLAG, cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHOWFLAG)));
            map.put(Constant.COLUMN_M_FROMWEB, cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_FROMWEB)));
            map.put(Constant.COLUMN_M_GROUP, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_GROUP)));
            list.add(map);
        }
        cursor.close();
        db.close();
        if (list.size() <= 0) {
            return list;
        }
        return list;
    }
}
