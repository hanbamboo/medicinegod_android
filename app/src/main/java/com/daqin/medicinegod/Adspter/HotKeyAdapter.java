package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class HotKeyAdapter extends ArrayAdapter {
    private final int resourceId;
    ArrayList<Map<String, Object>> hotkeys = new ArrayList<>();

    public HotKeyAdapter(Context context, int textViewResourceId, ArrayList<Map<String, Object>> hotkeys) {
        super(context, textViewResourceId, hotkeys);
        this.hotkeys = hotkeys;
        resourceId = textViewResourceId;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = hotkeys.get(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView hotkey_num = view.findViewById(R.id.id_hotkey_num);
        hotkey_num.setText(String.valueOf(position + 1));
        TextView hotkey_key = view.findViewById(R.id.id_hotkey_key);
        hotkey_key.setText((String) map.get(Constant.COLUMN_H_HOTKEY));
        TextView hotkey_times = view.findViewById(R.id.id_hotkey_tims);
        long times = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_H_TIMES)).toString());
        if (times >= 9999) {
            hotkey_times.setText("9999+");
        } else {
            hotkey_times.setText(String.valueOf(times));
        }


        return view;
    }
    /*

    @Override
    public int getCount() {
        return medicines == null ? 0 : medicines.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (medicines != null && position >= 0 && position < medicines.size()) {
            return medicines.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     */

}