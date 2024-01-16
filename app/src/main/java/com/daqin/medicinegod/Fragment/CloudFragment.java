package com.daqin.medicinegod.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

import com.daqin.medicinegod.Adspter.CloudCardAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.FragmentCloudBinding;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CloudFragment extends Fragment {

    private FragmentCloudBinding binding;
    View root;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private AutoSwipeRefreshLayout swipeRefreshLayout;
    static List<Map<String, Object>> medicines = new ArrayList<>();
    CloudCardAdapter adapter;
    boolean showshow;
    Map<String, Object> userInfo = new HashMap<>();


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCloudBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        drawer = binding.idCloudDrawerlayout;
        swipeRefreshLayout = binding.idCloudAutoswiperefreshlayout;
        navigationView = binding.navViewCloud;
        ImageButton btn_more = binding.idCloudMore;


        btn_more.setOnClickListener(l -> {
            //打开滑动菜单  左侧出现
            drawer.openDrawer(GravityCompat.START);
        });


        //获取侧滑控件
        Fragment fragment = requireActivity().getSupportFragmentManager().getFragments().get(0);
        //navigationView就是头部的提示View


        navController = Navigation.findNavController(fragment.requireView());
        navigationView.setCheckedItem(R.id.navigation_cloud_title);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                navController.navigate(item.getItemId());

//                navController.navigate(R.id.navigation_me_title);

                return false;
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


        swipeRefreshLayout.autoRefresh();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initCloud();
            }
        });
        return root;
    }


    private String lname = "0";

    @SuppressLint({"SetTextI18n", "Range"})
    private void initCloud() {
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (lname.equals("0")) {
            Toast.makeText(requireContext(), "暂未登录，请登陆后使用！", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            medicines.clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    PreparedStatement pps = null;
                    ResultSet resultSet = null;
                    try {
                        connection = JdbcUtil.getConnection();
                        pps = connection.prepareStatement("SELECT * FROM `" + lname + "`");
                        resultSet = pps.executeQuery();
                        while (resultSet.next()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(Constant.COLUMN_M_KEYID, resultSet.getString(Constant.COLUMN_M_KEYID));
                            map.put(Constant.COLUMN_M_NAME, resultSet.getString(Constant.COLUMN_M_NAME));
                            map.put(Constant.COLUMN_M_IMAGE, resultSet.getBytes(Constant.COLUMN_M_IMAGE));
                            map.put(Constant.COLUMN_M_UID, resultSet.getString(Constant.COLUMN_M_UID));
                            map.put(Constant.COLUMN_M_DESCRIPTION, resultSet.getString(Constant.COLUMN_M_DESCRIPTION));
                            map.put(Constant.COLUMN_M_OUTDATE, resultSet.getLong(Constant.COLUMN_M_OUTDATE));
                            map.put(Constant.COLUMN_M_OTC, resultSet.getString(Constant.COLUMN_M_OTC));
                            map.put(Constant.COLUMN_M_BARCODE, resultSet.getString(Constant.COLUMN_M_BARCODE));
                            map.put(Constant.COLUMN_M_YU, resultSet.getString(Constant.COLUMN_M_YU));
                            map.put(Constant.COLUMN_M_ELABEL, resultSet.getString(Constant.COLUMN_M_ELABEL));
                            map.put(Constant.COLUMN_M_LOVE, resultSet.getString(Constant.COLUMN_M_LOVE));
                            map.put(Constant.COLUMN_M_SHARE, resultSet.getString(Constant.COLUMN_M_SHARE));
                            map.put(Constant.COLUMN_M_MUSE, resultSet.getString(Constant.COLUMN_M_MUSE));
                            map.put(Constant.COLUMN_M_COMPANY, resultSet.getString(Constant.COLUMN_M_COMPANY));
                            map.put(Constant.COLUMN_M_DELFLAG, resultSet.getString(Constant.COLUMN_M_DELFLAG));
                            map.put(Constant.COLUMN_M_SHOWFLAG, resultSet.getString(Constant.COLUMN_M_SHOWFLAG));
                            map.put(Constant.COLUMN_M_FROMWEB, resultSet.getString(Constant.COLUMN_M_FROMWEB));
                            map.put(Constant.COLUMN_M_GROUP, resultSet.getString(Constant.COLUMN_M_GROUP));
                            medicines.add(map);
                        }
                        TextView h_title = root.findViewById(R.id.id_cloud_title);
                        h_title.post(new Runnable() {
                            @Override
                            public void run() {
                                h_title.setText("云端药品(" + medicines.size() + ")");
                            }
                        });
                        showshow = Utils.getBoolean(requireContext(), Constant.SHOWSHOW, false);

                        adapter = new CloudCardAdapter(getContext(), R.layout.list_medicine, medicines, showshow);
                        ListView listView = root.findViewById(R.id.id_cloud_list);
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                                .setTitle("已准备就绪")
                                                .setMessage("【删除须知】\n删除云端数据是不可逆操作。\n是否删除这个药品数据？")
                                                .setNegativeButton("返回", null)
                                                .setNeutralButton("确认删除", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        delCloudMedicine(position);
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    }
                                });
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
                                ArrayList<Map<String, Object>> medicines_forDel = new ArrayList<>();
                                //创造一个builder的构造器
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("请选择你要删除的药品");
                                builder.setMultiChoiceItems(m_nameList, m_checkList, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                        if (isChecked) {
                                            m_checkList[which] = isChecked;
                                            medicines_forDel.add(medicines.get(which));
                                        } else {
                                            m_checkList[which] = isChecked;
                                            medicines_forDel.remove(medicines.get(which));
                                        }

                                    }
                                });
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                                .setTitle(medicines_forDel.size() + "个已准备就绪")
                                                .setMessage("【删除须知】\n删除云端数据是不可逆操作。\n是否删除这些药品数据？")
                                                .setNegativeButton("返回", null)
                                                .setNeutralButton("开始删除", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        delCloudMedicine(medicines_forDel);
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

                    } catch (Exception sqlException) {
                        Looper.prepare();
                        Toast.makeText(requireContext(), "出现错误，请刷新重试", Toast.LENGTH_SHORT).show();
                        sqlException.printStackTrace();
                        Looper.loop();

                    } finally {
                        JdbcUtil.close(connection, null, pps, resultSet);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }
            }).start();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void delCloudMedicine(int which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection c = null;
                PreparedStatement p = null;
                try {
                    c = JdbcUtil.getConnection();
                    int count = 0;
                    p = c.prepareStatement("DELETE FROM `" + lname + "` WHERE KEYID = ?");
                    p.setString(1, (String) medicines.get(which).get(Constant.COLUMN_M_KEYID));
                    int s = p.executeUpdate();
                    if (s >= 1) {
                        medicines.remove(medicines.get(which));
                        ListView listView = root.findViewById(R.id.id_cloud_list);
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    Looper.prepare();
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_LONG).show();
                    TextView h_title = root.findViewById(R.id.id_cloud_title);
                    h_title.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            h_title.setText("云端药品(" + medicines.size() + ")");

                        }
                    });
                    Looper.loop();
                } catch (SQLException sqlException) {
                    Looper.prepare();
                    Toast.makeText(getContext(), "网络错误，请重试", Toast.LENGTH_SHORT).show();
                    sqlException.printStackTrace();
                    Looper.loop();
                } finally {
                    JdbcUtil.close(c, null, p, null);

                }
            }
        }).start();
    }

    public void delCloudMedicine(ArrayList<Map<String, Object>> medicines_forDel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection c = null;
                PreparedStatement p = null;
                try {
                    c = JdbcUtil.getConnection();
                    int count = 0;
                    for (Map<String, Object> map : medicines_forDel) {
                        p = c.prepareStatement("DELETE FROM `" + lname + "` WHERE KEYID = ?");
                        p.setString(1, (String) map.get(Constant.COLUMN_M_KEYID));
                        int s = p.executeUpdate();
                        if (s >= 1) {
                            count++;
                            medicines.remove(map);
                        }
                    }
                    ListView listView = root.findViewById(R.id.id_cloud_list);
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    Looper.prepare();
                    Toast.makeText(getContext(), "共" + medicines_forDel.size() + "个，成功删除" + count + "个", Toast.LENGTH_LONG).show();
                    TextView h_title = root.findViewById(R.id.id_cloud_title);
                    h_title.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            h_title.setText("云端药品(" + medicines.size() + ")");
                        }
                    });
                    Looper.loop();
                } catch (SQLException sqlException) {
                    Looper.prepare();
                    Toast.makeText(getContext(), "网络错误，请重试", Toast.LENGTH_SHORT).show();
                    sqlException.printStackTrace();
                    Looper.loop();
                } finally {
                    JdbcUtil.close(c, null, p, null);

                }
            }
        }).start();
    }

}