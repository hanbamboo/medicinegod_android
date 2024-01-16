package com.daqin.medicinegod.Fragment;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.daqin.medicinegod.Adspter.CollageBorrowDetailAdapter;
import com.daqin.medicinegod.Adspter.MedicineCardAdapter;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.NoticeActivity;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.RegisterActivity;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.FragmentIntelBinding;
import com.daqin.medicinegod.entity.Notice;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.xutils.x;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;

public class IntelFragment extends Fragment {

    private FragmentIntelBinding binding;
    View root;

    String signin = "1970-1-1";
    Map<String, Object> userInfo = new HashMap<>();
    final Handler handler = new Handler();
    List<Notice> notices = new ArrayList<>();
    static String lname;
    int isLogin;

    AutoSwipeRefreshLayout id_intel_swipelayout;


    TextView id_intel_name;
    RoundImageView id_intel_headimg;
    TextView id_intel_say;
    TextView id_intel_tips;
    TextView id_intel_vip;
    TextView id_intel_explor_green_title;
    TextView id_intel_explor_orange_title;
    TextView id_intel_explor_red_title;
    ImageButton id_intel_notice;
    TextView id_intel_notice_count;
    TextView id_intel_progress_title;
    LinearProgressIndicator id_intel_progress;
    LinearLayout id_intel_explor_green_layout;
    LinearLayout id_intel_explor_orange_layout;
    LinearLayout id_intel_explor_red_layout;
    FlowLayout id_intel_elabel_all;
    NotificationManager notificationManager;
//    List<Map<String, Object>> medicines = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntelBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        notices.clear();
        //防止闪退
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("err", e.toString());
            }
        });


        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");
        isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Log.i(Constant.PREFERENCES_NAME, "登录名:" + lname);
        id_intel_swipelayout = binding.idIntelSwipelayout;


        id_intel_headimg = binding.idIntelHeadimg;
        id_intel_name = binding.idIntelName;
        id_intel_say = binding.idIntelSay;
        id_intel_vip = binding.idIntelVip;
        id_intel_notice = binding.idIntelNotice;
        id_intel_notice_count = binding.idIntelNoticeCount;
        id_intel_progress_title = binding.idIntelProgressTitle;
        id_intel_tips = binding.idIntelTips;
        id_intel_progress = binding.idIntelProgress;
        banner = binding.idIntelBanner;
        id_intel_explor_green_title = binding.idIntelExplorGreenTitle;
        id_intel_explor_orange_title = binding.idIntelExplorOrangeTitle;
        id_intel_explor_red_title = binding.idIntelExplorRedTitle;
        id_intel_explor_green_layout = binding.idIntelExplorGreenLayout;
        id_intel_explor_orange_layout = binding.idIntelExplorOrangeLayout;
        id_intel_explor_red_layout = binding.idIntelExplorRedLayout;
        id_intel_elabel_all = binding.idIntelElabelAll;

        x.Ext.init(requireActivity().getApplication());
//        x.Ext.setDebug(BuildConfig.DEBUG);  // 是否输出debug日志, 开启debug会影响性能.
//        x.view().inject(getActivity());  //没有用到view注解可以先不用


        notificationManager = (NotificationManager) requireContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("MedicineGod药神", "药神 - 系统消息通知", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);


        Calendar d = Calendar.getInstance();
        int hour = d.get(Calendar.HOUR_OF_DAY);//调用Calender类，获取当前系统时，24小时进制
        Random randoma = new Random();
        int abb = randoma.nextInt(5) % 3;
        if (hour >= 6 && hour < 11) {
            switch (abb) {
                case 0:
                    id_intel_tips.setText("上午好,打工人，记得吃早饭");
                    break;
                case 1:
                    id_intel_tips.setText("艺术的成功在于没有人工雕琢的痕迹。早！");
                    break;
                case 2:
                    id_intel_tips.setText("生活得更好，是为了自己。早上好！");
                    break;
                case 3:
                    id_intel_tips.setText("义无反顾地走下去吧。早上好！");
                    break;
            }

        } else if (hour >= 11 && hour < 13) {
            switch (abb) {
                case 0:
                    id_intel_tips.setText("中午好，上午的砖烫手吗");
                    break;
                case 1:
                    id_intel_tips.setText("生活不简单，尽量简单过。午安");
                    break;
                case 2:
                    id_intel_tips.setText("一切都是美好的！午安。");
                    break;
                case 3:
                    id_intel_tips.setText("有时简单明了最恰当。午安。");
                    break;
            }

        } else if (hour >= 13 && hour < 16) {
            switch (abb) {
                case 0:
                    id_intel_tips.setText("下午好，准备好开始搬砖了吗");
                    break;
                case 1:
                    id_intel_tips.setText("他们不懂吃货的快乐。");
                    break;
                case 2:
                    id_intel_tips.setText("掐指一算，今天胃又赢了。");
                    break;
                case 3:
                    id_intel_tips.setText("唯美食与爱不可辜负。");
                    break;
            }


        } else if (hour >= 16 && hour < 19) {
            switch (abb) {
                case 0:
                    id_intel_tips.setText("傍晚好，准备下班啦");
                    break;
                case 1:
                    id_intel_tips.setText("好想周末双休的日子。");
                    break;
                case 2:
                    id_intel_tips.setText("梦想不大，道路很长，开始了就别停下。");
                    break;
                case 3:
                    id_intel_tips.setText("你们谁有我牛，来我和一起加班啊。");
                    break;
            }

        } else {
            switch (abb) {
                case 0:
                    id_intel_tips.setText("晚安，要好好休息明天有力气搬砖");
                    break;
                case 1:
                    id_intel_tips.setText("晚安，世界！");
                    break;
                case 2:
                    id_intel_tips.setText("艰难时段无一例外都会过去。");
                    break;
                case 3:
                    id_intel_tips.setText("爱该爱的人，过快意的人生。");
                    break;
            }

        }


        id_intel_swipelayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
        id_intel_swipelayout.setOnRefreshListener(new AutoSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initAll();
                Log.i(Constant.PREFERENCES_NAME, "刷新了主页");
                id_intel_swipelayout.setRefreshing(false);

            }
        });

        id_intel_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lname.equals("0")) {
                    Utils.putInt(requireContext(), "noticeStart", 1);
                    startActivity(new Intent(requireContext(), NoticeActivity.class));
                } else {
                    Toast.makeText(requireContext(), "请先登录后使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //登录状态，已登录的话会有数据
        if (!lname.equals("0")) {
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
                    String style = (String) userInfo.get(Constant.COLUMN_U_MYSTYLE);
                    int vip = Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIP)).toString());
                    id_intel_name.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                    if (style == null || style.trim().equals("")) {
                        id_intel_say.setText("你很有个性嘛~");
                    } else {
                        id_intel_say.setText(style);
                    }
                    if (vip == 0) {
                        id_intel_vip.setText("非会员");
                        id_intel_vip.setTextColor(ResourcesCompat.getColor(requireContext().getResources(),
                                R.color.m_blue_80, null));
                        id_intel_vip.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                R.drawable.bg_homepage_card_title_blue_border, null));
                    } else {
                        id_intel_vip.setText("药神会员");
                        id_intel_vip.setTextColor(ResourcesCompat.getColor(requireContext().getResources(),
                                R.color.m_out_70, null));
                        id_intel_vip.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                                R.drawable.bg_homepage_card_title_red_border, null));
                    }
                    id_intel_headimg.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
                }
                db.close();

            }
        } else {
            id_intel_name.setText("用户未登录");
            id_intel_say.setText("你很有个性嘛~");
            id_intel_headimg.setImageResource(R.mipmap.me_man_default);
            id_intel_vip.setText("非会员");
            id_intel_vip.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),
                    R.color.m_grey_50, null));
            id_intel_progress_title.setText("0/0");
            id_intel_progress.setProgress(0);
            id_intel_progress.setMax(0);
        }

//		Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 AND " + Constant.COLUMN_M_UID + " = ?", new String[]{String.valueOf(Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0"))});
//		Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_FROMWEB + " = 0 and " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
        List<Map<String, Object>> m_normal = new ArrayList<>();
        List<Map<String, Object>> m_near = new ArrayList<>();
        List<Map<String, Object>> m_out = new ArrayList<>();
        long now = Utils.getDateFromString(Utils.getDate());
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
            long out = Long.parseLong(String.valueOf(map.getOrDefault(Constant.COLUMN_M_OUTDATE, 9999999999L)));
            if (out - now <= 0) {
                m_out.add(map);
            } else if ((out - now) / 1000 <= 5356800 && out - now > 0) {
                //5356800 是 62天的时间 3600*24*62
                m_near.add(map);
            } else {
                m_normal.add(map);
            }

        }
        cursor.close();

        Utils.putInt(requireContext(), Constant.MG_DATE_OUT, m_out.size());
        Utils.putInt(requireContext(), Constant.MG_DATE_NEAR, m_near.size());
        Utils.putInt(requireContext(), Constant.MG_DATE_OK, m_normal.size());

        id_intel_explor_green_title.setText("正常使用药品(" + String.valueOf(m_normal.size() > 99 ? "99+" : m_normal.size()) + ")");
        id_intel_explor_orange_title.setText("即将过期药品(" + String.valueOf(m_near.size() > 99 ? "99+" : m_near.size()) + ")");
        id_intel_explor_red_title.setText("已经过期药品(" + String.valueOf(m_out.size() > 99 ? "99+" : m_out.size()) + ")");

        id_intel_explor_green_layout.removeAllViewsInLayout();
        id_intel_explor_orange_layout.removeAllViewsInLayout();
        id_intel_explor_red_layout.removeAllViewsInLayout();
        Random random = new Random();
        for (Map<String, Object> mapn : m_normal) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·" + Utils.getOutDateString(Long.parseLong(Objects.requireNonNull(mapn.get(Constant.COLUMN_M_OUTDATE)).toString()), 3) + Objects.requireNonNull(mapn.get(Constant.COLUMN_M_NAME)).toString());
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_green_layout.addView(textView, layoutParams);
        }
        for (Map<String, Object> mapn : m_near) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·" + Utils.getOutDateString(Long.parseLong(Objects.requireNonNull(mapn.get(Constant.COLUMN_M_OUTDATE)).toString()), 3) + Objects.requireNonNull(mapn.get(Constant.COLUMN_M_NAME)).toString());
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_orange_layout.addView(textView, layoutParams);
        }
        for (Map<String, Object> mapn : m_out) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·" + Utils.getOutDateString(Long.parseLong(Objects.requireNonNull(mapn.get(Constant.COLUMN_M_OUTDATE)).toString()), 3) + Objects.requireNonNull(mapn.get(Constant.COLUMN_M_NAME)).toString());
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_red_layout.addView(textView, layoutParams);
        }


        if (m_normal.size() == 0) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·您目前没添加任何药品");
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_green_layout.addView(textView, layoutParams);
        }
        if (m_near.size() == 0) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·您目前没添加任何药品");
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_orange_layout.addView(textView, layoutParams);
        }
        if (m_out.size() == 0) {
            int viewId = random.nextInt(8999) * 123;
            //设置一个标签
            TextView textView = new TextView(getContext());
            textView.setId(viewId);
            textView.setText("·您目前没添加任何药品");
            textView.setTextSize(12);
            textView.setTextColor(Color.argb(70, 0, 0, 0));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(1);
//            textView.setPadding(20, 5, 20, 5);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 10, 0);
            //添加到布局中
            id_intel_explor_red_layout.addView(textView, layoutParams);
        }

        return root;
    }


    Banner banner;
    List images = new ArrayList<>();

    Random random = new Random();

    @SuppressLint("SetTextI18n")
    private void initAll() {

        //加载标签
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select `" + Constant.COLUMN_M_ELABEL + "` from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_FROMWEB + " = 0 and " + Constant.COLUMN_M_DELFLAG + " = 0 ", null);
                Set<String> set = new HashSet<>();
                Random random = new Random();
                id_intel_elabel_all.post(new Runnable() {
                    @Override
                    public void run() {
                        id_intel_elabel_all.removeAllViews();
                    }
                });
                while (cursor.moveToNext()) {
                    Collections.addAll(set, cursor.getString(0).trim().split("@@"));
                }
                if (set.size() <= 0) {
                    int viewId = random.nextInt(8999) * 123;
                    //设置一个标签
                    TextView textView = new TextView(getContext());
                    textView.setId(viewId);
                    textView.setText("暂无药效标签");
                    textView.setTextSize(14);
                    textView.setTextColor(Color.rgb(255, 255, 255));
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setPadding(25, 10, 25, 10);
                    textView.setBackground(requireContext().getDrawable(R.drawable.bg_search_history_btn_blue));
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 10, 10, 10);
                    //添加到布局中
                    id_intel_elabel_all.post(new Runnable() {
                        @Override
                        public void run() {
                            id_intel_elabel_all.addView(textView, layoutParams);
                        }
                    });

                } else {
                    int count = 0;
                    for (String str : set) {
                        int viewId = random.nextInt(8999) * 123;
                        //设置一个标签
                        TextView textView = new TextView(getContext());
                        textView.setId(viewId);
                        textView.setText(str);
                        textView.setTextSize(14);
                        textView.setTextColor(Color.rgb(255, 255, 255));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setElevation(5);
                        textView.setPadding(25, 10, 25, 10);
                        textView.setBackground(requireContext().getDrawable(R.drawable.bg_search_history_btn_blue));
                        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(15, 15, 15, 15);
                        //添加到布局中
                        id_intel_elabel_all.post(new Runnable() {
                            @Override
                            public void run() {
                                id_intel_elabel_all.addView(textView, layoutParams);
                            }
                        });
                        count++;
                        if (count > 30) {
                            break;
                        }
                    }


                }

            }
        }).start();


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
                                AlertDialog dialog = new AlertDialog.Builder(requireContext())
                                        .setTitle(version + vername + (vername.contains("内部Snapshot") ? "更新预告" : "更新公告:"))
                                        .setMessage(tips)
                                        .setCancelable(false)
                                        .setPositiveButton(vername.contains("内部Snapshot") ? "好的" : "稍后更新", null)
                                        .setNegativeButton(vername.contains("内部Snapshot") ? "" : "去更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Utils.putInt(requireContext(), Constant.VERSIONTIPS, 0);
                                                Uri uri = Uri.parse("http://medicinegod.cn/s/download/android.html");
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
                                        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                                                .setTitle(version + vername + "版本公告:")
                                                .setMessage(tips)
                                                .setCancelable(false)
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

        //banner获取
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                PreparedStatement pps = null;
                ResultSet resultSet = null;
                images.clear();
                try {
                    connection = JdbcUtil.getConnection();
                    if (connection != null) {
                        pps = connection.prepareStatement("select * from `BANNER`");
                        resultSet = pps.executeQuery();
                        while (resultSet.next()) {
                            images.add(resultSet.getString(resultSet.findColumn("banner_url")));
                        }
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    Toast.makeText(requireContext(), "获取最新页面失败，请重试", Toast.LENGTH_SHORT).show();
                    images.clear();
                    images.add(null);
                } finally {
                    JdbcUtil.close(connection, null, pps, resultSet);
                    banner.post(new Runnable() {
                        @Override
                        public void run() {
                            banner.setAdapter(new BannerImageAdapter<String>(images) {
                                @Override
                                public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                                    if (data == null || ((String) data).trim().equals("") || !data.contains("http")) {
                                        Glide.with(holder.itemView)
                                                .load(R.mipmap.add_imgdefault)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                                .into(holder.imageView);
                                    } else {
                                        Glide.with(holder.itemView)
                                                .load(data)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                                                .into(holder.imageView);

                                    }

                                }

                            }).setIndicator(new CircleIndicator(getContext())).setLoopTime(2800).setOnBannerListener(null);
                        }
                    });
                }
            }
        }).start();


        //获取消息列表
        if (!lname.equals("0")) {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from " + Constant.TABLE_NAME_NOTICE + " where " + Constant.COLUMN_N_LNAME + " = '" + lname + "' ", null);
            int notice_noRead = 0;
            notices.clear();
            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_N_CHECKD))) == 0) {
                    notice_noRead++;
                }
            }

            if (notice_noRead <= 0) {
                id_intel_notice_count.post(new Runnable() {
                    @Override
                    public void run() {
                        id_intel_notice_count.setVisibility(View.INVISIBLE);
                    }
                });

            } else {
                int finalNotice_noRead = notice_noRead;
                id_intel_notice_count.post(new Runnable() {
                    @Override
                    public void run() {
                        id_intel_notice_count.setVisibility(View.VISIBLE);
                        id_intel_notice_count.setText(finalNotice_noRead > 99 ? "99+" : String.valueOf(finalNotice_noRead));
                    }
                });
            }
            Log.i(Constant.PREFERENCES_NAME, "notice : total " + notices.size() + " , new " + notice_noRead);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    PreparedStatement pps = null;
                    ResultSet resultSet = null;
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int notice_noRead = 0;
                    notices.clear();
                    try {
                        connection = JdbcUtil.getConnection();
                        if (connection != null) {
                            pps = connection.prepareStatement("select * from `" + lname + "NOTICE` order by  " + Constant.COLUMN_N_CHECKD);
                            resultSet = pps.executeQuery();
                            while (resultSet.next()) {
                                Notice notice = new Notice(resultSet.getInt(Constant.COLUMN_N_ID),
                                        resultSet.getString(Constant.COLUMN_N_TITLE),
                                        resultSet.getString(Constant.COLUMN_N_CONTEXT),
                                        resultSet.getString(Constant.COLUMN_N_FROM),
                                        resultSet.getString(Constant.COLUMN_N_TO),
                                        Utils.getNoticeTimeFromString(resultSet.getString(Constant.COLUMN_N_TIME).split("\\.")[0]),
                                        resultSet.getString(Constant.COLUMN_N_CHECKD),
                                        lname);
                                Utils.insertNotice(requireContext(), notice);
                                notices.add(notice);
                                if (resultSet.getInt(Constant.COLUMN_N_CHECKD) == 0) {
                                    notice_noRead++;
                                }
                            }

                            if (notice_noRead <= 0) {
                                id_intel_notice_count.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_intel_notice_count.setVisibility(View.INVISIBLE);
                                    }
                                });

                            } else {
                                int finalNotice_noRead = notice_noRead;
                                id_intel_notice_count.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_intel_notice_count.setVisibility(View.VISIBLE);
                                        id_intel_notice_count.setText(finalNotice_noRead > 99 ? "99+" : String.valueOf(finalNotice_noRead));
                                    }
                                });
                            }
                            Log.i(Constant.PREFERENCES_NAME, "notice : total " + notices.size() + " , new " + notice_noRead);
                            cursor.close();
                        }
                        for (int i = 0; i < notice_noRead; i++) {
                            notificationManager.notify(random.nextInt() * 2000, Utils.getNotification(requireContext(), notices.get(i).getNotice_title(), notices.get(i).getNotice_context()));
                        }


                        if (db != null) {
                            db.close();
                        }
                    } catch (SQLException sqlException) {
                        Looper.prepare();
                        Toast.makeText(requireContext(), "最新消息获取失败！请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        sqlException.printStackTrace();
                    } finally {
                        if (db != null) {
                            db.close();
                        }
                        JdbcUtil.close(connection, null, pps, resultSet);

                    }
                }
            }).start();


            //获取签到信息并签到
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    PreparedStatement pps = null;
                    ResultSet resultSet = null;
                    DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
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

            //云服务
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int cloudCount = 0;
                    int cloudTotal = 0;
                    Connection connection = null;
                    ResultSet resultSet1 = null;
                    ResultSet resultSet2 = null;
                    PreparedStatement pps1 = null;
                    PreparedStatement pps2 = null;
                    try {
                        connection = JdbcUtil.getConnection();
                        connection.setAutoCommit(false);
                        pps1 = connection.prepareStatement("SELECT COUNT(*) FROM `" + lname + "` ");
                        pps2 = connection.prepareStatement("SELECT CLOUDYU FROM USERINFO WHERE LNAME=?");
                        pps2.setString(1, lname);
                        resultSet1 = pps1.executeQuery();
                        resultSet2 = pps2.executeQuery();
                        connection.commit();
                        while (resultSet1.next()) {
                            cloudCount = resultSet1.getInt(1);
                        }
                        while (resultSet2.next()) {
                            cloudTotal = resultSet2.getInt(1);
                        }

                        System.out.println(cloudCount + "-" + cloudTotal);
                        int finalCloudCount = cloudCount;
                        int finalCloudTotal = cloudTotal;
                        id_intel_progress.post(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                id_intel_progress_title.setText(finalCloudCount + "/" + finalCloudTotal);
                                if (finalCloudCount > finalCloudTotal) {
                                    id_intel_progress.setProgress(finalCloudCount);
                                    id_intel_progress.setMax(finalCloudCount);
                                } else {
                                    id_intel_progress.setProgress(finalCloudCount);
                                    id_intel_progress.setMax(finalCloudTotal);
                                }
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        id_intel_progress.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "网络药品条目获取失败", Toast.LENGTH_SHORT).show();
                                id_intel_progress_title.setText("-/-");
                                id_intel_progress.setProgress(0);
                                id_intel_progress.setMax(0);
                            }
                        });
                        Looper.loop();
                    } finally {
                        JdbcUtil.close(connection, null, pps1, resultSet1);
                        JdbcUtil.close(connection, null, pps2, resultSet2);
                    }

                }
            }).start();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.putInt(requireContext(), "noticeStart", 0);
        initAll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}