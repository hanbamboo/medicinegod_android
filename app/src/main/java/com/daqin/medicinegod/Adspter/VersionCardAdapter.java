package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.entity.Version;

import java.util.ArrayList;
import java.util.List;

public class VersionCardAdapter extends ArrayAdapter {
    List<Version> version = new ArrayList<>();
    Context context;

    public VersionCardAdapter(Context context, int textViewResourceId, List<Version> version) {
        super(context, textViewResourceId, version);
        this.version = version;
        this.context = context;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Version n = version.get(position);
        View view = null;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_version, null);//实例化一个对象
        } else {
            view = convertView;
        }


        TextView id_version_time = (TextView) view.findViewById(R.id.id_version_time);
        TextView id_version_title = (TextView) view.findViewById(R.id.id_version_title);
        TextView id_version_context = (TextView) view.findViewById(R.id.id_version_context);

        if (n.getVersion() == Constant.VERSION) {
            id_version_title.setTextColor(context.getResources().getColor(R.color.m_blue_80, null));
        } else {
            id_version_title.setTextColor(context.getResources().getColor(R.color.m_grey_90, null));

        }
        id_version_time.setText(n.getVersionTips().split("@@")[0]);
        id_version_title.setText(n.getVersion() + n.getVersionName());
        id_version_context.setText(n.getVersionTips().replace("@@", "\n"));


//        if (Objects.requireNonNull(map.getOrDefault(Constant.COLUMN_N_FROM, "100000")).toString().equals("100000")) {
//            id_version_head.setImageResource(R.mipmap.version_system);
//        }


        return view;


    }


    @Override
    public int getCount() {
        return version == null ? 0 : version.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (version != null && position >= 0 && position < version.size()) {
            return version.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}