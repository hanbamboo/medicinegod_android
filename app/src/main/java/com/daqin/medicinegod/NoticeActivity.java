package com.daqin.medicinegod;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daqin.medicinegod.Adspter.NoticeCardAdapter;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Fragment.IntelFragment;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityDetailBinding;
import com.daqin.medicinegod.databinding.ActivityNoticeBinding;
import com.daqin.medicinegod.entity.Notice;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class NoticeActivity extends AppCompatActivity {
    private ActivityNoticeBinding binding;
    View root;
    private String lname = "0";
    Map<String, Object> userInfo = new HashMap<>();

    final DatabaseHelper dbHelper = new DatabaseHelper(NoticeActivity.this);
    List<Notice> notices = new ArrayList<>();
    NoticeCardAdapter noticeCardAdapter;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intentcheck = new Intent(NoticeActivity.this, MainActivity.class);
        TextView id_id_notice_title = binding.idIdNoticeTitle;
        AutoSwipeRefreshLayout id_id_notice_autoSwipeRefreshLayout = binding.idIdNoticeAutoSwipeRefreshLayout;
        SwipeMenuListView id_id_notice_list = binding.idIdNoticeList;
        ImageButton id_notice_back = binding.idNoticeBack;
        id_notice_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lname = Utils.getString(NoticeActivity.this, Constant.MG_LOGIN_LNAME, "0");
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + Constant.TABLE_NAME_NOTICE + " where " + Constant.COLUMN_N_LNAME + " = '" + lname + "' order by " + Constant.COLUMN_N_CHECKD, null);
        notices.clear();
        while (cursor.moveToNext()) {
            notices.add(new Notice(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_CONTEXT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_FROM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_TO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_CHECKD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_LNAME))
            ));
        }


        cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
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

        noticeCardAdapter = new NoticeCardAdapter(NoticeActivity.this, R.layout.list_notice, notices);
        id_id_notice_list.setAdapter(noticeCardAdapter);

        id_id_notice_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog dialog = new AlertDialog.Builder(NoticeActivity.this)
                        .setTitle("提示：")
                        .setMessage("确定删除全部消息？")
                        .setCancelable(false)
                        .setPositiveButton("取消", null)
                        .setNegativeButton("全部删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Connection connection = null;
                                        PreparedStatement pps = null;
                                        try {
                                            connection = JdbcUtil.getConnection();
                                            pps = connection.prepareStatement("delete from `" + lname + "NOTICE`  where 1 = 1");
                                            pps.executeUpdate();
                                            dbHelper.getWritableDatabase().delete(Constant.TABLE_NAME_NOTICE, " 1 = 1 ", null);
                                            id_id_notice_list.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notices.clear();
                                                    noticeCardAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        } finally {
                                            JdbcUtil.close(connection, null, pps, null);
                                            dialog.dismiss();
                                        }

                                    }
                                }).start();
                            }
                        })
                        .create();
                dialog.show();

                return false;
            }
        });
        ;
        id_id_notice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notice n = notices.get(i);

                synchronized (this) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Connection connection = null;
                            PreparedStatement pps = null;
                            try {
                                connection = JdbcUtil.getConnection();
                                pps = connection.prepareStatement("update `" + lname + "NOTICE` set " + Constant.COLUMN_N_CHECKD + " = 1 where " + Constant.COLUMN_N_ID + " = '" + n.getNotice_id() + "'");
                                pps.executeUpdate();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Constant.COLUMN_N_CHECKD, "1");
                                dbHelper.getWritableDatabase().update(Constant.TABLE_NAME_NOTICE, contentValues, Constant.COLUMN_N_ID + " = " + n.getNotice_id(), null);
                                id_id_notice_list.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        n.setNotice_check("1");
                                        noticeCardAdapter.notifyDataSetChanged();
                                    }
                                });
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                JdbcUtil.close(connection, null, pps, null);

                            }

                        }
                    }).start();
                }
                View v = LayoutInflater.from(NoticeActivity.this).inflate(R.layout.dialog_notice, null, false);
                final AlertDialog dialog = new AlertDialog
                        .Builder(NoticeActivity.this)
                        .setView(v)
                        .setCancelable(false)
                        .create();


                TextView id_notice_detail_close = v.findViewById(R.id.id_notice_detail_close);
                TextView id_notice_detail_time = v.findViewById(R.id.id_notice_detail_time);
                TextView id_notice_detail_title = v.findViewById(R.id.id_notice_detail_title);
                TextView id_notice_detail_context = v.findViewById(R.id.id_notice_detail_context);
                TextView id_notice_detail_username = v.findViewById(R.id.id_notice_detail_username);
                RoundImageView id_notice_detail_head = v.findViewById(R.id.id_notice_detail_head);
                id_notice_detail_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                id_notice_detail_time.setText(n.getNotice_time());
                id_notice_detail_title.setText(n.getNotice_title());
                id_notice_detail_context.setText(n.getNotice_context());
                id_notice_detail_username.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                id_notice_detail_head.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));

                //清空背景黑框
                dialog.getWindow().getDecorView().setBackground(null);
                dialog.show();

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialog.getWindow().setLayout((Utils.getScreenWidth(NoticeActivity.this) / 10 * 9), Utils.dp2px(NoticeActivity.this, 600));
            }
        });

        id_id_notice_autoSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection = null;
                        PreparedStatement pps = null;
                        ResultSet resultSet = null;
                        DatabaseHelper dbHelper = new DatabaseHelper(NoticeActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int notice_noRead = 0;
                        try {
                            connection = JdbcUtil.getConnection();
                            if (connection != null) {
                                pps = connection.prepareStatement("select * from `" + lname + "NOTICE` order by  " + Constant.COLUMN_N_CHECKD);
                                resultSet = pps.executeQuery();
                                notices.clear();
                                while (resultSet.next()) {
                                    Notice notice = new Notice(resultSet.getInt(Constant.COLUMN_N_ID),
                                            resultSet.getString(Constant.COLUMN_N_TITLE),
                                            resultSet.getString(Constant.COLUMN_N_CONTEXT),
                                            resultSet.getString(Constant.COLUMN_N_FROM),
                                            resultSet.getString(Constant.COLUMN_N_TO),
                                            Utils.getNoticeTimeFromString(resultSet.getString(Constant.COLUMN_N_TIME)),
                                            resultSet.getString(Constant.COLUMN_N_CHECKD),
                                            lname);
                                    Utils.insertNotice(NoticeActivity.this, notice);
                                    notices.add(notice);
                                    if (resultSet.getInt(Constant.COLUMN_N_CHECKD) == 0) {
                                        notice_noRead++;
                                    }

                                }


                                Log.i(Constant.PREFERENCES_NAME, "notice : total " + notices.size() + " , new " + notice_noRead);
                                if (notices.size() == 0) {
                                    Looper.prepare();
                                    Toast.makeText(NoticeActivity.this, "没有消息了", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                            if (db != null) {
                                db.close();
                            }
                        } catch (SQLException sqlException) {
                            Looper.prepare();
                            Toast.makeText(NoticeActivity.this, "最新消息获取失败！请重试", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            sqlException.printStackTrace();
                        } finally {
                            if (db != null) {
                                db.close();
                            }
                            id_id_notice_list.post(new Runnable() {
                                @Override
                                public void run() {
                                    noticeCardAdapter.notifyDataSetChanged();
                                }
                            });
                            JdbcUtil.close(connection, null, pps, resultSet);
                            id_id_notice_autoSwipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    id_id_notice_autoSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        //侧滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem yidu = new SwipeMenuItem(NoticeActivity.this);
                // set item background
                yidu.setBackground(new ColorDrawable(Color.rgb(104, 118, 237)));
                // set item width
                yidu.setWidth(200);
                // set item title
                yidu.setTitle("设为已读");
                // set item title fontsize
                yidu.setTitleSize(16);
                // set item title font color
                yidu.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(yidu);

                SwipeMenuItem weidu = new SwipeMenuItem(NoticeActivity.this);
                // set item background
                weidu.setBackground(new ColorDrawable(Color.rgb(159, 209,
                        161)));
                // set item width
                weidu.setWidth(200);
                // set item title
                weidu.setTitle("设为未读");
                // set item title fontsize
                weidu.setTitleSize(16);
                // set item title font color
                weidu.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(weidu);

                SwipeMenuItem del = new SwipeMenuItem(NoticeActivity.this);
                // set item background
                del.setBackground(new ColorDrawable(Color.rgb(254,
                        159, 145)));
                // set item width
                del.setWidth(200);
                // set item title
                del.setTitle("删除消息");
                // set item title fontsize
                del.setTitleSize(16);
                // set item title font color
                del.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(del);
            }
        };
        id_id_notice_list.setMenuCreator(creator);

        id_id_notice_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                lname = Utils.getString(NoticeActivity.this, Constant.MG_LOGIN_LNAME, "0");

                synchronized (this) {
                    ContentValues contentValues = new ContentValues();
                    switch (index) {
                        case 0:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Connection connection = null;
                                    PreparedStatement pps = null;
                                    try {
                                        connection = JdbcUtil.getConnection();
                                        pps = connection.prepareStatement("update `" + lname + "NOTICE` set " + Constant.COLUMN_N_CHECKD + " = 1 where " + Constant.COLUMN_N_ID + " = '" + notices.get(position).getNotice_id() + "'");
                                        pps.executeUpdate();
                                        contentValues.put(Constant.COLUMN_N_CHECKD, "1");
                                        dbHelper.getWritableDatabase().update(Constant.TABLE_NAME_NOTICE, contentValues, Constant.COLUMN_N_ID + " = " + notices.get(position).getNotice_id(), null);

                                        id_id_notice_list.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notices.get(position).setNotice_check("1");
                                                noticeCardAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(NoticeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } finally {
                                        JdbcUtil.close(connection, null, pps, null);
                                    }

                                }
                            }).start();
                            break;
                        case 1:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Connection connection = null;
                                    PreparedStatement pps = null;
                                    try {
                                        connection = JdbcUtil.getConnection();
                                        pps = connection.prepareStatement("update `" + lname + "NOTICE` set " + Constant.COLUMN_N_CHECKD + " = 0 where " + Constant.COLUMN_N_ID + " = '" + notices.get(position).getNotice_id() + "'");
                                        int a = pps.executeUpdate();
                                        contentValues.put(Constant.COLUMN_N_CHECKD, "0");
                                        dbHelper.getWritableDatabase().update(Constant.TABLE_NAME_NOTICE, contentValues, Constant.COLUMN_N_ID + " = " + notices.get(position).getNotice_id(), null);
                                        id_id_notice_list.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notices.get(position).setNotice_check("0");
                                                noticeCardAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(NoticeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } finally {
                                        JdbcUtil.close(connection, null, pps, null);
                                    }

                                }
                            }).start();
                            break;
                        case 2:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Connection connection = null;
                                    PreparedStatement pps = null;
                                    try {
                                        connection = JdbcUtil.getConnection();
                                        pps = connection.prepareStatement("delete from `" + lname + "NOTICE` where " + Constant.COLUMN_N_ID + " = '" + notices.get(position).getNotice_id() + "'");
                                        int a = pps.executeUpdate();
                                        contentValues.put(Constant.COLUMN_N_CHECKD, "1");
                                        dbHelper.getWritableDatabase().delete(Constant.TABLE_NAME_NOTICE, Constant.COLUMN_N_ID + " = " + notices.get(position).getNotice_id(), null);
                                        id_id_notice_list.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notices.remove(position);
                                                noticeCardAdapter.notifyDataSetChanged();
                                            }
                                        });


                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(NoticeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    } finally {
                                        JdbcUtil.close(connection, null, pps, null);
                                    }

                                }
                            }).start();
                            break;
                    }
                }

                return false;
            }
        });


        root = binding.getRoot();


    }


    @Override
    protected void onResume() {
        super.onResume();


    }
}