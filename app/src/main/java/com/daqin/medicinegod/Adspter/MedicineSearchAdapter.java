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
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class MedicineSearchAdapter extends ArrayAdapter {
    private final int resourceId;
    ArrayList<Map<String, Object>> medicines_result = new ArrayList<>();
    boolean show;

    public MedicineSearchAdapter(Context context, int textViewResourceId, ArrayList<Map<String, Object>> medicines_result, boolean show) {
        super(context, textViewResourceId, medicines_result);
        this.medicines_result = medicines_result;
        resourceId = textViewResourceId;
        this.show = show;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = medicines_result.get(position);
        View view = null;
        if (Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString()) == 1) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_medicine_web, null);//实例化一个对象
            ImageView medicineImage = (ImageView) view.findViewById(R.id.id_card_web_image);//获取该布局内的图片视图
            TextView medicineName = (TextView) view.findViewById(R.id.id_card_web_name);//获取该布局内的文本视图
            TextView medicineCompany = (TextView) view.findViewById(R.id.id_card_web_company);
            TextView medicineUsage = (TextView) view.findViewById(R.id.id_card_web_usage);
            LinearLayout id_card_web_view = (LinearLayout) view.findViewById(R.id.id_card_web_view);

            medicineName.setText((String) map.get(Constant.COLUMN_M_NAME));
            medicineCompany.setText((String) map.get(Constant.COLUMN_M_COMPANY));
            medicineUsage.setText((String) map.get(Constant.COLUMN_M_MUSE));
            medicineImage.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
            id_card_web_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                    R.drawable.bg_homepage_card_title_blue_border, null));
//            Utils.setImageViewCorner(medicineImage, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
            return view;
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_searchres_local, null);//实例化一个对象
            ImageView medicineImage = (ImageView) view.findViewById(R.id.id_searchlist_img);//获取该布局内的图片视图
            TextView medicineName = (TextView) view.findViewById(R.id.id_searchlist_name);//获取该布局内的文本视图
            TextView medicineCompany = (TextView) view.findViewById(R.id.id_searchlist_company);
            TextView medicineYu = (TextView) view.findViewById(R.id.id_searchlist_yu);
            TextView medicineUsage = (TextView) view.findViewById(R.id.id_searchlist_usage);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.id_searchlist_titletack);
            LinearLayout id_searchlist_view = (LinearLayout) view.findViewById(R.id.id_searchlist_view);
            TextView medicineOtc = (TextView) view.findViewById(R.id.id_searchlist_otc);
            TextView medicineDesp = (TextView) view.findViewById(R.id.id_searchlist_desp);
            TextView medicineLove = (TextView) view.findViewById(R.id.id_card_love);//获取该布局内的文本视图
            String love = String.valueOf(map.get(Constant.COLUMN_M_LOVE));
            medicineLove.setVisibility(love.equals("1") ? View.VISIBLE : View.GONE);
            TextView medicineKey = (TextView) view.findViewById(R.id.id_card_key);//获取该布局内的文本视图
            if (medicineKey != null) {
                medicineKey.setText("M-"+(String) map.get(Constant.COLUMN_M_KEYID));
                if (show) {
                    medicineKey.setVisibility(View.VISIBLE);
                } else {
                    medicineKey.setVisibility(View.GONE);
                }
            }

            String otc = (String) map.get(Constant.COLUMN_M_OTC);
            switch (Objects.requireNonNull(otc)) {
                case "NONE":
                    medicineOtc.setText("无");
                    medicineOtc.setTextColor(Color.rgb(168, 168, 168));
                    break;
                case "OTC-G":
                    medicineOtc.setText("OTC");
                    medicineOtc.setTextColor(Color.rgb(160, 210, 162));
                    break;
                case "OTC-R":
                    medicineOtc.setText("OTC");
                    medicineOtc.setTextColor(Color.rgb(250, 156, 149));
                    break;
                case "RX":
                    medicineOtc.setText("Rx");
                    medicineOtc.setTextColor(Color.rgb(250, 156, 149));
                    break;
            }


            long date = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
            String timeA = Utils.getStringFromDate(date);
            int res;
            Calendar cl = Calendar.getInstance();
            //timeA  2022-03-01 药品的时间
            //timeB  2022-01-01 现在的时间
            String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + cl.get(Calendar.DAY_OF_MONTH);
            res = Utils.isTimeOut(timeA, timeB);
            switch (res) {
                case -1:
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red, null));
                    id_searchlist_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red_border, null));
                    break;
                case 0:
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_orange, null));
                    id_searchlist_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red_border, null));
                    break;
                case 1:
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_green, null));
                    id_searchlist_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red_border, null));
                    break;
            }

            medicineName.setText((String) map.get(Constant.COLUMN_M_NAME));
            medicineCompany.setText("出产公司:" + (String) map.get(Constant.COLUMN_M_COMPANY));
            medicineDesp.setText("   " + (String) map.get(Constant.COLUMN_M_DESCRIPTION));
            String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
            medicineYu.setText("剩余:" + map.get(Constant.COLUMN_M_YU) + " " + muse[1]);
            medicineUsage.setText(muse[0] + muse[1] + " - " + muse[2] + muse[3] + " - " + muse[4] + muse[5]);
            medicineImage.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));

//            Utils.setImageViewCorner(medicineImage, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
            return view;
        }
    }
    /*

    @Override
    public int getCount() {
        return medicines_result == null ? 0 : medicines_result.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (medicines_result != null && position >= 0 && position < medicines_result.size()) {
            return medicines_result.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     */

}