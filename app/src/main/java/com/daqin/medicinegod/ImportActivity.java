package com.daqin.medicinegod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Adspter.HotKeyAdapter;
import com.daqin.medicinegod.Adspter.MedicineCardAdapter;
import com.daqin.medicinegod.Adspter.MedicineImportAdapter;
import com.daqin.medicinegod.CustomWidget.DragFloatingActionButton;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.ExcelUtils;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityImportBinding;
import com.daqin.medicinegod.databinding.ActivitySearchBinding;
import com.mysql.jdbc.Util;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;
import com.zlylib.fileselectorlib.FileSelector;
import com.zlylib.fileselectorlib.utils.Const;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ImportActivity extends AppCompatActivity {

    private ActivityImportBinding binding;
    View root;
    final DatabaseHelper dbHelper = new DatabaseHelper(ImportActivity.this);
    private List<Map<String, Object>> list_import = new ArrayList<>();
    final int REQUEST_CODE_CHOOSE_FILE = 103;
    final int REQUEST_CODE_CHOOSE_IMG = 200;
    final int REQUEST_CODE_SCAN = 201;
    final int REQUEST_CODE_CROP = 202;
    String otc;
    long outDate;
    String strPath;
    String strSheet;


    int usage_1_util = 0, usage_3_util = 0;
    String[] usage;

    String lname;
    Uri imgUri = null;
    Bitmap img = null;

    ArrayList<String> elabel = new ArrayList<>();
    String group = Constant.COLUMN_G_DEFAULT;
    View dialogView;
    String[] group_list_z = null;
    List<String> group_list = new ArrayList<>();
    Map<String, Object> map;


    Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        root = binding.getRoot();
        ImageButton id_import_back = binding.idImportBack;
        id_import_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                finish();
            }
        });
//        DragFloatingActionButton id_import_nextErr = binding.idImportNextErr;
//        id_import_nextErr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //TODO:完成跳转到未完成项目功能
//            }
//        });
        DragFloatingActionButton id_import_import = binding.idImportImport;
        id_import_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = true;
                for (Map<String, Object> temp : list_import) {
                    if (temp.get("isSuccess").equals("0")) {
                        success = false;
                        Dialog dialog_sheet = new AlertDialog.Builder(ImportActivity.this)
                                .setTitle("提示：")
                                .setMessage("还有未填写完成的内容")
                                .setNegativeButton("好", null)
                                .create();
                        dialog_sheet.show();
                    }
                }
                if (success) {
                    Dialog dialog_sheet = new AlertDialog.Builder(ImportActivity.this)
                            .setTitle("提示：")
                            .setMessage("是否确认导入？操作无法取消！")
                            .setNegativeButton("返回", null)
                            .setNeutralButton("开始导入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int progress = 0;
                                    final ProgressDialog dialog = new ProgressDialog(ImportActivity.this);
                                    final Timer timer = new Timer();
                                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    dialog.setMessage(list_import.size() + "条数据正在导入中...");
                                    dialog.setTitle("导入");
                                    dialog.setCancelable(false);
                                    dialog.setMax(list_import.size());
                                    dialog.show();
                                    for (int jj = 0; jj < list_import.size(); jj++) {
                                        String keyid = Utils.getRandomKeyId();
                                        while (isPresentkeyId(keyid)) {
                                            keyid = Utils.getRandomKeyId();
                                        }
                                        Map<String, Object> temp = list_import.get(jj);
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        long outdateee;
                                        if (Objects.requireNonNull(temp.get(Constant.COLUMN_M_OUTDATE)).toString().trim().length() != 13) {
                                            outdateee = Utils.getDateFromString(Objects.requireNonNull(temp.get(Constant.COLUMN_M_OUTDATE)).toString());
                                        } else {
                                            outdateee = Long.parseLong(Objects.requireNonNull(temp.get(Constant.COLUMN_M_OUTDATE)).toString());

                                        }
                                        values.put(Constant.COLUMN_M_KEYID, keyid);
                                        values.put(Constant.COLUMN_M_UID, lname);
                                        values.put(Constant.COLUMN_M_NAME, (String) temp.get(Constant.COLUMN_M_NAME));
                                        values.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(Utils.getBitmapFromResourse(ImportActivity.this, R.mipmap.add_imgdefault)));
                                        values.put(Constant.COLUMN_M_DESCRIPTION, (String) temp.get(Constant.COLUMN_M_DESCRIPTION));
                                        values.put(Constant.COLUMN_M_OUTDATE, outdateee);
                                        values.put(Constant.COLUMN_M_OTC, (String) temp.get(Constant.COLUMN_M_OTC));
                                        values.put(Constant.COLUMN_M_BARCODE, (String) temp.get(Constant.COLUMN_M_BARCODE));
                                        values.put(Constant.COLUMN_M_YU, (String) temp.get(Constant.COLUMN_M_YU));
                                        values.put(Constant.COLUMN_M_ELABEL, (String) temp.get(Constant.COLUMN_M_ELABEL));
                                        values.put(Constant.COLUMN_M_LOVE, "0");
                                        values.put(Constant.COLUMN_M_SHARE, "无");
                                        values.put(Constant.COLUMN_M_MUSE, (String) temp.get(Constant.COLUMN_M_MUSE));
                                        values.put(Constant.COLUMN_M_COMPANY, (String) temp.get(Constant.COLUMN_M_COMPANY));
                                        values.put(Constant.COLUMN_M_DELFLAG, 0);
                                        values.put(Constant.COLUMN_M_SHOWFLAG, 0);
                                        values.put(Constant.COLUMN_M_FROMWEB, 0);
                                        values.put(Constant.COLUMN_M_GROUP, (String) temp.get(Constant.COLUMN_M_GROUP));
                                        long status = db.insert(Constant.TABLE_NAME_MEDICINE, null, values);
                                        Log.i(Constant.PREFERENCES_NAME, "import success :" + jj + " status:" + status);
                                        if (status > 0) {
                                            dialog.setProgress(progress += 1);
                                            if (map != null) {
                                                map.remove(temp);
                                            }
                                        }

                                    }
                                    dialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                    System.out.println(strPath);
                                    Utils.insertHisExcel(ImportActivity.this, Constant.COLUMN_HE_FLAG_INPORT, "导入" + progress + "条数据 表：[" + strSheet + "]", strPath);
                                    if (list_import.size() - progress <= 0) {
                                        Dialog dialog_sheet = new AlertDialog.Builder(ImportActivity.this)
                                                .setTitle("提示：")
                                                .setMessage("全部数据导入成功！\n")
                                                .setNegativeButton("好", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        adapter.clear();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .create();
                                        dialog_sheet.show();
                                    } else {
                                        Dialog dialog_sheet = new AlertDialog.Builder(ImportActivity.this)
                                                .setTitle("提示：")
                                                .setMessage("导入操作完成！\n成功" + progress + "条，失败" + (list_import.size() - progress) + "条\n你可以修改后重新导入！")
                                                .setNegativeButton("好", null)
                                                .setNeutralButton("直接退出", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        adapter.clear();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .create();
                                        dialog_sheet.show();
                                    }

                                }
                            })
                                    .

                            create();
                    dialog_sheet.show();
                }


            }
        });

        FileSelector.from(this)
                // .onlyShowFolder()  //只显示文件夹
                //.onlySelectFolder()  //只能选择文件夹
                        .

                isSingle() // 只能选择一个
                //.setMaxCount(5) //设置最大选择数
                        .

                setFileTypes("xls") //设置文件类型
                //.setFileTypes("xls", "xlsx", "csv") //设置文件类型
                //.setSortType(FileSelector.BY_NAME_ASC) //设置名字排序
                        .

                setSortType(FileSelector.BY_TIME_ASC) //设置时间排序
                //.setSortType(FileSelector.BY_SIZE_DESC) //设置大小排序
//                                                                .setSortType(FileSelector.BY_EXTENSION_ASC) //设置类型排序
        .

                requestCode(REQUEST_CODE_CHOOSE_FILE) //设置返回码
                .

                setTargetPath("/storage/emulated/0/") //设置默认目录
                .

                start();
    }

    @Override
    public void onBackPressed() {
        adapter.clear();
        super.onBackPressed();

    }

    MedicineImportAdapter adapter;
    ListView listView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lname = Utils.getString(this, Constant.MG_LOGIN_LNAME, "0");

        if (requestCode == REQUEST_CODE_CHOOSE_IMG) {//判断是不是我们选择图片按钮的回调
            if (resultCode == Activity.RESULT_OK && null != data) {
                try {
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();

//                System.out.println(Utils.getBase64(cr,uri));
                    img = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageView b_chooseImg = dialogView.findViewById(R.id.id_dialog_medicine_import_img);
                    b_chooseImg.setImageBitmap(img);
                    map.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(img));
//                    Utils.setImageViewCorner(b_chooseImg, img);
                    imgUri = uri;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ImportActivity.this, "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String code = data.getStringExtra("codedContent");
                if (Utils.isNum(code)) {
                    EditText t_barcode = dialogView.findViewById(R.id.id_dialog_medicine_import_scan);
                    t_barcode.setText(code);
                    map.put(Constant.COLUMN_M_BARCODE, code);
                } else {
                    Toast.makeText(ImportActivity.this, "条码不正确，请重新扫码", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                System.out.println(resultUri);
                try {
                    img = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    ImageView b_chooseImg = dialogView.findViewById(R.id.id_dialog_medicine_import_img);
                    b_chooseImg.setImageBitmap(img);
                    map.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(img));

//                    Utils.setImageViewCorner(b_chooseImg, img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } else if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                ArrayList<String> essFileList = data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                strPath = essFileList.toString().split("'")[1];
                if (essFileList.toString().contains(".xls")) {
                    String[] sheets = ExcelUtils.getSheetNames(strPath);
                    if (sheets == null || sheets.length <= 0) {
                        Dialog dialog_sheet = new AlertDialog.Builder(this)
                                .setTitle("错误：")
                                .setMessage("打开文件失败，请使用修改后的模板文件导入！")
                                .setNegativeButton("返回", null)
                                .create();
                        dialog_sheet.show();
                    } else {
                        String[] temp = new String[sheets.length];
                        for (int i = 0; i < sheets.length; i++) {
                            temp[i] = (i + 1) + ". " + sheets[i];
                        }
                        Dialog dialog_sheet = new AlertDialog.Builder(this)
                                .setTitle("请选择需要操作的列")
                                .setNegativeButton("返回", null)
                                .setItems(temp, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            strSheet = sheets[i];
                                            list_import = ExcelUtils.readExcelAll(strPath, sheets[i], Utils.getMedicineColumn_origin());
                                        } catch (Exception e) {
                                            list_import.clear();
                                            e.printStackTrace();
                                        }

                                        if (list_import != null && list_import.size() > 0) {
                                            final ProgressDialog progressDialog_2 = new ProgressDialog(ImportActivity.this);
                                            progressDialog_2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            progressDialog_2.setMessage("正在解析药品中...");
                                            progressDialog_2.setTitle("请稍后(可置于后台)");
                                            progressDialog_2.setMax(list_import.size());
                                            Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                int progress = 0;

                                                @Override
                                                public void run() {
                                                    progressDialog_2.setProgress(progress += 1);

                                                }
                                            }, 0, 2000);
                                            adapter = new MedicineImportAdapter(ImportActivity.this, R.layout.list_import, list_import);
                                            listView = findViewById(R.id.id_import_list);
                                            listView.setAdapter(adapter);
                                            progressDialog_2.dismiss();
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    map = list_import.get(i);

                                                    dialogView = LayoutInflater.from(ImportActivity.this).inflate(R.layout.dialog_medicine_import, null, false);
                                                    final AlertDialog dialog_import = new AlertDialog.Builder(ImportActivity.this)
                                                            .setCancelable(false)
                                                            .setView(dialogView)
                                                            .create();
                                                    EditText i_name = dialogView.findViewById(R.id.id_dialog_medicine_import_name);
                                                    i_name.setText((String) map.get(Constant.COLUMN_M_NAME));
                                                    Button i_outdate = dialogView.findViewById(R.id.id_dialog_medicine_import_timechoose);
                                                    outDate = Utils.getDateFromString(Objects.requireNonNull(map.get(Constant.COLUMN_M_OUTDATE)).toString());
                                                    String value = Utils.getOutDateString(outDate, 2);
                                                    i_outdate.setText(value);
                                                    if (value.equals("已过期")) {
                                                        i_outdate.setTextColor(Color.rgb(249, 155, 148));
                                                    } else {
                                                        i_outdate.setTextColor(Color.rgb(159, 209, 161));
                                                    }
                                                    i_outdate.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View dialogView) {
                                                            Date myDate = new Date();
                                                            Calendar calendar = Calendar.getInstance(Locale.CHINA);
                                                            calendar.setTime(myDate);
                                                            DatePickerDialog dialog = new DatePickerDialog(ImportActivity.this, new DatePickerDialog.OnDateSetListener() {
                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                                    outDate = Utils.getDateFromString(year + "-" + ((month + 1) > 10 ? (month + 1) : "0" + (month + 1)) + "-" + ((dayOfMonth > 10) ? dayOfMonth : "0" + dayOfMonth));
                                                                    map.put(Constant.COLUMN_M_OUTDATE, outDate);
                                                                    String text = Utils.getOutDateString(outDate, 2);
                                                                    i_outdate.setText(text);
                                                                    if (text.equals("已过期")) {
                                                                        i_outdate.setTextColor(Color.rgb(249, 155, 148));
                                                                    } else {
                                                                        i_outdate.setTextColor(Color.rgb(159, 209, 161));
                                                                    }
                                                                }
                                                            },
                                                                    calendar.get(Calendar.YEAR),
                                                                    calendar.get(Calendar.MONTH),
                                                                    calendar.get(Calendar.DAY_OF_MONTH));
                                                            dialog.show();
                                                        }
                                                    });


                                                    otc = (String) map.get(Constant.COLUMN_M_OTC);
                                                    TextView otc_red = dialogView.findViewById(R.id.id_dialog_medicine_import_otc_red);
                                                    TextView otc_green = dialogView.findViewById(R.id.id_dialog_medicine_import_otc_green);
                                                    TextView otc_null = dialogView.findViewById(R.id.id_dialog_medicine_import_otc_null);
                                                    TextView otc_rx = dialogView.findViewById(R.id.id_dialog_medicine_import_otc_rx);
                                                    otc_rx.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            otc = "RX";
                                                            map.put(Constant.COLUMN_M_OTC, otc);
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_red);
                                                            otc_rx.setTextColor(Color.rgb(255, 67, 54));

                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                        }
                                                    });
                                                    otc_null.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            otc = "NONE";
                                                            map.put(Constant.COLUMN_M_OTC, otc);

                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_blue);
                                                            otc_null.setTextColor(Color.rgb(69, 93, 238));

                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));
                                                        }
                                                    });
                                                    otc_red.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            otc = "OTC_R";
                                                            map.put(Constant.COLUMN_M_OTC, otc);

                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_red);
                                                            otc_red.setTextColor(Color.rgb(255, 67, 54));

                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));

                                                        }
                                                    });
                                                    otc_green.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            otc = "OTC_G";
                                                            map.put(Constant.COLUMN_M_OTC, otc);

                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_green);
                                                            otc_green.setTextColor(Color.rgb(76, 175, 80));

                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));

                                                        }
                                                    });
                                                    switch (Objects.requireNonNull(otc)) {
                                                        case "OTC-G":
                                                        case "非处方药":
                                                        case "非处方药-绿":
                                                        case "非处方药绿":
                                                            otc = "OTC_G";
                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_green);
                                                            otc_green.setTextColor(Color.rgb(76, 175, 80));

                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));
                                                            break;
                                                        case "OTC-R":
                                                        case "非处方药-红":
                                                        case "非处方药红":
                                                            otc = "OTC-R";
                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_red);
                                                            otc_red.setTextColor(Color.rgb(255, 67, 54));

                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));
                                                            break;
                                                        case "RX":
                                                        case "处方药":
                                                            otc = "RX";
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_red);
                                                            otc_rx.setTextColor(Color.rgb(255, 67, 54));

                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_null.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                            break;
                                                        case "NONE":
                                                        case "无":
                                                        default:
                                                            otc = "NONE";
                                                            otc_null.setBackgroundResource(R.drawable.bg_collage_status_blue);
                                                            otc_null.setTextColor(Color.rgb(69, 93, 238));

                                                            otc_red.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_red.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_green.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_green.setTextColor(Color.rgb(141, 140, 133));
                                                            otc_rx.setBackgroundResource(R.drawable.bg_collage_status_grey);
                                                            otc_rx.setTextColor(Color.rgb(141, 140, 133));
                                                            break;
                                                    }


                                                    RoundImageView i_img = dialogView.findViewById(R.id.id_dialog_medicine_import_img);

                                                    img = Utils.getBitmapFromView(i_img);
                                                    TextView i_img_default = dialogView.findViewById(R.id.id_dialog_medicine_import_img_default);
                                                    i_img_default.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            i_img.setImageResource(R.mipmap.add_imgdefault);
                                                        }
                                                    });
                                                    ImageButton i_img_crop = dialogView.findViewById(R.id.id_dialog_medicine_import_img_crop);
                                                    i_img_crop.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            startImageCrop(imgUri);

                                                        }
                                                    });
                                                    i_img.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View dialogView) {
                                                            Intent intent = new Intent();
                                                            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                            intent.setType("image/*");
                                                            startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
                                                        }
                                                    });


                                                    TextView flowlayout_title = dialogView.findViewById(R.id.id_dialog_medicine_import_flowlayout_title);
                                                    FlowLayout flowLayout = dialogView.findViewById(R.id.id_dialog_medicine_import_flowlayout);
                                                    Button b_elabel1 = dialogView.findViewById(R.id.id_dialog_medicine_import_e_elabel1);
                                                    Button b_elabel2 = dialogView.findViewById(R.id.id_dialog_medicine_import_e_elabel2);
                                                    Button b_elabel3 = dialogView.findViewById(R.id.id_dialog_medicine_import_e_elabel3);
                                                    Button b_elabel4 = dialogView.findViewById(R.id.id_dialog_medicine_import_e_elabel4);
                                                    Button b_elabel5 = dialogView.findViewById(R.id.id_dialog_medicine_import_e_elabel5);
                                                    EditText t_elabelBox = dialogView.findViewById(R.id.id_dialog_medicine_import_elabel_box);
                                                    Button t_elabelAdd = dialogView.findViewById(R.id.id_dialog_medicine_import_elabel_add);

                                                    Button[] btn_elabel = new Button[]{b_elabel1, b_elabel2, b_elabel3, b_elabel4, b_elabel5};
                                                    String[] elableeString = ((String) map.get(Constant.COLUMN_M_ELABEL)).split("@@");
                                                    elabel.clear();
                                                    elabel.addAll(Arrays.asList(elableeString));
                                                    for (Button btn : btn_elabel) {
                                                        btn.setVisibility(View.GONE);
                                                    }
                                                    if (elabel != null && elabel.size() != 0) {
                                                        for (int j = 0; j < elabel.size(); j++) {
                                                            btn_elabel[j].setVisibility(View.VISIBLE);
                                                            btn_elabel[j].setText(elabel.get(j));
                                                        }

                                                    }
                                                    for (Button btn : btn_elabel) {
                                                        btn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String str = (String) btn.getText();
                                                                AlertDialog tips = new AlertDialog.Builder(ImportActivity.this)
                                                                        .setTitle("提示:")
                                                                        .setMessage("确认删除" + str + "吗")
                                                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                elabel.remove(str);
                                                                                map.put(Constant.COLUMN_M_ELABEL, Utils.getStringFromArrayList(elabel));
                                                                                for (Button btn : btn_elabel) {
                                                                                    btn.setVisibility(View.GONE);
                                                                                    btn.setText("");
                                                                                    flowlayout_title.setText("药效标签,请选择(" + elabel.size() + "/5)");
                                                                                }
                                                                                for (int i = 0; i < elabel.size(); i++) {
                                                                                    btn_elabel[i].setText(elabel.get(i));
                                                                                    btn_elabel[i].setVisibility(View.VISIBLE);
                                                                                }
                                                                            }
                                                                        })
                                                                        .setNegativeButton("取消", null)
                                                                        .create();
                                                                tips.show();
                                                            }
                                                        });
                                                    }

                                                    t_elabelAdd.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (t_elabelBox.getText() != null && !t_elabelBox.getText().toString().trim().equals("")) {
                                                                String str = t_elabelBox.getText().toString().trim();
                                                                if (elabel.size() < 5) {
                                                                    if (!elabel.contains(str)) {
                                                                        t_elabelBox.setText("");
                                                                        elabel.add(str);
                                                                        flowLayout.setVisibility(View.VISIBLE);
                                                                        flowlayout_title.setText("药效标签,请选择(" + elabel.size() + "/5)");
                                                                        Utils.getStringFromArrayList(elabel);
                                                                        map.put(Constant.COLUMN_M_ELABEL, Utils.getStringFromArrayList(elabel));
                                                                        for (Button btn : btn_elabel) {
                                                                            if (btn.getText().equals("")) {
                                                                                btn.setText(str);
                                                                                btn.setVisibility(View.VISIBLE);

                                                                                break;
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(ImportActivity.this, "此标签已存在", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(ImportActivity.this, "数量已达上限(5)", Toast.LENGTH_SHORT).show();
                                                                }


                                                            } else {
                                                                Toast.makeText(ImportActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });


                                                    EditText i_desp = dialogView.findViewById(R.id.id_dialog_medicine_import_desp);
                                                    i_desp.setText((String) map.get(Constant.COLUMN_M_DESCRIPTION));
                                                    usage = (Objects.requireNonNull(map.get(Constant.COLUMN_M_MUSE)).toString()).split("-");
                                                    if (usage.length != 6) {
                                                        usage = new String[]{"0", "包", "0", "次", "0", "天",};
                                                    }
                                                    EditText u1 = dialogView.findViewById(R.id.id_dialog_medicine_import_u1);
                                                    u1.setText(usage[0].toString());
                                                    TextView u11 = dialogView.findViewById(R.id.id_dialog_medicine_import_u1_u);
                                                    u11.setText(usage[1].toString());
                                                    EditText u2 = dialogView.findViewById(R.id.id_dialog_medicine_import_u2);
                                                    u2.setText(usage[2].toString());
                                                    TextView u22 = dialogView.findViewById(R.id.id_dialog_medicine_import_u2_u);
                                                    u22.setText(usage[3].toString());
                                                    EditText u3 = dialogView.findViewById(R.id.id_dialog_medicine_import_u3);
                                                    u3.setText(usage[4].toString());
                                                    TextView u33 = dialogView.findViewById(R.id.id_dialog_medicine_import_u3_u);
                                                    u33.setText(usage[5].toString());
                                                    TextView i_yu_title = dialogView.findViewById(R.id.id_dialog_medicine_import_yu_title);


                                                    u11.setOnLongClickListener(new View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View view) {
                                                            View dialogV = LayoutInflater.from(ImportActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                                                            final AlertDialog dialogu1 = new AlertDialog.Builder(ImportActivity.this).setView(dialogV).create();


                                                            TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                                                            EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                                                            Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                                                            Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                                                            id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                                                                @Override
                                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                    id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable s) {
                                                                }
                                                            });


                                                            id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                                                    5)});
                                                            id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    AlertDialog tips = new AlertDialog.Builder(ImportActivity.this)
                                                                            .setTitle("提示:")
                                                                            .setMessage("确认要关闭吗？")
                                                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialoga, int which) {
                                                                                    dialogu1.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("返回", null)
                                                                            .create();
                                                                    tips.show();

                                                                }
                                                            });


                                                            id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    String str = id_dialog_medicine_au_text.getText().toString().trim();
                                                                    if (str.length() == 0) {
                                                                        Toast.makeText(ImportActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        usage[1] = str;
                                                                        u11.setText(str);
                                                                        i_yu_title.setText("药品余量(单位:" + str + ")");
                                                                        dialogu1.dismiss();
                                                                    }
                                                                }
                                                            });

                                                            //清空背景黑框
                                                            dialogu1.getWindow().getDecorView().setBackground(null);
                                                            dialogu1.setCancelable(false);
                                                            dialogu1.show();

                                                            //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                                            dialogu1.getWindow().setLayout((Utils.getScreenWidth(ImportActivity.this) / 4 * 3), Utils.dp2px(ImportActivity.this, 400));


                                                            return false;
                                                        }
                                                    });
                                                    u22.setOnLongClickListener(new View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View view) {
                                                            View dialogV = LayoutInflater.from(ImportActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                                                            final AlertDialog dialogu2 = new AlertDialog.Builder(ImportActivity.this).setView(dialogV).create();


                                                            TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                                                            EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                                                            Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                                                            Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                                                            id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                                                                @Override
                                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                    id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable s) {
                                                                }
                                                            });


                                                            id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                                                    5)});
                                                            id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    AlertDialog tips = new AlertDialog.Builder(ImportActivity.this)
                                                                            .setTitle("提示:")
                                                                            .setMessage("确认要关闭吗？")
                                                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialoga, int which) {
                                                                                    dialogu2.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("返回", null)
                                                                            .create();
                                                                    tips.show();

                                                                }
                                                            });


                                                            id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    String str = id_dialog_medicine_au_text.getText().toString().trim();
                                                                    if (str.length() == 0) {
                                                                        Toast.makeText(ImportActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        usage[3] = str;
                                                                        u22.setText(str);
                                                                        i_yu_title.setText("药品余量(单位:" + str + ")");
                                                                        dialogu2.dismiss();

                                                                    }
                                                                }
                                                            });

                                                            //清空背景黑框
                                                            dialogu2.getWindow().getDecorView().setBackground(null);
                                                            dialogu2.setCancelable(false);
                                                            dialogu2.show();

                                                            //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                                            dialogu2.getWindow().setLayout((Utils.getScreenWidth(ImportActivity.this) / 4 * 3), Utils.dp2px(ImportActivity.this, 400));


                                                            return false;
                                                        }
                                                    });
                                                    u33.setOnLongClickListener(new View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View view) {
                                                            View dialogV = LayoutInflater.from(ImportActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                                                            final AlertDialog dialogu2 = new AlertDialog.Builder(ImportActivity.this).setView(dialogV).create();


                                                            TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                                                            EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                                                            Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                                                            Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                                                            id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                                                                @Override
                                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                                    id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                                                                }

                                                                @Override
                                                                public void afterTextChanged(Editable s) {
                                                                }
                                                            });


                                                            id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                                                    5)});
                                                            id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    AlertDialog tips = new AlertDialog.Builder(ImportActivity.this)
                                                                            .setTitle("提示:")
                                                                            .setMessage("确认要关闭吗？")
                                                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialoga, int which) {
                                                                                    dialogu2.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("返回", null)
                                                                            .create();
                                                                    tips.show();

                                                                }
                                                            });


                                                            id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    String str = id_dialog_medicine_au_text.getText().toString().trim();
                                                                    if (str.length() == 0) {
                                                                        Toast.makeText(ImportActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        usage[5] = str;
                                                                        u33.setText(str);
                                                                        i_yu_title.setText("药品余量(单位:" + str + ")");
                                                                        dialogu2.dismiss();

                                                                    }
                                                                }
                                                            });

                                                            //清空背景黑框
                                                            dialogu2.getWindow().getDecorView().setBackground(null);
                                                            dialogu2.setCancelable(false);
                                                            dialogu2.show();

                                                            //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                                            dialogu2.getWindow().setLayout((Utils.getScreenWidth(ImportActivity.this) / 4 * 3), Utils.dp2px(ImportActivity.this, 400));


                                                            return false;
                                                        }
                                                    });

                                                    u33.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            usage_3_util++;
                                                            switch (usage_3_util) {
                                                                case 0:
                                                                    u33.setText("时");
                                                                    break;
                                                                case 1:
                                                                    u33.setText("天");
                                                                    usage_3_util = -1;
                                                                    break;
                                                            }
                                                            usage[5] = u33.getText().toString().trim();
                                                        }
                                                    });
                                                    u11.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            usage_1_util++;
                                                            switch (usage_1_util) {
                                                                case 0:
                                                                    u11.setText("包");
                                                                    i_yu_title.setText("药品余量(单位:包)");
                                                                    break;
                                                                case 1:
                                                                    u11.setText("克");
                                                                    i_yu_title.setText("药品余量(单位:克)");
                                                                    break;
                                                                case 2:
                                                                    u11.setText("片");
                                                                    i_yu_title.setText("药品余量(单位:片)");
                                                                    usage_1_util = -1;
                                                                    break;
                                                            }
                                                            usage[1] = u11.getText().toString().trim();

                                                        }
                                                    });


                                                    EditText i_yu = dialogView.findViewById(R.id.id_dialog_medicine_import_yu);
                                                    i_yu.setText((String) map.get(Constant.COLUMN_M_YU));
                                                    EditText i_barcode = dialogView.findViewById(R.id.id_dialog_medicine_import_barcode);
                                                    i_barcode.setText((String) map.get(Constant.COLUMN_M_BARCODE));
                                                    EditText i_company = dialogView.findViewById(R.id.id_dialog_medicine_import_company);
                                                    i_company.setText((String) map.get(Constant.COLUMN_M_COMPANY));

                                                    Spinner i_groupf = dialogView.findViewById(R.id.id_dialog_medicine_import_spinner);
                                                    group_list.clear();

                                                    Set<String> set_tmp = Utils.getSet(ImportActivity.this, Constant.GROUPLISTKEY, null);
                                                    String aa = (String) map.get(Constant.COLUMN_M_GROUP);
                                                    set_tmp.add(aa);
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


                                                    ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(ImportActivity.this, R.layout.spanner_style_b, group_list_z);
                                                    //建立Adapter并且绑定数据源
                                                    //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
                                                    i_groupf.setAdapter(adapter_group);//绑定Adapter到控件

                                                    i_groupf.setSelection(Utils.findIndexInList(group_list, aa));

                                                    i_groupf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                            if (group_list_z[i].equals(Constant.COLUMN_G_ADD)) {
                                                                i_groupf.setSelection(Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT));


                                                                View dialogV = LayoutInflater.from(ImportActivity.this).inflate(R.layout.dialog_medicine_addgroup, null, false);
                                                                final AlertDialog dialoggg = new AlertDialog.Builder(ImportActivity.this).setView(dialogV).create();


                                                                TextView id_dialog_medicine_ag_title = dialogV.findViewById(R.id.id_dialog_medicine_ag_title);
                                                                EditText id_dialog_medicine_ag_text = dialogV.findViewById(R.id.id_dialog_medicine_ag_text);
                                                                Button id_dialog_medicine_ag_ok = dialogV.findViewById(R.id.id_dialog_medicine_ag_ok);
                                                                Button id_dialog_medicine_ag_back = dialogV.findViewById(R.id.id_dialog_medicine_ag_back);


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
                                                                        AlertDialog tips = new AlertDialog.Builder(ImportActivity.this)
                                                                                .setTitle("提示:")
                                                                                .setMessage("确认要关闭吗？")
                                                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialoga, int which) {
                                                                                        dialoggg.dismiss();
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
                                                                            Toast.makeText(ImportActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();
                                                                        } else {

                                                                            group_list.add(1, str);
                                                                            group_list_z = new String[group_list.size()];
                                                                            group_list.toArray(group_list_z);

                                                                            ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(ImportActivity.this, R.layout.spanner_style_b, group_list_z);
                                                                            //建立Adapter并且绑定数据源
                                                                            //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
                                                                            i_groupf.setAdapter(adapter_group);//绑定Adapter到控件
                                                                            i_groupf.setSelection(Utils.findIndexInList(group_list, str));
                                                                            group = str;
                                                                            map.put(Constant.COLUMN_M_GROUP, str);

                                                                            if (dialoggg != null) {
                                                                                dialoggg.dismiss();
                                                                            }


                                                                        }
                                                                    }
                                                                });

                                                                //清空背景黑框
                                                                dialoggg.getWindow().getDecorView().setBackground(null);
                                                                dialoggg.setCancelable(false);
                                                                dialoggg.show();

                                                                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                                                dialoggg.getWindow().setLayout((Utils.getScreenWidth(ImportActivity.this) / 4 * 3), Utils.dp2px(ImportActivity.this, 400));


                                                            } else {
                                                                group = i_groupf.getSelectedItem().toString();
                                                                map.put(Constant.COLUMN_M_GROUP, group);

                                                            }

                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                                        }
                                                    });

                                                    TextView close = dialogView.findViewById(R.id.id_dialog_medicine_import_close);
                                                    close.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            int isSuccess = 1;
                                                            StringBuffer sb = new StringBuffer();
                                                            if (i_name.getText().length() <= 0 || i_name.getText().toString().trim().equals("") || i_name.getText().toString().equals("空") || i_name.getText().toString().equals("无")) {
                                                                sb.append("药品名不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_NAME, i_name.getText().toString().trim());
                                                            }
                                                            if (otc == null || otc.equals("")) {
                                                                otc = "ERR";
                                                                sb.append("药品标识不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            }
                                                            if (group == null || group.equals("") || group.equals("空") || group.equals("无")) {
                                                                group = "默认";
                                                                sb.append("药品组别获取失败，已为你恢复默认，可自行修改").append("\n");
                                                                isSuccess = 0;
                                                            }
                                                            if (i_company.getText().length() <= 0 || i_company.getText().toString().trim().equals("") || i_company.getText().toString().equals("空") || i_company.getText().toString().trim().equals("无")) {
                                                                sb.append("药品公司名不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_COMPANY, i_company.getText().toString().trim());
                                                            }
                                                            if (i_desp.getText().length() <= 0 || i_desp.getText().toString().trim().equals("")) {
                                                                sb.append("药品描述不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_DESCRIPTION, i_desp.getText().toString().trim());
                                                            }
                                                            if (i_barcode.getText().length() != 13 || i_barcode.getText().toString().trim().equals("") || !Utils.isNum(i_barcode.getText().toString().trim())) {
                                                                sb.append("药品条码不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_BARCODE, i_barcode.getText().toString().trim());
                                                            }
                                                            if (i_yu.getText().length() <= 0 || i_yu.getText().toString().trim().equals("") || !Utils.isNum(i_yu.getText().toString().trim())) {
                                                                sb.append("药品余量不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_YU, i_yu.getText().toString().trim());
                                                            }
                                                            if (elabel == null || elabel.size() == 0) {
                                                                sb.append("药品药效不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            }
                                                            if (usage == null || usage.length != 6) {
                                                                sb.append("药品用法用量不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            } else {
                                                                map.put(Constant.COLUMN_M_MUSE, usage[0] + "-" + usage[1] + "-" + usage[2] + "-" + usage[3] + "-" + usage[4] + "-" + usage[5]);
                                                            }
                                                            if (outDate <= 0L) {
                                                                sb.append("药品过期时间不符合填写规范，请进行修改").append("\n");
                                                                isSuccess = 0;
                                                            }


                                                            map.put("isSuccess", isSuccess);
                                                            adapter.notifyDataSetChanged();
                                                            dialog_import.dismiss();
                                                        }
                                                    });

                                                    //取消了保存按钮

//                                                    Button okk = dialogView.findViewById(R.id.id_dialog_medicine_import_ok);
//                                                    okk.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            if (i_name.getText().length() == 0 || i_name.getText().toString().trim().equals("")) {
//                                                                Toast.makeText(ImportActivity.this, "未填写药品名", Toast.LENGTH_SHORT).show();
//                                                            } else if (img == null) {
//                                                                Toast.makeText(ImportActivity.this, "未填写图片，可设置默认图片", Toast.LENGTH_SHORT).show();
//                                                            } else if (i_desp.getText().length() == 0 || i_desp.getText().toString().trim().equals("")) {
//                                                                Toast.makeText(ImportActivity.this, "未填写药品描述", Toast.LENGTH_SHORT).show();
//                                                            } else if (outDate == 0L) {
//                                                                Toast.makeText(ImportActivity.this, "未选择过期时效", Toast.LENGTH_SHORT).show();
//                                                            } else if (otc == null || otc.equals("")) {
//                                                                Toast.makeText(ImportActivity.this, "未选择药品标识", Toast.LENGTH_SHORT).show();
//                                                            } else if (i_barcode.getText().length() == 0 || i_barcode.getText().toString().trim().equals("") || i_barcode.getText().length() != 13) {
//                                                                Toast.makeText(ImportActivity.this, "未选择药品条码", Toast.LENGTH_SHORT).show();
//                                                            } else if (!Utils.isNum(i_barcode.getText().toString())) {
//                                                                Toast.makeText(ImportActivity.this, "条码不符合规范", Toast.LENGTH_SHORT).show();
//                                                            } else if ((u1.getText().length() == 0 || u1.getText().toString().trim().equals("")) ||
//                                                                    (u2.getText().length() == 0 || u2.getText().toString().trim().equals("")) ||
//                                                                    (u3.getText().length() == 0 || u3.getText().toString().trim().equals(""))) {
//                                                                Toast.makeText(ImportActivity.this, "未填写药品用法用量", Toast.LENGTH_SHORT).show();
//                                                            } else if (!Utils.isNum(u1.getText().toString()) ||
//                                                                    !Utils.isNum(u2.getText().toString()) ||
//                                                                    !Utils.isNum(u3.getText().toString())) {
//                                                                Toast.makeText(ImportActivity.this, "用法用量不符合规范", Toast.LENGTH_SHORT).show();
//                                                            } else if (i_company.getText().length() == 0 || i_company.getText().toString().trim().equals("")) {
//                                                                Toast.makeText(ImportActivity.this, "未填写公司名", Toast.LENGTH_SHORT).show();
//                                                            } else if (i_yu.getText().length() == 0 || i_yu.getText().toString().trim().equals("")) {
//                                                                Toast.makeText(ImportActivity.this, "未填写余量", Toast.LENGTH_SHORT).show();
//                                                            } else if (!Utils.isNum(i_yu.getText().toString())) {
//                                                                Toast.makeText(ImportActivity.this, "余量不符合规范", Toast.LENGTH_SHORT).show();
//                                                            } else if (elabel.size() <= 0) {
//                                                                Toast.makeText(ImportActivity.this, "未填写标签，至少填写一个", Toast.LENGTH_SHORT).show();
//                                                            } else {
////                                                                SQLiteDatabase db = dbHelper.getWritableDatabase();
////                                                                ContentValues values = new ContentValues();
////                                                                String keyid = Utils.getRandomKeyId();
////                                                                while (isPresentkeyId(keyid)) {
////                                                                    keyid = Utils.getRandomKeyId();
////                                                                }
////                                                                values.put(Constant.COLUMN_M_KEYID, keyid);
////                                                                values.put(Constant.COLUMN_M_NAME, i_name.getText().toString());
////                                                                values.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(Utils.getBitmapFromView(i_img)));
////                                                                values.put(Constant.COLUMN_M_DESCRIPTION, i_desp.getText().toString());
////                                                                values.put(Constant.COLUMN_M_OUTDATE, outDate);
////                                                                values.put(Constant.COLUMN_M_OTC, otc);
////                                                                values.put(Constant.COLUMN_M_BARCODE, i_barcode.getText().toString().trim());
////                                                                values.put(Constant.COLUMN_M_YU, i_yu.getText().toString().trim());
////                                                                values.put(Constant.COLUMN_M_ELABEL, Utils.getStringFromArrayList(elabel));
////                                                                values.put(Constant.COLUMN_M_GROUP, group);
////                                                                values.put(Constant.COLUMN_M_MUSE, u1.getText().toString() + "-" + u11.getText().toString() + "-"
////                                                                        + u2.getText().toString() + "-" + u22.getText().toString() + "-" +
////                                                                        u3.getText().toString() + "-" + u33.getText().toString());
////                                                                values.put(Constant.COLUMN_M_COMPANY, i_company.getText().toString());
////                                                                long status = db.insert(Constant.TABLE_NAME_MEDICINE, null, values);
////                                                                if (status >= 0) {
////                                                                    Toast.makeText(ImportActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
////                                                                    Log.d(Constant.TABLE_NAME_MEDICINE, "update successful");
////                                                                    db.close();
////                                                                } else {
////                                                                    Toast.makeText(ImportActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
////                                                                }
////                    finish();
//                                                            }
//
//                                                        }
//                                                    });


                                                    //清空背景黑框
                                                    dialog_import.getWindow().getDecorView().setBackground(null);
                                                    dialog_import.show();

                                                    //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                                                    dialog_import.getWindow().setLayout((Utils.getScreenWidth(ImportActivity.this) / 10 * 9), Utils.dp2px(ImportActivity.this, 600));


                                                }

                                            });


                                        }


                                    }
                                })
                                .create();

                        dialog_sheet.show();
                    }

                } else {
                    Dialog dialog_t = new AlertDialog.Builder(this)
                            .setTitle("请选择.xls文件")
                            .setMessage("请选择导入填写后的模板文件！")
                            .setNegativeButton("返回", null)
                            .create();
                    dialog_t.show();
                }


            }
        }
    }

    //剪切图片
    //originUri--原始图片的Uri；
    //mDestinationUri--目标裁剪的图片保存的Uri
    private void startImageCrop(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setMaxBitmapSize(100);
        options.setMaxScaleMultiplier(6);
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "MgCropImage.jpeg"));
        UCrop.of(uri, mDestinationUri)
                .withOptions(options)
                .useSourceImageAspectRatio()
                .withAspectRatio(16, 9)
                .start(this, REQUEST_CODE_CROP);
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

}

