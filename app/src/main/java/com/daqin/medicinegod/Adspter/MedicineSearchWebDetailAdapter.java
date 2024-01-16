package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.AsynImageLoader;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MedicineSearchWebDetailAdapter extends ArrayAdapter {
    private final int resourceId;
    ArrayList<Map<String, Object>> medicines_detail = new ArrayList<>();
    Random random = new Random();

    public MedicineSearchWebDetailAdapter(Context context, int textViewResourceId, ArrayList<Map<String, Object>> medicines_detail) {

        super(context, textViewResourceId, medicines_detail);
        this.medicines_detail = medicines_detail;
        resourceId = textViewResourceId;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = medicines_detail.get(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView medicineImage = (ImageView) view.findViewById(R.id.id_searchres_web_img);//获取该布局内的图片视图
        TextView medicineName = (TextView) view.findViewById(R.id.id_searchres_web_name);//获取该布局内的文本视图
        TextView medicineCompany = (TextView) view.findViewById(R.id.id_searchres_web_company);
        TextView medicineDesp = (TextView) view.findViewById(R.id.id_searchres_web_desp);

        LinearLayout id_searchres_web_view = (LinearLayout) view.findViewById(R.id.id_searchres_web_view);
        id_searchres_web_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                R.drawable.bg_homepage_card_title_blue_border, null));
        medicineName.setText((String) map.get(Constant.COLUMN_S_RESULT_NAME));
        medicineCompany.setText("出产公司:" + (String) map.get(Constant.COLUMN_S_RESULT_COMPANY));
        medicineDesp.setText("药品描述" + (String) map.get(Constant.COLUMN_S_RESULT_DESP));

        String imgpath = (String) map.get(Constant.COLUMN_S_RESULT_IMG);
        if (Objects.requireNonNull(imgpath).contains("disease_default.jpg")) {
            medicineImage.setImageResource(R.mipmap.add_imgdefault);
        } else {
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            asynImageLoader.showImageAsyn(medicineImage, imgpath, R.mipmap.search_result_loading);
        }
        return view;
    }
    /*

    @Override
    public int getCount() {
        return medicines_detail == null ? 0 : medicines_detail.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (medicines_detail != null && position >= 0 && position < medicines_detail.size()) {
            return medicines_detail.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     */

}