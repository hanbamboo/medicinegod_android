package com.daqin.medicinegod.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daqin.medicinegod.AddActivity;
import com.daqin.medicinegod.Adspter.VersionCardAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.LoginActivity;
import com.daqin.medicinegod.NoticeActivity;
import com.daqin.medicinegod.PersonCenterActivity;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.FragmentMeBinding;
import com.daqin.medicinegod.entity.Version;
import com.mittsu.markedview.MarkedView;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    View root;
    Map<String, Object> userInfo = new HashMap<>();

    AutoSwipeRefreshLayout swipeRefreshLayout;

    List<Version> versions = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //防止闪退
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("err", e.toString());
            }
        });
        TextView id_me_version = binding.idMeVersion;
        id_me_version.setText("版本: V" + Constant.VERSION + "Beta");
        TextView id_me_name = binding.idMeName;
        TextView id_me_point = binding.idMePoint;
        TextView id_me_saveyu = binding.idMeSaveyu;
        RoundImageView id_me_head = binding.idMeHead;
        LinearLayout layout = binding.idMeLoginHead;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
                if (isLogin == 0) {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivity(i);
                }

            }
        });
        ScrollView id_me_scrollview = binding.idMeScrollview;
        TextView id_me_web = binding.idMeFromweb;
        TextView id_me_total = binding.idMeFromtotal;
        TextView id_me_local = binding.idMeFromlocal;
        TextView id_me_ok = binding.idMeOk;
        TextView id_me_near = binding.idMeNear;
        TextView id_me_out = binding.idMeOut;
        Button id_me_personcenter = binding.idMePersoncenter;
        Button id_me_kefu = binding.idMeKefu;
        Button id_me_verison = binding.idMeVerison;
        Button id_me_about = binding.idMeAbout;
        Button id_me_setting = binding.idMeSetting;
        TextView id_me_privacy = binding.idMePrivacy;
        id_me_setting.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_setting, null, false);
                final AlertDialog dialog = new AlertDialog
                        .Builder(requireContext())
                        .setView(v)
                        .create();
                //清空背景黑框
                dialog.getWindow().getDecorView().setBackground(null);
                dialog.show();
                TextView id_about_close =
                        v.findViewById(R.id.id_setting_close);
                id_about_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Switch id_dialog_setting_idkey = v.findViewById(R.id.id_dialog_setting_idkey);
                id_dialog_setting_idkey.setChecked(Utils.getBoolean(requireContext(), Constant.SHOWSHOW, false));
                id_dialog_setting_idkey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Utils.putBoolean(requireContext(), Constant.SHOWSHOW, b);
                    }
                });
                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));
            }
        });
        id_me_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_privacy, null, false);
                final AlertDialog dialog = new AlertDialog
                        .Builder(requireContext())
                        .setView(v)
                        .create();
                //清空背景黑框
                dialog.getWindow().getDecorView().setBackground(null);
                dialog.show();
                TextView id_about_close =
                        v.findViewById(R.id.id_privacy_close);
                id_about_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                MarkedView id_privacy_web =
                        v.findViewById(R.id.id_privacy_web);
                id_privacy_web.setMDText(requireContext().getResources().getString(R.string.app_privacy));

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));
            }
        });
        id_me_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_about, null, false);
                final AlertDialog dialog = new AlertDialog
                        .Builder(requireContext())
                        .setView(v)
                        .create();
                //清空背景黑框
                dialog.getWindow().getDecorView().setBackground(null);
                dialog.show();
                TextView id_about_close =
                        v.findViewById(R.id.id_about_close);
                id_about_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));

            }
        });


        id_me_verison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_version, null, false);
                final AlertDialog dialog = new AlertDialog
                        .Builder(requireContext())
                        .setView(v)
                        .create();
                TextView id_version_verison = v.findViewById(R.id.id_version_verison);
                TextView id_notice_detail_close = v.findViewById(R.id.id_notice_detail_close);
                ListView id_version_list = v.findViewById(R.id.id_version_list);
                id_version_verison.setText("当前版本：" + Constant.VERSION);
                VersionCardAdapter versionCardAdapter = new VersionCardAdapter(
                        requireContext(),
                        R.id.id_version_list,
                        versions);
                id_version_list.setAdapter(versionCardAdapter);
                versionCardAdapter.notifyDataSetChanged();
                id_notice_detail_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //清空背景黑框
                dialog.getWindow().getDecorView().setBackground(null);
                dialog.show();

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialog.getWindow().setLayout((Utils.getScreenWidth(requireContext()) / 10 * 9), Utils.dp2px(requireContext(), 600));

            }
        });
        id_me_personcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PersonCenterActivity.class);
                startActivity(i);
            }
        });
        id_me_kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog tips = new AlertDialog.Builder(requireContext())
                        .setTitle("提示:")
                        .setMessage("请添加客服微信 wfgmqhx 使用客服功能")
                        .setNegativeButton("好", null)
                        .create();
                tips.show();
            }
        });
        int num = Utils.getInt(requireContext(), Constant.MG_DATE_OK, 0);
        id_me_ok.setText(num > 999 ? "999+" : String.valueOf(num));
        num = Utils.getInt(requireContext(), Constant.MG_DATE_NEAR, 0);
        id_me_near.setText(num > 999 ? "999+" : String.valueOf(num));
        num = Utils.getInt(requireContext(), Constant.MG_DATE_OUT, 0);
        id_me_out.setText(num > 999 ? "999+" : String.valueOf(num));
        num = Utils.getInt(requireContext(), Constant.MG_M_COUNT_WEB, 0);
        id_me_web.setText(num > 999 ? "999+" : String.valueOf(num));
        num = Utils.getInt(requireContext(), Constant.MG_M_COUNT_LOCAL, 0);
        id_me_local.setText(num > 999 ? "999+" : String.valueOf(num));
        num = Utils.getInt(requireContext(), Constant.MG_M_COUNT_TOTAL, 0);
        id_me_total.setText(num > 999 ? "999+" : String.valueOf(num));

        LinearLayout id_me_layout_numcount_local = binding.idMeLayoutNumcountLocal;
        LinearLayout id_me_layout_numcount_total = binding.idMeLayoutNumcountTotal;
        id_me_layout_numcount_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_me_layout_numcount_local.getVisibility() == View.VISIBLE) {
                    id_me_layout_numcount_local.setVisibility(View.GONE);
                    id_me_layout_numcount_total.setVisibility(View.VISIBLE);
                }
            }
        });
        id_me_layout_numcount_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_me_layout_numcount_total.getVisibility() == View.VISIBLE) {
                    id_me_layout_numcount_total.setVisibility(View.GONE);
                    id_me_layout_numcount_local.setVisibility(View.VISIBLE);
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                versions.clear();
                Connection connection = null;
                PreparedStatement pps = null;
                try {
                    connection = JdbcUtil.getConnection();
                    pps = connection.prepareStatement("select * from VERSION");
                    ResultSet resultSet = pps.executeQuery();
                    while (resultSet.next()) {
                        versions.add(0, new Version(resultSet.getDouble(1),
                                resultSet.getString(2),
                                resultSet.getString(3))
                        );
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    JdbcUtil.close(connection, null, pps, null);
                }

            }
        }).start();

        swipeRefreshLayout = root.findViewById(R.id.id_me_SwipeRefreshLayout);//初始化下拉刷新控件，
        swipeRefreshLayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
                if (isLogin == 1) {
                    String lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                    new Thread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            Connection con = null;
                            PreparedStatement pps = null;
                            ResultSet resultSet = null;
                            int count = 0;
                            try {
                                con = JdbcUtil.getConnection();
                                pps = con.prepareStatement("SELECT * FROM USERINFO WHERE LNAME = ?");
                                pps.setString(1, lname);
                                resultSet = pps.executeQuery();
                                while (resultSet.next()) {
                                    userInfo.put(Constant.COLUMN_U_LNAME, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_LNAME)));
                                    userInfo.put(Constant.COLUMN_U_SNAME, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_SNAME)));
                                    userInfo.put(Constant.COLUMN_U_PWD, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_PWD)));
                                    userInfo.put(Constant.COLUMN_U_HEAD, resultSet.getBytes(resultSet.findColumn(Constant.COLUMN_U_HEAD)));
                                    userInfo.put(Constant.COLUMN_U_FRIEND, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_FRIEND)));
                                    userInfo.put(Constant.COLUMN_U_PHONE, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_PHONE)));
                                    userInfo.put(Constant.COLUMN_U_MAIL, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_MAIL)));
                                    userInfo.put(Constant.COLUMN_U_RGTIME, resultSet.getLong(resultSet.findColumn(Constant.COLUMN_U_RGTIME)));
                                    userInfo.put(Constant.COLUMN_U_ONLINE, 1);
                                    userInfo.put(Constant.COLUMN_U_POINT, resultSet.getInt(resultSet.findColumn(Constant.COLUMN_U_POINT)));
                                    userInfo.put(Constant.COLUMN_U_POINTHISTORY, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_POINTHISTORY)));
                                    userInfo.put(Constant.COLUMN_U_VIP, resultSet.getInt(resultSet.findColumn(Constant.COLUMN_U_VIP)));
                                    userInfo.put(Constant.COLUMN_U_VIPYU, resultSet.getLong(resultSet.findColumn(Constant.COLUMN_U_VIPYU)));
                                    userInfo.put(Constant.COLUMN_U_CLOUDYU, resultSet.getInt(resultSet.findColumn(Constant.COLUMN_U_CLOUDYU)));
                                    userInfo.put(Constant.COLUMN_U_SIGNIN, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_SIGNIN)));
                                    userInfo.put(Constant.COLUMN_U_MYSTYLE, resultSet.getString(resultSet.findColumn(Constant.COLUMN_U_MYSTYLE)));
                                }
                                pps = con.prepareStatement("SELECT COUNT(*) FROM `" + lname + "`");
                                resultSet = pps.executeQuery();
                                while (resultSet.next()) {
                                    count = resultSet.getInt(1);
                                }
                                int finalCount = count;
                                id_me_saveyu.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_me_saveyu.setText((String) userInfo.get(Constant.COLUMN_U_MYSTYLE));
                                    }
                                });
                                id_me_head.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_me_head.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
                                    }
                                });
                                id_me_name.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_me_name.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                                    }
                                });
                                id_me_point.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_me_point.setText("积分: " + userInfo.get(Constant.COLUMN_U_POINT));
                                    }
                                });
                                final DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                                Utils.putString(requireContext(), Constant.MG_LOGIN_LNAME, (String) userInfo.get(Constant.COLUMN_U_LNAME));
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(Constant.COLUMN_U_LNAME, (String) userInfo.get(Constant.COLUMN_U_LNAME));
                                values.put(Constant.COLUMN_U_SNAME, (String) userInfo.get(Constant.COLUMN_U_SNAME));
                                values.put(Constant.COLUMN_U_PWD, (String) userInfo.get(Constant.COLUMN_U_PWD));
                                values.put(Constant.COLUMN_U_HEAD, (byte[]) userInfo.get(Constant.COLUMN_U_HEAD));
                                values.put(Constant.COLUMN_U_FRIEND, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
                                values.put(Constant.COLUMN_U_PHONE, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
                                values.put(Constant.COLUMN_U_MAIL, (String) userInfo.get(Constant.COLUMN_U_MAIL));
                                values.put(Constant.COLUMN_U_RGTIME, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_RGTIME)).toString()));
                                values.put(Constant.COLUMN_U_ONLINE, 1);
                                values.put(Constant.COLUMN_U_POINT, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_POINT)).toString()));
                                values.put(Constant.COLUMN_U_POINTHISTORY, (String) userInfo.get(Constant.COLUMN_U_POINTHISTORY));
                                values.put(Constant.COLUMN_U_VIP, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIP)).toString()));
                                values.put(Constant.COLUMN_U_VIPYU, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIPYU)).toString()));
                                values.put(Constant.COLUMN_U_CLOUDYU, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_CLOUDYU)).toString()));
                                values.put(Constant.COLUMN_U_SIGNIN, String.valueOf(userInfo.get(Constant.COLUMN_U_SIGNIN)));
                                values.put(Constant.COLUMN_U_MYSTYLE, String.valueOf(userInfo.get(Constant.COLUMN_U_MYSTYLE)));
                                db.replace(Constant.TABLE_NAME_USER, null, values);
                                db.close();
                                Looper.prepare();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "已更新账号信息", Toast.LENGTH_SHORT).show();//刷新时要做的事情
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }, 500);
                                Looper.loop();
                            } catch (SQLException sqlException) {
                                Looper.prepare();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "更新信息失败", Toast.LENGTH_SHORT).show();//刷新时要做的事情
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }, 500);
                                Looper.loop();
                                sqlException.printStackTrace();
                            } finally {
                                JdbcUtil.close(con, null, pps, resultSet);
                            }
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            versions.clear();
                            Connection connection = null;
                            PreparedStatement pps = null;
                            try {
                                connection = JdbcUtil.getConnection();
                                pps = connection.prepareStatement("select * from VERSION");
                                ResultSet resultSet = pps.executeQuery();
                                while (resultSet.next()) {
                                    versions.add(new Version(resultSet.getDouble(1),
                                            resultSet.getString(2),
                                            resultSet.getString(3))
                                    );
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                JdbcUtil.close(connection, null, pps, null);
                            }

                        }
                    }).start();

                } else {
//					Toast.makeText(getActivity(), "已更新账号信息", Toast.LENGTH_SHORT).show();//刷新时要做的事情
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 500);
                }


            }
        });

        LinearLayout id_me_login_action_layout = binding.idMeLoginActionLayout;
        LinearLayout id_me_login_person_layout = binding.idMeLoginPersonLayout;
        LinearLayout id_me_login_jifen_layout = binding.idMeLoginJifenLayout;

        Button id_me_loginout = binding.idMeLoginout;
        id_me_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog tips = new AlertDialog.Builder(requireContext())
                        .setTitle("提示:")
                        .setMessage("确认退出账号吗")
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.putInt(requireContext(), Constant.MG_LOGIN, 0);
                                Utils.putString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                                id_me_login_action_layout.setVisibility(View.GONE);
                                id_me_login_person_layout.setVisibility(View.GONE);
                                id_me_login_jifen_layout.setVisibility(View.GONE);
                                id_me_head.setImageResource(R.mipmap.me_man_default);
                                id_me_name.setText("未登录");
                                id_me_saveyu.setText("登录享受更多功能");
                                id_me_point.setText("");
                                id_me_scrollview.scrollTo(0, 0);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                tips.show();

            }
        });

        return root;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        String a = Utils.getString(requireContext(), "EDITPERSON", "no");
        if (a.equals("ok")) {
            swipeRefreshLayout.autoRefresh();
            Utils.putString(requireContext(), "EDITPERSON", "no");
            return;
        }
        RoundImageView id_me_head = root.findViewById(R.id.id_me_head);
        TextView id_me_name = root.findViewById(R.id.id_me_name);
        TextView id_me_point = root.findViewById(R.id.id_me_point);
        TextView id_me_saveyu = root.findViewById(R.id.id_me_saveyu);
        final DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        userInfo.clear();
        LinearLayout id_me_login_action_layout = root.findViewById(R.id.id_me_login_action_layout);
        LinearLayout id_me_login_person_layout = root.findViewById(R.id.id_me_login_person_layout);
        LinearLayout id_me_login_jifen_layout = root.findViewById(R.id.id_me_login_jifen_layout);
        int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
        String lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        if (isLogin == 1 && !lname.equals("0")) {
            id_me_login_action_layout.setVisibility(View.VISIBLE);
            id_me_login_person_layout.setVisibility(View.VISIBLE);
            id_me_login_jifen_layout.setVisibility(View.VISIBLE);
            SQLiteDatabase db = null;
            db = dbHelper.getWritableDatabase();
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
                    id_me_head.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
                    id_me_name.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                    id_me_point.setText("积分: " + userInfo.get(Constant.COLUMN_U_POINT));
                    id_me_saveyu.setText(String.valueOf(userInfo.get(Constant.COLUMN_U_MYSTYLE)));


                } else {
                    Utils.putInt(requireContext(), Constant.MG_LOGIN, 0);
                    Utils.putString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
                    id_me_login_action_layout.setVisibility(View.GONE);
                    id_me_login_person_layout.setVisibility(View.GONE);
                    id_me_login_jifen_layout.setVisibility(View.GONE);
                }
                cursor.close();
                db.close();
            }

            if (db != null) {
                db.close();
            }
        } else {
            id_me_login_action_layout.setVisibility(View.GONE);
            id_me_login_person_layout.setVisibility(View.GONE);
            id_me_login_jifen_layout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}