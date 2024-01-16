package com.daqin.medicinegod;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityDetailBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    View root;
    private String keyid = "";
    private Map<String, Object> map = new HashMap<>();
    int usage_1_util = 0, usage_3_util = 0;

    final DatabaseHelper dbHelper = new DatabaseHelper(DetailActivity.this);

    View dialogV = null;
    View dialogV_addgroup = null;
    int love = 0;
    String w2l_otc = null;
    String w2l_group = null;
    long w2l_outdate = 0;
    Bitmap w2l_img = null;
    Bitmap w2l_img_orgin = null;
    Uri w2l_imgUri = null;

    final int REQUEST_CODE_CHOOSE_IMG = 100;
    final int REQUEST_CODE_SCAN = 101;
    final int REQUEST_CODE_CROP = 102;


    Set<String> w2l_elable = new HashSet<>();
    List<String> group_list = new ArrayList<>();
    String[] group_list_z = null;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        keyid = Utils.getString(DetailActivity.this, Constant.EDITKEY, "none");


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (keyid.equals("none")) {
            AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                    .setTitle("发生错误:")
                    .setMessage("你即将退出此页面，请稍后重试")
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            tips.show();
        } else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_KEYID + " = ? ;", new String[]{keyid});
            while (cursor.moveToNext()) {
                map.put(Constant.COLUMN_M_KEYID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_KEYID)));
                map.put(Constant.COLUMN_M_NAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_NAME)));
                map.put(Constant.COLUMN_M_IMAGE, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_IMAGE)));
                map.put(Constant.COLUMN_M_UID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_UID)));
                map.put(Constant.COLUMN_M_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DESCRIPTION)));
                map.put(Constant.COLUMN_M_OUTDATE, cursor.getLong(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OUTDATE)));
                map.put(Constant.COLUMN_M_OTC, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OTC)));
                map.put(Constant.COLUMN_M_BARCODE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_BARCODE)));
                map.put(Constant.COLUMN_M_YU, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_YU)));
                map.put(Constant.COLUMN_M_ELABEL, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_ELABEL)));
                map.put(Constant.COLUMN_M_LOVE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_LOVE)));
                map.put(Constant.COLUMN_M_SHARE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHARE)));
                map.put(Constant.COLUMN_M_MUSE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_MUSE)));
                map.put(Constant.COLUMN_M_COMPANY, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_COMPANY)));
                map.put(Constant.COLUMN_M_DELFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DELFLAG)));
                map.put(Constant.COLUMN_M_SHOWFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHOWFLAG)));
                map.put(Constant.COLUMN_M_FROMWEB, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_FROMWEB)));
                map.put(Constant.COLUMN_M_GROUP, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_GROUP)));
            }
            cursor.close();
            db.close();
        }
        ImageView id_detail_edit_ic = binding.idDetailEditIc;
        ImageView id_detail_copy_ic = binding.idDetailCopyIc;
        ImageView id_detail_use_ic = binding.idDetailUseIc;
        ImageView id_detail_share_ic = binding.idDetailShareIc;
        ImageView id_detail_del_ic = binding.idDetailDeleteIc;
        ImageView id_detail_w2l_ic = binding.idDetailTransIc;


        id_detail_del_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delMedicine(keyid);
            }
        });
        id_detail_share_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:分享
            }
        });
        id_detail_edit_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, DetailEditActivity.class);
                startActivity(i);
            }
        });

        id_detail_copy_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("提示:")
                        .setMessage("确认要复制一份 “" + map.get(Constant.COLUMN_M_NAME) + "”吗？")
                        .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoga, int which) {
                                int status = copyMedicine(keyid);
                                switch (status) {
                                    case -1:
                                        AlertDialog tips1 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("复制失败，请重试！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips1.show();

                                        break;
                                    case 0:
                                        AlertDialog tips2 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("药品不存在，请刷新后重试！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips2.show();
                                        break;
                                    case 1:
                                        Toast.makeText(DetailActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                                        break;


                                }

                            }
                        })
                        .setNegativeButton("返回", null)
                        .create();
                tips.show();
            }
        });

        TextView m_yu = binding.idDetailYu;
        TextView id_detail_copy = binding.idDetailCopy;
        TextView id_detail_w2l = binding.idDetailW2l;
        id_detail_w2l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showW2lDialog();

            }
        });
        id_detail_w2l_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showW2lDialog();
            }
        });


        TextView id_detail_del = binding.idDetailDel;
        id_detail_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delMedicine(keyid);
            }
        });

        id_detail_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("提示:")
                        .setMessage("确认要复制一份 “" + map.get(Constant.COLUMN_M_NAME) + "”吗？")
                        .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoga, int which) {
                                int status = copyMedicine(keyid);
                                switch (status) {
                                    case -1:
                                        AlertDialog tips1 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("复制失败，请重试！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips1.show();

                                        break;
                                    case 0:
                                        AlertDialog tips2 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("药品不存在，请刷新后重试！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips2.show();
                                        break;
                                    case 1:
                                        AlertDialog tips3 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("复制成功！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips3.show();
                                        break;


                                }

                            }
                        })
                        .setNegativeButton("返回", null)
                        .create();
                tips.show();
            }
        });
        id_detail_use_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yu = Integer.parseInt((Objects.requireNonNull(map.get(Constant.COLUMN_M_YU)).toString()));
                String[] muse = Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE)).toString().split("-");
                int yu2 = Integer.parseInt((muse[0]).toString());
                if ((yu - yu2) <= 0) {
                    AlertDialog tips0 = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("您剩余的药品数量已不足以使用一次！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips0.show();
                    return;
                }
                AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("提示:")
                        .setMessage("确认要使用" + map.get(Constant.COLUMN_M_NAME) + "”吗？\n原剩余" + yu + muse[1] + "，使用后剩余" + (yu - yu2) + muse[1])
                        .setPositiveButton("使用", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoga, int which) {
                                int status = useMedicine(keyid, m_yu);
                                switch (status) {
                                    case 0:
                                        AlertDialog tips1 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("您剩余的药品数量已不足以使用一次！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips1.show();
                                        break;
                                    case 1:
                                        Toast.makeText(DetailActivity.this, "使用成功，已为您更新！", Toast.LENGTH_SHORT).show();
                                        break;
                                    case -1:
                                        AlertDialog tips3 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("药品不存在！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips3.show();
                                        break;
                                    case -2:
                                        AlertDialog tips4 = new AlertDialog.Builder(DetailActivity.this)
                                                .setTitle("提示:")
                                                .setMessage("使用失败，请重试！")
                                                .setNegativeButton("返回", null)
                                                .create();
                                        tips4.show();
                                        break;

                                }
                            }
                        })
                        .setNegativeButton("返回", null)
                        .create();
                tips.show();
            }
        });


        LinearLayout id_detail_love_ic_layout = binding.idDetailLoveIcLayout;
        LinearLayout id_detail_edit_ic_layout = binding.idDetailEditIcLayout;
        LinearLayout id_detail_use_ic_layout = binding.idDetailUseIcLayout;
        LinearLayout id_detail_share_ic_layout = binding.idDetailShareIcLayout;
        LinearLayout id_detail_trans_ic_layout = binding.idDetailTransIcLayout;
        LinearLayout id_detail_outdate = binding.idDetailOutdateLayout;
        LinearLayout id_detail_otc = binding.idDetailOtcLayout;
        LinearLayout id_detail_barcode = binding.idDetailBarcodeLayout;
        LinearLayout id_detail_use = binding.idDetailUsageLayout;
        LinearLayout id_detail_yu = binding.idDetailYuLayout;


        if (Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_M_FROMWEB)).toString()) == 1) {
            id_detail_w2l.setVisibility(View.VISIBLE);
            id_detail_trans_ic_layout.setVisibility(View.VISIBLE);
            id_detail_use_ic_layout.setVisibility(View.GONE);
            id_detail_share_ic_layout.setVisibility(View.GONE);
            id_detail_love_ic_layout.setVisibility(View.GONE);
            id_detail_edit_ic_layout.setVisibility(View.GONE);
            id_detail_outdate.setVisibility(View.GONE);
            id_detail_otc.setVisibility(View.GONE);
            id_detail_barcode.setVisibility(View.GONE);
            id_detail_use.setVisibility(View.GONE);
            id_detail_yu.setVisibility(View.GONE);


        } else {
            id_detail_trans_ic_layout.setVisibility(View.GONE);
            id_detail_w2l.setVisibility(View.GONE);
            ImageView m_love = binding.idDetailLoveIc;
            love = Integer.parseInt((Objects.requireNonNull(map.get(Constant.COLUMN_M_LOVE))).toString());
            if (love == 1) {
                m_love.setImageResource(R.drawable.ic_detail_love_24dp);
            } else {
                m_love.setImageResource(R.drawable.ic_detail_nolove_24dp);
            }

            m_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (this) {
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        if (love == 1) {
                            ContentValues values = new ContentValues();
                            values.put(Constant.COLUMN_M_LOVE, "0");
                            long status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{keyid});
                            if (status >= 0) {
                                Log.d(Constant.TABLE_NAME_MEDICINE, "no love successful");
                                m_love.setImageResource(R.drawable.ic_detail_nolove_24dp);
                                love = 0;
                            } else {
                                Log.d(Constant.TABLE_NAME_MEDICINE, "no love error");
                            }
                        } else {
                            ContentValues values = new ContentValues();
                            values.put(Constant.COLUMN_M_LOVE, "1");
                            long status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{keyid});
                            if (status >= 0) {
                                Log.d(Constant.TABLE_NAME_MEDICINE, "love successful");
                                m_love.setImageResource(R.drawable.ic_detail_love_24dp);
                                love = 1;
                            } else {
                                Log.d(Constant.TABLE_NAME_MEDICINE, "love error");
                            }
                        }
                        db.close();
                    }

                }
            });


            TextView m_outdate1 = binding.idDetailOutdate1;
            TextView m_outdate2 = binding.idDetailOutdate2;
            long date = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
            String value = Utils.getOutDateString(date, 2);
            m_outdate2.setText(value);
            if (value.equals("已过期")) {
                m_outdate2.setVisibility(View.GONE);
                m_outdate2.setTextColor(Color.rgb(249, 155, 148));
            } else {
                m_outdate2.setVisibility(View.VISIBLE);
                m_outdate2.setTextColor(Color.rgb(159, 209, 161));
            }
            String timeA = Utils.getStringFromDate(date);
            int res;
            Calendar cl = Calendar.getInstance();
            //timeA  2022-03-01 药品的时间
            //timeB  2022-01-01 现在的时间
            String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + cl.get(Calendar.DAY_OF_MONTH);
            res = Utils.isTimeOut(timeA, timeB);
            switch (res) {
                case -1:
                    m_outdate1.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                    m_outdate1.setTextColor(Color.rgb(250, 156, 149));

                    break;
                case 0:
                    m_outdate1.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                    m_outdate1.setTextColor(Color.rgb(250, 198, 122));
                    break;
                case 1:
                    m_outdate1.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                    m_outdate1.setTextColor(Color.rgb(160, 210, 162));
                    break;
            }
            TextView m_otc = binding.idDetailOtc;
            String otc = (String) map.get(Constant.COLUMN_M_OTC);
            switch (Objects.requireNonNull(otc)) {
                case "OTC-R":
                    m_otc.setText("OTC");
                    m_otc.setBackground(ResourcesCompat.getDrawable(DetailActivity.this.getResources(),
                            R.drawable.bg_homepage_card_title_red_nohalf, null));
                    break;
                case "OTC-G":
                    m_otc.setText("OTC");
                    m_otc.setBackground(ResourcesCompat.getDrawable(DetailActivity.this.getResources(),
                            R.drawable.bg_homepage_card_title_green_nohalf, null));
                    break;
                case "NONE":
                    m_otc.setText("无");
                    m_otc.setBackground(ResourcesCompat.getDrawable(DetailActivity.this.getResources(),
                            R.drawable.bg_homepage_card_title_grey, null));
                    break;
                case "RX":
                    m_otc.setText("Rx");
                    m_otc.setBackground(ResourcesCompat.getDrawable(DetailActivity.this.getResources(),
                            R.drawable.bg_homepage_card_title_red_nohalf, null));
                    break;
                default:
                    m_otc.setText("未知");
                    m_otc.setBackground(ResourcesCompat.getDrawable(DetailActivity.this.getResources(),
                            R.drawable.bg_homepage_card_title_grey, null));
                    break;

            }

            TextView m_barcode = binding.idDetailBarcode;
            ImageView m_barcode_img = binding.idDetailBarcodeImg;
            m_barcode.setText((String) map.get(Constant.COLUMN_M_BARCODE));

            Bitmap bitmap_bacrcode = Utils.createBarcode(String.valueOf(map.get(Constant.COLUMN_M_BARCODE)), 150, 80, false);
            m_barcode_img.setImageBitmap(bitmap_bacrcode);

            String[] muse = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE))).split("-");
            TextView m_usage = binding.idDetailUsage;
            m_usage.setText(muse[0] + muse[1] + "/" + muse[2] + muse[3] + "/" + muse[4] + muse[5]);
            double yu = Double.parseDouble(Objects.requireNonNull(map.get(Constant.COLUMN_M_YU)).toString());
            double yu2 = Double.parseDouble(muse[0]);
            if ((yu - yu2) <= 0) {
                m_yu.setTextColor(Color.rgb(250, 156, 149));
                m_yu.setText("药品剩余:" + yu + muse[1] + "\n" + "已不够下次使用，如有需要请补充");
            } else if ((yu - yu2) <= 10) {
                m_yu.setTextColor(Color.rgb(250, 198, 122));
                m_yu.setText("即将用光光，如需要请提前补充！\n剩余:" + yu + muse[1] + "\n" + "预计可使用" + String.format("%.2f", (yu / yu2)) + "次");
            } else {
                m_yu.setTextColor(Color.rgb(160, 210, 162));
                m_yu.setText("剩余:" + yu + muse[1] + "\n" + "预计可使用" + String.format("%.2f", (yu / yu2)) + "次");
            }


        }


        ImageView m_img = binding.idDetailImg;
        w2l_img_orgin = Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE));
        m_img.setImageBitmap(w2l_img_orgin);


        ImageView m_back = binding.idDetailBack;
        m_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView m_name = binding.idDetailName;
        m_name.setText((String) map.get(Constant.COLUMN_M_NAME));
        TextView m_desp = binding.idDetailDesp;
        m_desp.setText((String) map.get(Constant.COLUMN_M_DESCRIPTION));

        TextView m_group = binding.idDetailGroup;
        m_group.setText("[ " + (String) map.get(Constant.COLUMN_M_GROUP) + " ] 分组");

        TextView m_company = binding.idDetailCompany;
        m_company.setText((String) map.get(Constant.COLUMN_M_COMPANY));
        FlowLayout flowLayout = binding.idDetailFlowlayout;
        Random random = new Random();
        String[] label = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_ELABEL))).split("@@");
        if (label.length != 0) {
            flowLayout.removeAllViewsInLayout();
            for (String str : label) {
                int viewId = random.nextInt(8999) * 123;
                //设置一个标签
                TextView textView = new TextView(this);
                textView.setId(viewId);
                textView.setText(str);
                textView.setTextSize(14);
                textView.setTextColor(Color.rgb(255, 255, 255));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(20, 5, 20, 5);
                textView.setBackground(this.getDrawable(R.drawable.bg_search_history_btn_blue));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                //添加到布局中
                flowLayout.addView(textView, layoutParams);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMG) {//判断是不是我们选择图片按钮的回调
            if (resultCode == Activity.RESULT_OK && null != data) {
                try {
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
//                System.out.println(Utils.getBase64(cr,uri));
                    w2l_img = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    RoundImageView b_chooseImg = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img);
                    b_chooseImg.setImageBitmap(w2l_img);
                    w2l_imgUri = uri;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailActivity.this, "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String code = data.getStringExtra("codedContent");
                if (Utils.isNum(code)) {
                    EditText t_barcode = dialogV.findViewById(R.id.id_dialog_medicine_w2l_barcode);
                    t_barcode.setText(code);
                } else {
                    Toast.makeText(DetailActivity.this, "条码不正确，请重新扫码", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                try {
                    w2l_img = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    RoundImageView b_chooseImg = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img);
                    b_chooseImg.setImageBitmap(w2l_img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }


    }

    //剪切图片
    //originUri--原始图片的Uri；
    //mDestinationUri--目标裁剪的图片保存的Uri
    private void startImageCrop(Uri uri) {
        if (uri == null) {
            return;
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setMaxBitmapSize(80);
        options.setMaxScaleMultiplier(6);
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "MgCropImage.jpeg"));
        UCrop.of(uri, mDestinationUri)
                .withOptions(options)
                .useSourceImageAspectRatio()
                .withAspectRatio(16, 9)
                .start(this, REQUEST_CODE_CROP);
    }

    private void showW2lDialog() {
        dialogV = LayoutInflater.from(DetailActivity.this).inflate(R.layout.dialog_medicine_w2l, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(DetailActivity.this).setView(dialogV).create();

        TextView id_dialog_medicine_w2l_close = dialogV.findViewById(R.id.id_dialog_medicine_w2l_close);
        id_dialog_medicine_w2l_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                        .setTitle("操作确认:")
                        .setMessage("确认关闭？")
                        .setNeutralButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("返回", null)
                        .create();
                tips.show();


            }
        });

        TextView id_dialog_medicine_w2l_name = dialogV.findViewById(R.id.id_dialog_medicine_w2l_name);
        id_dialog_medicine_w2l_name.setText((String) map.get(Constant.COLUMN_M_NAME));

        TextView id_dialog_medicine_w2l_otc_red = dialogV.findViewById(R.id.id_dialog_medicine_w2l_otc_red);
        TextView id_dialog_medicine_w2l_otc_green = dialogV.findViewById(R.id.id_dialog_medicine_w2l_otc_green);
        TextView id_dialog_medicine_w2l_otc_null = dialogV.findViewById(R.id.id_dialog_medicine_w2l_otc_null);
        TextView id_dialog_medicine_w2l_otc_rx = dialogV.findViewById(R.id.id_dialog_medicine_w2l_otc_rx);

        id_dialog_medicine_w2l_otc_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                w2l_otc = "OTC-R";
                id_dialog_medicine_w2l_otc_red.setBackgroundResource(R.drawable.bg_collage_status_red);
                id_dialog_medicine_w2l_otc_red.setTextColor(Color.rgb(255, 67, 54));

                id_dialog_medicine_w2l_otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_rx.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_green.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_null.setTextColor(Color.rgb(141, 140, 133));

            }
        });
        id_dialog_medicine_w2l_otc_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                w2l_otc = "OTC-G";
                id_dialog_medicine_w2l_otc_green.setBackgroundResource(R.drawable.bg_collage_status_green);
                id_dialog_medicine_w2l_otc_green.setTextColor(Color.rgb(76, 175, 80));

                id_dialog_medicine_w2l_otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_rx.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_red.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_null.setTextColor(Color.rgb(141, 140, 133));
            }
        });
        id_dialog_medicine_w2l_otc_null.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                w2l_otc = "NONE";
                id_dialog_medicine_w2l_otc_null.setBackgroundResource(R.drawable.bg_collage_status_blue);
                id_dialog_medicine_w2l_otc_null.setTextColor(Color.rgb(69, 93, 238));

                id_dialog_medicine_w2l_otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_rx.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_red.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_green.setTextColor(Color.rgb(141, 140, 133));
            }
        });
        id_dialog_medicine_w2l_otc_rx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                w2l_otc = "RX";
                id_dialog_medicine_w2l_otc_rx.setBackgroundResource(R.drawable.bg_collage_status_red);
                id_dialog_medicine_w2l_otc_rx.setTextColor(Color.rgb(255, 67, 54));

                id_dialog_medicine_w2l_otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_null.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_red.setTextColor(Color.rgb(141, 140, 133));
                id_dialog_medicine_w2l_otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                id_dialog_medicine_w2l_otc_green.setTextColor(Color.rgb(141, 140, 133));
            }
        });

        Button id_dialog_medicine_w2l_scanbar = dialogV.findViewById(R.id.id_dialog_medicine_w2l_scanbar);
        id_dialog_medicine_w2l_scanbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CAMERA},
                            1);
                }//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
                else {
                    Intent intent = new Intent(DetailActivity.this, CaptureActivity.class);//黄色是第三方类库里面的类
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }

            }
        });
        w2l_elable.clear();
        FlowLayout flowLayout = dialogV.findViewById(R.id.id_dialog_medicine_w2l_flowlayout);
        TextView id_dialog_medicine_w2l_flowlayout_title = dialogV.findViewById(R.id.id_dialog_medicine_w2l_flowlayout_title);
        Random random = new Random();
        String[] label = ((String) Objects.requireNonNull(map.get(Constant.COLUMN_M_ELABEL))).split("@@");
        if (label.length != 0) {
            flowLayout.removeAllViewsInLayout();
            for (String str : label) {
                int viewId = random.nextInt(8999) * 123;
                //设置一个标签
                TextView textView = new TextView(this);
                textView.setId(viewId);
                textView.setText(str);
                textView.setTextSize(14);
                textView.setTextColor(Color.rgb(141, 140, 133));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(20, 5, 20, 5);
                textView.setBackground(this.getDrawable(R.drawable.bg_collage_status_grey));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (w2l_elable.contains(str)) {
                            w2l_elable.remove(str);
                            textView.setBackground(DetailActivity.this.getDrawable(R.drawable.bg_collage_status_grey));
                            textView.setTextColor(Color.rgb(141, 140, 133));

                        } else {
                            if (w2l_elable.size() >= 5) {
                                Toast.makeText(DetailActivity.this, "已经达到选择上限", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            w2l_elable.add(str);
                            textView.setBackground(DetailActivity.this.getDrawable(R.drawable.bg_collage_status_blue));
                            textView.setTextColor(Color.rgb(69, 93, 238));
                        }
                        id_dialog_medicine_w2l_flowlayout_title.setText("药效标签,请选择(" + w2l_elable.size() + "/5)");

                    }
                });

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                //添加到布局中
                flowLayout.addView(textView, layoutParams);

            }
        }


        ImageView id_dialog_medicine_w2l_img = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img);
        id_dialog_medicine_w2l_img.setImageBitmap(w2l_img_orgin);
        id_dialog_medicine_w2l_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
            }
        });
        Button id_dialog_medicine_w2l_timechoose = dialogV.findViewById(R.id.id_dialog_medicine_w2l_timechoose);

        id_dialog_medicine_w2l_timechoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date myDate = new Date();
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTime(myDate);
                DatePickerDialog dialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        w2l_outdate = Utils.getDateFromString(year + "-" + ((month + 1) > 10 ? (month + 1) : "0" + (month + 1)) + "-" + ((dayOfMonth > 10) ? dayOfMonth : "0" + dayOfMonth));
                        id_dialog_medicine_w2l_timechoose.setText("已选：" + year + "年" + ((month + 1) > 10 ? (month + 1) : "0" + (month + 1)) + "月" + dayOfMonth + "日");
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        ImageButton id_dialog_medicine_w2l_img_orgin = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img_orgin);
        id_dialog_medicine_w2l_img_orgin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                w2l_img.recycle();
                id_dialog_medicine_w2l_img.setImageBitmap(w2l_img_orgin);
                w2l_img = null;
                w2l_imgUri = null;
            }
        });
        TextView id_dialog_medicine_w2l_img_default = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img_default);
        id_dialog_medicine_w2l_img_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_dialog_medicine_w2l_img.setImageResource(R.mipmap.add_imgdefault);
                w2l_img = Utils.getBitmapFromView(id_dialog_medicine_w2l_img);
                w2l_imgUri = null;
            }
        });


        Set<String> set_tmp = Utils.getSet(this, Constant.GROUPLISTKEY, null);
        if (set_tmp == null || set_tmp.size() == 0) {
            group_list = new ArrayList<>();
            group_list.add(Constant.COLUMN_G_ADD);
            group_list.add(Constant.COLUMN_G_DEFAULT);
        } else {
            group_list.add(Constant.COLUMN_G_ADD);
            group_list.addAll(set_tmp);
        }
        if (Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT) == -1) {
            group_list.add(group_list.size(), Constant.COLUMN_G_DEFAULT);
        }
        group_list_z = new String[group_list.size()];
        group_list.toArray(group_list_z);

        Spinner s_group = dialogV.findViewById(R.id.id_dialog_medicine_w2l_spinner);

        ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(this, R.layout.spanner_style_b, group_list_z);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        s_group.setAdapter(adapter_group);//绑定Adapter到控件
        s_group.setSelection(Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT));
        w2l_group = Constant.COLUMN_G_DEFAULT;
        s_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (group_list_z[i].equals(Constant.COLUMN_G_ADD)) {
                    s_group.setSelection(Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT));


                    dialogV_addgroup = LayoutInflater.from(DetailActivity.this).inflate(R.layout.dialog_medicine_addgroup, null, false);
                    final AlertDialog dialog_addgroup = new AlertDialog.Builder(DetailActivity.this).setView(dialogV_addgroup).create();


                    TextView id_dialog_medicine_ag_title = dialogV_addgroup.findViewById(R.id.id_dialog_medicine_ag_title);
                    EditText id_dialog_medicine_ag_text = dialogV_addgroup.findViewById(R.id.id_dialog_medicine_ag_text);
                    Button id_dialog_medicine_ag_ok = dialogV_addgroup.findViewById(R.id.id_dialog_medicine_ag_ok);
                    Button id_dialog_medicine_ag_back = dialogV_addgroup.findViewById(R.id.id_dialog_medicine_ag_back);


                    id_dialog_medicine_ag_text.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            id_dialog_medicine_ag_title.setText("请填写您想要分组名(" + s.length() + "/5)");
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    id_dialog_medicine_ag_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            5)});
                    id_dialog_medicine_ag_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                                    .setTitle("提示:")
                                    .setMessage("确认要关闭吗？")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoga, int which) {
                                            dialog_addgroup.dismiss();
                                        }
                                    })
                                    .setNegativeButton("返回", null)
                                    .create();
                            tips.show();

                        }
                    });


                    id_dialog_medicine_ag_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String str = id_dialog_medicine_ag_text.getText().toString().trim();

                            if (str.length() == 0) {
                                Toast.makeText(DetailActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                group_list.add(1, str);
                                group_list_z = new String[group_list.size()];
                                group_list.toArray(group_list_z);

                                ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(DetailActivity.this, R.layout.spanner_style_b, group_list_z);
                                //建立Adapter并且绑定数据源
                                //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
                                s_group.setAdapter(adapter_group);//绑定Adapter到控件
                                w2l_group = str;
                                s_group.setSelection(Utils.findIndexInList(group_list, str));
                                if (dialog_addgroup != null) {
                                    dialog_addgroup.dismiss();
                                }


                            }
                        }
                    });

                    //清空背景黑框
                    dialog_addgroup.getWindow().getDecorView().setBackground(null);
                    dialog_addgroup.setCancelable(false);
                    dialog_addgroup.show();

                    //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                    dialog_addgroup.getWindow().setLayout((Utils.getScreenWidth(DetailActivity.this) / 4 * 3), Utils.dp2px(DetailActivity.this, 400));


                } else {
                    w2l_group = s_group.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ImageButton id_dialog_medicine_w2l_img_crop = dialogV.findViewById(R.id.id_dialog_medicine_w2l_img_crop);

        id_dialog_medicine_w2l_img_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (w2l_imgUri != null) {
                    id_dialog_medicine_w2l_img.setScaleType(RoundImageView.ScaleType.FIT_XY);
                    startImageCrop(w2l_imgUri);
                } else {
                    Toast.makeText(DetailActivity.this, "原始/默认图片无法裁剪", Toast.LENGTH_SHORT).show();
                }

            }
        });


        EditText id_dialog_medicine_w2l_desp = dialogV.findViewById(R.id.id_dialog_medicine_w2l_desp);
        id_dialog_medicine_w2l_desp.setText((String) map.get(Constant.COLUMN_M_DESCRIPTION));

        EditText id_dialog_medicine_w2l_barcode = dialogV.findViewById(R.id.id_dialog_medicine_w2l_barcode);
        EditText id_dialog_medicine_w2l_company = dialogV.findViewById(R.id.id_dialog_medicine_w2l_company);
        EditText id_dialog_medicine_w2l_yu = dialogV.findViewById(R.id.id_dialog_medicine_w2l_yu);
        EditText id_dialog_medicine_w2l_u1 = dialogV.findViewById(R.id.id_dialog_medicine_w2l_u1);
        TextView id_dialog_medicine_w2l_u1u = dialogV.findViewById(R.id.id_dialog_medicine_w2l_u1_u);
        EditText id_dialog_medicine_w2l_u2 = dialogV.findViewById(R.id.id_dialog_medicine_w2l_u2);
        EditText id_dialog_medicine_w2l_u3 = dialogV.findViewById(R.id.id_dialog_medicine_w2l_u3);
        TextView id_dialog_medicine_w2l_u3u = dialogV.findViewById(R.id.id_dialog_medicine_w2l_u3_u);


        id_dialog_medicine_w2l_u1u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usage_1_util++;
                switch (usage_1_util) {
                    case 0:
                        id_dialog_medicine_w2l_u1u.setText("包");
                        break;
                    case 1:
                        id_dialog_medicine_w2l_u1u.setText("克");
                        break;
                    case 2:
                        id_dialog_medicine_w2l_u1u.setText("片");
                        usage_1_util = -1;
                        break;
                }
            }
        });
        id_dialog_medicine_w2l_u3u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usage_3_util++;
                switch (usage_3_util) {
                    case 0:
                        id_dialog_medicine_w2l_u3u.setText("时");
                        break;
                    case 1:
                        id_dialog_medicine_w2l_u3u.setText("天");
                        usage_3_util = -1;
                        break;
                }
            }
        });

        Button id_dialog_medicine_w2l_trans = dialogV.findViewById(R.id.id_dialog_medicine_w2l_trans);
        id_dialog_medicine_w2l_trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (w2l_img == null) {
                    id_dialog_medicine_w2l_img.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_M_IMAGE)));
                    w2l_img = Utils.getBitmapFromView(id_dialog_medicine_w2l_img);
                }
                if (w2l_outdate <= 0) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("请选择过期时间！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (w2l_otc == null || w2l_otc.equals("")) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("标识不能为空！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (w2l_group == null || w2l_group.equals(Constant.COLUMN_G_ADD)) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("分组不正确！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (w2l_elable.size() < 1 || w2l_elable.size() > 5) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("药效标签不能为空！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (id_dialog_medicine_w2l_desp.getText().length() == 0) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("描述不能为空！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (id_dialog_medicine_w2l_barcode.getText().length() != 13) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("条码格式不对！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (id_dialog_medicine_w2l_company.getText().length() == 0) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("公司名称不能为空！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (Long.parseLong(id_dialog_medicine_w2l_yu.getText().toString()) > Constant.MAX_YU) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("余量填写超过最大限制！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (id_dialog_medicine_w2l_yu.getText().length() == 0) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("余量不能为空！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else if (!Utils.isNum(id_dialog_medicine_w2l_u1.getText().toString()) ||
                        !Utils.isNum(id_dialog_medicine_w2l_u2.getText().toString()) ||
                        !Utils.isNum(id_dialog_medicine_w2l_u3.getText().toString()) ||
                        id_dialog_medicine_w2l_u1.getText().length() > Constant.MAX_USE ||
                        id_dialog_medicine_w2l_u2.getText().length() > Constant.MAX_USE ||
                        id_dialog_medicine_w2l_u3.getText().length() > Constant.MAX_USE) {
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("提示:")
                            .setMessage("用量格式错误！")
                            .setNegativeButton("返回", null)
                            .create();
                    tips.show();
                } else {
                    id_dialog_medicine_w2l_trans.setEnabled(false);
                    AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("操作确认:")
                            .setMessage("即将将网络药品转为本地药品，此操作不可逆!\n由于不正确的转换导致的后果将由您个人负责，请确认！")
                            .setCancelable(false)
                            .setNeutralButton("确认转换", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(Constant.COLUMN_M_KEYID, keyid);
                                    values.put(Constant.COLUMN_M_UID, (String) map.get(Constant.COLUMN_M_UID));
                                    values.put(Constant.COLUMN_M_NAME, (String) map.get(Constant.COLUMN_M_NAME));
                                    values.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(w2l_img));
                                    values.put(Constant.COLUMN_M_DESCRIPTION, id_dialog_medicine_w2l_desp.getText().toString());
                                    values.put(Constant.COLUMN_M_OUTDATE, String.valueOf(w2l_outdate));
                                    values.put(Constant.COLUMN_M_OTC, w2l_otc);
                                    values.put(Constant.COLUMN_M_BARCODE, id_dialog_medicine_w2l_barcode.getText().toString().trim());
                                    values.put(Constant.COLUMN_M_YU, id_dialog_medicine_w2l_yu.getText().toString().trim());
                                    values.put(Constant.COLUMN_M_ELABEL, Utils.getStringFromSet(w2l_elable));
                                    values.put(Constant.COLUMN_M_LOVE, (String) map.get(Constant.COLUMN_M_LOVE));
                                    values.put(Constant.COLUMN_M_SHARE, (String) map.get(Constant.COLUMN_M_SHARE));
                                    values.put(Constant.COLUMN_M_MUSE, id_dialog_medicine_w2l_u1.getText().toString() + "-" + id_dialog_medicine_w2l_u1u.getText().toString() + "-"
                                            + id_dialog_medicine_w2l_u2.getText().toString() + "-次-" +
                                            id_dialog_medicine_w2l_u3.getText().toString() + "-" + id_dialog_medicine_w2l_u3u.getText().toString());
                                    values.put(Constant.COLUMN_M_COMPANY, id_dialog_medicine_w2l_company.getText().toString());
                                    values.put(Constant.COLUMN_M_DELFLAG, 0);
                                    values.put(Constant.COLUMN_M_SHOWFLAG, 0);
                                    values.put(Constant.COLUMN_M_FROMWEB, 0);
                                    values.put(Constant.COLUMN_M_GROUP, w2l_group);
                                    long status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + " = ?", new String[]{keyid});
                                    db.close();
                                    if (status > 0) {
                                        Toast.makeText(DetailActivity.this, "转换成功！", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(DetailActivity.this, "转换失败", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    id_dialog_medicine_w2l_trans.setEnabled(true);
                                }
                            })
                            .create();
                    tips.show();

                }
            }
        });


        //清空背景黑框
        dialog.getWindow().getDecorView().setBackground(null);
        dialog.setCancelable(true);
        dialog.show();

        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((Utils.getScreenWidth(DetailActivity.this) / 10 * 9), Utils.dp2px(DetailActivity.this, 600));

    }

    public boolean isPresentkeyId(String keyid) {
        //TODO:联网
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select `" + Constant.COLUMN_M_KEYID +
                "` from " + Constant.TABLE_NAME_MEDICINE +
                " where `" + Constant.COLUMN_M_KEYID + "` = ?", new String[]{keyid});
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }


    }

    private void delMedicine(String keyid) {
        AlertDialog tips = new AlertDialog.Builder(DetailActivity.this)
                .setTitle("删除确认:")
                .setMessage("确认删除" + map.get(Constant.COLUMN_M_NAME) + "吗")
                .setPositiveButton("返回", null)
                .setNegativeButton("只删除本地", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.delMedicine(DetailActivity.this, keyid, true)) {
                            Toast.makeText(DetailActivity.this, "删除成功,你可以在回收站中找回它", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DetailActivity.this, "删除失败！请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton("彻底删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog dialog1 = new AlertDialog.Builder(DetailActivity.this)
                                .setTitle("确认彻底删除？")
                                .setMessage("是否真正抹除" + map.get(Constant.COLUMN_M_NAME) + "的痕迹？\n【本地无法恢复】")
                                .setNegativeButton("确认删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String lname = Utils.getString(DetailActivity.this, Constant.MG_LOGIN_LNAME, "0");
                                        if (lname.equals("0")) {
                                            Dialog dialog2 = new AlertDialog.Builder(DetailActivity.this)
                                                    .setTitle("错误")
                                                    .setMessage("用户id不存在，请尝试重新登录账号？")
                                                    .setNegativeButton("好", null)
                                                    .create();
                                            dialog2.show();
                                        } else {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Connection connection = JdbcUtil.getConnection();
                                                    PreparedStatement pps = null;
                                                    try {
                                                        pps = connection.prepareStatement("DELETE FROM `" + lname + "` WHERE " + Constant.COLUMN_M_KEYID + " = ?");
                                                        pps.setString(1, keyid);
                                                        long status = pps.executeUpdate();
                                                        if (status >= 1) {
                                                            Looper.prepare();
                                                            if (Utils.delMedicine(DetailActivity.this, keyid)) {
                                                                Toast.makeText(DetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            } else {
                                                                Toast.makeText(DetailActivity.this, "本地删除失败！请重试", Toast.LENGTH_SHORT).show();
                                                            }
                                                            Looper.loop();

                                                        }
                                                    } catch (SQLException sqlException) {
                                                        Looper.prepare();
                                                        Toast.makeText(DetailActivity.this, "云端删除失败，请重试", Toast.LENGTH_SHORT).show();
                                                        Looper.loop();
                                                        sqlException.printStackTrace();
                                                    } finally {
                                                        JdbcUtil.close(connection, null, pps, null);
                                                    }

                                                }
                                            }).start();
                                        }
                                    }
                                })
                                .setNeutralButton("取消", null)
                                .create();
                        dialog1.show();


                    }
                })
                .create();
        tips.show();
    }

    /**
     * useMedicine
     *
     * @return 0 不够使用 1 成功 -2 失败 -1 不存在
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private int useMedicine(String keyid, TextView textView) {
        if (keyid == null || keyid.trim().equals("")) {
            return -1;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + Constant.COLUMN_M_YU + "," + Constant.COLUMN_M_MUSE + " from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_KEYID + " = ? ;", new String[]{keyid});
        double yu = -1.0;
        double yu2 = -1.0;
        String muse = null;
        while (cursor.moveToNext()) {
            yu = cursor.getDouble(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_YU));
            muse = cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_MUSE));
        }

        if (yu <= 0 || muse == null || muse.equals("")) {
            return 0;
        }
        String[] usage = muse.split("-");
        if (usage.length <= 0) {
            return 0;
        }

        yu2 = Double.parseDouble(usage[0]);
        yu -= yu2;
        String yu_yu = String.valueOf(yu).substring(0, String.valueOf(yu).indexOf('.'));
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_M_YU, yu_yu);

        long status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{keyid});
        cursor.close();
        db.close();

        if (status >= 0) {
            Log.d(Constant.TABLE_NAME_MEDICINE, "use success");

            map.put(Constant.COLUMN_M_YU, yu_yu);
            if ((yu - yu2) <= 0) {
                textView.setTextColor(Color.rgb(250, 156, 149));
                textView.setText("药品剩余:" + yu_yu + usage[1] + "\n" + "已不够下次使用，如有需要请补充");
            } else if ((yu - yu2) <= 10) {
                textView.setTextColor(Color.rgb(250, 198, 122));
                textView.setText("即将用光光，如需要请提前补充！\n剩余:" + yu_yu + usage[1] + "\n" + "预计可使用" + String.format("%.2f", (yu / yu2)) + "次");
            } else {
                textView.setTextColor(Color.rgb(160, 210, 162));
                textView.setText("剩余:" + yu_yu + usage[1] + "\n" + "预计可使用" + String.format("%.2f", (yu / yu2)) + "次");
            }
            return 1;
        } else {
            Log.d(Constant.TABLE_NAME_MEDICINE, "use error");
            return -1;
        }

    }


    /**
     * copyMedicine
     *
     * @return 0 不存在  1 成功  -1 失败
     */
    private int copyMedicine(String keyid) {
        if (keyid == null || keyid.trim().equals("")) {
            return 0;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_KEYID + " = ? ;", new String[]{keyid});

        String keyid_new = Utils.getRandomKeyId();
        while (isPresentkeyId(keyid_new)) {
            keyid_new = Utils.getRandomKeyId();
        }
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            values.put(Constant.COLUMN_M_KEYID, keyid_new);
            values.put(Constant.COLUMN_M_UID, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_UID)));
            values.put(Constant.COLUMN_M_NAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_NAME)) + "_副本");
            values.put(Constant.COLUMN_M_IMAGE, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_IMAGE)));
            values.put(Constant.COLUMN_M_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DESCRIPTION)));
            values.put(Constant.COLUMN_M_OUTDATE, cursor.getLong(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OUTDATE)));
            values.put(Constant.COLUMN_M_OTC, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_OTC)));
            values.put(Constant.COLUMN_M_BARCODE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_BARCODE)));
            values.put(Constant.COLUMN_M_YU, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_YU)));
            values.put(Constant.COLUMN_M_ELABEL, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_ELABEL)));
            values.put(Constant.COLUMN_M_LOVE, "0");
            values.put(Constant.COLUMN_M_SHARE, "无");
            values.put(Constant.COLUMN_M_MUSE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_MUSE)));
            values.put(Constant.COLUMN_M_COMPANY, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_COMPANY)));
            values.put(Constant.COLUMN_M_DELFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_DELFLAG)));
            values.put(Constant.COLUMN_M_SHOWFLAG, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_SHOWFLAG)));
            values.put(Constant.COLUMN_M_FROMWEB, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_FROMWEB)));
            values.put(Constant.COLUMN_M_GROUP, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_M_GROUP)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (values.isEmpty()) {
                return 0;
            }
        }
        long status = db.insert(Constant.TABLE_NAME_MEDICINE, null, values);
        cursor.close();
        db.close();

        if (status >= 0) {
            Log.d(Constant.TABLE_NAME_MEDICINE, "copy success");
            return 1;
        } else {
            Log.d(Constant.TABLE_NAME_MEDICINE, "copy error");
            return -1;
        }

    }
}
