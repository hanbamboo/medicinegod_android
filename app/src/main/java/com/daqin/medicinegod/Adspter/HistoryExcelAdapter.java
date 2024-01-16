package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.HistoryExcelActivity;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HistoryExcelAdapter extends ArrayAdapter {
    private final int resourceId;
    List<Map<String, Object>> history_excel = new ArrayList<>();


    public HistoryExcelAdapter(Context context, int textViewResourceId, List<Map<String, Object>> history_excel) {
        super(context, textViewResourceId, history_excel);
        this.history_excel = history_excel;
        resourceId = textViewResourceId;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = history_excel.get(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView id_his_flag = view.findViewById(R.id.id_his_flag);
        TextView id_his_title = view.findViewById(R.id.id_his_title);
        TextView id_his_path = view.findViewById(R.id.id_his_path);
        TextView id_his_time = view.findViewById(R.id.id_his_time);


        String flag = (String) map.get(Constant.COLUMN_HE_FLAG);
        if (!Utils.isExists((String) map.get(Constant.COLUMN_HE_FILEPATH))) {
            flag = Constant.COLUMN_HE_FLAG_EXPIRE;
            map.put(Constant.COLUMN_HE_FLAG, Constant.COLUMN_HE_FLAG_EXPIRE);
        }
        id_his_flag.setText(flag);

        switch (Objects.requireNonNull(flag)) {
            case Constant.COLUMN_HE_FLAG_TEMP:
                id_his_flag.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_blue, null));
                id_his_flag.setTextColor(Color.rgb(155, 167, 240));
                break;
            case Constant.COLUMN_HE_FLAG_INPORT:
                id_his_flag.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_red, null));
                id_his_flag.setTextColor(Color.rgb(250, 156, 149));
                break;
            case Constant.COLUMN_HE_FLAG_EXPORT:
                id_his_flag.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_green, null));
                id_his_flag.setTextColor(Color.rgb(160, 210, 162));
                break;
            case Constant.COLUMN_HE_FLAG_EXPIRE:
                id_his_flag.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_grey, null));
                id_his_flag.setTextColor(Color.rgb(168, 168, 168));
                id_his_title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                id_his_path.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                id_his_time.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                break;
        }

        id_his_title.setText(Objects.requireNonNull(map.get(Constant.COLUMN_HE_NAME)).toString());
        id_his_path.setText(Objects.requireNonNull(map.get(Constant.COLUMN_HE_FILEPATH)).toString());
        id_his_time.setText((String) map.get(Constant.COLUMN_HE_TIME));


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