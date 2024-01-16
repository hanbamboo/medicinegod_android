package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.daqin.medicinegod.entity.Msg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatAdapter extends ArrayAdapter {
    List<Msg> msg = new ArrayList<>();
    Context context;

    public ChatAdapter(Context context, int textViewResourceId, List<Msg> msg) {
        super(context, textViewResourceId, msg);
        this.msg = msg;
        this.context = context;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg map = msg.get(position);
        View view = null;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_chat_list, null);//实例化一个对象
        } else {
            view = convertView;
        }


        RoundImageView id_dialog_chat_head = (RoundImageView) view.findViewById(R.id.id_dialog_chat_head);
        TextView id_dialog_chat_name = (TextView) view.findViewById(R.id.id_dialog_chat_name);
        TextView id_dialog_chat_msg = (TextView) view.findViewById(R.id.id_dialog_chat_msg);
        TextView id_dialog_chat_time = (TextView) view.findViewById(R.id.id_dialog_chat_time);

        id_dialog_chat_head.setImageResource(R.mipmap.me_man_default);

        id_dialog_chat_name.setText(map.getMsgTo());
        id_dialog_chat_msg.setText(map.getMsgLast());
        id_dialog_chat_time.setText(map.getMsgTime());

        return view;


    }

    public void updateView(List<Msg> msg) {
        this.msg = msg;
        this.notifyDataSetChanged();//强制动态刷新数据进而调用getView方法
    }

    @Override
    public int getCount() {
        return msg == null ? 0 : msg.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (msg != null && position >= 0 && position < msg.size()) {
            return msg.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}