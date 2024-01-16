package com.daqin.medicinegod.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daqin.medicinegod.Adspter.CollageBorrowAdapter;
import com.daqin.medicinegod.CollageBorrowDetailActivity;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.CustomWidget.DragFloatingActionButton;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.databinding.FragmentCollageBorrowBinding;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollageFragment extends Fragment {

    private FragmentCollageBorrowBinding binding;
    Bitmap img = null;
    View root;
    AutoSwipeRefreshLayout swipeRefreshLayout;
    final int REQUEST_CODE_CHOOSE_IMG = 100;
    final int REQUEST_CODE_CROP = 102;
    View dialogV = null;
    Uri uri = null;
    String lname = "0";
    static String method = null;
    static List<Map<String, Object>> collageBorrow = new ArrayList<>();
    CollageBorrowAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollageBorrowBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("err", e.toString());
            }
        });

        swipeRefreshLayout = binding.idCollageBAutowwiperefreshLayout;//初始化下拉刷新控件，
        swipeRefreshLayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
        swipeRefreshLayout.setOnRefreshListener(new AutoSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (lname.equals("0")) {
            Toast.makeText(requireContext(), "未登录无法使用该功能！", Toast.LENGTH_SHORT).show();
        } else {
            swipeRefreshLayout.autoRefresh();
        }


        swipeRefreshLayout = binding.idCollageBAutowwiperefreshLayout;//初始化下拉刷新控件，
        swipeRefreshLayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
        swipeRefreshLayout.setOnRefreshListener(new AutoSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initCollageBorrow();
//				Toast.makeText(getActivity(), "已更新药品数据", Toast.LENGTH_SHORT).show();//刷新时要做的事


            }
        });
        Spinner spinner = binding.idCollageBChoose;
        String[] choose = getResources().getStringArray(R.array.borrow_method);//建立数据源
//		ArrayAdapter<String> adapter_web_choose = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, choose);
        ArrayAdapter<String> adapter_web_choose = new ArrayAdapter<String>(getContext(), R.layout.spanner_style, choose);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        spinner.setAdapter(adapter_web_choose);//绑定Adapter到控件
        spinner.setSelection(3);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DragFloatingActionButton id_collage_b_write = binding.idCollageBWrite;
        id_collage_b_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                if (lname.equals("0")) {
                    AlertDialog tips = new AlertDialog.Builder(requireContext())
                            .setTitle("提示:")
                            .setMessage("暂未登录，请先登录")
                            .setNegativeButton("确认", null)
                            .create();
                    tips.show();
                    return;
                }

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

                Button id_collage_b_write = dialogV.findViewById(R.id.id_collage_b_write);
                id_collage_b_write.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                        pps.setString(1, lname);
                                        pps.setString(2, id_collage_b_title.getText().toString().trim());
                                        pps.setString(3, id_collage_b_context.getText().toString().trim());
                                        pps.setString(4, id_collage_b_rname.getText().toString().trim());
                                        pps.setString(5, id_collage_b_phone.getText().toString().trim());
                                        pps.setString(6, id_collage_b_addr.getText().toString().trim());
                                        //TODO:图片处理
                                        pps.setBytes(7, null);
//                                            pps.setBytes(7, (img != null) ? Utils.getBytesFromBitmap(img) : null);
                                        pps.setString(8, String.valueOf(Utils.getTimeFromString(Utils.getTime())));
                                        pps.setString(9, method);
                                        int status = pps.executeUpdate();
                                        if (status >= 1) {
                                            Looper.prepare();
                                            Log.i(Constant.PREFERENCES_NAME, "信息发布成功");
                                            dialog.dismiss();
                                            swipeRefreshLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    swipeRefreshLayout.autoRefresh();
                                                }
                                            });

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
                                        flag = true;
                                        JdbcUtil.close(con, null, pps, null);
                                        Looper.prepare();
                                        swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.autoRefresh();
                                            }
                                        });
                                        Looper.loop();
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
        });

        swipeRefreshLayout.autoRefresh();


        return root;
    }

    private static boolean flag = true;

    private void initCollageBorrow() {

        System.out.println(flag);
        synchronized (this) {
            if (flag) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Connection con = null;
                        PreparedStatement pps = null;
                        ResultSet resultSet = null;
                        collageBorrow.clear();
                        try {//TODO:修复点赞
						/*
						SELECT a.*,b.HEAD,b.SNAME,b.MYSTYLE,c.LNAME as ZANFROM,COUNT(c.LNAME) as ZANCOUNT FROM taishankejixueyuan_school_borrow as a
                        left JOIN USERINFO as b ON a.LNAME = b.LNAME
						left JOIN taishankejixueyuan_school_zan as c ON c.BID = a.BID
						GROUP BY a.BID,c.LNAME
						ORDER BY a.TIME_SHOW DESC
						 */
                            con = JdbcUtil.getConnection();
                            pps = con.prepareStatement("SELECT a.*,b.HEAD,b.SNAME,b.MYSTYLE FROM "
                                    + Constant.TABLE_COLLAGE_BORROW
                                    + " as a INNER JOIN USERINFO as b ON a.LNAME = b.LNAME "
                                    + "");
                            resultSet = pps.executeQuery();
                            flag = false;

                            while (resultSet.next()) {
                                Map<String, Object> map = new HashMap<>();
                                String bid = String.valueOf(resultSet.getInt(Constant.COLUMN_C_B_BID));
                                map.put(Constant.COLUMN_C_B_BID, bid);
                                map.put(Constant.COLUMN_C_B_LNAME, resultSet.getString(Constant.COLUMN_C_B_LNAME));
                                map.put(Constant.COLUMN_C_B_TITLE, resultSet.getString(Constant.COLUMN_C_B_TITLE));
                                map.put(Constant.COLUMN_C_B_CONTEXT, resultSet.getString(Constant.COLUMN_C_B_CONTEXT));
                                map.put(Constant.COLUMN_C_B_TORNAME, resultSet.getString(Constant.COLUMN_C_B_TORNAME));
                                map.put(Constant.COLUMN_C_B_TOPHONE, resultSet.getString(Constant.COLUMN_C_B_TOPHONE));
                                map.put(Constant.COLUMN_C_B_TOADDRESS, resultSet.getString(Constant.COLUMN_C_B_TOADDRESS));
                                map.put(Constant.COLUMN_C_B_IMAGE, resultSet.getBytes(Constant.COLUMN_C_B_IMAGE));
                                map.put(Constant.COLUMN_C_B_TIMESHOW, resultSet.getString(Constant.COLUMN_C_B_TIMESHOW));
                                map.put(Constant.COLUMN_C_B_METHOD, resultSet.getString(Constant.COLUMN_C_B_METHOD));
                                map.put(Constant.COLUMN_U_SNAME, resultSet.getString(Constant.COLUMN_U_SNAME));
                                map.put(Constant.COLUMN_U_HEAD, resultSet.getBytes(Constant.COLUMN_U_HEAD));
                                map.put(Constant.COLUMN_U_MYSTYLE, resultSet.getString(Constant.COLUMN_U_MYSTYLE));
                                collageBorrow.add(map);
                            }

                            if (collageBorrow.size() == 0) {
                                Looper.prepare();
                                Toast.makeText(requireContext(), "暂无数据，试试发一点信息吧", Toast.LENGTH_SHORT).show();
                                flag = true;
                                Looper.loop();
                            } else {
                                Collections.reverse(collageBorrow);
                                Looper.prepare();
                                synchronized (this) {
                                    adapter =
                                            new CollageBorrowAdapter(requireContext(),
                                                    R.layout.list_collage_borrow,
                                                    collageBorrow,
                                                    lname);
                                }
                                ListView listView = root.findViewById(R.id.id_collage_b_list);
                                listView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        synchronized (this) {
                                            listView.setAdapter(adapter);
                                            adapter.setOnItemDeleteClickListener(new CollageBorrowAdapter.onItemDeleteListener() {
                                                @Override
                                                public void onDeleteClick(String bid, int i) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Connection con = null;
                                                            PreparedStatement pps = null;
                                                            try {
                                                                con = JdbcUtil.getConnection();
                                                                pps = con.prepareStatement("delete from " + Constant.TABLE_COLLAGE_BORROW
                                                                        + " where " + Constant.COLUMN_C_B_BID + "= ?");
                                                                pps.setString(1, bid);
                                                                int status = pps.executeUpdate();
                                                                if (status >= 1) {
                                                                    Looper.prepare();
                                                                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                                    collageBorrow.remove(i);
                                                                    flag = true;
                                                                    listView.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    });
                                                                    swipeRefreshLayout.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            swipeRefreshLayout.autoRefresh();
                                                                        }
                                                                    });
                                                                    Looper.loop();
                                                                } else {
                                                                    Looper.prepare();
                                                                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                                                    Looper.loop();
                                                                }


                                                            } catch (
                                                                    SQLException sqlException) {
                                                                sqlException.printStackTrace();
                                                                Log.i(Constant.PREFERENCES_NAME, "帖子删除失败");
                                                            } finally {
                                                                JdbcUtil.close(con, null, pps, null);
                                                            }
                                                        }
                                                    }).start();
                                                }
                                            });

                                            adapter.setOnItemZanClickListener(new CollageBorrowAdapter.onItemZanListener() {
                                                @Override
                                                public void onZanClick(int i, String bid, View vvv, View countView) {


                                                }
                                            });

                                        }
                                        flag = true;
                                    }
                                });
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Utils.putString(requireContext(), Constant.CBBIDKEY, (String) collageBorrow.get(position).get(Constant.COLUMN_C_B_BID));
                                        Intent tt = new Intent(getContext(), CollageBorrowDetailActivity.class);
                                        startActivity(tt);
                                    }
                                });


                            }

                        } catch (Exception sqlException) {
                            Looper.loop();
                            Looper.prepare();
                            sqlException.printStackTrace();

                            flag = true;
                            Log.i(Constant.PREFERENCES_NAME, sqlException.toString());
                            Toast.makeText(getContext(), "发生错误，请重试", Toast.LENGTH_SHORT).show();
                            Looper.loop();

                        } finally {
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Log.i(Constant.PREFERENCES_NAME, "更新了校园借用数据");
                                }
                            });
                            flag = true;
                            JdbcUtil.close(con, null, pps, resultSet);


                        }
                    }
                }).start();
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
//		swipeRefreshLayout.post(new Runnable() {
//			@Override
//			public void run() {
//				swipeRefreshLayout.autoRefresh();
//			}
//		});

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);   //this


        if (requestCode == REQUEST_CODE_CHOOSE_IMG) {//判断是不是我们选择图片按钮的回调
            if (resultCode == Activity.RESULT_OK && null != data) {
                try {
                    uri = data.getData();
                    ContentResolver cr = requireActivity().getContentResolver();
//                System.out.println(Utils.getBase64(cr,uri));
                    img = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    TextView id_collage_b_img_title = dialogV.findViewById(R.id.id_collage_b_img_title);
                    RoundImageView b_chooseImg = dialogV.findViewById(R.id.id_collage_b_img);
                    id_collage_b_img_title.setText("介绍图片(1/1)");
                    b_chooseImg.setImageBitmap(img);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == REQUEST_CODE_CROP) {

            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                try {
                    img = BitmapFactory.decodeStream(requireActivity().getContentResolver().openInputStream(resultUri));
                    RoundImageView b_chooseImg = dialogV.findViewById(R.id.id_collage_b_img);
                    b_chooseImg.setImageBitmap(img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }

        }
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