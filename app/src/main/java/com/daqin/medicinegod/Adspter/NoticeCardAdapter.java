package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.entity.Notice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NoticeCardAdapter extends ArrayAdapter {
    List<Notice> notice = new ArrayList<>();
    Context context;

    public NoticeCardAdapter(Context context, int textViewResourceId, List<Notice> notice) {
        super(context, textViewResourceId, notice);
        this.notice = notice;
        this.context = context;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notice n = notice.get(position);
        View view = null;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_notice, null);//实例化一个对象
        } else {
            view = convertView;
        }


        TextView id_notice_time = (TextView) view.findViewById(R.id.id_notice_time);
        TextView id_notice_title = (TextView) view.findViewById(R.id.id_notice_title);
        TextView id_notice_context = (TextView) view.findViewById(R.id.id_notice_context);
        RoundImageView id_notice_head = (RoundImageView) view.findViewById(R.id.id_notice_head);

        id_notice_time.setText(n.getNotice_time());
        id_notice_title.setText(n.getNotice_title());
        id_notice_context.setText(n.getNotice_context());

        String check = n.getNotice_check();
        if (Integer.parseInt(check) == 0) {
            id_notice_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            id_notice_title.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            id_notice_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            id_notice_title.setTextColor(context.getResources().getColor(R.color.m_grey_70));
        }

//        if (Objects.requireNonNull(map.getOrDefault(Constant.COLUMN_N_FROM, "100000")).toString().equals("100000")) {
//            id_notice_head.setImageResource(R.mipmap.notice_system);
//        }


        return view;


    }


    @Override
    public int getCount() {
        return notice == null ? 0 : notice.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (notice != null && position >= 0 && position < notice.size()) {
            return notice.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}