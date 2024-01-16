package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;
import com.mysql.jdbc.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class MedicineImportAdapter extends ArrayAdapter {
    List<Map<String, Object>> medicines = new ArrayList<>();
    Context context;

    public MedicineImportAdapter(Context context, int textViewResourceId, List<Map<String, Object>> medicines) {
        super(context, textViewResourceId, medicines);
        this.medicines = medicines;
        this.context = context;
    }


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = medicines.get(position);
        View view = null;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_import, null);//实例化一个对象
        } else {
            view = convertView;
        }


        TextView medicineName = (TextView) view.findViewById(R.id.id_import_title);//获取该布局内的文本视图
        TextView medicinegroup = (TextView) view.findViewById(R.id.id_import_group);
        TextView medicineOtc = (TextView) view.findViewById(R.id.id_import_otc);
        ImageView medicineImage = (ImageView) view.findViewById(R.id.id_import_image);//获取该布局内的图片视图

        TextView medicineCompany = (TextView) view.findViewById(R.id.id_import_company);
        TextView medicineOutdate = (TextView) view.findViewById(R.id.id_import_outdate);
        TextView medicineUsage = (TextView) view.findViewById(R.id.id_import_usage);
        TextView medicineYu = (TextView) view.findViewById(R.id.id_import_yu);
        FlowLayout medicineFlowLayout = (FlowLayout) view.findViewById(R.id.id_import_elabel);
        TextView medicineDesp = (TextView) view.findViewById(R.id.id_import_desp);
        TextView medicineBarcode = (TextView) view.findViewById(R.id.id_import_barcode);
        TextView medicineStatus = (TextView) view.findViewById(R.id.id_import_status);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.id_import_title_view);
        LinearLayout id_import_view = (LinearLayout) view.findViewById(R.id.id_import_view);

        String name = (String) map.get(Constant.COLUMN_M_NAME);
        String otc = (String) map.get(Constant.COLUMN_M_OTC);
        String group = (String) map.get(Constant.COLUMN_M_GROUP);
        String company = (String) map.get(Constant.COLUMN_M_COMPANY);
        String desp = (String) map.get(Constant.COLUMN_M_DESCRIPTION);
        String barcode = (String) map.get(Constant.COLUMN_M_BARCODE);
        String elabel = (String) map.get(Constant.COLUMN_M_ELABEL);
        String usage = (String) map.get(Constant.COLUMN_M_MUSE);
        String yu = (String) map.get(Constant.COLUMN_M_YU);
        map.putIfAbsent(Constant.COLUMN_M_OUTDATE, "2020-01-01");
        long outdate;

        if (Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString().trim().length() != 13) {
            outdate = Utils.getDateFromString(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
        } else {
            outdate = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());

        }
        int isSuccess =Integer.parseInt( Objects.requireNonNull(map.get("isSuccess")).toString());

        StringBuffer sb = new StringBuffer();
        if (name == null || name.equals("") || name.equals("空") || name.equals("无")) {
            name = "药品名不符合填写规范";
            sb.append("药品名不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (otc == null || otc.equals("")) {
            otc = "ERR";
            map.put(Constant.COLUMN_M_OTC, otc);
            sb.append("药品标识不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (group == null || group.equals("") || group.equals("空") || group.equals("无")) {
            group = "默认";
            sb.append("药品组别获取失败，已为你恢复默认，可自行修改").append("\n");
            isSuccess = 0;
        }
        if (company == null || company.equals("") || company.equals("空") || company.equals("无")) {
            company = "药品公司不符合填写规范";
            sb.append("药品公司名不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (desp == null || desp.equals("") || desp.equals("空") || desp.equals("无")) {
            desp = "药品描述不符合填写规范";
            sb.append("药品描述不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (barcode == null || barcode.equals("") || barcode.equals("空") || barcode.equals("无")) {
            barcode = "0000000000000";
            sb.append("药品条码不符合填写规范，请进行修改").append("\n");
            map.put(Constant.COLUMN_M_BARCODE, barcode);
            isSuccess = 0;
        }
        if (yu == null || yu.equals("") || yu.equals("空") || yu.equals("无") || !Utils.isNum(yu)) {
            yu = "0";
            map.put(Constant.COLUMN_M_YU, yu);
            sb.append("药品余量不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (elabel == null || elabel.equals("") || elabel.equals("空") || elabel.equals("无")) {
            elabel = "";
            map.put(Constant.COLUMN_M_ELABEL, elabel);
            sb.append("药品药效不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (usage == null || usage.equals("") || usage.equals("空") || usage.equals("无")) {
            usage = "0-无-0-无-0-无";
            map.put(Constant.COLUMN_M_MUSE, usage);
            sb.append("药品用法用量不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }
        if (outdate == 0L) {
            outdate = Utils.getDateFromString("2020-01-01");
            map.put(Constant.COLUMN_M_OUTDATE, outdate);
            sb.append("药品过期时间不符合填写规范，请进行修改").append("\n");
            isSuccess = 0;
        }


        if (isSuccess == 1 || sb.toString().trim().equals("")) {
            map.put("isSuccess", 1);
            medicineStatus.setText("成功：【" + name + "】导入前置工作已就绪。");
            medicineStatus.setTextColor(context.getResources().getColor(R.color.m_blue));
        } else {
            medicineStatus.setText("错误！有未完成项，请单击未完成项填写完整：\n" + sb);
            medicineStatus.setTextColor(context.getResources().getColor(R.color.m_out_100));

        }

        medicinegroup.setText(group);
        medicineCompany.setText("所产公司：" + company);
        medicineDesp.setText("详情描述：" + desp);
        medicineName.setText(name);
        medicineBarcode.setText("条码：" + barcode);

        String[] elabels = Objects.requireNonNull(elabel).toString().split("@@");

        if (!elabel.equals("") && elabels.length != 0) {
            medicineFlowLayout.removeAllViewsInLayout();
            for (String str : elabels) {
                int viewId = new Random().nextInt(8999) * 123;
                //设置一个标签
                TextView textView = new TextView(getContext());
                textView.setId(viewId);
                textView.setText(str);
                textView.setTextSize(12);
                textView.setTextColor(Color.rgb(255, 255, 255));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(20, 5, 20, 5);
                textView.setBackground(context.getDrawable(R.drawable.bg_search_history_btn_blue));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                //添加到布局中
                medicineFlowLayout.addView(textView, layoutParams);

            }
        }

        map.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(Utils.getBitmapFromResourse(context, R.mipmap.add_imgdefault)));
        medicineImage.setImageResource(R.mipmap.add_imgdefault);


        switch (Objects.requireNonNull(otc)) {
            case "NONE":
            case "无":
                medicineOtc.setText("无");
                medicineOtc.setTextColor(Color.rgb(168, 168, 168));
                break;
            case "OTC-G":
            case "非处方药":
            case "非处方药-绿":
            case "非处方药绿":
                medicineOtc.setText("OTC");
                medicineOtc.setTextColor(Color.rgb(160, 210, 162));
                break;
            case "OTC-R":
            case "非处方药-红":
            case "非处方药红":
                medicineOtc.setText("OTC");
                medicineOtc.setTextColor(Color.rgb(250, 156, 149));
                break;
            case "RX":
            case "处方药":
                medicineOtc.setText("Rx");
                medicineOtc.setTextColor(Color.rgb(250, 156, 149));
                break;
            default:
                medicineOtc.setText("未知");
                medicineOtc.setTextColor(Color.rgb(168, 168, 168));
                break;
        }
        String[] muse = ((String) Objects.requireNonNull(usage)).split("-");

        medicineYu.setText("剩余:" + yu + " " + muse[1]);
        medicineUsage.setText(muse[0] + muse[1] + " - " + muse[2] + muse[3] + " - " + muse[4] + muse[5]);
        int res;
        Calendar cl = Calendar.getInstance();
        //timeA  2022-03-01 药品的时间
        //timeB  2022-01-01 现在的时间
        String timeB = Utils.getDate();
        res = Utils.isTimeOut(Utils.getStringFromDate(outdate), timeB);
        switch (res) {
            case -1:
                medicineOutdate.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                medicineOutdate.setTextColor(Color.rgb(250, 156, 149));
                linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_red, null));
                id_import_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_red_border, null));
                break;
            case 0:
                medicineOutdate.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                medicineOutdate.setTextColor(Color.rgb(250, 198, 122));
                linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_orange, null));
                id_import_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_orange_border, null));
                break;
            case 1:
                medicineOutdate.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                medicineOutdate.setTextColor(Color.rgb(160, 210, 162));
                linearLayout.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_green, null));
                id_import_view.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_homepage_card_title_green_border, null));
                break;
        }


        return view;


//        medicineStatus.setText();

    }


    public void updateView(ArrayList<Map<String, Object>> nowMedicines) {
        this.medicines = nowMedicines;
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