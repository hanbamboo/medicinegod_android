package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MedicineCardAdapter extends ArrayAdapter {
    private int date_out = 0, date_near = 0, date_ok = 0;
    ArrayList<Map<String, Object>> medicines = new ArrayList<>();
    Context context;
    boolean show;

    public MedicineCardAdapter(Context context, int textViewResourceId, ArrayList<Map<String, Object>> medicines, boolean show) {
        super(context, textViewResourceId, medicines);
        this.medicines = medicines;
        this.context = context;
        this.show = show;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = medicines.get(position);
        View view = null;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_medicine, null);//实例化一个对象
        } else {
            view = convertView;
        }


        ImageView medicineImage = (ImageView) view.findViewById(R.id.id_card_image);//获取该布局内的图片视图
        TextView medicineName = (TextView) view.findViewById(R.id.id_card_title);//获取该布局内的文本视图
        TextView medicineStatus = (TextView) view.findViewById(R.id.id_card_status);
        TextView medicineUsage = (TextView) view.findViewById(R.id.id_card_usage);
        TextView medicineUsage1 = (TextView) view.findViewById(R.id.id_card_usage1);
        TextView medicineYu = (TextView) view.findViewById(R.id.id_card_yu);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.id_card_title_view);
        LinearLayout id_card_view = (LinearLayout) view.findViewById(R.id.id_card_view);
        TextView medicineOtc = (TextView) view.findViewById(R.id.id_card_otc);
        TextView medicineLove = (TextView) view.findViewById(R.id.id_card_love);//获取该布局内的文本视图
        TextView medicineKey = (TextView) view.findViewById(R.id.id_card_key);//获取该布局内的文本视图
        medicineKey.setText("M-" + (String) map.get(Constant.COLUMN_M_KEYID));
        if (show) {
            medicineKey.setVisibility(View.VISIBLE);
        } else {
            medicineKey.setVisibility(View.GONE);
        }
        String otc = (String) map.get(Constant.COLUMN_M_OTC);
        medicineName.setText((String) map.get(Constant.COLUMN_M_NAME));
        medicineImage.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
//        Utils.setImageViewCorner(medicineImage, Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
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
            default:
                medicineOtc.setText("未知");
                medicineOtc.setTextColor(Color.rgb(168, 168, 168));
                break;
        }
        int fromweb = Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString());
        if (fromweb == 0) {
            String love = String.valueOf(map.get(Constant.COLUMN_M_LOVE));
            medicineLove.setVisibility(love.equals("1") ? View.VISIBLE : View.GONE);
            medicineUsage.setVisibility(View.VISIBLE);
            medicineUsage1.setVisibility(View.GONE);
            medicineYu.setVisibility(View.VISIBLE);
            medicineUsage.setMaxLines(1);
            String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
            medicineYu.setText("剩余:" + map.get(Constant.COLUMN_M_YU) + " " + muse[1]);
            medicineUsage.setText(muse[0] + muse[1] + " - " + muse[2] + muse[3] + " - " + muse[4] + muse[5]);
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
                    date_out++;
                    medicineStatus.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                    medicineStatus.setTextColor(Color.rgb(250, 156, 149));
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red, null));
                    id_card_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red_border, null));
                    break;
                case 0:
                    date_near++;
                    medicineStatus.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                    medicineStatus.setTextColor(Color.rgb(250, 198, 122));
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_orange, null));
                    id_card_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_orange_border, null));
                    break;
                case 1:
                    date_ok++;
                    medicineStatus.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                    medicineStatus.setTextColor(Color.rgb(160, 210, 162));
                    linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_green, null));
                    id_card_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                            R.drawable.bg_homepage_card_title_green_border, null));
                    break;
            }
        } else {
            medicineLove.setVisibility(View.GONE);
            medicineStatus.setText((String) map.get(Constant.COLUMN_M_COMPANY));
            medicineYu.setVisibility(View.GONE);
            medicineUsage1.setVisibility(View.VISIBLE);
            medicineUsage.setVisibility(View.GONE);
            medicineUsage1.setText((String) map.get(Constant.COLUMN_M_MUSE));
            medicineStatus.setTextColor(Color.rgb(155, 167, 240));
            linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                    R.drawable.bg_homepage_card_title_blue, null));
            id_card_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                    R.drawable.bg_homepage_card_title_blue_border, null));

        }

        return view;


    }

    public void updateView(ArrayList<Map<String, Object>> newMedicines) {
        this.medicines = newMedicines;
        this.notifyDataSetChanged();//强制动态刷新数据进而调用getView方法
    }

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


}