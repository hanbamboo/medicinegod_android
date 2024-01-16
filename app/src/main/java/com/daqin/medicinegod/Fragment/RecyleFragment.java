package com.daqin.medicinegod.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daqin.medicinegod.Adspter.CloudCardAdapter;
import com.daqin.medicinegod.Adspter.MedicineCardAdapter;
import com.daqin.medicinegod.Adspter.RecycleCardAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.FragmentLoveBinding;
import com.daqin.medicinegod.databinding.FragmentRecyleBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecyleFragment extends Fragment {

    private FragmentRecyleBinding binding;
    View root;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    static List<Map<String, Object>> medicines = new ArrayList<>();
    RecycleCardAdapter adapter;
    private String lname = "0";
    Map<String, Object> userInfo = new HashMap<>();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyleBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        drawer = binding.idRecyleDrawerlayout;
        navigationView = binding.navViewRecyle;
        ImageButton btn_more = binding.idRecyleMore;
        btn_more.setOnClickListener(l -> {
            //打开滑动菜单  左侧出现
            drawer.openDrawer(GravityCompat.START);
        });
        Fragment fragment = requireActivity().getSupportFragmentManager().getFragments().get(0);
        navController = Navigation.findNavController(fragment.requireView());
        navigationView.setCheckedItem(R.id.navigation_recyle_title);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                navController.navigate(item.getItemId());
//                navController.navigate(R.id.navigation_me_title);

                return false;
            }
        });
        swipeRefreshLayout = binding.idRecyleAutoswiperefreshlayout;

        swipeRefreshLayout.autoRefresh();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMedicine();

            }
        });

        TextView nav_header_version = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_version);

        //软件内置版本
        nav_header_version.setText("版本: V" + Constant.VERSION + Constant.VNAME);


        TextView nav_main_header_usercloud = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_usercloud);
        ImageView nav_header_img = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_imageView);
        TextView nav_main_header_username = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_username);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        //登录状态，已登录的话会有数据
        int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");

        if (isLogin == 1 && !lname.equals("0")) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db != null) {
                Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
                while (cursor.moveToNext()) {
                    userInfo.put(Constant.COLUMN_U_LNAME, lname);
                    userInfo.put(Constant.COLUMN_U_SNAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SNAME)));
                    userInfo.put(Constant.COLUMN_U_HEAD, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_HEAD)));
                }
                cursor.close();
                if (!userInfo.toString().equals("{}") && userInfo != null) {
                    nav_main_header_username.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                    nav_header_img.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
                }
                cursor.close();
                db.close();
            }

            if (db != null) {
                db.close();
            }
        } else {
            nav_main_header_username.setText("MedicineGod药神");
            nav_main_header_usercloud.setText("暂未开启多端共享( - )");
            nav_header_img.setImageResource(R.mipmap.icon);
        }

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void initMedicine() {
        medicines.clear();
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 1 ", null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.COLUMN_M_KEYID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_KEYID)));
            map.put(Constant.COLUMN_M_NAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_NAME)));
            map.put(Constant.COLUMN_M_IMAGE, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_IMAGE)));
            map.put(Constant.COLUMN_M_UID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_UID)));
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
            map.put(Constant.COLUMN_M_SHOWFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHOWFLAG)));
            map.put(Constant.COLUMN_M_FROMWEB, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_FROMWEB)));
            map.put(Constant.COLUMN_M_GROUP, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_GROUP)));
            medicines.add(map);
        }
        cursor.close();
        db.close();
        TextView h_title = root.findViewById(R.id.id_recyle_title);
        h_title.setText("药品回收站(" + medicines.size() + ")");
        adapter = new RecycleCardAdapter(getContext(), R.layout.list_medicine, medicines);
        ListView listView = root.findViewById(R.id.id_recyle_list);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dialog dialog = new AlertDialog.Builder(requireContext())
                        .setTitle("已准备就绪")
                        .setMessage("是否恢复这个药品数据？")
                        .setNegativeButton("返回", null)
                        .setNeutralButton("确认恢复", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                recyleMedicine(position);
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String[] m_nameList = new String[medicines.size()];
                boolean[] m_checkList = new boolean[medicines.size()];
                for (int j = 0; j < medicines.size(); j++) {
                    Map<String, Object> map = medicines.get(j);
                    m_nameList[j] = (String) map.get(Constant.COLUMN_M_NAME);
                    m_checkList[j] = false;
                }
                ArrayList<Map<String, Object>> medicines_forRec = new ArrayList<>();
                //创造一个builder的构造器
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("请选择你要恢复的药品");
                builder.setMultiChoiceItems(m_nameList, m_checkList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        if (isChecked) {
                            m_checkList[which] = isChecked;
                            medicines_forRec.add(medicines.get(which));
                        } else {
                            m_checkList[which] = isChecked;
                            medicines_forRec.remove(medicines.get(which));
                        }

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                .setTitle(medicines_forRec.size() + "个已准备就绪")
                                .setMessage("是否恢复这些药品数据？")
                                .setNegativeButton("返回", null)
                                .setNeutralButton("开始恢复", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        recyleMedicine(medicines_forRec);
                                    }
                                })
                                .create();
                        dialog.show();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();

                return true;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void recyleMedicine(int which) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_M_DELFLAG, 0);
        int status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{(String) medicines.get(which).get(Constant.COLUMN_M_KEYID)});
        db.close();
        if (status >= 1) {
            medicines.remove(medicines.get(which));
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "恢复成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "恢复失败", Toast.LENGTH_SHORT).show();
        }
        TextView h_title = root.findViewById(R.id.id_recyle_title);
        h_title.setText("药品回收站(" + medicines.size() + ")");
    }

    @SuppressLint("SetTextI18n")
    public void recyleMedicine(ArrayList<Map<String, Object>> medicines_forRe) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        for (Map<String, Object> map : medicines_forRe) {
            ContentValues values = new ContentValues();
            values.put(Constant.COLUMN_M_DELFLAG, 0);
            int status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{(String) map.get(Constant.COLUMN_M_KEYID)});
            if (status >= 1) {
                medicines.remove(map);
                count++;
            }
        }
        db.close();
        adapter.notifyDataSetChanged();
        TextView h_title = root.findViewById(R.id.id_recyle_title);
        h_title.setText("药品回收站(" + medicines.size() + ")");
        Dialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("恢复完成")
                .setMessage("共" + medicines_forRe.size() + "个，恢复成功" + count + "个")
                .setNegativeButton("好", null)
                .create();
        dialog.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}