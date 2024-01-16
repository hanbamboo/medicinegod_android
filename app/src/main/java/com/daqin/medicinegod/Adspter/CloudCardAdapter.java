package com.daqin.medicinegod.Adspter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2015/10/20.
 */
public class CloudCardAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    int resourcesid;
    static List<Map<String, Object>> medicines = new ArrayList<>();
    boolean show;

    public CloudCardAdapter(Context context, int resourcesid, List<Map<String, Object>> medicines, boolean show) {
        this.resourcesid = resourcesid;
        layoutInflater = LayoutInflater.from(context);
        this.medicines = medicines;
        this.show = show;
    }

    @Override
    public int getCount() {
        return medicines.size();
    }

    @Override
    public Object getItem(int position) {
        return medicines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = medicines.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(resourcesid, null);

            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        byte[] b = (byte[]) map.get(Constant.COLUMN_M_IMAGE);
        if (b == null) {
            viewHolder.id_cloud_image.setImageResource(R.mipmap.add_imgdefault);
        } else {
            viewHolder.id_cloud_image.setImageBitmap(Utils.getBitmapFromByte(b));
        }
        String otc = (String) map.get(Constant.COLUMN_M_OTC);
        switch (Objects.requireNonNull(otc)) {
            case "NONE":
                viewHolder.id_cloud_otc.setText("无");
                viewHolder.id_cloud_otc.setTextColor(Color.rgb(168, 168, 168));
                break;
            case "OTC-G":
                viewHolder.id_cloud_otc.setText("OTC");
                viewHolder.id_cloud_otc.setTextColor(Color.rgb(160, 210, 162));
                break;
            case "OTC-R":
                viewHolder.id_cloud_otc.setText("OTC");
                viewHolder.id_cloud_otc.setTextColor(Color.rgb(250, 156, 149));
                break;
            case "RX":
                viewHolder.id_cloud_otc.setText("Rx");
                viewHolder.id_cloud_otc.setTextColor(Color.rgb(250, 156, 149));
                break;
            default:
                viewHolder.id_cloud_otc.setText("未知");
                viewHolder.id_cloud_otc.setTextColor(Color.rgb(168, 168, 168));
                break;
        }
        viewHolder.id_cloud_title.setText((String) map.get(Constant.COLUMN_M_NAME));

        viewHolder.id_cloud_key.setText("M-" + (String) map.get(Constant.COLUMN_M_KEYID));



        int fromweb = Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString());
        if (fromweb == 0) {
            viewHolder.id_cloud_usage.setVisibility(View.VISIBLE);
            viewHolder.id_cloud_usage1.setVisibility(View.GONE);
            viewHolder.id_cloud_yu.setVisibility(View.VISIBLE);
            viewHolder.id_cloud_usage.setMaxLines(1);
            String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
            viewHolder.id_cloud_yu.setText("剩余:" + map.get(Constant.COLUMN_M_YU) + " " + muse[1]);
            viewHolder.id_cloud_usage.setText(muse[0] + muse[1] + " - " + muse[2] + muse[3] + " - " + muse[4] + muse[5]);
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
                    viewHolder.id_cloud_status.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                    viewHolder.id_cloud_status.setTextColor(Color.rgb(250, 156, 149));
                    viewHolder.id_cloud_title_view.setBackground(ResourcesCompat.getDrawable(convertView.getContext().getResources(),
                            R.drawable.bg_homepage_card_title_red, null));
                    viewHolder.id_cloud_card_view.setBackground(ResourcesCompat.getDrawable(viewHolder.root.getResources(),
                            R.drawable.bg_homepage_card_title_red_border, null));
                    break;
                case 0:
                    viewHolder.id_cloud_status.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                    viewHolder.id_cloud_status.setTextColor(Color.rgb(250, 198, 122));
                    viewHolder.id_cloud_title_view.setBackground(ResourcesCompat.getDrawable(convertView.getContext().getResources(),
                            R.drawable.bg_homepage_card_title_orange, null));
                    viewHolder.id_cloud_card_view.setBackground(ResourcesCompat.getDrawable(viewHolder.root.getResources(),
                            R.drawable.bg_homepage_card_title_orange_border, null));
                    break;
                case 1:
                    viewHolder.id_cloud_status.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                    viewHolder.id_cloud_status.setTextColor(Color.rgb(160, 210, 162));
                    viewHolder.id_cloud_title_view.setBackground(ResourcesCompat.getDrawable(convertView.getContext().getResources(),
                            R.drawable.bg_homepage_card_title_green, null));
                    viewHolder.id_cloud_card_view.setBackground(ResourcesCompat.getDrawable(viewHolder.root.getResources(),
                            R.drawable.bg_homepage_card_title_green_border, null));
                    break;
            }
        } else {
            viewHolder.id_cloud_status.setText((String) map.get(Constant.COLUMN_M_COMPANY));
            viewHolder.id_cloud_yu.setVisibility(View.GONE);
            viewHolder.id_cloud_usage1.setVisibility(View.VISIBLE);
            viewHolder.id_cloud_usage.setVisibility(View.GONE);
            viewHolder.id_cloud_usage1.setText((String) map.get(Constant.COLUMN_M_MUSE));
            viewHolder.id_cloud_status.setTextColor(Color.rgb(155, 167, 240));
            viewHolder.id_cloud_title_view.setBackground(ResourcesCompat.getDrawable(convertView.getContext().getResources(),
                    R.drawable.bg_homepage_card_title_blue, null));
            viewHolder.id_cloud_card_view.setBackground(ResourcesCompat.getDrawable(viewHolder.root.getResources(),
                    R.drawable.bg_homepage_card_title_blue_border, null));
        }


        return convertView;
    }

    public class ViewHolder {
        public final RoundImageView id_cloud_image;
        public final LinearLayout id_cloud_title_view;
        public final LinearLayout id_cloud_card_view;
        public final TextView id_cloud_otc;
        public final TextView id_cloud_title;
        public final TextView id_cloud_status;
        public final TextView id_cloud_usage;
        public final TextView id_cloud_usage1;
        public final TextView id_cloud_yu;
        public final TextView id_cloud_key;
        public final View root;

        public ViewHolder(View root) {
            id_cloud_card_view = (LinearLayout) root.findViewById(R.id.id_card_view);
            id_cloud_key = (TextView) root.findViewById(R.id.id_card_key);
            id_cloud_title_view = (LinearLayout) root.findViewById(R.id.id_card_title_view);
            id_cloud_image = (RoundImageView) root.findViewById(R.id.id_card_image);
            id_cloud_otc = (TextView) root.findViewById(R.id.id_card_otc);
            id_cloud_title = (TextView) root.findViewById(R.id.id_card_title);
            id_cloud_status = (TextView) root.findViewById(R.id.id_card_status);
            id_cloud_usage = (TextView) root.findViewById(R.id.id_card_usage);
            id_cloud_usage1 = (TextView) root.findViewById(R.id.id_card_usage1);
            id_cloud_yu = (TextView) root.findViewById(R.id.id_card_yu);
            this.root = root;
        }
    }
}
