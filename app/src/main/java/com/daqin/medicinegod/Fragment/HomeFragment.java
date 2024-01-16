package com.daqin.medicinegod.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.daqin.medicinegod.Adspter.MedicineCardAdapter;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daqin.medicinegod.AddActivity;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.MyService;
import com.daqin.medicinegod.CustomWidget.MyWidget;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.DetailActivity;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.HistoryExcelActivity;
import com.daqin.medicinegod.ImportActivity;
import com.daqin.medicinegod.LoginActivity;
import com.daqin.medicinegod.Utils.ExcelUtils;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.SearchActivity;
import com.daqin.medicinegod.CustomWidget.DragFloatingActionButton;
import com.daqin.medicinegod.databinding.FragmentHomeBinding;
import com.google.android.material.navigation.NavigationView;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import jxl.write.WriteException;

public class HomeFragment extends Fragment {
    //    private MyDBOpenHelper mhelper;//定义数据库帮助类对象
//    private SQLiteDatabase db;//定义一个可以操作的数据库对象
    private AppBarConfiguration mAppBarConfiguration;
    final Handler handler = new Handler();
    static ArrayList<Map<String, Object>> medicines = new ArrayList<>();

    Map<String, Object> userInfo = new HashMap<>();
    List<String> gruopInfo = new ArrayList<>();
    boolean showshow;

    private FragmentHomeBinding binding;
    Uri uri = null;
    Bitmap img = null;
    final int REQUEST_CODE_CHOOSE_IMG = 100;
    final int REQUEST_CODE_CROP = 102;
    final int REQUEST_CODE_CHOOSE_FILE = 103;

    View root;
    Random random = new Random();
    String signin = "1970-1-1";
    String group = Constant.COLUMN_G_DEFAULT;
    static String lname = "0";
    /**
     * 0 有效期由近到远
     * 1 有效期由远到近
     * 2 存储时间由近到远
     * 3 存储时间由近到远
     */
    int showMethod = 0;
    View dialogV = null;
    static String method = null;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private ActionBarDrawerToggle toggle;


    // 用于分页显示数据的属性
    private int loadPageSize = 20;// 每页显示的条数
    private int loadPageNow = 1;
    private int loadRowCount = 0;
    private int loadPageShow = 0;
    private int loadPageCount = 0;// 总页数
    private boolean loadIsBottom = false;// 判断是否滚动到数据最后一条


    @SuppressLint({"Range", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //先做一点初始化工作
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();


        //防止闪退
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("err", e.toString());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requireContext().checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "需要申请存储权限，否则无法使用部分功能", Toast.LENGTH_SHORT).show();
            requireActivity().requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

        }


        //这是展示方法，根据时间如何展示，为0
        showMethod = Utils.getInt(requireContext(), Constant.SHOWMETHODKEY, 0);
        //这是lname登录名，用户名，默认为0
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        Log.i(Constant.PREFERENCES_NAME, "登录名:" + lname);
        showshow = Utils.getBoolean(requireContext(), Constant.SHOWSHOW, false);
        //这是分组名，没有就是默认
        group = Utils.getString(requireContext(), Constant.GROUPKEY, Constant.COLUMN_G_DEFAULT);


        //侧滑
        drawer = binding.idHomepageDrawerlayout;
        ImageButton btn_more = binding.idHomepageMore;
        btn_more.setOnClickListener(l -> {
            //打开滑动菜单  左侧出现
            drawer.openDrawer(GravityCompat.START);
        });


//        NavController navController = Navigation.findNavController(binding.navHostFragmentContentHome);

        //获取侧滑控件
        Fragment fragment = requireActivity().getSupportFragmentManager().getFragments().get(0);
        //navigationView就是头部的提示View
        navigationView = binding.navViewHome;

        TextView nav_header_version = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_version);

        //软件内置版本
        nav_header_version.setText("版本: V" + Constant.VERSION + Constant.VNAME);
        Log.i(Constant.PREFERENCES_NAME, "当前版本：" + Constant.VERSION + Constant.VNAME);

//        toggle = new ActionBarDrawerToggle(, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        navController = Navigation.findNavController(fragment.requireView());
        navigationView.setCheckedItem(R.id.navigation_home_title);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                navController.navigate(item.getItemId());

//                navController.navigate(R.id.navigation_me_title);

                return false;
            }
        });


        //搜索
        ImageButton btn = binding.idHomepageSearch;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });
        //添加fab的按钮
        DragFloatingActionButton fab1 = binding.idHomepageToadd;

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddActivity.class);
                startActivity(i);
            }
        });


        TextView nav_main_header_usercloud = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_usercloud);
        ImageView nav_header_img = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_imageView);
        TextView nav_main_header_username = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_username);

        //登录状态，已登录的话会有数据
        int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
        if (isLogin == 1 && !lname.equals("0")) {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db != null) {
                Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
                while (cursor.moveToNext()) {
                    userInfo.put(Constant.COLUMN_U_LNAME, lname);
                    userInfo.put(Constant.COLUMN_U_SNAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SNAME)));
                    userInfo.put(Constant.COLUMN_U_PWD, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_PWD)));
                    userInfo.put(Constant.COLUMN_U_HEAD, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_HEAD)));
                    userInfo.put(Constant.COLUMN_U_FRIEND, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_FRIEND)));
                    userInfo.put(Constant.COLUMN_U_PHONE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_PHONE)));
                    userInfo.put(Constant.COLUMN_U_MAIL, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_MAIL)));
                    userInfo.put(Constant.COLUMN_U_RGTIME, Long.parseLong(Objects.requireNonNull(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_RGTIME)))));
                    userInfo.put(Constant.COLUMN_U_ONLINE, 1);
                    userInfo.put(Constant.COLUMN_U_POINT, Integer.parseInt(Objects.requireNonNull(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_POINT)))));
                    userInfo.put(Constant.COLUMN_U_POINTHISTORY, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_POINTHISTORY)));
                    userInfo.put(Constant.COLUMN_U_VIP, Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_VIP))));
                    userInfo.put(Constant.COLUMN_U_VIPYU, Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_VIPYU))));
                    userInfo.put(Constant.COLUMN_U_CLOUDYU, Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_CLOUDYU))));
                    userInfo.put(Constant.COLUMN_U_SIGNIN, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SIGNIN)));
                    userInfo.put(Constant.COLUMN_U_MYSTYLE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_MYSTYLE)));
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

/*
        //检查更新
        new Thread(new TimerTask() {
            @Override
            public void run() {
                Connection connection = null;
                ResultSet resultSet = null;
                PreparedStatement pps = null;
                try {
                    connection = JdbcUtil.getConnection();
                    if (connection != null) {
                        pps = connection.prepareStatement("SELECT * FROM VERSION ORDER BY `version` Desc LIMIT 1 ;");
                        resultSet = pps.executeQuery();
                        while (resultSet.next()) {
                            double version = resultSet.getDouble(1);
                            String vername = resultSet.getString(2);
                            String tips = resultSet.getString(3);
                            if (version > Constant.VERSION) {
                                Looper.prepare();
                                tips = "当前版本:" + Constant.VERSION + Constant.VNAME + "\n最新版本:" + version + vername + "\n\n更新内容:\n" + tips.replaceAll("@@", "\n");
                                AlertDialog dialog = new AlertDialog.Builder(getContext())
                                        .setTitle(version + vername + "更新公告:")
                                        .setMessage(tips)
                                        .setPositiveButton("稍后更新", null)
                                        .setNegativeButton("去更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Utils.putInt(requireContext(), Constant.VERSIONTIPS, 0);
                                                Uri uri = Uri.parse("http://medicinegod.cn/");
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        })
                                        .create();
                                dialog.show();
                                Looper.loop();
                            } else if (version == Constant.VERSION) {
                                if (isAdded()) {
                                    if (Utils.getInt(requireContext(), Constant.VERSIONTIPS, 0) == 0) {
                                        Looper.prepare();
                                        tips = "当前版本:" + Constant.VERSION + Constant.VNAME + "\n最新版本:" + version + vername + "\n\n更新内容:\n" + tips.replaceAll("@@", "\n");
                                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                .setTitle(version + vername + "版本公告:")
                                                .setMessage(tips)
                                                .setPositiveButton("确认", null)
                                                .setNegativeButton("不再提醒", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Utils.putInt(requireContext(), Constant.VERSIONTIPS, 1);
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                        Looper.loop();
                                    }
                                }
                            }
                        }
                    } else {
                        Looper.prepare();
                        Toast.makeText(requireContext(), "获取最新版本失败~", Toast.LENGTH_SHORT).show();
                        Log.i(Constant.PREFERENCES_NAME, "检查更新失败:链接自检发现为null");
                        Looper.loop();
                    }
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                    Toast.makeText(requireContext(), "获取最新版本失败~", Toast.LENGTH_SHORT).show();
                    Log.i(Constant.PREFERENCES_NAME, "检查更新失败：" + throwables.toString());


                } finally {
                    JdbcUtil.close(connection, null, pps, resultSet);
                }
            }
        }).start();




        //如果登录则执行签到
        if (isLogin == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    PreparedStatement pps = null;
                    ResultSet resultSet = null;
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    try {
                        connection = JdbcUtil.getConnection();
                        if (connection != null) {
                            pps = connection.prepareStatement("select " + Constant.COLUMN_U_SIGNIN +
                                    " from USERINFO where " + Constant.COLUMN_U_LNAME + " = ? ");
                            pps.setString(1, lname);
                            resultSet = pps.executeQuery();
                            while (resultSet.next()) {
                                signin = resultSet.getString(1);
                            }
                        }

                        if (db != null) {
                            db.close();
                        }
                    } catch (SQLException sqlException) {

                        Cursor ccursor = db.rawQuery("select " + Constant.COLUMN_U_SIGNIN +
                                        " from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ? ",
                                new String[]{lname});


                        while (ccursor.moveToNext()) {
                            signin = ccursor.getString(0);
                        }
                        ccursor.close();
                        sqlException.printStackTrace();
                    } finally {
                        if (db != null) {
                            db.close();
                        }

                    }
                    SQLiteDatabase db1 = dbHelper.getReadableDatabase();
                    Log.i(Constant.PREFERENCES_NAME, "上次签到日期：:" + signin + ",今天日期：" + Utils.getDate());
                    if (!Utils.getDate().equals(signin)) {
                        try {
                            if (connection != null) {
                                connection.setAutoCommit(false);

                                pps = connection.prepareStatement("update USERINFO set " + Constant.COLUMN_U_POINT
                                        + "=" + Constant.COLUMN_U_POINT + "+? where " + Constant.COLUMN_U_LNAME + " =?  ;");
                                pps.setInt(1, 20);
                                pps.setString(2, lname);
                                pps.executeUpdate();
                                pps = connection.prepareStatement("update USERINFO set " + Constant.COLUMN_U_POINTHISTORY
                                        + "=CONCAT(" + Constant.COLUMN_U_POINTHISTORY + ",?) where " + Constant.COLUMN_U_LNAME + " =?  ;");
                                pps.setString(1, "@@签到赠送|20|" + Utils.getDate());
                                pps.setString(2, lname);
                                pps.executeUpdate();
                                pps = connection.prepareStatement("update USERINFO set " + Constant.COLUMN_U_SIGNIN
                                        + "=? where " + Constant.COLUMN_U_LNAME + " =?  ;");
                                pps.setString(1, Utils.getDate());
                                pps.setString(2, lname);
                                pps.executeUpdate();
                                connection.commit();
                                ContentValues values = new ContentValues();
                                values.put(Constant.COLUMN_U_SIGNIN, Utils.getDate());
                                db1.update(Constant.TABLE_NAME_USER, values, Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
                                db1.close();
                                signin = Utils.getDate();
                                Log.i(Constant.PREFERENCES_NAME, signin + "签到成功");
                            } else {
                                Log.i(Constant.PREFERENCES_NAME, signin + "签到失败");

                            }


                        } catch (SQLException sqlException) {
                            signin = Utils.getDate();
                            Log.i(Constant.PREFERENCES_NAME, signin + "签到失败，等待回滚：" + sqlException.toString());
                            sqlException.printStackTrace();
                            if (connection != null) {
                                try {
                                    connection.rollback();
                                } catch (SQLException sqlException1) {
                                    sqlException1.printStackTrace();
                                    Log.i(Constant.PREFERENCES_NAME, "签到混滚失败:" + sqlException1.toString());
                                }
                            }

                        } finally {
                            if (db1 != null) {
                                db1.close();
                            }

                            JdbcUtil.close(connection, null, pps, resultSet);
                        }

                    } else {
                        Log.i(Constant.PREFERENCES_NAME, signin + "已经签到了");
                    }
                }
            }).start();

*//*
            //查询云端存储数量
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    ResultSet resultSet = null;
                    Connection connection = null;
                    PreparedStatement pps = null;
                    try {
                        connection = JdbcUtil.getConnection();
                        pps = connection.prepareStatement("SELECT COUNT(*) FROM `" + lname + "`");
                        resultSet = pps.executeQuery();
                        while (resultSet.next()) {
                            count = resultSet.getInt(1);
                        }
                        int finalCount = count;
                        nav_main_header_usercloud.post(new Runnable() {
                            @Override
                            public void run() {
                                //TODO:以后添加限制
//												nav_main_header_usercloud.setText("已开启多端共享(" + finalCount + "/" + userInfo.get(Constant.COLUMN_U_CLOUDYU) + ")");
                                nav_main_header_usercloud.setText("已开启多端共享(" + finalCount + "/∞)");
                            }
                        });
                    } catch (SQLException throwables) {
                        nav_main_header_usercloud.post(new Runnable() {
                            @Override
                            public void run() {
                                nav_main_header_usercloud.setText("暂未开启多端共享( - )");
                            }
                        });
                        throwables.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(requireContext(), "获取药品云端数据失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } finally {
                        JdbcUtil.close(connection, null, pps, resultSet);
                    }
                }
            }).start();


*/

        if (swipeRefreshLayout == null) {
            swipeRefreshLayout = root.findViewById(R.id.id_homepage_SwipeRefreshLayout);//初始化下拉刷新控件，
        }
//        DragFloatingActionButton fab2 = binding.idHomepageTotop;
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listView.scrollListBy(-999999999);
//            }
//        });
        //云端功能
        ImageButton idHomepageLoad = binding.idHomepageLoad;
        idHomepageLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requireContext().checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "需要申请存储权限，否则无法使用导入导出功能", Toast.LENGTH_SHORT).show();
                    requireActivity().requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);

                } else {


                    Dialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("请选择导入/导出操作")
                            .setNegativeButton("返回", null)
                            .setItems(R.array.load_method, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            try {
                                                String fileName = "药神导入药品模板" + Utils.getTimeStamp() + ".xls";
                                                String filePath = ExcelUtils.getRootPath() + fileName;
                                                ExcelUtils.initExcel(requireContext(),
                                                        filePath
                                                        , "Sheet1",
                                                        Utils.getMedicineColumn_cn());
                                                AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                        .setTitle("生成模板文件成功！")
                                                        .setMessage("模板文件已生成在" + filePath + "下！\n是否需要现在进行一些操作？\n稍后可在[历史记录]中找到你的文件")
                                                        .setPositiveButton("发送至...", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Utils.startShareFile(requireContext(), filePath, Constant.FILE_TYPE_XLS, "将药神导入药品模板文件分享到...");
                                                            }
                                                        })
                                                        .setNegativeButton("打开...", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Utils.startOpenFile(requireContext(), filePath, Constant.FILE_TYPE_XLS, "选择应用程序来打开药神导入药品模板文件");
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .setNeutralButton("返回", null)
                                                        .create();
                                                tips.show();
                                                Utils.insertHisExcel(requireContext(), Constant.COLUMN_HE_FLAG_TEMP, fileName, filePath);
                                            } catch (IOException | WriteException e) {
                                                e.printStackTrace();
                                                AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                        .setTitle("生成模板文件失败！")
                                                        .setMessage("请检查文件权限是否已开启，否则无法生成模板文件！")
                                                        .setNeutralButton("好", null)
                                                        .create();
                                                tips.show();
                                            }
                                            break;
                                        case 1:
//                                            AlertDialog tips11 = new AlertDialog.Builder(requireContext())
//                                                    .setTitle("提示：")
//                                                    .setMessage("正在开发中")
//                                                    .setNeutralButton("好", null)
//                                                    .create();
//                                            tips11.show();
                                            //TODO:图片导不进来，日后想办法，现在手动吧
                                            AlertDialog tips11 = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示：")
                                                    .setMessage("1.请按照模板模块填入并导入内容。\n2.导入的药品需符合法律法规，不违反国家法律。")
                                                    .setNeutralButton("我承诺，开始导入", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent a = new Intent(requireContext(), ImportActivity.class);
                                                            startActivity(a);

                                                        }
                                                    })
                                                    .setNegativeButton("取消", null)
                                                    .create();
                                            tips11.show();


                                            break;
                                        case 2:
                                            final ProgressDialog progressDialog_2 = new ProgressDialog(requireContext());
                                            progressDialog_2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            progressDialog_2.setMessage("正在导出药品中(仅限于本地药品)...");
                                            progressDialog_2.setTitle("请稍后(可置于后台)");
                                            int num = Utils.getInt(requireContext(), Constant.MG_M_COUNT_LOCAL, 0);
                                            int count = 0;
                                            progressDialog_2.setMax(num);
                                            String filename = "药神本地数据_导出" + Utils.getTimeStamp() + ".xls";
                                            String filepath = ExcelUtils.getRootPath() + filename;
                                            Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                int progress = 0;

                                                @Override
                                                public void run() {
                                                    progressDialog_2.setProgress(progress += 1);
                                                }
                                            }, 0, 2000);

                                            try {
                                                count = ExcelUtils.exportMedicineToExcel(medicines, filepath, Utils.getMedicineColumn_cn(), Utils.getMedicineColumn_origin());
                                                progressDialog_2.show();
                                            } catch (WriteException | IOException e) {
                                                Looper.prepare();
                                                Dialog dialoga = new AlertDialog.Builder(requireContext())
                                                        .setTitle("提示：")
                                                        .setMessage("写出文件出现错误，请检查权限是否开启！\n错误原因" + e)
                                                        .setNegativeButton("了解", null)
                                                        .create();
                                                dialoga.show();
                                                progressDialog_2.dismiss();
                                                timer.cancel();
                                                Looper.loop();
                                                e.printStackTrace();
                                            }
                                            progressDialog_2.dismiss();
                                            timer.cancel();
                                            Utils.insertHisExcel(requireContext(), Constant.COLUMN_HE_FLAG_EXPORT, filename, filepath);
                                            Dialog dialoga = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示：")
                                                    .setMessage("导出成功！药品数据中包含本地药品" + num + "个，导出成功" + count + "个。\n你可以在" + filepath + "目录下找到他，或是通过[历史记录]进行操作。")
                                                    .setPositiveButton("发送至...", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Utils.startShareFile(requireContext(), filepath, Constant.FILE_TYPE_XLS, "将药神导出药品的数据文件分享到...");
                                                        }
                                                    })
                                                    .setNegativeButton("打开...", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Utils.startOpenFile(requireContext(), filepath, Constant.FILE_TYPE_XLS, "选择应用程序来打开药神导出药品的数据文件");

                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .setNeutralButton("返回", null)
                                                    .create();
                                            dialoga.show();


                                            break;
                                        case 3:
                                            Intent a = new Intent(requireContext(), HistoryExcelActivity.class);
                                            startActivity(a);
                                            break;
                                    }
                                }
                            })

                            .create();

                    dialog.show();
                }
            }
        });
        ImageButton id_homepage_upAndDown = binding.idHomepageUpAndDown;
        id_homepage_upAndDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
                lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                if (isLogin == 0) {
                    AlertDialog tips = new AlertDialog.Builder(requireContext())
                            .setTitle("提示:")
                            .setMessage("未登录无法使用云端功能")
                            .setNeutralButton("去登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(requireContext(), LoginActivity.class);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    tips.show();
                } else if (lname.equals("0")) {
                    Dialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("错误")
                            .setMessage("用户id不存在，请尝试重新登录账号？")
                            .setNegativeButton("好", null)
                            .create();
                    dialog.show();
                } else {
                    Dialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("请选择云端操作")
                            .setNegativeButton("返回", null)
                            .setItems(R.array.cloud_method,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                String[] m_nameList = new String[medicines.size()];
                                                boolean[] m_checkList = new boolean[medicines.size()];
                                                for (int j = 0; j < medicines.size(); j++) {
                                                    Map<String, Object> map = medicines.get(j);
                                                    m_nameList[j] = (String) map.get(Constant.COLUMN_M_NAME);
                                                    m_checkList[j] = false;
                                                }
                                                ArrayList<Map<String, Object>> medicines_forUpLoad = new ArrayList<>();
                                                //创造一个builder的构造器
                                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                                builder.setTitle("请选择你要上传的药品");
                                                builder.setMultiChoiceItems(m_nameList, m_checkList, new DialogInterface.OnMultiChoiceClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                                        if (isChecked) {
                                                            m_checkList[which] = isChecked;
                                                            medicines_forUpLoad.add(medicines.get(which));
                                                        } else {
                                                            m_checkList[which] = isChecked;
                                                            medicines_forUpLoad.remove(medicines.get(which));
                                                        }

                                                    }
                                                });
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                                                .setTitle("准备就绪")
                                                                .setMessage("【上传须知】\n1.上传药品需符合法律法规，不违反国家法律。\n是否开始上传药品数据？")
                                                                .setNegativeButton("返回", null)
                                                                .setNeutralButton("开始上传", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        upLoadMedicines(medicines_forUpLoad);
                                                                    }
                                                                })
                                                                .create();
                                                        dialog.show();
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                AlertDialog ad = builder.create();
                                                ad.show();
                                            } else if (i == 1) {
                                                dialogInterface.dismiss();
                                                final ProgressDialog progressDialog = new ProgressDialog(requireContext());
                                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                                progressDialog.setMessage("正在下载中(可置于后台)...");
                                                progressDialog.setTitle("请稍后");
//                                                progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        Dialog dialoga = new AlertDialog.Builder(requireContext())
//                                                                .setTitle("提示：")
//                                                                .setCancelable(false)
//                                                                .setMessage("真的要取消吗？(后台仍在进行中)")
//                                                                .setNegativeButton("是", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                                        progressDialog.dismiss();
//                                                                        progressDialog.cancel();
//                                                                    }
//                                                                })
//                                                                .setNeutralButton("返回", new DialogInterface.OnClickListener() {
//                                                                    @Override
//                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                                        progressDialog.show();
//
//                                                                    }
//                                                                })
//                                                                .create();
//                                                        dialoga.show();
//                                                    }
//                                                });

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        int progress = 0;
                                                        Connection connection = null;
                                                        PreparedStatement pps1 = null;
                                                        PreparedStatement pps2 = null;
                                                        ResultSet resultSet1 = null;
                                                        ResultSet resultSet2 = null;
                                                        ArrayList<Map<String, Object>> medicines_DownLoads = new ArrayList<>();
                                                        try {
                                                            int maxIndex = 0;
                                                            connection = JdbcUtil.getConnection();
                                                            connection.setAutoCommit(false);
                                                            pps1 = connection.prepareStatement("SELECT count(*) `COUNT` FROM `" + lname + "`");
                                                            pps2 = connection.prepareStatement("SELECT * FROM `" + lname + "`");
                                                            resultSet1 = pps1.executeQuery();
                                                            resultSet2 = pps2.executeQuery();
                                                            connection.commit();
                                                            while (resultSet1.next()) {
                                                                maxIndex = resultSet1.getInt("COUNT");
                                                            }
                                                            if (maxIndex <= 0) {

                                                                Looper.prepare();
                                                                Dialog dialoga = new AlertDialog.Builder(requireContext())
                                                                        .setTitle("提示：")
                                                                        .setCancelable(false)
                                                                        .setMessage("您的云端数据为空！")
                                                                        .setNegativeButton("了解", null)
                                                                        .create();
                                                                dialoga.show();
                                                                Looper.loop();
                                                                return;
                                                            }
                                                            progressDialog.setMax(maxIndex);
                                                            handler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progressDialog.show();
                                                                }
                                                            });


                                                            while (resultSet2.next()) {
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put(Constant.COLUMN_M_KEYID, resultSet2.getString(Constant.COLUMN_M_KEYID));
                                                                map.put(Constant.COLUMN_M_NAME, resultSet2.getString(Constant.COLUMN_M_NAME));
                                                                map.put(Constant.COLUMN_M_IMAGE, resultSet2.getBytes(Constant.COLUMN_M_IMAGE));
                                                                map.put(Constant.COLUMN_M_DESCRIPTION, resultSet2.getString(Constant.COLUMN_M_DESCRIPTION));
                                                                map.put(Constant.COLUMN_M_OUTDATE, resultSet2.getLong(Constant.COLUMN_M_OUTDATE));
                                                                map.put(Constant.COLUMN_M_OTC, resultSet2.getString(Constant.COLUMN_M_OTC));
                                                                map.put(Constant.COLUMN_M_BARCODE, resultSet2.getString(Constant.COLUMN_M_BARCODE));
                                                                map.put(Constant.COLUMN_M_YU, resultSet2.getString(Constant.COLUMN_M_YU));
                                                                map.put(Constant.COLUMN_M_ELABEL, resultSet2.getString(Constant.COLUMN_M_ELABEL));
                                                                map.put(Constant.COLUMN_M_LOVE, resultSet2.getInt(Constant.COLUMN_M_LOVE));
                                                                map.put(Constant.COLUMN_M_SHARE, resultSet2.getString(Constant.COLUMN_M_SHARE));
                                                                map.put(Constant.COLUMN_M_MUSE, resultSet2.getString(Constant.COLUMN_M_MUSE));
                                                                map.put(Constant.COLUMN_M_COMPANY, resultSet2.getString(Constant.COLUMN_M_COMPANY));
                                                                map.put(Constant.COLUMN_M_DELFLAG, 0);
                                                                map.put(Constant.COLUMN_M_SHOWFLAG, resultSet2.getInt(Constant.COLUMN_M_SHOWFLAG));
                                                                map.put(Constant.COLUMN_M_FROMWEB, resultSet2.getInt(Constant.COLUMN_M_FROMWEB));
                                                                map.put(Constant.COLUMN_M_UID, resultSet2.getString(Constant.COLUMN_M_UID));
                                                                map.put(Constant.COLUMN_M_GROUP, resultSet2.getString(Constant.COLUMN_M_GROUP));
                                                                medicines_DownLoads.add(map);
                                                                System.out.println(progress);
                                                                progressDialog.setProgress(progress += 1);

                                                                try {
                                                                    Thread.sleep(200);
                                                                } catch (Exception ex) {
                                                                    Log.e(Constant.PREFERENCES_NAME, ex.toString());
                                                                }
                                                            }
                                                            Looper.prepare();
                                                            progressDialog.dismiss();

                                                            //TODO:修改这里的显示
                                                            String[] m_nameList = new String[medicines_DownLoads.size()];
                                                            boolean[] m_checkList = new boolean[medicines_DownLoads.size()];
                                                            for (int j = 0; j < medicines_DownLoads.size(); j++) {
                                                                Map<String, Object> map = medicines_DownLoads.get(j);
                                                                m_nameList[j] = (String) map.get(Constant.COLUMN_M_NAME);
                                                                m_checkList[j] = false;
                                                            }
                                                            ArrayList<Map<String, Object>> medicines_forDownLoad = new ArrayList<>();
                                                            //创造一个builder的构造器
                                                            AlertDialog.Builder builder00 = new AlertDialog.Builder(requireContext());
                                                            builder00.setTitle("请选择你要缓存的药品");
                                                            builder00.setMultiChoiceItems(m_nameList, m_checkList, new DialogInterface.OnMultiChoiceClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                                                    if (isChecked) {
                                                                        m_checkList[which] = isChecked;
                                                                        medicines_forDownLoad.add(medicines_DownLoads.get(which));
                                                                    } else {
                                                                        m_checkList[which] = isChecked;
                                                                        medicines_forDownLoad.remove(medicines_DownLoads.get(which));
                                                                    }
                                                                }
                                                            });
                                                            builder00.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int which) {
                                                                    Dialog dialog = new AlertDialog.Builder(requireContext())
                                                                            .setTitle("准备就绪")
                                                                            .setMessage("【下载须知】\n1.下载的药品也许会覆盖某些药品内容。\n2.如已修改某些药品内容，建议先单例上传后再进行更新。")
                                                                            .setNegativeButton("返回", null)
                                                                            .setNeutralButton("缓存到本机", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                    downLoadMedicines(medicines_forDownLoad);

                                                                                }
                                                                            })
                                                                            .create();
                                                                    dialog.show();
                                                                    dialogInterface.dismiss();
                                                                }
                                                            });
                                                            AlertDialog ad00 = builder00.create();
                                                            ad00.show();
                                                            Looper.loop();

                                                        } catch (SQLException sqlException) {
                                                            Looper.prepare();
                                                            Toast.makeText(requireContext(), "下载失败，请保证网络畅通", Toast.LENGTH_SHORT).show();
                                                            Looper.loop();
                                                            sqlException.printStackTrace();
                                                        } finally {
                                                            progressDialog.dismiss();
                                                            JdbcUtil.close(connection, null, pps1, resultSet1);
                                                            JdbcUtil.close(null, null, pps2, resultSet2);
                                                        }


                                                    }
                                                }).start();

                                            } else if (i == 2) {
                                                int count = 0;
                                                ArrayList<Map<String, Object>> mediciness = new ArrayList<>();
                                                for (Map<String, Object> map : medicines) {
                                                    String uid = (String) map.get(Constant.COLUMN_M_UID);
                                                    if (uid == null) {
                                                        uid = "0";
                                                    }
                                                    if (!uid.equals(lname) || uid.equals("0")) {
                                                        count++;
                                                    } else {
                                                        mediciness.add(map);
                                                    }
                                                }
                                                if (medicines.size() == count) {
                                                    Dialog dialog = new AlertDialog.Builder(requireContext())
                                                            .setTitle("准备就绪")
                                                            .setMessage("【上传须知】\n1.上传药品需符合法律法规，不违反国家法律。\n是否开始上传药品数据？")
                                                            .setNegativeButton("返回", null)
                                                            .setNeutralButton("开始上传", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    upLoadMedicines();
                                                                }
                                                            })
                                                            .create();
                                                    dialog.show();
                                                } else {
                                                    Dialog dialog = new AlertDialog.Builder(requireContext())
                                                            .setTitle("准备就绪")
                                                            .setMessage("【上传须知】\n1.上传药品需符合法律法规，不违反国家法律。\n当前本地共有" + medicines.size() + "条数据，" +
                                                                    "此账号所属" + count + "条数据，请选择是否合并上传到此账号？\n\n【上传后本地数据无影响】")
                                                            .setNegativeButton("返回", null)
                                                            .setNeutralButton("上传账号所属", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    upLoadMedicines(mediciness);
                                                                }
                                                            })
                                                            .setNeutralButton("上传所有药品", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    upLoadMedicines();
                                                                }
                                                            })
                                                            .create();
                                                    dialog.show();
                                                }
                                            } else if (i == 3) {
                                                Dialog dialog = new AlertDialog.Builder(requireContext())
                                                        .setTitle("准备就绪")
                                                        .setMessage("【下载须知】\n1.下载的药品也许会覆盖某些药品内容。\n2.如已修改某些药品内容，请先上传后再进行更新。")
                                                        .setNegativeButton("返回", null)
                                                        .setNeutralButton("下载到本机", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                downLoadMedicines();
                                                            }
                                                        })
                                                        .create();
                                                dialog.show();
                                            }
                                        }
                                    })
                            .create();

                    dialog.show();
                }

            }
        });

        if (listView == null) {
            listView = (SwipeMenuListView) root.findViewById(R.id.homepagelist);
        }
        View view_loadmore = getLayoutInflater().inflate(R.layout.bar_loadmore, null);
        //先使用inflate实例化然后使用下面的方法填入
        listView.addFooterView(view_loadmore);
        //使该页面加载在底部，相反的加载在顶部的是 listView.addHeaderView();
        //页面数据单击弹窗
        TextView bar_load_more_title = view_loadmore.findViewById(R.id.bar_load_more_title);
        ProgressBar bar_load_more_cycle = view_loadmore.findViewById(R.id.bar_load_more_cycle);

        if (loadRowCount <= 0) {
            view_loadmore.setVisibility(View.GONE);
        }
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (loadIsBottom) {
                    if (loadRowCount <= 0) {
                        view_loadmore.setVisibility(View.GONE);
                    }
                    // 如果滚到最后一条数据（即：屏幕最底端），则显示：“加载更多新数据”
                    else if (loadPageNow < loadPageCount) {
                        view_loadmore.setVisibility(View.VISIBLE);
                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        bar_load_more_title.setText("数据加载中...");
                        bar_load_more_cycle.setVisibility(View.VISIBLE);
                        loadPageNow++;
                        Cursor cursor;
                        String sql_group = group.equals(Constant.COLUMN_G_All) ? " " : " and `" + Constant.COLUMN_M_GROUP + "`= '" + group + "' ";
                        switch (showMethod) {
                            case 1:
                                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " ORDER BY `" + Constant.COLUMN_M_OUTDATE + "` DESC limit ?,? ", new String[]{String.valueOf(loadPageShow), String.valueOf((loadPageShow + loadPageSize))});
                                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                                break;
                            case 2:
                            case 3:
                                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " limit ?,? ", new String[]{String.valueOf(loadPageShow), String.valueOf((loadPageShow + loadPageSize))});
                                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                                break;
                            case 0:
                            default:
                                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " ORDER BY `" + Constant.COLUMN_M_OUTDATE + "` limit ?,? ", new String[]{String.valueOf(loadPageShow), String.valueOf((loadPageShow + loadPageSize))});
                                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                                break;
                        }
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
                        db.close();
                        adapter.notifyDataSetChanged();
                        TextView h_title = root.findViewById(R.id.id_homepage_title);
                        if (!group.equals(Constant.COLUMN_G_All)) {
                            h_title.setText(group + " (" + loadRowCount + ")");
                        } else {
                            h_title.setText("全部药品(" + loadRowCount + ")");
                        }
                    } else {
                        loadPageNow = loadPageCount;
                        view_loadmore.setVisibility(View.VISIBLE);
                        bar_load_more_cycle.setVisibility(View.GONE);
                        bar_load_more_title.setText("再怎么翻也没有啦~");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                // i  firstVisibleItem
                // i1 visibleItemCount
                // i2 totalItemCount
                loadIsBottom = (i + i1 == i2);

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != view_loadmore) {
                    Map<String, Object> map = medicines.get(position);
                    if (Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString()) == 1) {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_medicine_web, null, false);
                        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();
                        TextView name = v.findViewById(R.id.id_dialog_medicine_web_name);
                        ImageView img = v.findViewById(R.id.id_dialog_medicine_web_image);
                        Button btn_edi = v.findViewById(R.id.id_dialog_medicine_web_edit);
                        Button btn_del = v.findViewById(R.id.id_dialog_medicine_web_del);
                        TextView desp = v.findViewById(R.id.id_dialog_medicine_web_desp);
                        TextView company = v.findViewById(R.id.id_dialog_medicine_web_company);
                        TextView usage = v.findViewById(R.id.id_dialog_medicine_web_usage);
                        TextView group = v.findViewById(R.id.id_dialog_medicine_web_group);
                        TextView close = v.findViewById(R.id.id_dialog_medicine_web_close);
                        FlowLayout flowLayout = v.findViewById(R.id.id_dialog_medicine_web_elabel);
                        name.setText((String) map.get(Constant.COLUMN_M_NAME));
//					Utils.setImageViewCorner(img, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                        img.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                        group.setText((String) map.get(Constant.COLUMN_M_GROUP));
                        desp.setText((String) map.get(Constant.COLUMN_M_DESCRIPTION));
                        company.setText((String) map.get(Constant.COLUMN_M_COMPANY));
                        usage.setText((String) map.get(Constant.COLUMN_M_MUSE));
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
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        btn_edi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Utils.putString(requireContext(), Constant.EDITKEY, (String) map.get(Constant.COLUMN_M_KEYID));
                                dialog.dismiss();
                                Intent i = new Intent(getContext(), DetailActivity.class);
                                startActivity(i);
                            }
                        });
                        btn_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                delMedDialog(map);
                            }
                        });
                        //清空背景黑框
                        dialog.getWindow().getDecorView().setBackground(null);
                        dialog.show();

                        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                        dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));


                    } else {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_medicine_local, null, false);
                        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();
                        TextView name = v.findViewById(R.id.id_dialog_medicine_name);

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

                        Button btn_edit = v.findViewById(R.id.id_dialog_medicine_edit);
                        Button btn_del = v.findViewById(R.id.id_dialog_medicine_del);
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
                        group.setText((String) map.get(Constant.COLUMN_M_GROUP));
                        String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
                        name.setText((String) map.get(Constant.COLUMN_M_NAME));
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
                        yu.setText("约可使用:" + Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_YU)).toString()) / Integer.parseInt(muse[0]) + " " + muse[3]);
//					Utils.setImageViewCorner(img, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                        img.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));

                        Bitmap bitmap_bacrcode = Utils.createBarcode(String.valueOf(map.get(Constant.COLUMN_M_BARCODE)), 150, 80, false);
                        //TODO:分享码
                        Bitmap bitmap_share = CodeCreator.createQRCode("测试数据", 80, 80, null);
                        share.setImageBitmap(bitmap_share);
                        barcode.setImageBitmap(bitmap_bacrcode);

                        long date = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
                        outdate.setText(Utils.getOutDateString(date, 1));
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        btn_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.putString(requireContext(), Constant.EDITKEY, (String) map.get(Constant.COLUMN_M_KEYID));
                                dialog.dismiss();
                                Intent i = new Intent(getContext(), DetailActivity.class);
                                startActivity(i);

                            }
                        });
                        btn_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                delMedDialog(map);
                            }
                        });
                        //清空背景黑框
                        dialog.getWindow().getDecorView().setBackground(null);
                        dialog.show();

                        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                        dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));

                    }
                }
            }
        });

        //下拉刷新框
        swipeRefreshLayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
        swipeRefreshLayout.setOnRefreshListener(new AutoSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPageNow = 1;
                reMedicine();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.i(Constant.PREFERENCES_NAME, "更新了药品数据");

                    }

                }, 500);


            }
        });

        //侧滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "Edit" item
                SwipeMenuItem XianZhiItem = new SwipeMenuItem(getContext());
                // set item background
                XianZhiItem.setBackground(new ColorDrawable(Color.rgb(104, 118, 237)));
                // set item width
                XianZhiItem.setWidth(200);
                // set item title
                XianZhiItem.setTitle("发 布");
                // set item title fontsize
                XianZhiItem.setTitleSize(16);
                // set item title font color
                XianZhiItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(XianZhiItem);

                // create "Edit" item
                SwipeMenuItem EditItem = new SwipeMenuItem(getContext());
                // set item background
                EditItem.setBackground(new ColorDrawable(Color.rgb(159, 209,
                        161)));
                // set item width
                EditItem.setWidth(200);
                // set item title
                EditItem.setTitle("编 辑");
                // set item title fontsize
                EditItem.setTitleSize(16);
                // set item title font color
                EditItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(EditItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(254,
                        159, 145)));
                // set item width
                deleteItem.setWidth(200);
                // set item title
                deleteItem.setTitle("删 除");
                // set item title fontsize
                deleteItem.setTitleSize(16);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Map<String, Object> map = medicines.get(position);
                lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                switch (index) {
                    case 0:
                        //发布

                        if (!lname.equals("0")) {
                            if (Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString()) == 1) {
                                dialogV = LayoutInflater.from(getContext()).inflate(R.layout.dialog_collage_borrow, null, false);
                                final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogV).create();
                                TextView id_collage_b_method_xianzhi = dialogV.findViewById(R.id.id_collage_b_method_xianzhi);
                                TextView id_collage_b_method_xuqiu = dialogV.findViewById(R.id.id_collage_b_method_xuqiu);
                                TextView id_collage_b_method_jiaohuan = dialogV.findViewById(R.id.id_collage_b_method_jiaohuan);
                                id_collage_b_method_xianzhi.setVisibility(View.GONE);
                                id_collage_b_method_jiaohuan.setVisibility(View.GONE);
                                TextView id_collage_b_method_title = dialogV.findViewById(R.id.id_collage_b_method_title);

                                method = Constant.COLUMN_C_B_METHOD_XUQIU;
                                id_collage_b_method_xuqiu.setBackgroundResource(R.drawable.bg_collage_status_red);
                                id_collage_b_method_xuqiu.setTextColor(Color.rgb(255, 67, 54));
                                id_collage_b_method_title.setText("分类(1/1)");

                                EditText id_collage_b_rname = dialogV.findViewById(R.id.id_collage_b_rname);
                                EditText id_collage_b_addr = dialogV.findViewById(R.id.id_collage_b_addr);
                                EditText id_collage_b_phone = dialogV.findViewById(R.id.id_collage_b_phone);
                                id_collage_b_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                                TextView id_collage_b_title_title = dialogV.findViewById(R.id.id_collage_b_title_title);
                                EditText id_collage_b_title = dialogV.findViewById(R.id.id_collage_b_title);
                                id_collage_b_title.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        id_collage_b_title_title.setText("标题(" + s.length() + "/100)");
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                id_collage_b_title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                        100)});
                                TextView id_collage_b_close = dialogV.findViewById(R.id.id_collage_b_close);
                                id_collage_b_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                .setTitle("提示:")
                                                .setMessage("确认要关闭吗？")
                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialoga, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips.show();

                                    }
                                });

                                TextView id_collage_b_context_title = dialogV.findViewById(R.id.id_collage_b_context_title);
                                EditText id_collage_b_context = dialogV.findViewById(R.id.id_collage_b_context);
                                id_collage_b_context.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        id_collage_b_context_title.setText("内容(" + s.length() + "/350)");
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                id_collage_b_context.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                        350)});
                                id_collage_b_title.setText("我需要" + map.get(Constant.COLUMN_M_NAME) + "，请联系我！");
                                id_collage_b_context.setText("我需要这种药品，请持有这种药品的联系我!\n\n药名:"
                                        + map.get(Constant.COLUMN_M_NAME)
                                        + "\n描述:" + map.get(Constant.COLUMN_M_DESCRIPTION)
                                        + "\n制药:" + map.get(Constant.COLUMN_M_COMPANY));
                                TextView id_collage_b_img_title = dialogV.findViewById(R.id.id_collage_b_img_title);
                                ImageButton id_collage_b_img_clear = dialogV.findViewById(R.id.id_collage_b_img_clear);
                                ImageButton id_collage_b_img_crop = dialogV.findViewById(R.id.id_collage_b_img_crop);
                                RoundImageView id_collage_b_img = dialogV.findViewById(R.id.id_collage_b_img);
                                id_collage_b_img_crop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (uri == null) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("图片为空，无法裁剪！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else {
                                            startImageCrop(uri);
                                        }
                                    }
                                });

                                id_collage_b_img_clear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        id_collage_b_img_title.setText("介绍图片(0/1)");
                                        id_collage_b_img.setImageResource(R.mipmap.add_imgdefault);
                                        uri = null;
                                        img = null;
                                    }
                                });
                                id_collage_b_img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("image/*");
                                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
                                    }
                                });
                                id_collage_b_img_title.setText("介绍图片(1/1)");
                                img = Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE));
                                if (img != null) {
                                    id_collage_b_img.setImageBitmap(img);
                                } else {
                                    id_collage_b_img.setImageResource(R.mipmap.add_imgdefault);
                                }
                                uri = null;

                                Button id_collage_b_write = dialogV.findViewById(R.id.id_collage_b_write);
                                id_collage_b_write.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (method == null) {
                                            method = Constant.COLUMN_C_B_METHOD_XUQIU;
                                        }
                                        if (method == null) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("未选择分类！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_title.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("标题不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_context.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("内容不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_phone.getText().length() == 0 ||
                                                id_collage_b_phone.getText().length() != 11) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("手机格式错误！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_addr.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("地址不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_rname.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("名字不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Connection con = null;
                                                    PreparedStatement pps = null;
                                                    try {
                                                        con = JdbcUtil.getConnection();
                                                        pps = con.prepareStatement("INSERT INTO " + Constant.TABLE_COLLAGE_BORROW + " ("
                                                                + Constant.COLUMN_C_B_LNAME + ","
                                                                + Constant.COLUMN_C_B_TITLE + ","
                                                                + Constant.COLUMN_C_B_CONTEXT + ","
                                                                + Constant.COLUMN_C_B_TORNAME + ","
                                                                + Constant.COLUMN_C_B_TOPHONE + ","
                                                                + Constant.COLUMN_C_B_TOADDRESS + ","
                                                                + Constant.COLUMN_C_B_IMAGE + ","
                                                                + Constant.COLUMN_C_B_TIMESHOW + ","
                                                                + Constant.COLUMN_C_B_METHOD
                                                                + ")VALUES(?,?,?,?,?,?,?,?,?)");
                                                        //TODO:图片修改为原来的情况
                                                        pps.setString(1, lname);
                                                        pps.setString(2, id_collage_b_title.getText().toString().trim());
                                                        pps.setString(3, id_collage_b_context.getText().toString().trim());
                                                        pps.setString(4, id_collage_b_rname.getText().toString().trim());
                                                        pps.setString(5, id_collage_b_phone.getText().toString().trim());
                                                        pps.setString(6, id_collage_b_addr.getText().toString().trim());
//                                                        pps.setBytes(7, (img != null) ? Utils.getBytesFromBitmap(img) : null);
                                                        pps.setBytes(7, null);
                                                        pps.setString(8, String.valueOf(Utils.getTimeFromString(Utils.getTime())));
                                                        pps.setString(9, method);
                                                        int status = pps.executeUpdate();
                                                        if (status >= 1) {
                                                            Looper.prepare();
                                                            Log.i(Constant.PREFERENCES_NAME, "信息发布成功");
                                                            dialog.dismiss();
                                                            Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_SHORT).show();
                                                            Looper.loop();
                                                        } else {
                                                            Looper.prepare();
                                                            Log.i(Constant.PREFERENCES_NAME, "信息发布出错");
                                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                                    .setTitle("提示:")
                                                                    .setMessage("发布失败！请重试")
                                                                    .setNegativeButton("返回", null)
                                                                    .create();
                                                            tips.show();
                                                            Looper.loop();
                                                        }
                                                    } catch (SQLException sqlException) {
                                                        sqlException.printStackTrace();
                                                        Looper.prepare();
                                                        Log.i(Constant.PREFERENCES_NAME, sqlException.toString());
                                                        AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                                .setTitle("提示:")
                                                                .setMessage("网络连接失败！")
                                                                .setNegativeButton("返回", null)
                                                                .create();
                                                        tips.show();
                                                        Looper.loop();
                                                    } finally {
                                                        JdbcUtil.close(con, null, pps, null);
                                                    }
                                                }
                                            }).start();
                                        }

                                    }
                                });


                                //清空背景黑框
                                dialog.getWindow().getDecorView().setBackground(null);
                                dialog.setCancelable(false);
                                dialog.show();

                                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 20 * 19), Utils.dp2px(requireContext(), 600));

                            } else {

                                dialogV = LayoutInflater.from(getContext()).inflate(R.layout.dialog_collage_borrow, null, false);
                                final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogV).create();
                                TextView id_collage_b_method_xianzhi = dialogV.findViewById(R.id.id_collage_b_method_xianzhi);
                                TextView id_collage_b_method_xuqiu = dialogV.findViewById(R.id.id_collage_b_method_xuqiu);
                                TextView id_collage_b_method_jiaohuan = dialogV.findViewById(R.id.id_collage_b_method_jiaohuan);
                                TextView id_collage_b_method_title = dialogV.findViewById(R.id.id_collage_b_method_title);
                                id_collage_b_method_xianzhi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        method = Constant.COLUMN_C_B_METHOD_XIANZHI;
                                        id_collage_b_method_xianzhi.setBackgroundResource(R.drawable.bg_collage_status_green);
                                        id_collage_b_method_xianzhi.setTextColor(Color.rgb(76, 175, 80));

                                        id_collage_b_method_xuqiu.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_xuqiu.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_jiaohuan.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_jiaohuan.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_title.setText("分类(1/1)");
                                    }
                                });
                                id_collage_b_method_xuqiu.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        method = Constant.COLUMN_C_B_METHOD_XUQIU;
                                        id_collage_b_method_xuqiu.setBackgroundResource(R.drawable.bg_collage_status_red);
                                        id_collage_b_method_xuqiu.setTextColor(Color.rgb(255, 67, 54));

                                        id_collage_b_method_xianzhi.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_xianzhi.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_jiaohuan.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_jiaohuan.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_title.setText("分类(1/1)");
                                    }
                                });
                                id_collage_b_method_jiaohuan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        method = Constant.COLUMN_C_B_METHOD_JIAOHUAN;
                                        id_collage_b_method_jiaohuan.setBackgroundResource(R.drawable.bg_collage_status_blue);
                                        id_collage_b_method_jiaohuan.setTextColor(Color.rgb(69, 93, 238));
                                        id_collage_b_method_xianzhi.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_xianzhi.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_xuqiu.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                        id_collage_b_method_xuqiu.setTextColor(Color.rgb(141, 140, 133));
                                        id_collage_b_method_title.setText("分类(1/1)");
                                    }
                                });
                                TextView id_collage_b_close = dialogV.findViewById(R.id.id_collage_b_close);
                                id_collage_b_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                .setTitle("提示:")
                                                .setMessage("确认要关闭吗？")
                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialoga, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips.show();

                                    }
                                });

                                method = Constant.COLUMN_C_B_METHOD_XIANZHI;
                                id_collage_b_method_xianzhi.setBackgroundResource(R.drawable.bg_collage_status_green);
                                id_collage_b_method_xianzhi.setTextColor(Color.rgb(76, 175, 80));
                                id_collage_b_method_title.setText("分类(1/1)");

                                EditText id_collage_b_rname = dialogV.findViewById(R.id.id_collage_b_rname);
                                EditText id_collage_b_addr = dialogV.findViewById(R.id.id_collage_b_addr);
                                EditText id_collage_b_phone = dialogV.findViewById(R.id.id_collage_b_phone);
                                id_collage_b_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                                TextView id_collage_b_title_title = dialogV.findViewById(R.id.id_collage_b_title_title);
                                EditText id_collage_b_title = dialogV.findViewById(R.id.id_collage_b_title);
                                id_collage_b_title.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        id_collage_b_title_title.setText("标题(" + s.length() + "/100)");
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                id_collage_b_title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                        100)});


                                TextView id_collage_b_context_title = dialogV.findViewById(R.id.id_collage_b_context_title);
                                EditText id_collage_b_context = dialogV.findViewById(R.id.id_collage_b_context);
                                id_collage_b_context.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        id_collage_b_context_title.setText("内容(" + s.length() + "/350)");
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });
                                id_collage_b_context.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                        350)});
                                id_collage_b_title.setText("我有" + map.get(Constant.COLUMN_M_NAME) + "这种药，有需要的请联系我！");
                                id_collage_b_context.setText("这种药品我有闲置，请有需要的的联系我!\n\n药名:"
                                        + map.get(Constant.COLUMN_M_NAME)
                                        + "\n描述:" + map.get(Constant.COLUMN_M_DESCRIPTION)
                                        + "\n制药:" + map.get(Constant.COLUMN_M_COMPANY));
                                TextView id_collage_b_img_title = dialogV.findViewById(R.id.id_collage_b_img_title);
                                ImageButton id_collage_b_img_clear = dialogV.findViewById(R.id.id_collage_b_img_clear);
                                ImageButton id_collage_b_img_crop = dialogV.findViewById(R.id.id_collage_b_img_crop);
                                RoundImageView id_collage_b_img = dialogV.findViewById(R.id.id_collage_b_img);
                                id_collage_b_img_crop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (uri == null) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("图片为空，无法裁剪！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else {
                                            startImageCrop(uri);
                                        }
                                    }
                                });

                                id_collage_b_img_clear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        id_collage_b_img_title.setText("介绍图片(0/1)");
                                        id_collage_b_img.setImageResource(R.mipmap.add_imgdefault);
                                        uri = null;
                                        img = null;
                                    }
                                });
                                id_collage_b_img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("image/*");
                                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
                                    }
                                });
                                id_collage_b_img_title.setText("介绍图片(1/1)");
                                img = Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE));
                                if (img != null) {
                                    id_collage_b_img.setImageBitmap(img);
                                } else {
                                    id_collage_b_img.setImageResource(R.mipmap.add_imgdefault);
                                }
                                uri = null;

                                Button id_collage_b_write = dialogV.findViewById(R.id.id_collage_b_write);
                                id_collage_b_write.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (method == null) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("请选择帖子类型！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_title.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("标题不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_context.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("内容不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_phone.getText().length() == 0 ||
                                                id_collage_b_phone.getText().length() != 11) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("手机格式错误！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_addr.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("地址不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else if (id_collage_b_rname.getText().length() == 0) {
                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                    .setTitle("提示:")
                                                    .setMessage("名字不能为空！")
                                                    .setNegativeButton("返回", null)
                                                    .create();
                                            tips.show();
                                        } else {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Connection con = null;
                                                    PreparedStatement pps = null;
                                                    try {
                                                        con = JdbcUtil.getConnection();
                                                        pps = con.prepareStatement("INSERT INTO " + Constant.TABLE_COLLAGE_BORROW + " ("
                                                                + Constant.COLUMN_C_B_LNAME + ","
                                                                + Constant.COLUMN_C_B_TITLE + ","
                                                                + Constant.COLUMN_C_B_CONTEXT + ","
                                                                + Constant.COLUMN_C_B_TORNAME + ","
                                                                + Constant.COLUMN_C_B_TOPHONE + ","
                                                                + Constant.COLUMN_C_B_TOADDRESS + ","
                                                                + Constant.COLUMN_C_B_IMAGE + ","
                                                                + Constant.COLUMN_C_B_TIMESHOW + ","
                                                                + Constant.COLUMN_C_B_METHOD
                                                                + ")VALUES(?,?,?,?,?,?,?,?,?)");
                                                        pps.setString(1, lname);
                                                        pps.setString(2, id_collage_b_title.getText().toString().trim());
                                                        pps.setString(3, id_collage_b_context.getText().toString().trim());
                                                        pps.setString(4, id_collage_b_rname.getText().toString().trim());
                                                        pps.setString(5, id_collage_b_phone.getText().toString().trim());
                                                        pps.setString(6, id_collage_b_addr.getText().toString().trim());
//                                                        pps.setBytes(7, (img != null) ? Utils.getBytesFromBitmap(img) : null);
                                                        pps.setBytes(7, null);
                                                        pps.setString(8, String.valueOf(Utils.getTimeFromString(Utils.getTime())));
                                                        pps.setString(9, method);
                                                        int status = pps.executeUpdate();
                                                        if (status >= 1) {
                                                            Looper.prepare();
                                                            Log.i(Constant.PREFERENCES_NAME, "信息发布成功");
                                                            dialog.dismiss();
                                                            Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_SHORT).show();
                                                            Looper.loop();
                                                        } else {
                                                            Looper.prepare();
                                                            Log.i(Constant.PREFERENCES_NAME, "信息发布出错");
                                                            AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                                    .setTitle("提示:")
                                                                    .setMessage("发布失败！请重试")
                                                                    .setNegativeButton("返回", null)
                                                                    .create();
                                                            tips.show();
                                                            Looper.loop();
                                                        }
                                                    } catch (SQLException sqlException) {
                                                        sqlException.printStackTrace();
                                                        Looper.prepare();
                                                        Log.i(Constant.PREFERENCES_NAME, sqlException.toString());
                                                        AlertDialog tips = new AlertDialog.Builder(requireContext())
                                                                .setTitle("提示:")
                                                                .setMessage("网络连接失败！")
                                                                .setNegativeButton("返回", null)
                                                                .create();
                                                        tips.show();
                                                        Looper.loop();
                                                    } finally {
                                                        JdbcUtil.close(con, null, pps, null);
                                                    }
                                                }
                                            }).start();
                                        }

                                    }
                                });


                                //清空背景黑框
                                dialog.getWindow().getDecorView().setBackground(null);
                                dialog.setCancelable(false);
                                dialog.show();

                                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 20 * 19), Utils.dp2px(requireContext(), 600));

                            }
                        } else {
                            Toast.makeText(requireContext(), "请先登录后再使用此功能", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1:
                        // Edit
                        Utils.putString(requireContext(), Constant.EDITKEY, (String) map.get(Constant.COLUMN_M_KEYID));
                        Intent i = new Intent(getContext(), DetailActivity.class);
                        startActivity(i);
                        break;
                    case 2:
                        // delete
                        delMedDialog(medicines.get(position));
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        // Left


        return root;
    }

    /**
     * 提示删除
     *
     * @param map 单条药品数据
     */
    private void delMedDialog(Map<String, Object> map) {
        if (listView == null) {
            listView = (SwipeMenuListView) root.findViewById(R.id.homepagelist);
        }

        String keyid = (String) map.get(Constant.COLUMN_M_KEYID);
        AlertDialog tips = new AlertDialog.Builder(getContext())
                .setTitle("删除确认:")
                .setMessage("确认删除" + map.get(Constant.COLUMN_M_NAME) + "吗")
                .setPositiveButton("返回", null)
                .setNegativeButton("只删除本地", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.delMedicine(getContext(), keyid, true)) {
                            Toast.makeText(getContext(), "删除成功,你可以在回收站中找回它", Toast.LENGTH_SHORT).show();
                            medicines.remove(map);
                            loadRowCount--;
                            if (medicines.size() <= 0) {
                                group = Constant.COLUMN_G_All;
                                reMedicine();
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                            TextView h_title = root.findViewById(R.id.id_homepage_title);
                            if (!group.equals(Constant.COLUMN_G_All)) {
                                h_title.setText(group + " (" + loadRowCount + ")");
                            } else {
                                h_title.setText("全部药品(" + loadRowCount + ")");
                            }
                        } else {
                            Toast.makeText(getContext(), "删除失败！请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton("彻底删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog dialog1 = new AlertDialog.Builder(requireContext())
                                .setTitle("确认彻底删除？")
                                .setMessage("是否真正抹除" + map.get(Constant.COLUMN_M_NAME) + "的痕迹？\n【本地无法恢复】")
                                .setNegativeButton("确认删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                                        if (lname.equals("0")) {
                                            Dialog dialog2 = new AlertDialog.Builder(requireContext())
                                                    .setTitle("错误")
                                                    .setMessage("用户id不存在，请尝试重新登录账号？")
                                                    .setNegativeButton("好", null)
                                                    .create();
                                            dialog2.show();
                                        } else {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Connection connection = JdbcUtil.getConnection();
                                                    PreparedStatement pps = null;
                                                    try {
                                                        pps = connection.prepareStatement("DELETE FROM `" + lname + "` WHERE " + Constant.COLUMN_M_KEYID + " = ?");
                                                        pps.setString(1, keyid);
                                                        long status = pps.executeUpdate();
                                                        if (status >= 1) {
                                                            Looper.prepare();
                                                            if (Utils.delMedicine(requireContext(), keyid)) {
                                                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                                root.post(new Runnable() {
                                                                    @SuppressLint("SetTextI18n")
                                                                    @Override
                                                                    public void run() {
                                                                        medicines.remove(map);
                                                                        if (medicines.size() <= 0) {
                                                                            group = Constant.COLUMN_G_All;
                                                                            loadRowCount--;
                                                                            reMedicine();
                                                                        } else {
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                        TextView h_title = root.findViewById(R.id.id_homepage_title);
                                                                        if (!group.equals(Constant.COLUMN_G_All)) {
                                                                            h_title.setText(group + " (" + loadRowCount + ")");
                                                                        } else {
                                                                            h_title.setText("全部药品(" + loadRowCount + ")");
                                                                        }

                                                                    }
                                                                });

                                                            } else {
                                                                Toast.makeText(getContext(), "本地删除失败！请重试", Toast.LENGTH_SHORT).show();
                                                            }

                                                            Looper.loop();

                                                        }
                                                    } catch (SQLException sqlException) {
                                                        Looper.prepare();
                                                        Toast.makeText(requireContext(), "云端删除失败，请重试", Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                        sqlException.printStackTrace();
                                                    } finally {
                                                        JdbcUtil.close(connection, null, pps, null);
                                                    }

                                                }
                                            }).start();
                                        }
                                    }
                                })
                                .setNeutralButton("取消", null)
                                .create();
                        dialog1.show();


                    }
                })
                .create();
        tips.show();
    }

    @SuppressLint({"SetTextI18n", "Range", "Recycle"})
    private void initMedicine() {


        int web = 0;
        int local = 0;
        medicines.clear();
        gruopInfo.clear();
        loadPageShow = loadPageNow * loadPageSize;
        Spinner spinner_group = binding.idHomepageSpGroup;
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//		Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 AND " + Constant.COLUMN_M_UID + " = ?", new String[]{String.valueOf(Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0"))});
//		Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        Cursor cursor = null;


        cursor = dbHelper.getReadableDatabase().rawQuery("select count(*) from " + Constant.TABLE_NAME_MEDICINE + " where `" + Constant.COLUMN_M_FROMWEB + "` = 0 and " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        while (cursor.moveToNext()) {
            local = cursor.getInt(0);
        }
        cursor = dbHelper.getReadableDatabase().rawQuery("select count(*) from " + Constant.TABLE_NAME_MEDICINE + " where `" + Constant.COLUMN_M_FROMWEB + "` = 1 and " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        while (cursor.moveToNext()) {
            web = cursor.getInt(0);
        }
        cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_FROMWEB + " = 0 and " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        long now = Utils.getDateFromString(Utils.getDate());
        int date_out = 0, date_near = 0, date_ok = 0;
        while (cursor.moveToNext()) {
            long out = cursor.getLong(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OUTDATE));
            if (out - now <= 0) {
                date_out++;
            } else if ((out - now) / 1000 <= 5356800 && out - now > 0) {
                //5356800 是 62天的时间 3600*24*62
                date_near++;
            } else {
                date_ok++;
            }
        }
        Utils.putInt(requireContext(), Constant.MG_DATE_OUT, date_out);
        Utils.putInt(requireContext(), Constant.MG_DATE_NEAR, date_near);
        Utils.putInt(requireContext(), Constant.MG_DATE_OK, date_ok);


        String sql_group = group.equals(Constant.COLUMN_G_All) ? " " : " and `" + Constant.COLUMN_M_GROUP + "`= '" + group + "' ";

        cursor = db.rawQuery("select count(*) from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " ", null);
        while (cursor.moveToNext()) {
            loadRowCount = cursor.getInt(0);
            // 计算总页码数
            loadPageCount = (int) Math.ceil(loadRowCount / (float) loadPageSize);
        }

        switch (showMethod) {
            case 1:
                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " ORDER BY `" + Constant.COLUMN_M_OUTDATE + "` DESC limit ? ", new String[]{String.valueOf(loadPageShow)});
                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                break;
            case 2:
            case 3:
                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " limit ? ", new String[]{String.valueOf(loadPageShow)});
                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                break;
            case 0:
            default:
                cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 " + sql_group + " ORDER BY `" + Constant.COLUMN_M_OUTDATE + "` limit ? ", new String[]{String.valueOf(loadPageShow)});
                Utils.putInt(requireContext(), Constant.SHOWMETHODKEY, showMethod);
                break;
        }

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


        cursor = db.rawQuery("select distinct `" + Constant.COLUMN_M_GROUP + "` from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);

        while (cursor.moveToNext()) {
            gruopInfo.add(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_GROUP)));
        }


        cursor.close();
        db.close();


        Utils.putInt(requireContext(), Constant.MG_M_COUNT_TOTAL, loadRowCount);
        Utils.putInt(requireContext(), Constant.MG_M_COUNT_LOCAL, local);
        Utils.putInt(requireContext(), Constant.MG_M_COUNT_WEB, web);
        if (showMethod == 3) {
            Collections.reverse(medicines);
        }


        TextView h_title = root.findViewById(R.id.id_homepage_title);
        if (!group.equals(Constant.COLUMN_G_All)) {
//            if (medicines.size() <= 0) {
//                group = Constant.COLUMN_G_All;
//                initMedicine();
//                return;
//            } else {
//                h_title.setText(group + " (" + medicines.size() + ")");
//            }
            h_title.setText(group + " (" + loadRowCount + ")");
        } else {
            h_title.setText("全部药品(" + loadRowCount + ")");
        }


        if (swipeRefreshLayout == null) {
            swipeRefreshLayout = root.findViewById(R.id.id_homepage_SwipeRefreshLayout);//初始化下拉刷新控件，
        }

        if (gruopInfo == null || gruopInfo.size() == 0) {
            gruopInfo = new ArrayList<>();
            gruopInfo.add(Constant.COLUMN_G_DEFAULT);
        }
        Utils.putSet(requireContext(), Constant.GROUPLISTKEY, new HashSet<>(gruopInfo));

        gruopInfo.add(Constant.COLUMN_G_All);


        String[] lsit_group = new String[gruopInfo.size()];
        gruopInfo.toArray(lsit_group);
        ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(getContext(), R.layout.spanner_style_s, lsit_group);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        spinner_group.setAdapter(adapter_group);//绑定Adapter到控件
        int index_group = Utils.findIndexInList(gruopInfo, group);

        spinner_group.setSelection(index_group == -1 ? Utils.findIndexInList(gruopInfo, Constant.COLUMN_G_All) : index_group);

        spinner_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (lsit_group[position].equals(Constant.COLUMN_G_ADD)) {
                    spinner_group.setSelection(Utils.findIndexInList(gruopInfo, group));
                } else {
                    Utils.putString(requireContext(), Constant.GROUPKEY, group);
                    group = lsit_group[position];
                    spinner_group.setSelection(position);
                    loadPageNow = 1;
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.autoRefresh();
//                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    Toast.makeText(requireContext(), "已调整为" + group, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Spinner spinner_choose = binding.idHomepageSpChoose;

        String[] list_choose = getResources().getStringArray(R.array.show_method);//建立数据源
        ArrayAdapter<String> adapter_choose = new ArrayAdapter<String>(getContext(), R.layout.spanner_style_s, list_choose);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        spinner_choose.setAdapter(adapter_choose);//绑定Adapter到控件
        spinner_choose.setSelection(showMethod);
        spinner_choose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showMethod = position;
                loadPageNow = 1;
                Toast.makeText(requireContext(), "已将顺序更改为:" + getResources().getStringArray(R.array.show_method)[position], Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.autoRefresh();
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    SwipeMenuListView listView = null;
    MedicineCardAdapter adapter = null;

    public void reMedicine() {
        if (listView == null) {
            listView = (SwipeMenuListView) root.findViewById(R.id.homepagelist);
        }
        initMedicine();
        if (adapter == null) {
            // 如果当前页为第一页，则数据源集合中就是第一页的内容
            adapter = new MedicineCardAdapter(getContext(), R.layout.list_medicine, medicines, showshow);
            listView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();


        synchronized (this) {
            AppWidgetManager manager = AppWidgetManager.getInstance(requireContext());
            ComponentName provider = new ComponentName(requireActivity(), MyWidget.class);
            RemoteViews views = new RemoteViews(requireContext().getPackageName(), R.layout.widght_medicine);
            int[] res = Utils.getMedicinesCount(requireContext());
            views.setTextViewText(R.id.widght_all, String.valueOf(res[0]));
            views.setTextViewText(R.id.widght_green, String.valueOf(res[1]));
            views.setTextViewText(R.id.widght_orange, String.valueOf(res[2]));
            views.setTextViewText(R.id.widght_red, String.valueOf(res[3]));
            System.out.println(Arrays.toString(res));
            Intent numberIntent = new Intent("ACTION_MAKE_NUMBER");
            views.setOnClickPendingIntent(R.id.widght_fresh, PendingIntent.getBroadcast(requireContext(), 0, numberIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            manager.updateAppWidget(provider, views);
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (null != db) {
            db.close();
        }
    }

    AutoSwipeRefreshLayout swipeRefreshLayout = null;

    @Override
    public void onResume() {
        super.onResume();
        showshow = Utils.getBoolean(requireContext(), Constant.SHOWSHOW, false);
        reMedicine();
//        if (swipeRefreshLayout == null) {
//            swipeRefreshLayout = root.findViewById(R.id.id_homepage_SwipeRefreshLayout);//初始化下拉刷新控件，
//        }
//        swipeRefreshLayout.autoRefresh();
//        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * 仅把自己的上传到云端
     */
    public void upLoadMedicines() {
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (medicines.size() == 0) {
            return;
        }
        if (lname.equals("0")) {
            Dialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("错误")
                    .setMessage("用户id不存在，请尝试重新登录账号？")
                    .setNegativeButton("好", null)
                    .create();
            dialog.show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在上传中(可置于后台)...");
        progressDialog.setTitle("请稍后");
        progressDialog.setMax(medicines.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
                Connection connection = JdbcUtil.getConnection();
                PreparedStatement pps = null;
                try {
                    connection.setAutoCommit(false);
                    int successCount = 0;
                    for (Map<String, Object> map : medicines) {
                        pps = connection.prepareStatement("REPLACE `" + lname + "` VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                        pps.setString(1, (String) map.get(Constant.COLUMN_M_KEYID));
                        pps.setString(2, lname);
                        pps.setString(3, (String) map.get(Constant.COLUMN_M_NAME));
                        pps.setBytes(4, (byte[]) map.get(Constant.COLUMN_M_IMAGE));
                        pps.setString(5, (String) map.get(Constant.COLUMN_M_DESCRIPTION));
                        pps.setLong(6, Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString()));
                        pps.setString(7, (String) map.get(Constant.COLUMN_M_OTC));
                        pps.setString(8, (String) map.get(Constant.COLUMN_M_BARCODE));
                        pps.setString(9, (String) map.get(Constant.COLUMN_M_YU));
                        pps.setString(10, (String) map.get(Constant.COLUMN_M_ELABEL));
                        pps.setString(11, (String) map.get(Constant.COLUMN_M_LOVE));
                        pps.setString(12, (String) map.get(Constant.COLUMN_M_SHARE));
                        pps.setString(13, (String) map.get(Constant.COLUMN_M_MUSE));
                        pps.setString(14, (String) map.get(Constant.COLUMN_M_COMPANY));
                        pps.setString(15, (String) map.get(Constant.COLUMN_M_DELFLAG));
                        pps.setString(16, (String) map.get(Constant.COLUMN_M_SHOWFLAG));
                        pps.setString(17, (String) map.get(Constant.COLUMN_M_FROMWEB));
                        pps.setString(18, (String) map.get(Constant.COLUMN_M_GROUP));
                        long status = pps.executeUpdate();
                        if (status >= 1) {
                            successCount++;
                            progressDialog.setProgress(progress += 1);

                        }
                    }
                    connection.commit();
                    progressDialog.dismiss();
                    if (successCount == medicines.size()) {
                        Looper.prepare();
                        Toast.makeText(requireContext(), "云端更新完成", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                .setTitle("全部完成")
                                .setMessage("药品全部上传成功！")
                                .setNegativeButton("好耶", null)
                                .create();
                        dialog.show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                .setTitle("部分完成(" + successCount + "/" + medicines.size() + ")")
                                .setMessage("出现了一点小错误！重试一下？")
                                .setNegativeButton("好", null)
                                .create();
                        dialog.show();
                        Looper.loop();
                    }
                } catch (SQLException sqlException) {
                    try {
                        connection.rollback();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    sqlException.printStackTrace();
                    Looper.prepare();
                    Dialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("出错")
                            .setMessage("药品上传失败，请重试！")
                            .setNegativeButton("好", null)
                            .create();
                    dialog.show();
                    Looper.loop();
                } finally {
                    JdbcUtil.close(connection, null, pps, null);
                }
            }
        }).start();
    }

    /**
     * 根据keyid选择性上传到云端
     */
    public void upLoadMedicines(ArrayList<Map<String, Object>> mediciness) {
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (mediciness.size() == 0) {
            return;
        }
        if (lname.equals("0")) {
            Dialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("错误")
                    .setMessage("用户id不存在，请尝试重新登录账号？")
                    .setNegativeButton("好", null)
                    .create();
            dialog.show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在上传中(可置于后台)...");
        progressDialog.setTitle("请稍后");
        progressDialog.setMax(mediciness.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
                Connection connection = JdbcUtil.getConnection();
                PreparedStatement pps = null;
                try {
                    connection.setAutoCommit(false);
                    int successCount = 0;
                    for (Map<String, Object> map : mediciness) {
                        pps = connection.prepareStatement("REPLACE `" + lname + "` VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                        pps.setString(1, (String) map.get(Constant.COLUMN_M_KEYID));
                        pps.setString(2, lname);
                        pps.setString(3, (String) map.get(Constant.COLUMN_M_NAME));
                        pps.setBytes(4, (byte[]) map.get(Constant.COLUMN_M_IMAGE));
                        pps.setString(5, (String) map.get(Constant.COLUMN_M_DESCRIPTION));
                        pps.setLong(6, Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString()));
                        pps.setString(7, (String) map.get(Constant.COLUMN_M_OTC));
                        pps.setString(8, (String) map.get(Constant.COLUMN_M_BARCODE));
                        pps.setString(9, (String) map.get(Constant.COLUMN_M_YU));
                        pps.setString(10, (String) map.get(Constant.COLUMN_M_ELABEL));
                        pps.setString(11, (String) map.get(Constant.COLUMN_M_LOVE));
                        pps.setString(12, (String) map.get(Constant.COLUMN_M_SHARE));
                        pps.setString(13, (String) map.get(Constant.COLUMN_M_MUSE));
                        pps.setString(14, (String) map.get(Constant.COLUMN_M_COMPANY));
                        pps.setString(15, (String) map.get(Constant.COLUMN_M_DELFLAG));
                        pps.setString(16, (String) map.get(Constant.COLUMN_M_SHOWFLAG));
                        pps.setString(17, (String) map.get(Constant.COLUMN_M_FROMWEB));
                        pps.setString(18, (String) map.get(Constant.COLUMN_M_GROUP));
                        long status = pps.executeUpdate();
                        if (status >= 1) {
                            successCount++;
                            progressDialog.setProgress(progress += 1);
                        }
                        try {
                            Thread.sleep(200);
                        } catch (Exception ex) {
                            Log.e(Constant.PREFERENCES_NAME, ex.toString());
                        }

                    }
                    connection.commit();
                    progressDialog.dismiss();
                    if (successCount == mediciness.size()) {
                        Looper.prepare();
                        Toast.makeText(requireContext(), "云端更新完成", Toast.LENGTH_SHORT).show();
                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                .setTitle("全部完成")
                                .setMessage("所选药品全部上传成功！")
                                .setNegativeButton("好耶", null)
                                .create();
                        dialog.show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Dialog dialog = new AlertDialog.Builder(requireContext())
                                .setTitle("部分完成(" + successCount + "/" + mediciness.size() + ")")
                                .setMessage("出现了一点小错误！重试一下？")
                                .setNegativeButton("好", null)
                                .create();
                        dialog.show();
                        Looper.loop();
                    }
                } catch (SQLException sqlException) {
                    try {
                        connection.rollback();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    sqlException.printStackTrace();
                    Looper.prepare();
                    Dialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("出错")
                            .setMessage("药品上传失败，请重试！")
                            .setNegativeButton("好", null)
                            .create();
                    dialog.show();
                    Looper.loop();
                } finally {
                    progressDialog.dismiss();

                    JdbcUtil.close(connection, null, pps, null);
                }
            }
        }).start();
    }

    /**
     * 全部下载到本地
     */
    public void downLoadMedicines() {
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (lname.equals("0")) {
            Dialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("错误")
                    .setMessage("用户id不存在，请尝试重新登录账号？")
                    .setNegativeButton("好", null)
                    .create();
            dialog.show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载中(可置于后台)...");
        progressDialog.setTitle("请稍后（可能会等待较长时间）");
        progressDialog.setMax(medicines.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
                Connection con = JdbcUtil.getConnection();
                PreparedStatement pps = null;
                ResultSet resultSet = null;
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                try {
                    pps = con.prepareStatement("SELECT * FROM `" + lname + "` WHERE " + Constant.COLUMN_M_DELFLAG + "=0;");
                    resultSet = pps.executeQuery();
                    while (resultSet.next()) {
                        ContentValues values = new ContentValues();
                        values.put(Constant.COLUMN_M_KEYID, resultSet.getString(Constant.COLUMN_M_KEYID));
                        values.put(Constant.COLUMN_M_NAME, resultSet.getString(Constant.COLUMN_M_NAME));
                        values.put(Constant.COLUMN_M_IMAGE, resultSet.getBytes(Constant.COLUMN_M_IMAGE));
                        values.put(Constant.COLUMN_M_DESCRIPTION, resultSet.getString(Constant.COLUMN_M_DESCRIPTION));
                        values.put(Constant.COLUMN_M_OUTDATE, resultSet.getLong(Constant.COLUMN_M_OUTDATE));
                        values.put(Constant.COLUMN_M_OTC, resultSet.getString(Constant.COLUMN_M_OTC));
                        values.put(Constant.COLUMN_M_BARCODE, resultSet.getString(Constant.COLUMN_M_BARCODE));
                        values.put(Constant.COLUMN_M_YU, resultSet.getString(Constant.COLUMN_M_YU));
                        values.put(Constant.COLUMN_M_ELABEL, resultSet.getString(Constant.COLUMN_M_ELABEL));
                        values.put(Constant.COLUMN_M_LOVE, resultSet.getInt(Constant.COLUMN_M_LOVE));
                        values.put(Constant.COLUMN_M_SHARE, resultSet.getString(Constant.COLUMN_M_SHARE));
                        values.put(Constant.COLUMN_M_MUSE, resultSet.getString(Constant.COLUMN_M_MUSE));
                        values.put(Constant.COLUMN_M_COMPANY, resultSet.getString(Constant.COLUMN_M_COMPANY));
                        values.put(Constant.COLUMN_M_DELFLAG, 0);
                        values.put(Constant.COLUMN_M_SHOWFLAG, resultSet.getInt(Constant.COLUMN_M_SHOWFLAG));
                        values.put(Constant.COLUMN_M_FROMWEB, resultSet.getInt(Constant.COLUMN_M_FROMWEB));
                        values.put(Constant.COLUMN_M_UID, resultSet.getString(Constant.COLUMN_M_UID));
                        values.put(Constant.COLUMN_M_GROUP, resultSet.getString(Constant.COLUMN_M_GROUP));
                        long status = db.replace(Constant.TABLE_NAME_MEDICINE, null, values);
                        Log.i(Constant.PREFERENCES_NAME, "下载进度" + status);
                        progressDialog.setProgress(progress += 1);
                        try {
                            Thread.sleep(200);
                        } catch (Exception ex) {
                            Log.e(Constant.PREFERENCES_NAME, ex.toString());
                        }
                    }

                } catch (SQLException sqlException) {
                    Looper.prepare();
                    Toast.makeText(requireContext(), "下载出错，请重新试试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    sqlException.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                    db.close();
                    if (swipeRefreshLayout == null) {
                        swipeRefreshLayout = root.findViewById(R.id.id_homepage_SwipeRefreshLayout);//初始化下拉刷新控件，
                    }
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            group = Constant.COLUMN_G_All;
                            swipeRefreshLayout.autoRefresh();
//                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                    JdbcUtil.close(con, null, pps, resultSet);
                }

            }

        }).start();

    }

    /**
     * 根据内容选择性下载到本地
     */
    public void downLoadMedicines(ArrayList<Map<String, Object>> medicines_forDownLoad) {
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (lname.equals("0")) {
            Dialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("错误")
                    .setMessage("用户id不存在，请尝试重新登录账号？")
                    .setNegativeButton("好", null)
                    .create();
            dialog.show();
            return;
        }
        if (medicines_forDownLoad.size() == 0) {
            Toast.makeText(requireContext(), "数据为空，下载取消", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Map<String, Object> map : medicines_forDownLoad) {
                    ContentValues values = new ContentValues();
                    values.put(Constant.COLUMN_M_KEYID, (String) map.get(Constant.COLUMN_M_KEYID));
                    values.put(Constant.COLUMN_M_NAME, (String) map.get(Constant.COLUMN_M_NAME));
                    values.put(Constant.COLUMN_M_IMAGE, (byte[]) map.get(Constant.COLUMN_M_IMAGE));
                    values.put(Constant.COLUMN_M_DESCRIPTION, (String) map.get(Constant.COLUMN_M_DESCRIPTION));
                    values.put(Constant.COLUMN_M_OUTDATE, Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString()));
                    values.put(Constant.COLUMN_M_OTC, (String) map.get(Constant.COLUMN_M_OTC));
                    values.put(Constant.COLUMN_M_BARCODE, (String) map.get(Constant.COLUMN_M_BARCODE));
                    values.put(Constant.COLUMN_M_YU, (String) map.get(Constant.COLUMN_M_YU));
                    values.put(Constant.COLUMN_M_ELABEL, (String) map.get(Constant.COLUMN_M_ELABEL));
                    values.put(Constant.COLUMN_M_LOVE, Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_LOVE)).toString()));
                    values.put(Constant.COLUMN_M_SHARE, (String) map.get(Constant.COLUMN_M_SHARE));
                    values.put(Constant.COLUMN_M_MUSE, (String) map.get(Constant.COLUMN_M_MUSE));
                    values.put(Constant.COLUMN_M_COMPANY, (String) map.get(Constant.COLUMN_M_COMPANY));
                    values.put(Constant.COLUMN_M_DELFLAG, 0);
                    values.put(Constant.COLUMN_M_SHOWFLAG, Integer.parseInt(map.get(Constant.COLUMN_M_SHOWFLAG).toString()));
                    values.put(Constant.COLUMN_M_FROMWEB, Integer.parseInt(map.get(Constant.COLUMN_M_FROMWEB).toString()));
                    values.put(Constant.COLUMN_M_UID, Integer.parseInt(map.get(Constant.COLUMN_M_UID).toString()));
                    values.put(Constant.COLUMN_M_GROUP, (String) map.get(Constant.COLUMN_M_GROUP));
                    long status = db.replace(Constant.TABLE_NAME_MEDICINE, null, values);
                    Log.i(Constant.PREFERENCES_NAME, "下载进度" + status);
                }
                db.close();
                Looper.prepare();
                Dialog dialoga = new AlertDialog.Builder(requireContext())
                        .setTitle("提示：")
                        .setCancelable(false)
                        .setMessage("下载完成！")
                        .setNegativeButton("了解", null)
                        .create();
                dialoga.show();
                Looper.loop();
                if (swipeRefreshLayout == null) {
                    swipeRefreshLayout = root.findViewById(R.id.id_homepage_SwipeRefreshLayout);//初始化下拉刷新控件，
                }
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.autoRefresh();
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });


            }

        }).start();

    }


    private void startImageCrop(Uri uri) {

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setMaxBitmapSize(80);
        options.setMaxScaleMultiplier(6);
        Uri mDestinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "MgCropImage.jpeg"));
        UCrop.of(uri, mDestinationUri)
                .withOptions(options)
                .useSourceImageAspectRatio()
                .withAspectRatio(16, 9)
                .start(requireContext(), this, REQUEST_CODE_CROP);
    }


}


